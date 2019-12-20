package com.newsgateway;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;
import java.util.ArrayList;
import static com.newsgateway.MainActivity.ACTION_MSG_TO_SERVICE;
import static com.newsgateway.MainActivity.ACTION_NEWS_STORY;


public class NewService extends Service {

    private static final String TAG = "NewService";
    private boolean running = true;
    public ServiceGetSet source;
    private ArrayList<NewsArticleData> storylist = new ArrayList<>();
    private ServiceReciever serviceReciever;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("rachana", "new Intent service has been started:");

        serviceReciever = new ServiceReciever();
        IntentFilter filter1 = new IntentFilter(ACTION_MSG_TO_SERVICE);
        registerReceiver(serviceReciever, filter1);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (running) {
                    try {
                        while (storylist.size() == 0) {

                            Thread.sleep(250);
                        }

                        Intent i = new Intent();
                        i.setAction(ACTION_NEWS_STORY);
                        i.putExtra("rachana", storylist);
                        sendBroadcast(i);
                        storylist.removeAll(storylist);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        }).start();
        return Service.START_STICKY;
    }

    private class ServiceReciever extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            switch (intent.getAction()) {
                case ACTION_MSG_TO_SERVICE:
                    if (intent.hasExtra("myinfo"))
                    {
                        source = (ServiceGetSet) intent.getSerializableExtra("myinfo");
                        new NewsArticleDownloader(NewService.this, source.getId()).execute();
                    }
            }

        }
    }

    public void setArticles(ArrayList<NewsArticleData> newsarticlelist)
    {
        storylist.clear();
        storylist.addAll(newsarticlelist);
    }

    @Override
    public void onDestroy()
    {
        unregisterReceiver(serviceReciever);
        Intent intent = new Intent(NewService.this, MainActivity.class);
        stopService(intent);
        super.onDestroy();
    }
}



