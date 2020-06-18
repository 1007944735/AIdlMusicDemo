package com.example.aidlmusicdemo.notification;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.NotificationManagerCompat;
import android.widget.RemoteViews;


import com.example.aidlmusicdemo.R;

import java.util.concurrent.ExecutionException;

public class MusicNotification {
    private static final String channelId = "music_notify";
    private static final String channelName = "音乐";
    private static final int notifyId = 1;
    private Context mContext;
    private NotificationManager notificationManager;
    private Notification notification;
    private Notification.Builder builder;
    private RemoteViews mRemoteView;


    public MusicNotification(Context context) {
        this.mContext = context;
        createNotify();
    }

    private void createNotify() {
        notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        mRemoteView = createRemoteView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
            builder = new Notification.Builder(mContext, channelId)
                    .setCustomContentView(mRemoteView)
                    .setSmallIcon(android.R.drawable.ic_dialog_alert)
                    .setDefaults(Notification.FLAG_NO_CLEAR | Notification.FLAG_FOREGROUND_SERVICE);
        } else {
            builder = new Notification.Builder(mContext)
                    .setContent(mRemoteView)
                    .setSmallIcon(android.R.drawable.ic_dialog_alert)
                    .setDefaults(Notification.FLAG_NO_CLEAR | Notification.FLAG_FOREGROUND_SERVICE);
        }
    }

    private RemoteViews createRemoteView() {
        RemoteViews remoteView = new RemoteViews(mContext.getPackageName(), R.layout.layout_music_notify);
        return remoteView;
    }

    public void sendNotification(String title, String teacher, String imageUrl, boolean isPlay) {
        mRemoteView.setTextViewText(R.id.tv_music_name, title);
        mRemoteView.setTextViewText(R.id.tv_music_author, teacher);
        mRemoteView.setImageViewResource(R.id.btn_play, isPlay ? R.mipmap.ico_music_pause : R.mipmap.ico_music_play);
        PendingIntent lastIntent = PendingIntent.getBroadcast(mContext, 1, new Intent("com.example.music.notification.MusicBroadcast.update").putExtra("type", MusicBroadcast.LAST_MUSIC), PendingIntent.FLAG_ONE_SHOT);
        mRemoteView.setOnClickPendingIntent(R.id.btn_last, lastIntent);
        PendingIntent playIntent = PendingIntent.getBroadcast(mContext, 2, new Intent("com.example.music.notification.MusicBroadcast.update").putExtra("type", MusicBroadcast.PLAY_MUSIC), PendingIntent.FLAG_ONE_SHOT);
        mRemoteView.setOnClickPendingIntent(R.id.btn_play, playIntent);
        PendingIntent nextIntent = PendingIntent.getBroadcast(mContext, 3, new Intent("com.example.music.notification.MusicBroadcast.update").putExtra("type", MusicBroadcast.NEXT_MUSIC), PendingIntent.FLAG_ONE_SHOT);
        mRemoteView.setOnClickPendingIntent(R.id.btn_next, nextIntent);
//        new LoadImageTask().execute(imageUrl);
        notification = builder.build();
        notificationManager.notify(notifyId, notification);
    }

//    class LoadImageTask extends AsyncTask<String, Void, Bitmap> {
//
//        @Override
//        protected Bitmap doInBackground(String... strings) {
//            FutureTarget<Bitmap> target = Glide.with(mContext).asBitmap().load(strings[0]).submit();
//            Bitmap bitmap = null;
//            try {
//                bitmap = target.get();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            } catch (ExecutionException e) {
//                e.printStackTrace();
//            }
//            return bitmap;
//        }
//
//        @Override
//        protected void onPostExecute(Bitmap bitmap) {
//            mRemoteView.setBitmap(R.id.iv_image, "setImageBitmap", bitmap);
//            notification = builder.build();
//            notificationManager.notify(notifyId, notification);
//        }
//    }
}
