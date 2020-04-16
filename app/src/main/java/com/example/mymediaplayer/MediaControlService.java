package com.example.mymediaplayer;

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import java.io.File;
import java.io.IOException;
import java.util.List;


public class MediaControlService extends Service {

    private List<FolderData> allMediaFolderList;

    String channelId = "MyMediaPlayer", channelName = "MediaPlayer";
    String playAction = "Play", stopAction = "Stop", nextAction = "Next", viewAction = "View";
    RemoteViews collasped, expended;

    private MediaPlayer mp = new MediaPlayer();
    private MediaData currentMediaData, changeMediaData;

    private MyBinder mBinder = new MyBinder();

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        super.onCreate();

        registerNotificationChannel();
        initMediaControl();
        serviceStopThread();
    }

    private void initMediaControl() {
        SharedPreferences shp = getSharedPreferences(getResources().getString(R.string.shp_key_data), MODE_PRIVATE);
        String medianame = shp.getString(getResources().getString(R.string.shp_key_medianame), "");
        int mediaposition = shp.getInt(getResources().getString(R.string.shp_key_mediaposition), 0);
        String medialoopmode = shp.getString(getResources().getString(R.string.shp_key_medialoopmode), "");

        allMediaFolderList = DataManager.instance().getAllMediaFolderList();

        if (medianame.isEmpty() == false && medialoopmode.isEmpty() == false) {

            mBinder.setMediaLoopMode(MediaLoopMode.valueOf(medialoopmode));

            List<MediaData> mediaDataList;
            MediaData check = null;

            for (int i = 0; i < allMediaFolderList.size(); i++) {
                mediaDataList = allMediaFolderList.get(i).getMediaDatas();
                for (int j = 0; j < mediaDataList.size(); j++) {
                    if (mediaDataList.get(j).getTitle().equals(medianame)) {
                        check = mediaDataList.get(j);
                        break;
                    }
                }
                if (check != null) {
                    break;
                }
            }

            if (check != null) {
                mBinder.setChangeMediaData(check);
                mBinder.initMediaPlayer(mediaposition);
                mBinder.mediaPlayPause();
            }
        }
    }

    private void registerNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            NotificationChannel mNotificationchannel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
            mNotificationchannel.enableVibration(false);
            mNotificationchannel.enableLights(false);
            mNotificationchannel.setVibrationPattern(new long[]{0});
            mNotificationchannel.setSound(null , null);

            mNotificationManager.createNotificationChannel(mNotificationchannel);

            PendingIntent notifyPlay = PendingIntent.getBroadcast(this, 0, new Intent(playAction), PendingIntent.FLAG_UPDATE_CURRENT);
            PendingIntent notifyStop = PendingIntent.getBroadcast(this, 0, new Intent(stopAction), PendingIntent.FLAG_UPDATE_CURRENT);
            PendingIntent notifyNext = PendingIntent.getBroadcast(this, 0, new Intent(nextAction), PendingIntent.FLAG_UPDATE_CURRENT);

            collasped = new RemoteViews(getPackageName(), R.layout.notify_collasped);
            collasped.setImageViewResource(R.id.notify_col_imvIcon, R.mipmap.ic_launcher);

            expended = new RemoteViews(getPackageName(), R.layout.notify_expend);
            expended.setImageViewResource(R.id.notification_imvIcon, R.mipmap.ic_launcher);
            expended.setImageViewResource(R.id.notification_ibtnPlay, R.drawable.ic_play_arrow_black_48dp);
            expended.setImageViewResource(R.id.notification_ibtnStop, R.drawable.ic_stop_black_48dp);
            expended.setImageViewResource(R.id.notification_ibtnNext, R.drawable.ic_skip_next_black_48dp);
            expended.setOnClickPendingIntent(R.id.notification_ibtnPlay, notifyPlay);
            expended.setOnClickPendingIntent(R.id.notification_ibtnStop, notifyStop);
            expended.setOnClickPendingIntent(R.id.notification_ibtnNext, notifyNext);

            NotificationReceiver notificationReceiver = new NotificationReceiver(mBinder);
            IntentFilter filter = new IntentFilter();
            filter.addAction(playAction);
            filter.addAction(stopAction);
            filter.addAction(nextAction);
            filter.addAction(viewAction);
            registerReceiver(notificationReceiver, filter);
        }
    }

    private void updateNotification() {
        if (mBinder.getCurrentMediaData() == null) {
            collasped.setTextViewText(R.id.notify_col_Title, "");
            collasped.setTextViewText(R.id.notify_col_SongName, getResources().getString(R.string.no_music));
            expended.setTextViewText(R.id.notification_txvTitle, "");
            expended.setTextViewText(R.id.notification_txvName, getResources().getString(R.string.no_music));
        } else {
            collasped.setTextViewText(R.id.notify_col_Title, getResources().getString(R.string.now_is_playing));
            collasped.setTextViewText(R.id.notify_col_SongName, mBinder.getCurrentMediaData().getTitle());
            expended.setTextViewText(R.id.notification_txvTitle, getResources().getString(R.string.song));
            expended.setTextViewText(R.id.notification_txvName, mBinder.getCurrentMediaData().getTitle());
            if (mBinder.isPlaying()) {
                expended.setImageViewResource(R.id.notification_ibtnPlay, R.drawable.ic_pause_black_48dp);
            } else {
                expended.setImageViewResource(R.id.notification_ibtnPlay, R.drawable.ic_play_arrow_black_48dp);
            }
        }

        PendingIntent notifyView = PendingIntent.getBroadcast(this, 0, new Intent(viewAction), PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setContentIntent(notifyView)
                .setWhen(System.currentTimeMillis())
                .setVibrate(new long[]{0})
                .setSound(null)
                .setCustomContentView(collasped)
                .setCustomBigContentView(expended);

        startForeground(1, builder.build());
    }

    private void serviceStopThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mBinder.isPlaying() || checkActivityAlive(getApplicationContext(), MainActivity.class.getName())) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                    }
                }
                stopSelf();
            }
        }).start();
    }

    private boolean checkActivityAlive(Context context, String activityName) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> infos = activityManager.getRunningTasks(100);
        for (int i = 0; i < infos.size(); i++) {
            ComponentName cpn = infos.get(i).topActivity;
            if (cpn.getClassName().equals(activityName)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //return super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onDestroy() {

        if (mBinder.getCurrentMediaData() != null) {
            SharedPreferences shp = this.getSharedPreferences(getResources().getString(R.string.shp_key_data), MODE_PRIVATE);
            SharedPreferences.Editor edit = shp.edit();

            MediaData mediaData = mBinder.getCurrentMediaData();
            int mediaposition = mBinder.getMediaPosition();
            MediaLoopMode mediaLoopMode = mBinder.getMediaLoopMode();

            edit.putString(getResources().getString(R.string.shp_key_medianame), mediaData.getTitle());
            edit.putInt(getResources().getString(R.string.shp_key_mediaposition), mediaposition);
            edit.putString(getResources().getString(R.string.shp_key_medialoopmode), mediaLoopMode.toString());

            edit.apply();
            edit.commit();

            stopForeground(true);
        }
        super.onDestroy();
    }

    public class MyBinder extends Binder {

        MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

                switch (getMediaLoopMode()) {
                    case AllMediaLooping:
                    case FolderLooping: {
                        mediaNextSong();
                        break;
                    }
                    case SingleLooping: {
                        mp.seekTo(0);
                        mediaPlayPause();
                        break;
                    }
                }
            }
        };
        MediaPlayer.OnErrorListener errorListener = new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                return true;
            }
        };
        private boolean bThread = false;
        private MediaLoopMode mediaLoopMode = MediaLoopMode.FolderLooping;
        private Handler activityHandler;

        public void startThread() {
            if (bThread == false) {
                bThread = true;

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (bThread) {
                            try {
                                Message msg = new Message();
                                msg.what = getMediaPosition();
                                activityHandler.sendMessage(msg);
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                            }
                        }
                    }
                }).start();
            }
        }

        public void stopThread() {
            bThread = false;
        }


        //MediaPlayer Controller
        public void initMediaPlayer(int msec) {

            if (currentMediaData != changeMediaData) {
                try {
                    currentMediaData = changeMediaData;
                    mp.reset();
                    mp.setDataSource(currentMediaData.getPath());
                    mp.prepare();
                    mp.seekTo(msec);
                    mp.setOnCompletionListener(onCompletionListener);
                    mp.setOnErrorListener(errorListener);
                } catch (IOException e) {
                }
            }
            if (mp.isPlaying() == false) {
                mediaPlayPause();
            }

        }

        public void mediaPlayPause() {
            if (mp != null) {
                if (mp.isPlaying()) {
                    mp.pause();
                } else {
                    mp.start();
                }
                updateNotification();
            }
        }

        public void mediaStop() {
            mp.pause();
            mp.seekTo(0);
            updateNotification();
        }

        public void mediaNextSong() {

            mp.stop();
            mp.reset();

            File file = new File(currentMediaData.getPath()).getParentFile();
            for (int i = 0; i < allMediaFolderList.size(); i++) {
                if (allMediaFolderList.get(i).getFolderPath().getPath().equals(file.getPath())) {
                    for (int j = 0; j < allMediaFolderList.get(i).getMediaDatas().size(); j++) {
                        if (currentMediaData == allMediaFolderList.get(i).getMediaDatas().get(j)) {
                            if (j == allMediaFolderList.get(i).getMediaDatas().size() - 1) {
                                if (mediaLoopMode != MediaLoopMode.FolderLooping) {
                                    if (i == allMediaFolderList.size() - 1) {
                                        changeMediaData = allMediaFolderList.get(0).getMediaDatas().get(0);
                                    } else {
                                        changeMediaData = allMediaFolderList.get(i + 1).getMediaDatas().get(0);
                                    }
                                } else {
                                    changeMediaData = allMediaFolderList.get(i).getMediaDatas().get(0);
                                }
                            } else {
                                changeMediaData = allMediaFolderList.get(i).getMediaDatas().get(j + 1);
                            }
                            break;
                        }
                    }
                }
            }
            initMediaPlayer(0);
        }


        //Setter & Getter (Media)
        public MediaData getCurrentMediaData() {
            return currentMediaData;
        }

        public void setChangeMediaData(MediaData changeMediaData) {
            MediaControlService.this.changeMediaData = changeMediaData;
        }

        public int getMediaDuration() {
            if (mp != null) {
                return mp.getDuration();
            }
            return 0;
        }

        public int getMediaPosition() {
            return mp.getCurrentPosition();
        }

        public void setMediaTime(int i) {
            mp.seekTo(i);
        }

        public boolean isPlaying() {
            return mp.isPlaying();
        }

        //Setter & Getter (Other)
        public void setMediaLoopMode(MediaLoopMode mediaLoopMode) {
            this.mediaLoopMode = mediaLoopMode;
        }

        public MediaLoopMode getMediaLoopMode() {
            return mediaLoopMode;
        }

        public void setActivityHandler(Handler activityHandler) {
            this.activityHandler = activityHandler;
        }

    }
}
