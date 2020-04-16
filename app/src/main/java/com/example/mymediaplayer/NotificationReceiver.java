package com.example.mymediaplayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

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
                    context.sendBroadcast(new Intent(context.getResources().getString(R.string.miniplayer_receiver_action)));
                    context.sendBroadcast(new Intent(context.getResources().getString(R.string.detail_receiver_action)));
                    break;
                }
                case "Stop": {
                    mBinder.mediaStop();
                    context.sendBroadcast(new Intent(context.getResources().getString(R.string.miniplayer_receiver_action)));
                    context.sendBroadcast(new Intent(context.getResources().getString(R.string.detail_receiver_action)));
                    break;
                }
                case "Next": {
                    mBinder.mediaNextSong();
                    break;
                }
                case "View": {
                    Intent mIntent = new Intent();
                    mIntent.setClass(context, MainActivity.class);
                    mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(mIntent);
                    break;
                }
            }
        }
    }
}
