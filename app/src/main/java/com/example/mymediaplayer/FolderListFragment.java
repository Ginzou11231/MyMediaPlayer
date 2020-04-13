package com.example.mymediaplayer;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class FolderListFragment extends Fragment {

    private TextView textViewTitle;
    private ImageButton imageButtonMenu;
    private RecyclerView recyclerView;
    private FolderListAdapter folderAdapter;
    private DividerItemDecoration dividerItemDecoration;

    public FolderListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_folder_list, container, false);
        imageButtonMenu = view.findViewById(R.id.imageButtonMenuButton);
        textViewTitle = view.findViewById(R.id.textViewItem);
        recyclerView = view.findViewById(R.id.recyclerViewFolder);

        ConstraintLayout miniPlayer = requireActivity().findViewById(R.id.miniPlayerLayout);
        if (miniPlayer.getVisibility() != View.VISIBLE) {
            miniPlayer.setVisibility(View.VISIBLE);
        }
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        textViewTitle.setText(getResources().getString(R.string.mainmenu));
        imageButtonMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                PopupMenu popupMenu = new PopupMenu(requireActivity(), v);
                MenuInflater inflater = popupMenu.getMenuInflater();
                inflater.inflate(R.menu.popmenu, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.app_bar_menuSetting: {
                                NavController controller = Navigation.findNavController(v);
                                controller.navigate(R.id.action_folderListFragment_to_optionFragment);
                                break;
                            }
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });

        FolderListAdapter folderAdapter = new FolderListAdapter(DataManager.instance().getAllMediaFolderList());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(requireActivity(), DividerItemDecoration.VERTICAL);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        recyclerView.setAdapter(folderAdapter);
        recyclerView.addItemDecoration(dividerItemDecoration);
    }
}