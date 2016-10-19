package com.mezmeraiz.verimag_tz;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.VideoView;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,MediaPlayer.OnPreparedListener,SeekBar.OnSeekBarChangeListener,MediaPlayer.OnCompletionListener{

    private VideoView mVideoView;
    private String mPath;
    private AnimateLayout mAnimateLayout;
    private boolean mIsStarted = false;
    private boolean mIsPlaying = false;
    private Timer mTimer;
    private TimerTask mTimerTask;
    private SeekBar mSeekBar;
    private ImageView mPlayImageView, mCloseImageView;
    private Animation mBlackAnimation;
    private View mBlackView, mPressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_BEHIND);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(getResources().getConfiguration().orientation);
        findViewById(R.id.button_play).setOnClickListener(this);
        findViewById(R.id.button_full_screen).setOnClickListener(this);
        findViewById(R.id.button_modal).setOnClickListener(this);
        mAnimateLayout = (AnimateLayout) findViewById(R.id.animate_layout);
        mBlackView = findViewById(R.id.black_layout);
        mSeekBar = (SeekBar) findViewById(R.id.seekBar);
        mSeekBar.setOnSeekBarChangeListener(this);
        mPlayImageView = (ImageView) findViewById(R.id.imageViewPlay);
        mPlayImageView.setOnClickListener(this);
        mCloseImageView = (ImageView) findViewById(R.id.imageViewClose);
        mCloseImageView.setOnClickListener(this);
        mPressView = findViewById(R.id.press_layout);
        mPressView.setOnTouchListener(mAnimateLayout);
        mBlackAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        mBlackAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mBlackView.setAlpha(1);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mBlackView.setAlpha(0);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mPath = "android.resource://" + getPackageName() + "/" + R.raw.verimag;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_play:
                if(mIsStarted)
                    break;
                play();
                break;
            case R.id.button_full_screen:
                if(mIsStarted)
                    startFullScreenActivity();
                break;
            case R.id.button_modal:
                startModalActivity();
                break;
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

    private void play(){
        mVideoView = (VideoView) findViewById(R.id.videoView);
        mTimer = new Timer();
        mTimerTask = new TimerTask(){
            @Override
            public void run() {
                if(mVideoView != null){
                    mSeekBar.setProgress(mVideoView.getCurrentPosition());
                }
            }
        };
        mAnimateLayout.setVisibility(View.VISIBLE);
        mVideoView.setVideoURI(Uri.parse(mPath));
        mVideoView.setOnPreparedListener(this);
        mVideoView.setOnCompletionListener(this);
        mVideoView.start();
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

    @Override
    public void onPrepared(MediaPlayer mp) {
        if (!mIsStarted && mVideoView != null){
            mSeekBar.setMax(mVideoView.getDuration());
            mTimer.schedule(mTimerTask, 1000, 1000);
            mIsStarted = true;
            mIsPlaying = true;
            setIcon(true);
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        cancelTimerTask();
        mIsStarted = false;
        mIsPlaying = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelTimerTask();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if(fromUser){
            mVideoView.seekTo(progress);
        }
    }

    private void onClickClose(){
        cancelTimerTask();
        mVideoView.stopPlayback();
        mAnimateLayout.setVisibility(View.INVISIBLE);
        mSeekBar.setProgress(0);
        mIsStarted = false;
        mIsPlaying = false;
        mVideoView = null;
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    private void startFullScreenActivity(){
        Intent intent = new Intent(this, FullScreenActivity.class);
        intent.putExtra("PROGRESS", mVideoView.getCurrentPosition());
        intent.putExtra("STATE", mIsPlaying);
        startActivity(intent);
        onClickClose();
    }

    private void startModalActivity(){
        startActivityForResult(new Intent(this, ModalActivity.class), 10);
        mBlackView.setAlpha(1);
        if(mIsStarted)
            onClickClose();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 10){
            mBlackView.startAnimation(mBlackAnimation);
        }
    }
}
