package com.example.aidlmusicdemo.callback;

import com.example.aidlmusicdemo.bean.MusicMediaInfo;
import com.example.aidlmusicdemo.bean.PlayMode;

import java.util.List;

public interface IPlayList {
    //设置播放模式
    void setMode(PlayMode mode);

    <T extends MusicMediaInfo> void addMusicList(List<T> mediaList,boolean isPlay);

    <T extends MusicMediaInfo> void addMusic(T media,boolean isPlay);

    //清空播放列表
    //isStopPlay 是否停止当前播放
    void clear(boolean isStopPlay);

    //播放上一首
    void playNext();

    //播放下一首
    void playLast();

    //获取当前正在播放的歌曲
    <T extends MusicMediaInfo> T getCurrentPlay();

    //获取播放列表
    List<? extends MusicMediaInfo> getPlayList();

    void play(int index);

    int getCurrentIndex();
}
