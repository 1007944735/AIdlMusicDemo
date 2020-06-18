package com.example.aidlmusicdemo.manager;

import android.app.Application;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.example.aidlmusicdemo.callback.IMusicServiceCallback;
import com.example.music.aidl.IMusicManager;
import com.example.music.aidl.bean.Music;

public class MusicServiceManager {
    private static final String TAG = MusicServiceManager.class.getName();
    private static MusicServiceManager instance;
    private static IMusicManager iMusicManager;
    private static IMusicServiceCallback iMusicServiceCallback;
    private static Application mApplicationContext;

    private MusicServiceManager() {
    }

    public static MusicServiceManager getInstance() {
        if (instance == null) {
            synchronized (MusicServiceManager.class) {
                if (instance == null) {
                    instance = new MusicServiceManager();
                }
            }
        }
        return instance;
    }

    //初始化
    public static void init(Application context) {
        if (iMusicManager == null && context != null) {
            mApplicationContext = context;
            bindService(context);
        }
    }

    private static void bindService(Application context) {
        Intent intent = new Intent();
        intent.setAction("com.example.music.service.MusicService.Connect");
        intent.setPackage("com.example.aidlmusicdemo");
        context.bindService(intent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                iMusicManager = IMusicManager.Stub.asInterface(service);
                try {
                    iMusicManager.asBinder().linkToDeath(mDeathRecipient, 0);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                if (iMusicServiceCallback != null) {
                    iMusicServiceCallback.onConnectSuccess(iMusicManager);
                }
                Log.d(TAG, "MusicService connect success");
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                if (iMusicServiceCallback != null) {
                    iMusicServiceCallback.onConnectFailure();
                }
                Log.d(TAG, "MusicService disconnect");
            }
        }, Service.BIND_AUTO_CREATE);
    }

    public IMusicManager getManager() {
        if (iMusicManager == null) {
            Log.d(TAG, "iMusicManager is null");
            return null;
        }
        return iMusicManager;
    }

    public void setMusicServiceCallback(IMusicServiceCallback callback) {
        iMusicServiceCallback = callback;
    }

    private static IBinder.DeathRecipient mDeathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            if (iMusicManager == null)
                return;
            iMusicManager.asBinder().unlinkToDeath(this, 0);
            iMusicManager = null;
            Log.d(TAG, "MusicService is disconnect,reconnecting now");
            bindService(mApplicationContext);
        }
    };


}
