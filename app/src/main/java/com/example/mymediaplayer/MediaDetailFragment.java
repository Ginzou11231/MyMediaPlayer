package com.example.mymediaplayer;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class MediaDetailFragment extends Fragment {

    private ImageView imageViewTitle;
    private ImageButton imageButtonPlay, imageButtonStop, imageButtonNext, miniPlay, imageButtonBack;
    private TextView textViewCurrentTime, textViewCountTime, textViewTitle, miniName, miniTime, miniDuration;
    private SeekBar seekBarTimeDuration;

    private MediaControlService.MyBinder mServiceBinder = MainActivity.mServiceBinder();

    private detailfragReceiver receiver;

    public class detailfragReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mServiceBinder.isPlaying()) {
                miniPlay.setImageResource(R.drawable.ic_pause_black_48dp);
                imageButtonPlay.setImageResource(R.drawable.ic_pause_black_96dp);
            } else {
                miniPlay.setImageResource(R.drawable.ic_play_arrow_black_48dp);
                imageButtonPlay.setImageResource(R.drawable.ic_play_arrow_black_96dp);
            }
        }
    }

    public MediaDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_media_detail, container, false);
        imageButtonBack = view.findViewById(R.id.imageButtonBack);
        imageViewTitle = view.findViewById(R.id.imageViewTitle);
        imageButtonPlay = view.findViewById(R.id.imageButtonPlay);
        imageButtonStop = view.findViewById(R.id.imageButtonStop);
        imageButtonNext = view.findViewById(R.id.imageButtonNext);
        textViewCurrentTime = view.findViewById(R.id.textViewCurrentTime);
        textViewCountTime = view.findViewById(R.id.textViewCountTime);
        textViewTitle = view.findViewById(R.id.textViewItem);
        seekBarTimeDuration = view.findViewById(R.id.seekBarTimeDuration);

        ConstraintLayout miniPlayer = requireActivity().findViewById(R.id.miniPlayerLayout);
        miniPlayer.setVisibility(View.GONE);
        miniPlay = requireActivity().findViewById(R.id.imageButtonMiniPlay);
        miniName = requireActivity().findViewById(R.id.textViewMiniName);
        miniTime = requireActivity().findViewById(R.id.textViewMiniCurrentTime);
        miniDuration = requireActivity().findViewById(R.id.textViewMiniDuration);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        registerMediaReceiver();
        initFragment();
        setUIListener();
    }
    
    private void registerMediaReceiver() {
        receiver = new detailfragReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(getResources().getString(R.string.detail_receiver_action));
        requireActivity().registerReceiver(receiver , filter);
    }

    private void initFragment() {
        mServiceBinder.setActivityHandler(handler);
        mServiceBinder.initMediaPlayer(0);
        mServiceBinder.startThread();

        if (mServiceBinder.isPlaying()) {
            miniPlay.setImageResource(R.drawable.ic_pause_black_48dp);
            imageButtonPlay.setImageResource(R.drawable.ic_pause_black_96dp);
        } else {
            miniPlay.setImageResource(R.drawable.ic_play_arrow_black_48dp);
            imageButtonPlay.setImageResource(R.drawable.ic_play_arrow_black_96dp);
        }

        seekBarTimeDuration.setMax(mServiceBinder.getMediaDuration());

        textViewTitle.setText(mServiceBinder.getCurrentMediaData().getTitle());
        miniName.setText(mServiceBinder.getCurrentMediaData().getTitle());

        String countTime = DataManager.instance().timeToString(mServiceBinder.getMediaDuration());
        textViewCountTime.setText(countTime);
        miniDuration.setText(" / " + countTime);
    }

    private void setUIListener() {
        View.OnClickListener DetailListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.imageButtonPlay: {
                        mServiceBinder.mediaPlayPause();

                        if (mServiceBinder.isPlaying()) {
                            imageButtonPlay.setImageResource(R.drawable.ic_pause_black_96dp);
                            miniPlay.setImageResource(R.drawable.ic_pause_black_48dp);
                        } else {
                            imageButtonPlay.setImageResource(R.drawable.ic_play_arrow_black_96dp);
                            miniPlay.setImageResource(R.drawable.ic_play_arrow_black_48dp);
                        }
                        break;
                    }

                    case R.id.imageButtonStop: {
                        mServiceBinder.mediaStop();
                        imageButtonPlay.setImageResource(R.drawable.ic_play_arrow_black_96dp);
                        miniPlay.setImageResource(R.drawable.ic_play_arrow_black_48dp);
                        break;
                    }

                    case R.id.imageButtonNext: {
                        mServiceBinder.mediaNextSong();

                        imageButtonPlay.setImageResource(R.drawable.ic_pause_black_96dp);
                        miniPlay.setImageResource(R.drawable.ic_pause_black_48dp);
                        textViewTitle.setText(mServiceBinder.getCurrentMediaData().getTitle());
                        miniName.setText(mServiceBinder.getCurrentMediaData().getTitle());
                        seekBarTimeDuration.setMax(mServiceBinder.getMediaDuration());

                        String countTime = DataManager.instance().timeToString(mServiceBinder.getMediaDuration());
                        textViewCountTime.setText(countTime);
                        miniDuration.setText(" / " + countTime);
                        break;
                    }
                    case R.id.imageButtonBack: {
                        requireActivity().onBackPressed();
                        break;
                    }
                }
            }
        };

        imageButtonPlay.setOnClickListener(DetailListener);
        imageButtonStop.setOnClickListener(DetailListener);
        imageButtonNext.setOnClickListener(DetailListener);
        imageButtonBack.setOnClickListener(DetailListener);

        seekBarTimeDuration.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mServiceBinder.setMediaTime(progress);
                    seekBarTimeDuration.setProgress(progress);
                    if (mServiceBinder.isPlaying() == false) {
                        mServiceBinder.mediaPlayPause();
                        imageButtonPlay.setImageResource(R.drawable.ic_pause_black_96dp);
                        miniPlay.setImageResource(R.drawable.ic_pause_black_48dp);
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    public void onResume() {
        if (mServiceBinder != null) {
            if (mServiceBinder.isPlaying() == false) {
                imageButtonPlay.setImageResource(R.drawable.ic_play_arrow_black_96dp);
                miniPlay.setImageResource(R.drawable.ic_play_arrow_black_48dp);
            } else {
                imageButtonPlay.setImageResource(R.drawable.ic_pause_black_96dp);
                miniPlay.setImageResource(R.drawable.ic_pause_black_48dp);
            }
            mServiceBinder.setActivityHandler(handler);
            mServiceBinder.startThread();
        }
        super.onResume();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (textViewTitle.getText().equals(mServiceBinder.getCurrentMediaData().getTitle()) == false) {
                textViewTitle.setText(mServiceBinder.getCurrentMediaData().getTitle());
                seekBarTimeDuration.setMax(mServiceBinder.getMediaDuration());
                miniName.setText(mServiceBinder.getCurrentMediaData().getTitle());

                String countTime = DataManager.instance().timeToString(mServiceBinder.getMediaDuration());
                textViewCountTime.setText(countTime);
                miniDuration.setText(" / " + countTime);
            }

            int currentTime = msg.what;
            if (currentTime > seekBarTimeDuration.getMax()) {
                currentTime = 0;
            }
            seekBarTimeDuration.setProgress(currentTime);
            String sCurrentTime = DataManager.instance().timeToString(currentTime);
            textViewCurrentTime.setText(sCurrentTime);
            miniTime.setText(sCurrentTime);
        }
    };
}
