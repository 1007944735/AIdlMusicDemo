package com.example.aidlmusicdemo;

import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.aidlmusicdemo.bean.DefaultMusicInfo;
import com.example.aidlmusicdemo.manager.MusicPlayListManager;
import com.example.aidlmusicdemo.manager.MusicServiceManager;
import com.example.music.aidl.bean.Music;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void play(View view) {

        Music music = new Music("http://m10.music.126.net/20200615161018/fb0a6b1b6eb3ebbfcf4b5be9578148c6/ymusic/5659/0609/510f/a39c417da71c2763b77afe16892fe571.mp3", 0);
        try {
            MusicServiceManager.getInstance().getManager().play(music);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void start(View view) {
        try {
            MusicServiceManager.getInstance().getManager().start();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    public void pause(View view) {
        try {
            MusicServiceManager.getInstance().getManager().pause();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void playLast(View view) {
        MusicPlayListManager.getInstance().playLast();
    }

    public void playNext(View view) {
        MusicPlayListManager.getInstance().playNext();
    }
}