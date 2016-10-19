package com.mezmeraiz.verimag_tz;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.VideoView;

/**
 * Created by pc on 09.10.2016.
 */

public class ModalActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener{

    private VideoView mVideoView;
    private String mPath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modal);
        mVideoView = (VideoView) findViewById(R.id.videoViewFullSize);
        mPath = "android.resource://" + getPackageName() + "/" + R.raw.verimag;
        mVideoView.setVideoURI(Uri.parse(mPath));
        mVideoView.setOnCompletionListener(this);
        mVideoView.seekTo(130000);
        mVideoView.start();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        finish();
    }

    @Override
    public void onBackPressed() {

    }
}
