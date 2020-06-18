package com.example.aidlmusicdemo.callback;

import com.example.aidlmusicdemo.bean.MusicMediaInfo;
import com.example.music.aidl.IMusicManager;

public interface IPlayListCallback {
    //播放上一首或者下一首之前
    void onPlayBefore(IMusicManager iMusicManager, MusicMediaInfo oldMusicMediaInfo);

    //播放上一首或者下一首之后
    void onPlayAfter(IMusicManager iMusicManager, MusicMediaInfo newMusicMediaInfo);

    void onPlayNext(IMusicManager iMusicManager, MusicMediaInfo media);

    void onPlayLast(IMusicManager iMusicManager, MusicMediaInfo media);

    void onProgress(long progress, long duration, long buffer, MusicMediaInfo media);
}
