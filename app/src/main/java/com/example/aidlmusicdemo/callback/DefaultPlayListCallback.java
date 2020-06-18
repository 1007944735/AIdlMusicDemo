package com.example.aidlmusicdemo.callback;

import android.os.RemoteException;

import com.example.aidlmusicdemo.bean.MusicMediaInfo;
import com.example.music.aidl.IMusicManager;

public class DefaultPlayListCallback implements IPlayListCallback {

    @Override
    public void onPlayBefore(IMusicManager iMusicManager, MusicMediaInfo oldMusicMediaInfo) {

    }

    @Override
    public void onPlayAfter(IMusicManager iMusicManager, MusicMediaInfo newMusicMediaInfo) {

    }

    @Override
    public void onPlayNext(IMusicManager iMusicManager, MusicMediaInfo media) {
        try {
            iMusicManager.play(media.getMusic());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPlayLast(IMusicManager iMusicManager, MusicMediaInfo media) {
        try {
            iMusicManager.play(media.getMusic());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onProgress(long progress, long duration, long buffer, MusicMediaInfo media) {

    }
}
