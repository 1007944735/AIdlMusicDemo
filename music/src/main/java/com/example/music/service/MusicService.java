package com.example.music.service;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.music.aidl.IMusicManager;
import com.example.music.aidl.IOnProgressListener;
import com.example.music.aidl.IOnStatusChangeListener;
import com.example.music.aidl.MusicConstant;
import com.example.music.aidl.bean.Music;

import java.io.IOException;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class MusicService extends Service {
    private static final String TAG = MusicService.class.getName();
    private IjkMediaPlayer mIjkMediaPlayer;
    private int mState = MusicConstant.STATE_IDLE;
    private RemoteCallbackList<IOnProgressListener> mProgressListener = new RemoteCallbackList<>();
    private RemoteCallbackList<IOnStatusChangeListener> mStatusChangeListener = new RemoteCallbackList<>();

    private Handler timerHandler;
    private long delayMillis = 1000;
    private long bufferDuration = 0;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (mIjkMediaPlayer != null) {
                notifyProgress(mIjkMediaPlayer.getCurrentPosition(), mIjkMediaPlayer.getDuration(), bufferDuration);
            }
            timerHandler.postDelayed(runnable, delayMillis);
        }
    };
    private boolean autoPlay = true;
    private boolean needSeekTo = false;
    private long position = 0;
    private Music music;

    private Binder mBinder = new IMusicManager.Stub() {
        @Override
        public void start() throws RemoteException {
            MusicService.this.start();
        }

        @Override
        public void play(Music music) throws RemoteException {
            MusicService.this.music = music;
            MusicService.this.pause();
            initMediaPlayer();
            initParams();
            notifyStatus(MusicConstant.STATE_IDLE);
            try {
                mIjkMediaPlayer.setDataSource(MusicService.this, Uri.parse(music.getUrl()));

                bufferDuration = 0;
                startTimer();
                mIjkMediaPlayer.prepareAsync();
                notifyStatus(MusicConstant.STATE_PREPARING);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                notifyStatus(MusicConstant.STATE_ERROR);
            }
        }

        @Override
        public void pause() throws RemoteException {
            MusicService.this.pause();
        }

        @Override
        public void reset() throws RemoteException {
            // TODO: 2020/6/9 重置播放器
            initMediaPlayer();
            initParams();
        }

        @Override
        public void seekTo(long millisecond) throws RemoteException {
            if (mIjkMediaPlayer != null) {
                if (mIjkMediaPlayer.isPlaying()) {
                    mIjkMediaPlayer.seekTo(position);
                } else {
                    needSeekTo = true;
                    position = millisecond;
                }
            }
        }

        @Override
        public void setSpeed(float speed) throws RemoteException {
            if (speed > 0 && mIjkMediaPlayer != null) {
                delayMillis = (long) (delayMillis / speed);
                mIjkMediaPlayer.setSpeed(speed);
            }
        }

        @Override
        public float getSpeed() throws RemoteException {
            if (mIjkMediaPlayer != null) {
                mIjkMediaPlayer.getSpeed(0.0f);
            }
            return 0.0f;
        }

        @Override
        public Music getPlayMusic() throws RemoteException {
            return MusicService.this.music;
        }

        @Override
        public void registerProgressListener(IOnProgressListener listener) throws RemoteException {
            if (listener == null) {
                Log.d(TAG, "IOnProgressListener is null");
                return;
            }
            boolean success = mProgressListener.register(listener);
            if (success) {
                Log.d(TAG, "IOnProgressListener register success");
            } else {
                Log.d(TAG, "IOnProgressListener register failure");
            }
        }

        @Override
        public void unregisterProgressListener(IOnProgressListener listener) throws RemoteException {
            if (listener == null) {
                Log.d(TAG, "unregisterProgressListener is null");
                return;
            }
            boolean success = mProgressListener.unregister(listener);
            if (success) {
                Log.d(TAG, "IOnProgressListener unregister success");
            } else {
                Log.d(TAG, "IOnProgressListener unregister failure");
            }
        }

        @Override
        public void registerStatusChangeListener(IOnStatusChangeListener listener) throws RemoteException {
            if (listener == null) {
                Log.d(TAG, "IOnStatusChangeListener is null");
                return;
            }
            boolean success = mStatusChangeListener.register(listener);
            if (success) {
                Log.d(TAG, "IOnStatusChangeListener register success");
            } else {
                Log.d(TAG, "IOnStatusChangeListener register failure");
            }
        }

        @Override
        public void unregisterStatusChangeListener(IOnStatusChangeListener listener) throws RemoteException {
            if (listener == null) {
                Log.d(TAG, "IOnStatusChangeListener is null");
                return;
            }
            boolean success = mStatusChangeListener.unregister(listener);
            if (success) {
                Log.d(TAG, "IOnStatusChangeListener unregister success");
            } else {
                Log.d(TAG, "IOnStatusChangeListener unregister failure");
            }
        }

        @Override
        public boolean isPlaying() throws RemoteException {
            return mState == MusicConstant.STATE_PLAYING;
        }

        @Override
        public long getCurrentPosition() throws RemoteException {
            if (mIjkMediaPlayer != null) {
                return mIjkMediaPlayer.getCurrentPosition();
            }
            return 0;
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    //初始化播放器
    private void initMediaPlayer() {
        if (mIjkMediaPlayer != null) {
            mIjkMediaPlayer.stop();
            mIjkMediaPlayer.release();
            mIjkMediaPlayer = null;
        }
        mIjkMediaPlayer = new IjkMediaPlayer();
        mIjkMediaPlayer.setOnPreparedListener(mOnPreparedListener);
        mIjkMediaPlayer.setOnCompletionListener(mOnCompletionListener);
        mIjkMediaPlayer.setOnErrorListener(mOnErrorListener);
        mIjkMediaPlayer.setOnInfoListener(mOnInfoListener);
        mIjkMediaPlayer.setOnBufferingUpdateListener(mOnBufferingUpdateListener);
        mIjkMediaPlayer.setOnSeekCompleteListener(mOnSeekCompleteListener);
        //取消自动播放
        mIjkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "start-on-prepared", 0);
    }

    //初始化数据
    private void initParams() {
        notifyStatus(MusicConstant.STATE_IDLE);
        needSeekTo = false;
        position = 0;
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                timerHandler = new Handler();
                Looper.loop();
            }
        }).start();
    }

    private void notifyStatus(int state) {
        if (state != mState) {
            Log.d(TAG, "notifyStatus: " + format(state));
            mState = state;
            int N = mStatusChangeListener.beginBroadcast();
            for (int i = 0; i < N; i++) {
                IOnStatusChangeListener l = mStatusChangeListener.getBroadcastItem(i);
                if (l != null) {
                    try {
                        l.onStatusChange(state);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
            mStatusChangeListener.finishBroadcast();
        }
    }

    private void notifyProgress(long progress, long duration, long buffer) {
        int N = mProgressListener.beginBroadcast();
        for (int i = 0; i < N; i++) {
            IOnProgressListener l = mProgressListener.getBroadcastItem(i);
            if (l != null) {
                try {
                    l.onProgress(progress, duration, buffer);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
        mProgressListener.finishBroadcast();
    }

    private void startTimer() {
        if (timerHandler != null) {
            timerHandler.post(runnable);
        }
    }

    private void removeTimer(boolean release) {
        if (timerHandler != null) {
            timerHandler.removeCallbacks(runnable);
            if (release) {
                timerHandler = null;
            }
        }
    }

    private void start() {
        if (mIjkMediaPlayer != null && !mIjkMediaPlayer.isPlaying()) {
            if (needSeekTo && position > 0 && position < mIjkMediaPlayer.getDuration()) {
                mIjkMediaPlayer.seekTo(position);
                needSeekTo = false;
            }
            mIjkMediaPlayer.start();
            notifyStatus(MusicConstant.STATE_PLAYING);
        }
    }

    public void pause() {
        if (mIjkMediaPlayer != null && mIjkMediaPlayer.isPlaying()) {
            mIjkMediaPlayer.pause();
            notifyStatus(MusicConstant.STATE_PAUSED);
        }
    }

    private final IMediaPlayer.OnPreparedListener mOnPreparedListener = new IMediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(IMediaPlayer iMediaPlayer) {
            notifyStatus(MusicConstant.STATE_PREPARED);
            if (autoPlay) {
                start();
            }
        }
    };

    private final IMediaPlayer.OnCompletionListener mOnCompletionListener = new IMediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(IMediaPlayer iMediaPlayer) {
            notifyStatus(MusicConstant.STATE_FINISH);
            removeTimer(false);
        }
    };

    private final IMediaPlayer.OnErrorListener mOnErrorListener = new IMediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(IMediaPlayer iMediaPlayer, int i, int i1) {
            notifyStatus(MusicConstant.STATE_ERROR);
            bufferDuration = 0;
            removeTimer(false);
            return false;
        }
    };

    private final IMediaPlayer.OnInfoListener mOnInfoListener = new IMediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(IMediaPlayer iMediaPlayer, int i, int i1) {
            switch (i) {
                case IMediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING:
                    Log.d("TAG", "MEDIA_INFO_VIDEO_TRACK_LAGGING:");
                    break;
                case IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                    Log.d("TAG", "MEDIA_INFO_VIDEO_RENDERING_START:");
                    break;
                case IMediaPlayer.MEDIA_INFO_BUFFERING_START:
                    Log.d("TAG", "MEDIA_INFO_BUFFERING_START:");
                    notifyStatus(MusicConstant.STATE_LOADING);
                    break;
                case IMediaPlayer.MEDIA_INFO_BUFFERING_END:
                    Log.d("TAG", "MEDIA_INFO_BUFFERING_END:");
                    if (mState != MusicConstant.STATE_PAUSED && mState != MusicConstant.STATE_ERROR) {
                        notifyStatus(MusicConstant.STATE_PLAYING);
                    }
                    break;
                case IMediaPlayer.MEDIA_INFO_NETWORK_BANDWIDTH:
                    Log.d("TAG", "MEDIA_INFO_NETWORK_BANDWIDTH: " + i1);
                    break;
                case IMediaPlayer.MEDIA_INFO_BAD_INTERLEAVING:
                    Log.d("TAG", "MEDIA_INFO_BAD_INTERLEAVING:");
                    break;
                case IMediaPlayer.MEDIA_INFO_NOT_SEEKABLE:
                    Log.d("TAG", "MEDIA_INFO_NOT_SEEKABLE:");
                    break;
                case IMediaPlayer.MEDIA_INFO_METADATA_UPDATE:
                    Log.d("TAG", "MEDIA_INFO_METADATA_UPDATE:");
                    break;
                case IMediaPlayer.MEDIA_INFO_UNSUPPORTED_SUBTITLE:
                    Log.d("TAG", "MEDIA_INFO_UNSUPPORTED_SUBTITLE:");
                    break;
                case IMediaPlayer.MEDIA_INFO_SUBTITLE_TIMED_OUT:
                    Log.d("TAG", "MEDIA_INFO_SUBTITLE_TIMED_OUT:");
                    break;
                case IMediaPlayer.MEDIA_INFO_VIDEO_ROTATION_CHANGED:
                    Log.d("TAG", "MEDIA_INFO_VIDEO_ROTATION_CHANGED: " + i1);
                    break;
                case IMediaPlayer.MEDIA_INFO_AUDIO_RENDERING_START:
                    Log.d("TAG", "MEDIA_INFO_AUDIO_RENDERING_START:");
                    break;
            }
            return true;
        }
    };

    private final IMediaPlayer.OnBufferingUpdateListener mOnBufferingUpdateListener = new IMediaPlayer.OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(IMediaPlayer iMediaPlayer, int i) {
            bufferDuration = i;
        }
    };

    private final IMediaPlayer.OnSeekCompleteListener mOnSeekCompleteListener = new IMediaPlayer.OnSeekCompleteListener() {
        @Override
        public void onSeekComplete(IMediaPlayer iMediaPlayer) {

        }
    };

    private String format(int state) {
        String result = "";
        switch (state) {
            case -1:
                result = "state error";
                break;
            case 0:
                result = "state idle";
                break;
            case 1:
                result = "state preparing";
                break;
            case 2:
                result = "state prepared";
                break;
            case 3:
                result = "state playing";
                break;
            case 4:
                result = "state played";
                break;
            case 5:
                result = "state loading";
                break;
            case 6:
                result = "state finish";
                break;
        }
        return result;
    }
}
