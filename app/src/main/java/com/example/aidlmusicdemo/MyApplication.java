package com.example.aidlmusicdemo;

import android.app.Application;

import com.example.aidlmusicdemo.callback.IMusicServiceCallback;
import com.example.aidlmusicdemo.manager.MusicPlayListManager;
import com.example.aidlmusicdemo.manager.MusicServiceManager;
import com.example.music.aidl.IMusicManager;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        MusicServiceManager.init(this);
        MusicServiceManager.getInstance().setMusicServiceCallback(new IMusicServiceCallback() {
            @Override
            public void onConnectSuccess(IMusicManager iMusicManager) {
                MusicPlayListManager.getInstance().init(iMusicManager);
            }

            @Override
            public void onConnectFailure() {

            }
        });
    }
}
