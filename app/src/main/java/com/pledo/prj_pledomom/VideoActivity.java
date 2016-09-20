package com.pledo.prj_pledomom;

/**
 * Created by Ulnamsong on 2016. 6. 10..
 */
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.PlaybackEventListener;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.android.youtube.player.YouTubePlayer.PlayerStateChangeListener;
import com.google.android.youtube.player.YouTubePlayer.Provider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class VideoActivity extends YouTubeBaseActivity implements
        YouTubePlayer.OnInitializedListener{

    private static final String TAG = "VideoActivity";

    // API KEY 와 주소 ID입력받는 부분입니다
    // VIDEO ID 는
    // https://www.youtube.com/watch?v=a4NT5iBFuZs 가 주소라고 가정했을때
    // a4NT5iBFuZs <- 이것이 VIDEO_ID가 됩니다.

    public static final String API_KEY = "AIzaSyBePPzKZgkMWuJQl5WVkB-v5h7U8LNqoCw";
    //public static final String VIDEO_ID = "UqqK9L6ec14";
    private String VIDEO_ID = "";


    private YouTubePlayer youTubePlayer;
    private YouTubePlayerView youTubePlayerView;

    private static final int RQS_ErrorDialog = 1;
    private MyPlayerStateChangeListener myPlayerStateChangeListener;
    private MyPlaybackEventListener myPlaybackEventListener;

    String log = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        Intent intent   = getIntent();
        VIDEO_ID    = intent.getStringExtra("URL_DATA");
        Log.d(TAG, "onCreate   url: " + VIDEO_ID );

        youTubePlayerView = (YouTubePlayerView)findViewById(R.id.youtubeplayerview);
        youTubePlayerView.initialize(API_KEY, this);

        myPlayerStateChangeListener = new MyPlayerStateChangeListener();
        myPlaybackEventListener = new MyPlaybackEventListener();
    }

    @Override
    public void onInitializationFailure(Provider provider, YouTubeInitializationResult result) {

        if (result.isUserRecoverableError()) {
            result.getErrorDialog(this, RQS_ErrorDialog).show();
        } else {
            Toast.makeText(this,
                    "YouTubePlayer.onInitializationFailure(): " + result.toString(),
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onInitializationSuccess(Provider provider, YouTubePlayer player,
                                        boolean wasRestored) {
        youTubePlayer = player;

        youTubePlayer.setFullscreen(true);
        youTubePlayer.setPlayerStateChangeListener(myPlayerStateChangeListener);
        youTubePlayer.setPlaybackEventListener(myPlaybackEventListener);

        if (!wasRestored) {
            player.cueVideo(VIDEO_ID);
//            player.cueVideo("https://www.youtube.com/embed/tKW6louhuSY");
        }
    }

    private final class MyPlayerStateChangeListener implements PlayerStateChangeListener {

        private void updateLog(String prompt){
            log +=  "MyPlayerStateChangeListener" + "\n" +
                    prompt + "\n\n=====";
        };

        @Override
        public void onAdStarted() {
            updateLog("onAdStarted()");
        }

        @Override
        public void onError(
                com.google.android.youtube.player.YouTubePlayer.ErrorReason arg0) {
            updateLog("onError(): " + arg0.toString());
        }

        @Override
        public void onLoaded(String arg0) {
            updateLog("onLoaded(): " + arg0);
        }

        @Override
        public void onLoading() {
            updateLog("onLoading()");
        }

        @Override
        public void onVideoEnded() {
            updateLog("onVideoEnded()");
        }

        @Override
        public void onVideoStarted() {
            updateLog("onVideoStarted()");
        }

    }

    private final class MyPlaybackEventListener implements PlaybackEventListener {

        private void updateLog(String prompt){
            log +=  "MyPlaybackEventListener" + "\n-" +
                    prompt + "\n\n=====";
        };

        @Override
        public void onBuffering(boolean arg0) {
            updateLog("onBuffering(): " + String.valueOf(arg0));
        }

        @Override
        public void onPaused() {
            updateLog("onPaused()");
        }

        @Override
        public void onPlaying() {
            updateLog("onPlaying()");
        }

        @Override
        public void onSeekTo(int arg0) {
            updateLog("onSeekTo(): " + String.valueOf(arg0));
        }

        @Override
        public void onStopped() {
            updateLog("onStopped()");
        }

    }

}

//AIzaSyBePPzKZgkMWuJQl5WVkB-v5h7U8LNqoCw