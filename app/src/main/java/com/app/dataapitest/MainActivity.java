package com.app.dataapitest;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Playlist;
import com.google.api.services.youtube.model.PlaylistListResponse;

import java.io.IOException;
import java.util.ArrayList;

import Adapters.PlaylistAdapter;

import static Utils.Constants.YOUTUBE_API_KEY;
import static Utils.Constants.YOUTUBE_PLAYLISTS;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();
    private RecyclerView recyclerView;
    private PlaylistAdapter listAdapter;
    public String nextPageToken = "";
    Animation slide_down,slide_up;
    private RecyclerView.LayoutManager mLayoutManager;
    RelativeLayout loadingLayout;
    boolean isLoading = false;
    private ArrayList<Playlist> mPlaylistList = new ArrayList<>();
    private final GsonFactory mJsonFactory = new GsonFactory();
    private final HttpTransport mTransport = AndroidHttp.newCompatibleTransport();
    private YouTube mYoutubeDataApi;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewID);
        loadingLayout = (RelativeLayout) findViewById(R.id.loadingRecyclerID);
        //Load animation
        slide_down = AnimationUtils.loadAnimation(this, R.anim.slide_down);
        slide_up = AnimationUtils.loadAnimation(this, R.anim.slide_up);

        mYoutubeDataApi = new YouTube.Builder(mTransport, mJsonFactory, null)
                .setApplicationName(getResources().getString(R.string.app_name))
                .build();

        loadingLayout.setVisibility(View.VISIBLE);
        loadingLayout.startAnimation(slide_up);
        new GetPlaylistTitlesAsyncTask(mYoutubeDataApi).execute(YOUTUBE_PLAYLISTS);



        // Load more images onScroll end
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                // Check if end of page has been reached
//                if( !isLoading && ((LinearLayoutManager)mLayoutManager).findLastVisibleItemPosition() == listAdapter.getItemCount()-1 ){
//                    isLoading = true;
//                    Log.d(TAG , "End has reached, loading more images!");
//                    loadingLayout.startAnimation(slide_up);
//                    loadingLayout.setVisibility(View.VISIBLE);
//                    GetVideoList(nextPageToken);
//                }
            }
        });


    }


    public void generateRecyclerView(){

        // Set up RecyclerView
        mLayoutManager = new GridLayoutManager(MainActivity.this, 1);
        listAdapter = new PlaylistAdapter(MainActivity.this,mPlaylistList);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(listAdapter);
        listAdapter.setOnItemClickListener(new PlaylistAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(int position) {

                Intent intent = new Intent(MainActivity.this,PlaylistActivity.class);
                intent.putExtra("Id",mPlaylistList.get(position).getId());
                startActivity(intent);

            }

            @Override
            public void OnItemLongClick(int position) {

            }
        });

    }


    public class GetPlaylistTitlesAsyncTask  extends AsyncTask<String[], Void, PlaylistListResponse> {


        //see: https://developers.google.com/youtube/v3/docs/playlists/list
        private static final String YOUTUBE_PLAYLIST_PART = "snippet";
        private static final String YOUTUBE_PLAYLIST_FIELDS = "items(id,snippet(title),snippet(thumbnails))";

        private YouTube mYouTubeDataApi;

        public GetPlaylistTitlesAsyncTask(YouTube api) {
            mYouTubeDataApi = api;
        }


        protected void onPreExecute() {
            super.onPreExecute();
            //mProgressDialog.show();


        }


        @Override
        protected PlaylistListResponse doInBackground(String[]... params) {

            final String[] playlistIds = params[0];

            PlaylistListResponse playlistListResponse;
            try {
                playlistListResponse = mYouTubeDataApi.playlists()
                        .list(YOUTUBE_PLAYLIST_PART)
                        .setId(TextUtils.join(",", YOUTUBE_PLAYLISTS))
                        .setFields(YOUTUBE_PLAYLIST_FIELDS)
                        .setKey(YOUTUBE_API_KEY)
                        .execute();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

            return playlistListResponse;
        }




        @Override
        protected void onPostExecute(PlaylistListResponse playlistListResponse) {

            // if we didn't receive a response for the playlist titles, then there's nothing to update
            if (playlistListResponse == null)
                return;

            mPlaylistList.addAll(playlistListResponse.getItems());
            generateRecyclerView();
            //mProgressDialog.hide();
            loadingLayout.setVisibility(View.INVISIBLE);
            loadingLayout.startAnimation(slide_down);
        }
    }




}
