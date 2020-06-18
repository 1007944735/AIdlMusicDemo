package com.example.aidlmusicdemo.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.RemoteException;

import com.example.aidlmusicdemo.manager.MusicPlayListManager;
import com.example.aidlmusicdemo.manager.MusicServiceManager;

public class MusicBroadcast extends BroadcastReceiver {
    public static final String LAST_MUSIC="LAST_MUSIC";
    public static final String PLAY_MUSIC="PLAY_MUSIC";
    public static final String NEXT_MUSIC="NEXT_MUSIC";
    @Override
    public void onReceive(Context context, Intent intent) {
        String type = intent.getStringExtra("type");
        switch (type) {
            case LAST_MUSIC:
                MusicPlayListManager.getInstance().playLast();
                break;
            case PLAY_MUSIC:
                try {
                    if (MusicServiceManager.getInstance().getManager().isPlaying()) {
                        MusicServiceManager.getInstance().getManager().pause();
                    } else {
                        MusicServiceManager.getInstance().getManager().start();
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case NEXT_MUSIC:
                MusicPlayListManager.getInstance().playNext();
                break;
        }
    }
}
