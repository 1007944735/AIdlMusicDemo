package com.example.music.aidl;

public interface MusicConstant {
    int STATE_ERROR = -1;//播放错误
    int STATE_IDLE = 0;//初始状态
    int STATE_PREPARING = 1;//资源准备中
    int STATE_PREPARED = 2;//资源准备完毕
    int STATE_PLAYING = 3;//播放中
    int STATE_PAUSED = 4;//暂停
    int STATE_LOADING = 5;//加载中
    int STATE_FINISH = 6;//播放结束
}
