package com.example.mymediaplayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.Toast;

public class NotificationReceiver extends BroadcastReceiver {

    MediaControlService.MyBinder mBinder;

    public NotificationReceiver(MediaControlService.MyBinder myBinder) {
        mBinder = myBinder;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().isEmpty() == false) {
            switch (intent.getAction()) {
                case "Play": {
                    mBinder.mediaPlayPause();
                    break;
                }
                case "Stop": {
                    mBinder.mediaStop();
                    break;
                }
                case "Next": {
                    mBinder.mediaNextSong();
                    break;
                }
                case "View": {
                    Intent mIntent = new Intent();
                    mIntent.setClass(context, MainActivity.class);
                    mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(mIntent);
                    break;
                }
            }
        }
    }
}
