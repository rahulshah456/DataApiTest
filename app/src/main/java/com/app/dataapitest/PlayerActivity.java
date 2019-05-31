package com.app.dataapitest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoContentDetails;
import com.google.api.services.youtube.model.VideoSnippet;
import com.google.api.services.youtube.model.VideoStatistics;
import com.google.gson.Gson;

import java.text.DecimalFormat;

import Utils.Constants;

public class PlayerActivity extends YouTubeBaseActivity {

    public static final String TAG = PlaylistActivity.class.getSimpleName();
    private YouTubePlayerView playerView;
    private TextView videoTitle,videoViews,publishTime,videoDescription,likeCount,dislikeCount;
    private static final DecimalFormat sFormatter = new DecimalFormat("#,###,###");
    private RelativeLayout videoLikes,videoDislikes,download,share;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        Intent intent = getIntent();
        final String id = intent.getStringExtra("id");
        final String title = intent.getStringExtra("title");
        final String description = intent.getStringExtra("description");
        final Double like = intent.getDoubleExtra("like",0);
        final Double dislike = intent.getDoubleExtra("dislike",0);
        final Double views = intent.getDoubleExtra("views",0);
        final String duration = intent.getStringExtra("duration");


        playerView = (YouTubePlayerView) findViewById(R.id.playerId);
        videoTitle = (TextView) findViewById(R.id.videoNameId);
        videoViews = (TextView) findViewById(R.id.viewsId);
        publishTime = (TextView) findViewById(R.id.publishId);
        videoDescription = (TextView) findViewById(R.id.descId);
        videoLikes = (RelativeLayout) findViewById(R.id.likeId);
        videoDislikes = (RelativeLayout) findViewById(R.id.dislikeId);
        download = (RelativeLayout) findViewById(R.id.downloadId);
        share = (RelativeLayout) findViewById(R.id.shareId);
        likeCount = (TextView) findViewById(R.id.likeCountId);
        dislikeCount = (TextView) findViewById(R.id.dislikeCountId);


        YouTubePlayer.OnInitializedListener onInitializedListener = new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {

                youTubePlayer.loadVideo(id);
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            }
        };

        // initialize video with details
        playerView.initialize(Constants.YOUTUBE_API_KEY, onInitializedListener);
        videoTitle.setText(title);
        videoDescription.setText(description);


//        Log.d(TAG,String.valueOf(duration));
//        Log.d(TAG,String.valueOf(dislike));
//        Log.d(TAG,String.valueOf(views));


//         set the video statistics
        publishTime.setText(parseDuration(duration));
        videoViews.setText(sFormatter.format(views) + " views");
        likeCount.setText(sFormatter.format(like));
        dislikeCount.setText(sFormatter.format(dislike));

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Watch \"" + title + "\" on YouTube");
                sendIntent.putExtra(Intent.EXTRA_TEXT, "https://www.youtube.com/watch?v=" + id);
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }
        });


        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(PlayerActivity.this, "Feature not available yet...", Toast.LENGTH_SHORT).show();

            }
        });

    }


    private String parseDuration(String in) {
        boolean hasSeconds = in.indexOf('S') > 0;
        boolean hasMinutes = in.indexOf('M') > 0;

        String s;
        if (hasSeconds) {
            s = in.substring(2, in.length() - 1);
        } else {
            s = in.substring(2, in.length());
        }

        String minutes = "0";
        String seconds = "00";

        if (hasMinutes && hasSeconds) {
            String[] split = s.split("M");
            minutes = split[0];
            seconds = split[1];
        } else if (hasMinutes) {
            minutes = s.substring(0, s.indexOf('M'));
        } else if (hasSeconds) {
            seconds = s;
        }

        // pad seconds with a 0 if less than 2 digits
        if (seconds.length() == 1) {
            seconds = "0" + seconds;
        }

        return minutes +" minutes " + seconds + " seconds";
    }
}
