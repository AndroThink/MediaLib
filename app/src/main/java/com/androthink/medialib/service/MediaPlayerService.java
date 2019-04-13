package com.androthink.medialib.service;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.androthink.medialib.utils.MediaPlayerUtils;

public class MediaPlayerService extends Service {

    private Binder binder;
    private MediaPlayerUtils mediaPlayerUtils;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        mediaPlayerUtils = MediaPlayerUtils.getInstance(this);
        binder = new Binder();
    }

    @Override
    public void onDestroy() {
        mediaPlayerUtils.releaseMediaPlayer();
    }

    public void playTrack(Uri mediaUri){
        mediaPlayerUtils.playSound(this,mediaUri,null);
    }

    public class Binder extends android.os.Binder {
        public MediaPlayerService getService() {
            return MediaPlayerService.this;
        }
    }
}