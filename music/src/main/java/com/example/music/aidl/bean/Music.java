package com.example.music.aidl.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class Music implements Parcelable {
    private String url;
    private int fromType;//0 来自网络 1 来自本地
//    private Class clz;
//    private T t;

    public Music(String url, int fromType) {
        this.url = url;
        this.fromType = fromType;
    }


    protected Music(Parcel in) {
        url = in.readString();
        fromType = in.readInt();
    }

    public static final Creator<Music> CREATOR = new Creator<Music>() {
        @Override
        public Music createFromParcel(Parcel in) {
            return new Music(in);
        }

        @Override
        public Music[] newArray(int size) {
            return new Music[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(url);
        dest.writeInt(fromType);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getFromType() {
        return fromType;
    }

    public void setFromType(int fromType) {
        this.fromType = fromType;
    }
}
