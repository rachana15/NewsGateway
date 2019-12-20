package com.newsgateway;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.content.res.Configuration;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.*;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
{
    private static final String TAG = "MainActivity";
    static final String ACTION_MSG_TO_SERVICE = "ACTION_MSG_TO_SERVICE";
    static final String ACTION_NEWS_STORY = "ACTION_NEWS_STORY";
    private ArrayAdapter mDrawerListadapter;
    private List<Fragment> fragments;
    private ViewPager pager;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private NewsReciever category;
    private MyPageAdapter newsAdapter;
    HashMap hashmap = new HashMap();
    private int flag = 1;
    private ActionBarDrawerToggle mDrawerToggle;
    private ArrayList<String> items = new ArrayList<>();
    private ArrayList<String> newsResource = new ArrayList<>();
    private ArrayList<ServiceGetSet> newsResourceList = new ArrayList<>();
    String[] categoryArray = new String[newsResource.size()];
    Fragment mContent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.drawer);
        String all = "all";
        new NewsSourceDownloader(MainActivity.this).execute(all);
        category = new NewsReciever();

        Intent serviceintent = new Intent(MainActivity.this, NewService.class);
        startService(serviceintent);

        IntentFilter filter1 = new IntentFilter(ACTION_NEWS_STORY);
        registerReceiver(category, filter1);

        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                R.string.drawer_open,
                R.string.drawer_close
        );


        mDrawerListadapter = new ArrayAdapter<>(this,
                R.layout.drawer_list_item, items);
        mDrawerList.setAdapter(mDrawerListadapter);
        mDrawerList.setOnItemClickListener(
                new ListView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                    {
                        selectItem(position);
                        Log.d(TAG, items.get(position));
                        pager.setBackground(null);
                        for(int i = 0; i< newsResourceList.size(); i++)
                        {
                            if(items.get(position).equals(newsResourceList.get(i).getName()))
                            {
                                Intent newintent = new Intent();
                                Log.d("Position", items.get(position));
                                newintent.putExtra("myinfo", newsResourceList.get(i));
                                newintent.setAction(ACTION_MSG_TO_SERVICE);
                                sendBroadcast(newintent);
                                mDrawerLayout.closeDrawer(mDrawerList);
                            }
                        }
                    }
                }
        );

        if (savedInstanceState != null) {
            mContent = getSupportFragmentManager().getFragment(savedInstanceState, "myFragmentName");
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        fragments = getFragments();
        newsAdapter = new MyPageAdapter(getSupportFragmentManager());
        pager = (ViewPager) findViewById(R.id.viewpager);
        pager.setBackgroundResource(R.drawable.newsbackground);
        pager.setAdapter(newsAdapter);

    }

    private void selectItem(int position)
    {
        Toast.makeText(this, items.get(position), Toast.LENGTH_SHORT).show();
        setTitle(items.get(position));
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    private class NewsReciever extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            switch (intent.getAction())
            {
                case ACTION_NEWS_STORY:
                    if (intent.hasExtra("rachana"))
                    {
                       reDoFragments((ArrayList<NewsArticleData>) intent.getSerializableExtra("rachana"));
                    }
            }

        }
    }

    public void setSources(ArrayList<ServiceGetSet> newsResourceList, ArrayList<String> newsresourcecategory1)
    {
        hashmap.clear();
        items.removeAll(items);
        this.newsResourceList.removeAll(this.newsResourceList);
        this.newsResourceList.addAll(newsResourceList);
        Log.d(TAG, String.valueOf(this.newsResourceList.size()));
        newsresourcecategory1.add(0, "all");

        Log.d("flag1", String.valueOf(flag));
        if(flag == 1)
        {
            newsResource.removeAll(newsResource);
            newsResource.addAll(newsresourcecategory1);
            categoryArray = newsResource.toArray(new String[newsResource.size()]);
            flag++;
            Log.d("flag2", String.valueOf(flag));
        }
        for(int k = 0; k< this.newsResourceList.size(); k++)
        {
            items.add(this.newsResourceList.get(k).getName());
            hashmap.put(this.newsResourceList.get(k).getName(), this.newsResourceList.get(k));
        }
        invalidateOptionsMenu();
        Log.d(TAG, String.valueOf(items.size()));
        mDrawerListadapter.notifyDataSetChanged();
    }

    private void reDoFragments(ArrayList<NewsArticleData> article)
    {
        for (int i = 0; i < newsAdapter.getCount(); i++)
        {
            newsAdapter.notifyChangeInPosition(i);
        }
        fragments.clear();

        for (int f = 0; f < article.size(); f++)
        {
            Log.d("DEEP", article.get(f).getTitle());
            fragments.add(NewsFragment.newInstance(article.get(f).getTitle(), article.get(f).getUrlToImage(), article.get(f).getAuthor(), article.get(f).getDescription(), article.get(f).getPublishedAt(), article.get(f).getUrl(), " Page " + (f+1) + " of" + article.size()));
        }

        newsAdapter.notifyDataSetChanged();
        pager.setCurrentItem(0);

    }

    private List<Fragment> getFragments() {
        List<Fragment> fList = new ArrayList<Fragment>();
        return fList;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.action_menu, menu);
        return true;
    }
    public boolean onPrepareOptionsMenu(Menu menu) {
        Log.d(TAG,"onPrepareOptionsMenu "+ categoryArray.length);
        menu.clear();
        if(categoryArray.length != 0)
        {
            for (int i = 0; i < categoryArray.length; i++)
            {
                menu.add(R.menu.action_menu, Menu.NONE, 0, categoryArray[i]);
                Log.d(TAG, "onPrepareOptionsMenu: " + categoryArray[i]);
            }
            return true;
        }
        else
            return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (mDrawerToggle.onOptionsItemSelected(item))
        {
            Log.d(TAG, "onOptionsItemSelected: mDrawerToggle " + item);
            return true;
        }


        Log.d("item", String.valueOf(item));
        new NewsSourceDownloader(MainActivity.this).execute(String.valueOf(item));
        return true;
    }

    private class MyPageAdapter extends FragmentPagerAdapter {
        private long baseId = 0;


        public MyPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public long getItemId(int position) {
            return baseId + position;
        }


        public void notifyChangeInPosition(int n) {
            baseId += getCount() + n;
        }

    }


    @Override
    protected void onDestroy()
    {
        unregisterReceiver(category);
        Intent intent = new Intent(MainActivity.this, NewService.class);
        stopService(intent);
        super.onDestroy();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
        items.addAll(savedInstanceState.getStringArrayList("HISTORY"));
        newsResource.addAll(savedInstanceState.getStringArrayList("HISTORY1"));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList("HISTORY",items);
        outState.putStringArrayList("HISTORY1",newsResource);
    }
}


