package com.example.aidlmusicdemo.bean;

import com.example.music.aidl.bean.Music;

public abstract class MusicMediaInfo {
    private Music music;

    public MusicMediaInfo(String url, int fromType) {
        this.music = new Music(url, fromType);
    }

    public Music getMusic() {
        return music;
    }

    public void setMusic(Music music) {
        this.music = music;
    }
}
