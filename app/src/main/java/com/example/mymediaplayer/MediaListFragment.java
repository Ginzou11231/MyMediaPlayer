package com.example.mymediaplayer;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class MediaListFragment extends Fragment {

    private RecyclerView recyclerView;
    private ImageButton imageButtonBack;
    private TextView textViewTitle;

    public MediaListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_media_list,container , false);
        imageButtonBack = view.findViewById(R.id.imageButtonBack);
        textViewTitle = view.findViewById(R.id.textViewItem);
        recyclerView = view.findViewById(R.id.recyclerViewMusic);

        ConstraintLayout miniPlayer = requireActivity().findViewById(R.id.miniPlayerLayout);
        if (miniPlayer.getVisibility() != View.VISIBLE) {
            miniPlayer.setVisibility(View.VISIBLE);
        }

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        textViewTitle.setText(DataManager.instance().getCurrentFolder().getFolderName());
        imageButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().onBackPressed();
            }
        });

        MediaListAdapter mediaAdapter = new MediaListAdapter(DataManager.instance().getCurrentFolder().getMediaDatas());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(requireActivity() , DividerItemDecoration.VERTICAL);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        recyclerView.setAdapter(mediaAdapter);
        recyclerView.addItemDecoration(dividerItemDecoration);
    }
}

