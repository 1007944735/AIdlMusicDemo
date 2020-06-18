package com.example.aidlmusicdemo.manager;

import android.os.RemoteException;
import android.util.Log;

import com.example.aidlmusicdemo.bean.MusicMediaInfo;
import com.example.aidlmusicdemo.bean.PlayMode;
import com.example.aidlmusicdemo.callback.DefaultPlayListCallback;
import com.example.aidlmusicdemo.callback.IPlayList;
import com.example.aidlmusicdemo.callback.IPlayListCallback;
import com.example.music.aidl.IMusicManager;
import com.example.music.aidl.IOnProgressListener;
import com.example.music.aidl.IOnStatusChangeListener;
import com.example.music.aidl.MusicConstant;

import java.util.ArrayList;
import java.util.List;

public class MusicPlayListManager implements IPlayList {
    private static final String TAG = MusicPlayListManager.class.getName();
    private static MusicPlayListManager instance;
    private List<MusicMediaInfo> mPlayList;
    private IMusicManager iMusicManager;
    //当前播放的index
    private int position;
    private IPlayListCallback iPlayListCallback;

    private PlayMode mPlayMode;

    private MusicPlayListManager() {
        if (mPlayList == null) {
            mPlayList = new ArrayList<>();
            position = -1;
            mPlayMode = PlayMode.MODE_NORMAL;
        }
    }

    public static MusicPlayListManager getInstance() {
        if (instance == null) {
            synchronized (MusicPlayListManager.class) {
                if (instance == null) {
                    instance = new MusicPlayListManager();
                }
            }
        }
        return instance;
    }

    public void init(IMusicManager iMusicManager) {
        bindMusicManager(iMusicManager);
        iPlayListCallback = new DefaultPlayListCallback();
    }

    private void bindMusicManager(final IMusicManager iMusicManager) {
        this.iMusicManager = iMusicManager;
        try {
            iMusicManager.registerStatusChangeListener(onStatusChangeListener);
            iMusicManager.registerProgressListener(onProgressListener);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setMode(PlayMode mode) {
        this.mPlayMode = mode;
    }

    @Override
    public <T extends MusicMediaInfo> void addMusicList(List<T> mediaList, boolean isPlay) {
        if (mPlayList != null && mediaList != null && !mediaList.isEmpty()) {
            if (isPlay && iPlayListCallback != null && !mPlayList.isEmpty() && position != -1) {
                iPlayListCallback.onPlayBefore(iMusicManager, mPlayList.get(position));
            }
            position = mPlayList.size();
            mPlayList.addAll(mediaList);
            if (isPlay && iPlayListCallback != null) {
                iPlayListCallback.onPlayNext(iMusicManager, mPlayList.get(position));
                iPlayListCallback.onPlayAfter(iMusicManager, mPlayList.get(position));
            }
        }
    }

    @Override
    public <T extends MusicMediaInfo> void addMusic(T media, boolean isPlay) {
        if (mPlayList != null && media != null) {
            if (isPlay && iPlayListCallback != null && !mPlayList.isEmpty() && position != -1) {
                iPlayListCallback.onPlayBefore(iMusicManager, mPlayList.get(position));
            }
            position = mPlayList.size();
            mPlayList.add(media);
            if (isPlay && iPlayListCallback != null) {
                iPlayListCallback.onPlayNext(iMusicManager, media);
                iPlayListCallback.onPlayAfter(iMusicManager, media);
            }
        }
    }


    @Override
    public void clear(boolean isStopPlay) {
        if (mPlayList != null) {
            if (isStopPlay) {
                mPlayList.clear();
                try {
                    iMusicManager.pause();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            } else if (position != -1) {
                MusicMediaInfo musicMediaInfo = mPlayList.get(position);
                mPlayList.add(musicMediaInfo);
            } else {
                mPlayList.clear();
            }
        }
    }

    @Override
    public void playNext() {
        if (mPlayList != null) {
            if (iPlayListCallback != null && !mPlayList.isEmpty() && position != -1) {
                iPlayListCallback.onPlayBefore(iMusicManager, mPlayList.get(position));
            }
            position = (position + 1) % mPlayList.size();
            if (iPlayListCallback != null) {
                iPlayListCallback.onPlayNext(iMusicManager, mPlayList.get(position));
                iPlayListCallback.onPlayAfter(iMusicManager, mPlayList.get(position));
            }
        }
    }

    @Override
    public void playLast() {
        if (mPlayList != null) {
            if (iPlayListCallback != null && !mPlayList.isEmpty() && position != -1) {
                iPlayListCallback.onPlayBefore(iMusicManager, mPlayList.get(position));
            }
            position = position == -1 ? -1 : (position - 1);
            if (position < 0) {
                position = position + mPlayList.size();
            }
            if (iPlayListCallback != null) {
                iPlayListCallback.onPlayLast(iMusicManager, mPlayList.get(position));
                iPlayListCallback.onPlayAfter(iMusicManager, mPlayList.get(position));
            }
        }
    }

    @Override
    public <T extends MusicMediaInfo> T getCurrentPlay() {
        if (mPlayList != null && position != -1) {
            return (T) mPlayList.get(position);
        }
        return null;
    }

    @Override
    public List<? extends MusicMediaInfo> getPlayList() {
        return mPlayList;
    }

    @Override
    public void play(int index) {
        if (index < 0 || index >= mPlayList.size()) {
            return;
        }

        if (iPlayListCallback != null && !mPlayList.isEmpty() && position != -1) {
            iPlayListCallback.onPlayBefore(iMusicManager, mPlayList.get(position));
        }
        position = index;
        if (iPlayListCallback != null) {
            iPlayListCallback.onPlayNext(iMusicManager, mPlayList.get(index));
            iPlayListCallback.onPlayAfter(iMusicManager, mPlayList.get(index));
        }
    }

    @Override
    public int getCurrentIndex() {
        return position == -1 ? 0 : position;
    }

    public void setPlayListCallback(IPlayListCallback callback) {
        this.iPlayListCallback = callback;
    }

    public void destroy() {
        try {
            this.iMusicManager.unregisterStatusChangeListener(onStatusChangeListener);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private IOnStatusChangeListener onStatusChangeListener = new IOnStatusChangeListener.Stub() {
        @Override
        public void onStatusChange(int status) throws RemoteException {
            if (status == MusicConstant.STATE_FINISH) {
                switch (mPlayMode) {
                    case MODE_NORMAL:
                        break;
                    case MODE_LIST_LOOP:
                        playNext();
                        break;
                    case MODE_SINGLE_LOOP:
                        iMusicManager.play(getCurrentPlay().getMusic());
                        break;
                }
            }
        }
    };

    private IOnProgressListener onProgressListener = new IOnProgressListener.Stub() {
        @Override
        public void onProgress(long progress, long duration, long buffer) throws RemoteException {
            if (iPlayListCallback != null) {
                iPlayListCallback.onProgress(progress, duration, buffer, getCurrentPlay());
            }
        }
    };
}
