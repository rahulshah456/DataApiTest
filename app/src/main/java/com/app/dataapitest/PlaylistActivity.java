package com.app.dataapitest;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.PlaylistItemListResponse;
import com.google.api.services.youtube.model.PlaylistListResponse;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import Adapters.PlaylistAdapter;
import Adapters.VideolistAdapter;

import static Utils.Constants.YOUTUBE_API_KEY;

public class PlaylistActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();
    private RecyclerView recyclerView;
    private VideolistAdapter listAdapter;
    public String nextPageToken = null;
    Animation slide_down,slide_up;
    int page = 0;
    private RecyclerView.LayoutManager mLayoutManager;
    RelativeLayout loadingLayout;
    boolean isLoading = false;
    private ArrayList<Video> mPlaylistList = new ArrayList<>();
    private final GsonFactory mJsonFactory = new GsonFactory();
    private final HttpTransport mTransport = AndroidHttp.newCompatibleTransport();
    private YouTube mYoutubeDataApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);


        Intent intent = getIntent();
        final String id = intent.getStringExtra("Id");
        Log.d("Id:",id);


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

        new GetPlaylistAsyncTask(mYoutubeDataApi).execute(id);


        // Load more images onScroll end
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

//                 Check if end of page has been reached
                if( !isLoading && ((LinearLayoutManager)mLayoutManager).findLastVisibleItemPosition() == listAdapter.getItemCount()-1 ){
                    isLoading = true;
                    Log.d(TAG , "End has reached, loading more images!");
                    loadingLayout.startAnimation(slide_up);
                    loadingLayout.setVisibility(View.VISIBLE);
                    if (nextPageToken!=null){
                        new GetPlaylistAsyncTask(mYoutubeDataApi).execute(id,nextPageToken);
                    }else {
                        loadingLayout.setVisibility(View.INVISIBLE);
                        loadingLayout.startAnimation(slide_down);
                    }
                }
            }
        });
    }


    public void generateRecyclerView(ArrayList<Video> result){

        // Set up RecyclerView
        mLayoutManager = new GridLayoutManager(PlaylistActivity.this, 1);
        listAdapter = new VideolistAdapter(PlaylistActivity.this,result);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(listAdapter);
        listAdapter.setOnItemClickListener(new VideolistAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(int position) {

                Intent intent = new Intent(PlaylistActivity.this,PlayerActivity.class);
                intent.putExtra("Id",listAdapter.getItemList().get(position).getId());
                startActivity(intent);

            }

            @Override
            public void OnItemLongClick(int position) {

            }
        });

    }



    public class GetPlaylistAsyncTask extends AsyncTask<String, Void, Pair<String, ArrayList<Video>>> {


        private static final String TAG = "GetPlaylistAsyncTask";
        private final Long YOUTUBE_PLAYLIST_MAX_RESULTS = 10L;

        //see: https://developers.google.com/youtube/v3/docs/playlistItems/list
        private static final String YOUTUBE_PLAYLIST_PART = "snippet";
        private static final String YOUTUBE_PLAYLIST_FIELDS = "pageInfo,nextPageToken,items(id,snippet(resourceId/videoId))";
        //see: https://developers.google.com/youtube/v3/docs/videos/list
        private static final String YOUTUBE_VIDEOS_PART = "snippet,contentDetails,statistics"; // video resource properties that the response will include.
        private static final String YOUTUBE_VIDEOS_FIELDS = "items(id,snippet(title,description,thumbnails/high),contentDetails/duration,statistics)"; // selector specifying which fields to include in a partial response.

        private YouTube mYouTubeDataApi;

        public GetPlaylistAsyncTask(YouTube api) {
            mYouTubeDataApi = api;
        }


        @Override
        protected Pair<String, ArrayList<Video>> doInBackground(String... params) {
            final String playlistId = params[0];
            final String nextPageToken;

            if (params.length == 2) {
                nextPageToken = params[1];
            } else {
                nextPageToken = null;
            }

            PlaylistItemListResponse playlistItemListResponse;
            try {
                playlistItemListResponse = mYouTubeDataApi.playlistItems()
                        .list(YOUTUBE_PLAYLIST_PART)
                        .setPlaylistId(playlistId)
                        .setPageToken(nextPageToken)
                        .setFields(YOUTUBE_PLAYLIST_FIELDS)
                        .setMaxResults(YOUTUBE_PLAYLIST_MAX_RESULTS)
                        .setKey(YOUTUBE_API_KEY)
                        .execute();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

            if (playlistItemListResponse == null) {
                Log.e(TAG, "Failed to get playlist");
                return null;
            }

            List<String> videoIds = new ArrayList();

            // pull out the video id's from the playlist page
            for (PlaylistItem item : playlistItemListResponse.getItems()) {
                videoIds.add(item.getSnippet().getResourceId().getVideoId());
            }

            // get details of the videos on this playlist page
            VideoListResponse videoListResponse = null;
            try {
                videoListResponse = mYouTubeDataApi.videos()
                        .list(YOUTUBE_VIDEOS_PART)
                        .setFields(YOUTUBE_VIDEOS_FIELDS)
                        .setKey(YOUTUBE_API_KEY)
                        .setId(TextUtils.join(",", videoIds)).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return new Pair(playlistItemListResponse.getNextPageToken(), videoListResponse.getItems());
        }


        @Override
        protected void onPostExecute(Pair<String, ArrayList<Video>> stringArrayListPair) {
            super.onPostExecute(stringArrayListPair);



            if (stringArrayListPair == null) return;

            if (nextPageToken == null){

                if (page==0){
                    generateRecyclerView(stringArrayListPair.second);
                }

            }else {
                listAdapter.addVideos(stringArrayListPair.second);
            }
            nextPageToken = stringArrayListPair.first;

            page++;
            isLoading = false;
            loadingLayout.setVisibility(View.INVISIBLE);
            loadingLayout.startAnimation(slide_down);

        }

    }
}
