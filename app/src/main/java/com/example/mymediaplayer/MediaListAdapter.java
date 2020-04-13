package com.example.mymediaplayer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MediaListAdapter extends RecyclerView.Adapter<MediaListAdapter.MediaViewHolder> {

    List<MediaData> list;

    public MediaListAdapter(List<MediaData> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public MediaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.cell_medialist,parent,false);

        final MediaViewHolder holder = new MediaViewHolder(view);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MediaData mediaData = (MediaData) holder.itemView.getTag(R.id.target_media_viewholder);

                if(mediaData != null) {

                    MainActivity.mServiceBinder().setChangeMediaData(mediaData);

                    NavController nav = Navigation.findNavController(v);
                    nav.navigate(R.id.action_mediaListFragment_to_mediaDetailFragment);
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MediaViewHolder holder, int position) {

        MediaData mediaData = list.get(position);
        holder.itemView.setTag(R.id.target_media_viewholder , mediaData);

        String str = "";

        if(position < 9) {
            str = "0" + (position + 1);
            holder.textViewMediaNumber.setText(str);
        }
        else
        {
            holder.textViewMediaNumber.setText(String.valueOf(position + 1));
        }

        holder.textViewMediaName.setText(list.get(position).getTitle());
        holder.textViewArtistName.setText(list.get(position).getArtist());
        int time = (int) list.get(position).getDuration();

        holder.textViewMediaTime.setText(DataManager.instance().timeToString(time));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    static class MediaViewHolder extends RecyclerView.ViewHolder
    {
        TextView textViewMediaName , textViewMediaNumber , textViewMediaTime , textViewArtistName;

        public MediaViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewMediaName = itemView.findViewById(R.id.textViewMediaName);
            textViewMediaNumber = itemView.findViewById(R.id.textViewMediaNumber);
            textViewMediaTime = itemView.findViewById(R.id.textViewMiniCurrentTime);
            textViewArtistName =itemView.findViewById(R.id.textViewArtistName);
        }
    }
}
