package com.example.mymediaplayer;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;


/**
 * A simple {@link Fragment} subclass.
 */
public class OptionFragment extends Fragment {

    private ImageButton imageButtonBack;
    private TextView textViewTitle;
    private RecyclerView recyclerView;

    private List<OptionItemBase> playModeItems = new ArrayList<>();
    private List<OptionItemBase> otherItems = new ArrayList<>();

    private SectionedRecyclerViewAdapter sectionAdapter = new SectionedRecyclerViewAdapter();

    public OptionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_option, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewOption);
        imageButtonBack = view.findViewById(R.id.imageButtonBack);
        textViewTitle = view.findViewById(R.id.textViewItem);

        ConstraintLayout miniPlayer = requireActivity().findViewById(R.id.miniPlayerLayout);
        miniPlayer.setVisibility(View.GONE);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        textViewTitle.setText(getResources().getString(R.string.setting));


        imageButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().onBackPressed();
            }
        });

        String header = getResources().getString(R.string.playmode);
        String single = getResources().getString(R.string.mode_singlelooping);
        String folder = getResources().getString(R.string.mode_folderlooping);
        String allmedia = getResources().getString(R.string.mode_allmedialooping);

        String header2 = getResources().getString(R.string.other);
        String other = getResources().getString(R.string.other_no_item);
        playModeItems.add(new OptionItemMedia(single, MediaLoopMode.SingleLooping));
        playModeItems.add(new OptionItemMedia(folder, MediaLoopMode.FolderLooping));
        playModeItems.add(new OptionItemMedia(allmedia, MediaLoopMode.AllMediaLooping));

        otherItems.add(new OptionItemBase(other));

        sectionAdapter.addSection(new OptionSectionAdapter(requireActivity(),sectionAdapter, header, playModeItems));
        sectionAdapter.addSection(new OptionSectionAdapter(requireActivity(),sectionAdapter, header2, otherItems));

        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        recyclerView.setAdapter(sectionAdapter);


    }
}
