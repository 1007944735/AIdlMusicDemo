package com.example.aidlmusicdemo.callback;

import com.example.music.aidl.IMusicManager;

public interface IMusicServiceCallback {
    void onConnectSuccess(IMusicManager iMusicManager);

    void onConnectFailure();
}
