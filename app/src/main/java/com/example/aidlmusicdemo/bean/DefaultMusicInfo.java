package com.example.aidlmusicdemo.bean;

public class DefaultMusicInfo extends MusicMediaInfo {
    public String name;
    public DefaultMusicInfo(String url, int fromType) {
        super(url, fromType);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
