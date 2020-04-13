package com.example.mymediaplayer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FolderListAdapter extends RecyclerView.Adapter<FolderListAdapter.FolderViewHolder> {

    List<FolderData> list;

    public FolderListAdapter(List<FolderData> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public FolderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = layoutInflater.inflate(R.layout.cell_folderlist, parent, false);

        final FolderViewHolder holder = new FolderViewHolder(itemView);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FolderData folderData = (FolderData) holder.itemView.getTag(R.id.target_folder_viewholder);

                if (folderData != null) {
                    DataManager.instance().setCurrentFolder(folderData);

                    NavController nav = Navigation.findNavController(v);
                    nav.navigate(R.id.action_folderListFragment_to_mediaListFragment);
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull FolderViewHolder holder, int position) {

        FolderData folderData = list.get(position);
        holder.itemView.setTag(R.id.target_folder_viewholder, folderData);

        holder.textViewFolderName.setText(list.get(position).getFolderName());
        holder.textViewFileCount.setText(list.get(position).getTotalSong());
        holder.textViewTotalTime.setText(list.get(position).getTotalTime());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    static class FolderViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textViewFolderName, textViewFileCount, textViewTotalTime;

        public FolderViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageViewFolder);
            textViewFolderName = itemView.findViewById(R.id.textViewFolderName);
            textViewFileCount = itemView.findViewById(R.id.textViewFileCount);
            textViewTotalTime = itemView.findViewById(R.id.textViewFolderAllTime);
        }
    }
}