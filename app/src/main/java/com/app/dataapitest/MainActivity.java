package com.app.dataapitest;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import API.APIService;
import API.DataAPI;
import Adapters.VideoListAdapter;
import Modals.Example;
import Modals.Item;
import retrofit2.Call;
import retrofit2.Callback;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();
    public static List<Item> videoList;
    private RecyclerView recyclerView;
    private VideoListAdapter listAdapter;
    private RecyclerView.LayoutManager mLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewID);

        generateRecyclerView();

        GetVideoList("");

    }


    public void generateRecyclerView(){
        // Set up RecyclerView
        videoList = new ArrayList<>();
        mLayoutManager = new GridLayoutManager(MainActivity.this, 1);
        listAdapter = new VideoListAdapter(MainActivity.this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(listAdapter);
    }


    public void GetVideoList(String pageToken){

        Call<Example> youtubeResponseCall = APIService.getInstance()
                .listVideos(DataAPI.CHANNEL_ID,DataAPI.API_KEY,"snippet","date","IN","20",pageToken);

        youtubeResponseCall.enqueue(new Callback<Example>() {
            @Override
            public void onResponse(Call<Example> call, retrofit2.Response<Example> response) {

                Example data = response.body();
                if(data!=null){

                    Log.d("YoutubeAPI",String.valueOf(data.getNextPageToken()));
                    listAdapter.addVideos(data.getItems());

                }



            }

            @Override
            public void onFailure(Call<Example> call, Throwable t) {

                Log.d("YoutubeAPI", String.valueOf(t.getMessage()));
                Log.d("YoutubeAPI", String.valueOf(t.fillInStackTrace()));

            }
        });

    }





}
