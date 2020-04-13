package com.example.mymediaplayer;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;


public class OptionSectionAdapter extends Section {

    private Context context;
    private String header;
    private List<OptionItemBase> item;
    private SectionedRecyclerViewAdapter mSectionAdapter;


    public OptionSectionAdapter(Context context, SectionedRecyclerViewAdapter sectionAdapter, String header, List<OptionItemBase> item) {
        super(SectionParameters.builder()
                .itemResourceId(R.layout.cell_optionitem)
                .headerResourceId(R.layout.cell_optionheader)
                .build());
        this.context = context;
        this.header = header;
        this.item = item;
        this.mSectionAdapter = sectionAdapter;
    }

    @Override
    public int getContentItemsTotal() {
        return item.size();
    }

    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
        return new HeaderViewHolder(view);
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(final View view) {
        final ItemViewHolder holder = new ItemViewHolder(view);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OptionItemBase optionItem = (OptionItemBase) holder.itemView.getTag(R.id.target_option_adapteritem);

                switch (optionItem.getTypeEnum()) {
                    case Base: {
                        Toast.makeText(context, context.getResources().getString(R.string.no_function), Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case Media: {
                        OptionItemMedia optionItemMedia = (OptionItemMedia) optionItem;
                        MainActivity.mServiceBinder().setMediaLoopMode(optionItemMedia.getPlayMode());
                        Toast.makeText(context, context.getResources().getString(R.string.playmode_change_to) +
                                "\n\"" + optionItemMedia.getItemString() + "\"", Toast.LENGTH_SHORT).show();

                        SharedPreferences shp = context.getSharedPreferences(context.getResources().getString(R.string.shp_key_data), Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = shp.edit();
                        editor.putString(context.getResources().getString(R.string.shp_key_medialoopmode), optionItemMedia.getPlayMode().toString());
                        editor.apply();
                        editor.commit();

                        mSectionAdapter.notifyDataSetChanged();
                        break;
                    }
                }

            }
        });

        return holder;
    }

    @Override
    public View getItemView(ViewGroup parent) {
        return super.getItemView(parent);
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
        HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
        headerViewHolder.textViewHeader.setText(header);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {

        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        OptionItemBase optionItem = item.get(position);
        itemViewHolder.textViewItem.setText(optionItem.getItemString());
        itemViewHolder.itemView.setTag(R.id.target_option_adapteritem, optionItem);

        if (optionItem.getTypeEnum() == OptionItemEnum.Media) {
            OptionItemMedia optionItemMedia = (OptionItemMedia) optionItem;
            if (optionItemMedia.getPlayMode() == MainActivity.mServiceBinder().getMediaLoopMode()) {
                itemViewHolder.imageViewCheck.setVisibility(View.VISIBLE);
            } else {
                itemViewHolder.imageViewCheck.setVisibility(View.INVISIBLE);
            }
        }
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewHeader;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewHeader = itemView.findViewById(R.id.textViewHeader);
        }
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewItem;
        private ImageView imageViewCheck;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewItem = itemView.findViewById(R.id.textViewItem);
            imageViewCheck = itemView.findViewById(R.id.imageViewCheck);
        }
    }
}
