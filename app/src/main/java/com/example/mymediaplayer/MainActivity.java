package com.example.mymediaplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


enum MediaLoopMode {SingleLooping, FolderLooping, AllMediaLooping}

public class MainActivity extends AppCompatActivity {

    ConstraintLayout miniMediaPlayer;
    NavController navController;
    ImageButton imageButtonMiniPlay, imageButtonMiniStop, imageButtonMiniNext;
    TextView textViewMiniName, textViewMiniCurrentTime, textViewMiniDuration;
    activityReceiver receiver;

    private boolean isExit = false;

    private Intent mServiceIntent;

    private static MediaControlService.MyBinder myBinder;

    public static synchronized MediaControlService.MyBinder mServiceBinder() {
        return myBinder;
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (textViewMiniName.getText().equals(myBinder.getCurrentMediaData().getTitle()) == false) {
                textViewMiniName.setText(myBinder.getCurrentMediaData().getTitle());
                String duration = DataManager.instance().timeToString(myBinder.getMediaDuration());
                textViewMiniDuration.setText(" / " + duration);
            }
            int currentTime = msg.what;
            String sCurrentTime = DataManager.instance().timeToString(currentTime);
            textViewMiniCurrentTime.setText(sCurrentTime);
        }
    };

    class activityReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (myBinder.isPlaying()) {
                imageButtonMiniPlay.setImageResource(R.drawable.ic_pause_black_48dp);
            } else {
                imageButtonMiniPlay.setImageResource(R.drawable.ic_play_arrow_black_48dp);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initDataManager();
        registerMediaReceiver();
        initLayoutUI();

        if (savedInstanceState != null) {
            myBinder = (MediaControlService.MyBinder) savedInstanceState.getBinder(getResources().getString(R.string.savestate_servicebinder));

            if (myBinder != null) {
                myBinder.setActivityHandler(handler);
                myBinder.startThread();
            }
        } else {
            mServiceIntent = new Intent(this, MediaControlService.class);
            startService(mServiceIntent);
        }
    }

    private void initDataManager() {

        List<FolderData> folderList = new ArrayList<>();
        int totalTime = 0;
        try {

            Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            ContentResolver resolver = MainActivity.this.getContentResolver();
            Cursor cursor;

            cursor = resolver.query(uri, null, null, null, null);
            cursor.moveToFirst();

            MediaData mediaData;
            for (int i = 0, j = 0; i < cursor.getCount(); i++) {

                mediaData = new MediaData();
                mediaData.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)));
                mediaData.setArtist(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)));
                mediaData.setAlbum(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)));
                mediaData.setDisplayName(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)));

                mediaData.setId(cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)));
                mediaData.setAlbumId(cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)));
                mediaData.setDuration(cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)));
                mediaData.setSize(cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)));

                mediaData.setPath(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)));
                mediaData.setIsMusic(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.IS_MUSIC)));

                //集合每首音樂父層資料夾進行分類 & 統計資料夾所有音樂時間
                File file = new File(mediaData.getPath()).getParentFile();
                if (folderList.size() == 0) {

                    folderList.add(new FolderData());
                    folderList.get(j).setFolderPath(file);
                    folderList.get(j).setFolderName(file.getName());
                } else if (folderList.get(j).getFolderPath().getPath().equals(file.getPath()) == false) {
                    folderList.get(j).setTotalTime(DataManager.instance().timeToString(totalTime));
                    folderList.get(j).setTotalSong(folderList.get(j).getMediaDatas().size() + " " + getResources().getString(R.string.files));
                    totalTime = 0;

                    j++;
                    folderList.add(new FolderData());
                    folderList.get(j).setFolderPath(file);
                    folderList.get(j).setFolderName(file.getName());
                }

                folderList.get(j).getMediaDatas().add(mediaData);
                totalTime += mediaData.getDuration();

                if (i == cursor.getCount() - 1) {
                    folderList.get(j).setTotalTime(DataManager.instance().timeToString(totalTime));
                    folderList.get(j).setTotalSong(folderList.get(j).getMediaDatas().size() + " " + getResources().getString(R.string.files));
                }

                cursor.moveToNext();
            }
            cursor.close();
            DataManager.instance().setAllMediaFolderList(folderList);

        } catch (NullPointerException e) {
        }
    }

    private void registerMediaReceiver() {
        receiver = new activityReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(getResources().getString(R.string.miniplayer_receiver_action));
        registerReceiver(receiver, filter);
    }

    private void initLayoutUI() {

        miniMediaPlayer = findViewById(R.id.miniPlayerLayout);
        imageButtonMiniPlay = findViewById(R.id.imageButtonMiniPlay);
        imageButtonMiniStop = findViewById(R.id.imageButtonMiniStop);
        imageButtonMiniNext = findViewById(R.id.imageButtonMiniNext);
        textViewMiniName = findViewById(R.id.textViewMiniName);
        textViewMiniCurrentTime = findViewById(R.id.textViewMiniCurrentTime);
        textViewMiniDuration = findViewById(R.id.textViewMiniDuration);
        navController = Navigation.findNavController(this, R.id.fragment);

        View.OnClickListener MiniLayoutListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myBinder.getCurrentMediaData() != null) {
                    switch (v.getId()) {
                        case R.id.imageButtonMiniPlay: {
                            myBinder.mediaPlayPause();

                            if (myBinder.isPlaying()) {
                                imageButtonMiniPlay.setImageResource(R.drawable.ic_pause_black_48dp);
                            } else {
                                imageButtonMiniPlay.setImageResource(R.drawable.ic_play_arrow_black_48dp);
                            }

                            break;
                        }

                        case R.id.imageButtonMiniStop: {
                            myBinder.mediaStop();
                            imageButtonMiniPlay.setImageResource(R.drawable.ic_play_arrow_black_48dp);

                            break;
                        }

                        case R.id.imageButtonMiniNext: {

                            myBinder.mediaNextSong();

                            imageButtonMiniPlay.setImageResource(R.drawable.ic_pause_black_48dp);
                            textViewMiniName.setText(myBinder.getCurrentMediaData().getTitle());
                            String duration = DataManager.instance().timeToString(myBinder.getMediaDuration());
                            textViewMiniDuration.setText(" / " + duration);

                            break;
                        }

                        case R.id.miniPlayerLayout: {

                            MediaData currentMedia = myBinder.getCurrentMediaData();
                            myBinder.setChangeMediaData(currentMedia);

                            navController.navigate(R.id.mediaDetailFragment);

                            break;
                        }
                    }
                } else {
                    Toast.makeText(v.getContext(), getResources().getString(R.string.no_music_in_mediaplayer), Toast.LENGTH_SHORT).show();
                }

            }
        };
        imageButtonMiniPlay.setOnClickListener(MiniLayoutListener);
        imageButtonMiniStop.setOnClickListener(MiniLayoutListener);
        imageButtonMiniNext.setOnClickListener(MiniLayoutListener);
        miniMediaPlayer.setOnClickListener(MiniLayoutListener);
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            myBinder = (MediaControlService.MyBinder) service;

            if (myBinder.getCurrentMediaData() != null) {

                if (myBinder.isPlaying()) {
                    imageButtonMiniPlay.setImageResource(R.drawable.ic_pause_black_48dp);
                } else {
                    imageButtonMiniPlay.setImageResource(R.drawable.ic_play_arrow_black_48dp);
                }
                myBinder.setActivityHandler(handler);
                myBinder.startThread();

            } else {
                textViewMiniName.setText(getResources().getString(R.string.no_music));
                textViewMiniCurrentTime.setText("00 : 00");
                textViewMiniDuration.setText(" / 00 : 00");
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onResume() {
        if (myBinder == null) {
            bindService(mServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
        } else {
            if (myBinder.isPlaying()) {
                imageButtonMiniPlay.setImageResource(R.drawable.ic_pause_black_48dp);
            } else {
                imageButtonMiniPlay.setImageResource(R.drawable.ic_play_arrow_black_48dp);
            }
            myBinder.setActivityHandler(handler);
            myBinder.startThread();
        }
        super.onResume();
    }

    @Override
    protected void onStop() {
        myBinder.stopThread();
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        if (navController.getCurrentDestination().getId() == R.id.folderListFragment) {
            if (isExit) {
                finish();
            } else {

                this.isExit = true;

                Toast.makeText(this, "再按一次返回鍵將關閉應用程式", Toast.LENGTH_SHORT).show();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(2000);
                            isExit = false;
                        } catch (InterruptedException e) {
                        }
                    }
                }).start();
            }
        } else {
            navController.navigateUp();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putBinder(getResources().getString(R.string.savestate_servicebinder), myBinder);
    }


}

