package com.androthink.medialib.utils;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;

public class MediaPlayerUtils {

    private static MediaPlayerUtils mediaPlayerUtils = null;

    private MediaPlayer mMediaPlayer;
    private AudioManager mAudioManager;

    private NotificationUtils notificationUtils;
    private boolean showMediaNotification = false;

    public static MediaPlayerUtils getInstance(Context context) {
        if (mediaPlayerUtils == null)
            mediaPlayerUtils = new MediaPlayerUtils(context);

        return mediaPlayerUtils;
    }

    private MediaPlayerUtils(Context context){
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        notificationUtils = NotificationUtils.getInstance(context);
    }

    public void setShowMediaNotification(boolean showMediaNotification){
        this.showMediaNotification = showMediaNotification;
    }

    public void playSound(Context context, Uri fileUri, Intent intent){
        int result = mAudioManager.requestAudioFocus(mOnAudioFocusChangeListener,
                AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);

        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {

            if(mMediaPlayer !=null && mMediaPlayer.isPlaying())
                releaseMediaPlayer();

            MediaModel media = getMediaDetails(context,fileUri);

            mMediaPlayer = MediaPlayer.create(context , media.getFileUri());
            //mMediaPlayer = MediaPlayer.create(context, audioId);
            mMediaPlayer.start();

            if(this.showMediaNotification)
                notificationUtils.MediaNotification(context,media,intent);

            mMediaPlayer.setOnCompletionListener(mCompletionListener);
        }
    }

    public void playSound(Context context, Uri fileUri, Intent intent,MediaPlayer.OnCompletionListener onCompletionListener){
        int result = mAudioManager.requestAudioFocus(mOnAudioFocusChangeListener,
                AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);

        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {

            if(mMediaPlayer !=null && mMediaPlayer.isPlaying())
                releaseMediaPlayer();

            MediaModel media = getMediaDetails(context,fileUri);

            mMediaPlayer = MediaPlayer.create(context , media.getFileUri());
            //mMediaPlayer = MediaPlayer.create(context, audioId);
            mMediaPlayer.start();

            if(this.showMediaNotification)
                notificationUtils.MediaNotification(context,media,intent);

            mMediaPlayer.setOnCompletionListener(onCompletionListener);
        }
    }

    private MediaModel getMediaDetails(Context context, Uri fileUri){

        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(context, fileUri);

        MediaModel details = new MediaModel();

        details.setFileUri(fileUri);
        details.setTitle(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE));
        details.setArtist(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST));
        details.setAlbum(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM));
        details.setAuthor(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_AUTHOR));
        details.setDuration(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
        details.setYear(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_YEAR));
        details.setMimeType(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE));
        details.setDate(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE));

        details.setPicture(mediaMetadataRetriever.getEmbeddedPicture());

        mediaMetadataRetriever.release();
        return details;
    }

    public void releaseMediaPlayer() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;

            mAudioManager.abandonAudioFocus(mOnAudioFocusChangeListener);
        }
    }

    private AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT ||
                    focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                mMediaPlayer.pause();
                mMediaPlayer.seekTo(0);
            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                mMediaPlayer.start();
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                releaseMediaPlayer();
            }
        }
    };

    private MediaPlayer.OnCompletionListener mCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            releaseMediaPlayer();
        }
    };
}
