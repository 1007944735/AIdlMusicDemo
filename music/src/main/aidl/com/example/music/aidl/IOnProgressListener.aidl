package com.example.music.aidl;

interface IOnProgressListener{
    void onProgress(long progress,long duration,long buffer);
}