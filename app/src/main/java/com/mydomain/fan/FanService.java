package com.mydomain.fan;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

public class FanService extends Service {

    public static final String ACTION_PLAY = "com.example.fanapp.PLAY";
    public static final String ACTION_STOP = "com.example.fanapp.STOP";

    private final IBinder binder = new FanBinder();
    private MediaPlayer mediaPlayer;

    public class FanBinder extends Binder {
        FanService getService() {
            return FanService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            if (ACTION_PLAY.equals(action)) {
                startPlaying();
            } else if (ACTION_STOP.equals(action)) {
                stopPlaying();
            }
        }
        // START_STICKY keeps the service running if the system kills it
        return START_STICKY;
    }

    private void startPlaying() {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(this, R.raw.fan);
            mediaPlayer.setLooping(true);  // loops continuously
        }
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    private void stopPlaying() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        stopSelf();
    }

    public boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
