package com.newsgateway;


import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class NewsSourceDownloader extends AsyncTask<String, Void, String>
{
    String yahoo, facebook;
    String pass;
    Uri URLdatauri = null;
    String urlToUse = null;
    private MainActivity mainActivity;
    private static final String TAG = "NewsSourceDownloader";
    private final String APIKEY = "https://newsapi.org/v1/sources?language=en&country=us&category=";
    private final String apiresourcekey ="&apiKey=b9d880e99b394d4e80aedadcc0e9ecf2";
    ArrayList<ServiceGetSet> newsresourcelist = new ArrayList<>();
    ArrayList<String> newsresourcecategory = new ArrayList<>();
    ArrayList<String> newsresourcecategory1 = new ArrayList<>();

    public NewsSourceDownloader(MainActivity ma)
    {
        mainActivity = ma;
    }


    @Override
    protected void onPreExecute()
    {
        Toast.makeText(mainActivity, "Loading NewsSource Data...", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected String doInBackground(String... params)
    {
        String line, s11;
        StringBuilder sb = new StringBuilder();
        yahoo = null;
        facebook = null;
        urlToUse = null;
        URLdatauri = null;

        if(params[0].equals("all"))
        {
            URLdatauri = Uri.parse(APIKEY + apiresourcekey);
            urlToUse = URLdatauri.toString();
        }
        else
        {
            URLdatauri = Uri.parse(APIKEY + params[0] + apiresourcekey);
            urlToUse = URLdatauri.toString();
        }

        try
        {
            URL url = new URL(urlToUse);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));
            while ((line = reader.readLine()) != null)
            {
                sb.append(line).append('\n');
            }
        }
        catch (FileNotFoundException e)
        {
            ArrayList<String> newssource = new ArrayList<>();
            return String.valueOf(newssource);
        }
        catch (Exception e)
        {
            ArrayList<String> newssource = new ArrayList<>();
            return String.valueOf(newssource);
        }

        ArrayList<ServiceGetSet> newssource = parseJSON(sb.toString());
        return String.valueOf(newssource);
    }

    private ArrayList<ServiceGetSet> parseJSON(String s)
    {
        String cid = null, cname = null, curl = null, ccategory = null;
        try
        {
            JSONObject jr1 = new JSONObject(s);
            JSONArray response1 = jr1.getJSONArray("sources");
            Log.d(TAG, "response1 Length: " +response1.length());

            //NewsResource data
            for(int i =0; i<response1.length(); i++)
            {
                {
                    JSONObject jb1 = response1.getJSONObject(i);
                    cid = jb1.getString("id");
                    Log.d("[" + i + "]" + "Channelid:", cid);
                    cname = jb1.getString("name");
                    Log.d("[" + i + "]" + "Channelname:", cname);
                    curl = jb1.getString("url");
                    Log.d("[" + i + "]" + "ChannelURL:", curl);
                    ccategory = jb1.getString("category");
                    Log.d("[" + i + "]" + "ChannelCategory:", ccategory);
                }
                newsresourcelist.add(new ServiceGetSet(cid, cname, curl, ccategory));
                newsresourcecategory.add(ccategory);
            }
            for(int k = 0; k<newsresourcecategory.size(); k++)
            {
                Log.d(TAG, "ResourceList: [" + k + "]" + newsresourcecategory);
            }
            Set<String> hashmap = new HashSet<>();
            hashmap.addAll(newsresourcecategory);
            newsresourcecategory.clear();
            newsresourcecategory1.addAll(hashmap);
            for(int i = 0; i<newsresourcelist.size(); i++)
            {
                Log.d(TAG, "ResourceList: [" + i + "]" + newsresourcelist.get(i).getId());
                Log.d(TAG, "ResourceList: [" + i + "]" + newsresourcelist.get(i).getName());
                Log.d(TAG, "ResourceList: [" + i + "]" + newsresourcelist.get(i).getUrl());
                Log.d(TAG, "ResourceList: [" + i + "]" + newsresourcelist.get(i).getCategory());
            }

            return newsresourcelist;

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s)
    {
        Toast.makeText(mainActivity, "Loading Article Data..", Toast.LENGTH_SHORT).show();
        if(newsresourcelist.size() > 0)
        {
            mainActivity.setSources(newsresourcelist, newsresourcecategory1);
        }
    }
}
