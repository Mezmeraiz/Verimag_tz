package com.mezmeraiz.verimag_tz;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.VideoView;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by pc on 08.10.2016.
 */

public class FullScreenActivity extends AppCompatActivity implements View.OnTouchListener,  View.OnClickListener,MediaPlayer.OnPreparedListener,SeekBar.OnSeekBarChangeListener,MediaPlayer.OnCompletionListener{

    private VideoView mVideoView;
    private String mPath;
    private View mUpView, mDownView;
    private boolean isVisible;
    private boolean mIsStarted = false;
    private boolean mIsPlaying = false;
    private Timer mTimer;
    private TimerTask mTimerTask;
    private SeekBar mSeekBar;
    private ImageView mPlayImageView, mCloseImageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_size);
        mVideoView = (VideoView) findViewById(R.id.videoViewFullSize);
        mVideoView.setOnTouchListener(this);
        mUpView = findViewById(R.id.press_layout);
        mDownView = findViewById(R.id.control_layout);
        mUpView.animate().translationY(-100);
        mDownView.animate().translationY(100);
        isVisible = false;
        mSeekBar = (SeekBar) findViewById(R.id.seekBar);
        mSeekBar.setOnSeekBarChangeListener(this);
        mPlayImageView = (ImageView) findViewById(R.id.imageViewPlay);
        mPlayImageView.setOnClickListener(this);
        mCloseImageView = (ImageView) findViewById(R.id.imageViewClose);
        mCloseImageView.setOnClickListener(this);

        mTimer = new Timer();
        mTimerTask = new TimerTask(){
            @Override
            public void run() {
                if(mVideoView != null){
                    mSeekBar.setProgress(mVideoView.getCurrentPosition());
                    Log.d("myLogs", "p = " + mVideoView.getCurrentPosition());
                }
            }
        };
        mPath = "android.resource://" + getPackageName() + "/" + R.raw.verimag;
        mVideoView.setVideoURI(Uri.parse(mPath));
        mVideoView.setOnCompletionListener(this);
        mVideoView.setOnPreparedListener(this);
        mVideoView.seekTo(getIntent().getIntExtra("PROGRESS", 0));
        mVideoView.start();
    }



    private void onClickVideoView(){
        if(isVisible){
            mUpView.animate().translationY(-100).setDuration(300);
            mDownView.animate().translationY(100).setDuration(300);
            isVisible = false;
        }else {
            mUpView.setVisibility(View.VISIBLE);
            mDownView.setVisibility(View.VISIBLE);
            mUpView.animate().translationY(0).setDuration(300);
            mDownView.animate().translationY(0).setDuration(300);
            isVisible = true;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(event.getAction() != MotionEvent.ACTION_UP)
            return true;
        switch (v.getId()){
            case R.id.videoViewFullSize:
                onClickVideoView();
                break;
        }
        return true;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        cancelTimerTask();
        mIsStarted = false;
        mIsPlaying = false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        if(!mIsStarted){
            mSeekBar.setMax(mVideoView.getDuration());
            mTimer.schedule(mTimerTask, 1000, 1000);
            mIsStarted = true;
            if(getIntent().getBooleanExtra("STATE", false)){
                mIsPlaying = true;
                setIcon(true);
            }else{
                mVideoView.pause();
                mIsPlaying = false;
                setIcon(false);
            }
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imageViewPlay:
                if(!mIsStarted)
                    break;
                if(mIsPlaying){
                    mIsPlaying = false;
                    setIcon(mIsPlaying);
                    mVideoView.pause();
                }else{
                    mIsPlaying = true;
                    setIcon(mIsPlaying);
                    mVideoView.start();
                }
                break;
            case R.id.imageViewClose:
                onClickClose();
                break;
        }

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if(fromUser){
            mVideoView.seekTo(progress);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    private void setIcon(boolean state){
        if(state){
            mPlayImageView.setBackground(getResources().getDrawable(R.drawable.pause_white));
        }else{
            mPlayImageView.setBackground(getResources().getDrawable(R.drawable.play_white));
        }
    }

    private void cancelTimerTask(){
        if (mTimerTask !=null){
            mTimerTask.cancel();
            mTimerTask = null;
        }
        if (mTimer !=null){
            mTimer.cancel();
            mTimer = null;
        }
    }

    private void onClickClose(){
        cancelTimerTask();
        finish();
    }

    @Override
    public void onBackPressed() {
        cancelTimerTask();
        super.onBackPressed();
    }
}
