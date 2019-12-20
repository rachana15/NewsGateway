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


class NewsArticleDownloader extends AsyncTask<String, Void, String>
{
    String x, y;
    String id1;
    Uri datagenerator = null;
    String urlToUse = null;
    ArrayList<NewsArticleData> articlelist = new ArrayList<>();
    private NewService newsservice;
    private static final String TAG = "NewsArticleDownloader";
    private final String dataURL = "https://newsapi.org/v1/articles?source=";
    private final String apikey ="&apiKey=b9d880e99b394d4e80aedadcc0e9ecf2";


    public NewsArticleDownloader(NewService ma, String id)
    {
        newsservice = ma;
        id1 = id;
    }

    @Override
    protected void onPreExecute()
    {
        Toast.makeText(newsservice, "Loading NewsArticle Data...", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected String doInBackground(String... params)
    {
        x = null;
        y = null;
        urlToUse = null;
        datagenerator = null;
        datagenerator = Uri.parse(dataURL + id1 + apikey);
        urlToUse = datagenerator.toString();
        Log.d(TAG, urlToUse);
        StringBuilder sb = new StringBuilder();
        String line, s11;
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
            Log.e(TAG, "DoException: ", e);
            return String.valueOf(newssource);
        }
        catch (Exception e)
        {
            ArrayList<String> newssource = new ArrayList<>();
            Log.e(TAG, "DoException: ", e);
            return String.valueOf(newssource);
        }
        ArrayList<NewsArticleData> newsArticle = parseJSON(sb.toString());
        return String.valueOf(newsArticle);
    }

    private ArrayList<NewsArticleData> parseJSON(String s)
    {
        Log.d("00000", s);
        String author = null, title = null, description = null, urlToImage = null, publishedAt = null, url = null;
        try
        {
            JSONObject jObject = new JSONObject(s);
            JSONArray response1 = jObject.getJSONArray("articles");
            Log.d(TAG, "Article Length: " +response1.length());
            for(int i =0; i<response1.length(); i++)
            {
                {
                    JSONObject job = response1.getJSONObject(i);
                    author = job.getString("author");
                    title = job.getString("title");
                    description = job.getString("description");
                    urlToImage = job.getString("urlToImage");
                    publishedAt = job.getString("publishedAt");
                    url = job.getString("url");
                }
                articlelist.add(new NewsArticleData(author, title, description, urlToImage, publishedAt, url));
            }
            for(int k = 0; k< articlelist.size(); k++)
            {
                Log.d(TAG, "ArrayList: [" + k + "]" + articlelist.get(k).getAuthor());
                Log.d(TAG, "ArrayList: [" + k + "]" + articlelist.get(k).getTitle());
                Log.d(TAG, "ArrayList: [" + k + "]" + articlelist.get(k).getDescription());
                Log.d(TAG, "ArrayList: [" + k + "]" + articlelist.get(k).getUrlToImage());
                Log.d(TAG, "ArrayList: [" + k + "]" + articlelist.get(k).getPublishedAt());
                Log.d(TAG, "ArrayList: [" + k + "]" + articlelist.get(k).getUrl());
            }
            return articlelist;
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
        if(articlelist.size() > 0)
        {
            newsservice.setArticles(articlelist);
        }
    }
}
