package com.app.dataapitest;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import API.APIService;
import API.DataAPI;
import Adapters.VideoListAdapter;
import Modals.Example;
import Modals.Item;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();
    public static List<Item> videoList;
    private RecyclerView recyclerView;
    private VideoListAdapter listAdapter;
    public String nextPageToken = "";
    Animation slide_down,slide_up;
    private RecyclerView.LayoutManager mLayoutManager;
    RelativeLayout loadingLayout;
    boolean isLoading = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewID);
        loadingLayout = (RelativeLayout) findViewById(R.id.loadingRecyclerID);
        //Load animation
        slide_down = AnimationUtils.loadAnimation(this, R.anim.slide_down);
        slide_up = AnimationUtils.loadAnimation(this, R.anim.slide_up);

        generateRecyclerView();

        loadingLayout.setVisibility(View.VISIBLE);
        loadingLayout.startAnimation(slide_down);
        GetVideoList("");



        // Load more images onScroll end
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                // Check if end of page has been reached
                if( !isLoading && ((LinearLayoutManager)mLayoutManager).findLastVisibleItemPosition() == listAdapter.getItemCount()-1 ){
                    isLoading = true;
                    Log.d(TAG , "End has reached, loading more images!");
                    loadingLayout.startAnimation(slide_up);
                    loadingLayout.setVisibility(View.VISIBLE);
                    GetVideoList(nextPageToken);
                }
            }
        });


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

//        youtubeResponseCall.enqueue(new Callback<Example>() {
//            @Override
//            public void onResponse(Call<Example> call, retrofit2.Response<Example> response) {
//
//                Example data = response.body();
//                if(data!=null){
//
//                    nextPageToken = data.getNextPageToken();
//                    Log.d("YoutubeAPI",String.valueOf(data.getNextPageToken()));
//                    listAdapter.addVideos(data.getItems());
//                    loadingLayout.setVisibility(View.INVISIBLE);
//                    loadingLayout.startAnimation(slide_down);
//
//                }
//
//
//
//            }
//
//            @Override
//            public void onFailure(Call<Example> call, Throwable t) {
//
//                Log.d("YoutubeAPI", String.valueOf(t.getMessage()));
//                Log.d("YoutubeAPI", String.valueOf(t.fillInStackTrace()));
//
//            }
//        });


        Call<Example> playlistResponseCall = APIService.getInstance()
                .listPlaylist(DataAPI.PLAYLIST_ID,DataAPI.API_KEY,"snippet","date","US","20",pageToken);

        playlistResponseCall.enqueue(new Callback<Example>() {
            @Override
            public void onResponse(Call<Example> call, Response<Example> response) {

                Example data = response.body();
                if(data!=null){

                    nextPageToken = data.getNextPageToken();
                    Log.d("YoutubeAPI",String.valueOf(data.getNextPageToken()));
                    listAdapter.addVideos(data.getItems());
                    loadingLayout.setVisibility(View.INVISIBLE);
                    loadingLayout.startAnimation(slide_down);

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
