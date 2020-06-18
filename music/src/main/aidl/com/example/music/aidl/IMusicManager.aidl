package com.example.music.aidl;

import com.example.music.aidl.bean.Music;
import com.example.music.aidl.IOnProgressListener;
import com.example.music.aidl.IOnStatusChangeListener;

interface IMusicManager {
    //播放
    void start();
    //播放music
    void play(in Music music);
    //暂停
    void pause();
    //重置
    void reset();
    //滑动
    void seekTo(long millisecond);
    //设置播放速度
    void setSpeed(float speed);
    //获取播放速度
    float getSpeed();
    //播放下一首
//    void playNext();
    //播放上一首
//    void playLast();
    //获取播放列表
//    List<Music> getMusicList();
    //获取当前播放信息
    Music getPlayMusic();
    //添加歌曲
//    boolean addMusic(in Music music);
    //批量添加
//    boolean addAll(in List<Music> musics);
    //删除歌曲
//    boolean removeMusic(in Music music);
    //自动播放下一首
//    void setAutoPlayNext(boolean auto);
    //是否循环播放列表
    //0 正常播放 1 列表循环播放 2 单曲播放
//    void setPlayMode(int mode);
    //绑定进度监听
    void registerProgressListener(IOnProgressListener listener);
    //解绑进度监听
    void unregisterProgressListener(IOnProgressListener listener);
    //绑定状态监听
    void registerStatusChangeListener(IOnStatusChangeListener listener);
    //解绑状态监听
    void unregisterStatusChangeListener(IOnStatusChangeListener listener);
    //获取是否正在播放
    boolean isPlaying();
    //获取当前播放进度
    long getCurrentPosition();
}
