package com.pledo.prj_pledomom;

import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.MediaController;
import android.widget.VideoView;

public class VideoPlayVw {

    private VideoView myVideoView;
    private int position = 0;
    private MediaController mediaControls;

    private Activity mListener;

    public void initView( Activity	s_activity, VideoView s_Vw ){
        mListener	= s_activity;
        myVideoView	= s_Vw;

        //set the media controller buttons
        if (mediaControls == null) {
            mediaControls = new MediaController(mListener);
        }
    }

    public void playVideo( String s_url ){

        try {
            //set the media controller in the VideoView
            myVideoView.setMediaController(mediaControls);
            String	t_fullUri	= "sdcard/Download" + "/" + s_url;

            Log.d("VideoPlayvw", "initView 0  s_url: " + s_url + "  t_fullUri: " + t_fullUri);
            myVideoView.setVideoPath( t_fullUri );

        } catch (Exception e) {
            Log.e("Error", "Error!!!   " + e.getMessage());
            e.printStackTrace();
        }

        Log.d("VideoPlayvw", "initView 1" );
        myVideoView.requestFocus();
        //we also set an setOnPreparedListener in order to know when the video file is ready for playback
        myVideoView.setOnPreparedListener(new OnPreparedListener() {
            public void onPrepared(MediaPlayer mediaPlayer) {
            // close the progress bar and play the video
            Log.d("VideoPlayvw", "setOnPreparedListener.onPrepared  0" );

//                progressDialog.dismiss();
            //if we have a position on savedInstanceState, the video playback should start from here
            myVideoView.seekTo(position);
            Log.d("VideoPlayvw", "setOnPreparedListener.onPrepared  1   position: " + position );

            if (position == 0) {
                myVideoView.start();
            } else {
                //if we come from a resumed activity, video playback will be paused
                myVideoView.pause();
            }
            }
        });
    }

    public int getVisibility(){
        return	myVideoView.getVisibility();
    }

    public void setVisibility( int s_value ){
        myVideoView.setVisibility(s_value);
    }

}
