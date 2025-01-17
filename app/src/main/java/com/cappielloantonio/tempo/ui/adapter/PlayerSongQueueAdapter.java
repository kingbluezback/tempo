package com.cappielloantonio.tempo.ui.adapter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.media3.session.MediaBrowser;
import androidx.recyclerview.widget.RecyclerView;

import com.cappielloantonio.tempo.R;
import com.cappielloantonio.tempo.databinding.ItemPlayerQueueSongBinding;
import com.cappielloantonio.tempo.glide.CustomGlideRequest;
import com.cappielloantonio.tempo.interfaces.ClickCallback;
import com.cappielloantonio.tempo.interfaces.MediaIndexCallback;
import com.cappielloantonio.tempo.service.MediaManager;
import com.cappielloantonio.tempo.subsonic.models.Child;
import com.cappielloantonio.tempo.util.Constants;
import com.cappielloantonio.tempo.util.MusicUtil;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlayerSongQueueAdapter extends RecyclerView.Adapter<PlayerSongQueueAdapter.ViewHolder> {
    private final ClickCallback click;

    private ListenableFuture<MediaBrowser> mediaBrowserListenableFuture;
    private List<Child> songs;

    public PlayerSongQueueAdapter(ClickCallback click) {
        this.click = click;
        this.songs = Collections.emptyList();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPlayerQueueSongBinding view = ItemPlayerQueueSongBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Child song = songs.get(position);

        holder.item.queueSongTitleTextView.setText(MusicUtil.getReadableString(song.getTitle()));
        holder.item.queueSongSubtitleTextView.setText(holder.itemView.getContext().getString(R.string.song_subtitle_formatter, MusicUtil.getReadableString(song.getArtist()), MusicUtil.getReadableDurationString(song.getDuration(), false)));

        CustomGlideRequest.Builder
                .from(holder.itemView.getContext(), song.getCoverArtId(), CustomGlideRequest.ResourceType.Song)
                .build()
                .into(holder.item.queueSongCoverImageView);

        MediaManager.getCurrentIndex(mediaBrowserListenableFuture, new MediaIndexCallback() {
            @Override
            public void onRecovery(int index) {
                if (position < index) {
                    holder.item.queueSongTitleTextView.setAlpha(0.2f);
                    holder.item.queueSongSubtitleTextView.setAlpha(0.2f);
                } else {
                    holder.item.queueSongTitleTextView.setAlpha(1.0f);
                    holder.item.queueSongSubtitleTextView.setAlpha(1.0f);
                }
            }
        });
    }

    public List<Child> getItems() {
        return this.songs;
    }

    public void setItems(List<Child> songs) {
        this.songs = songs;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (songs == null) {
            return 0;
        }
        return songs.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setMediaBrowserListenableFuture(ListenableFuture<MediaBrowser> mediaBrowserListenableFuture) {
        this.mediaBrowserListenableFuture = mediaBrowserListenableFuture;
    }

    public Child getItem(int id) {
        return songs.get(id);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemPlayerQueueSongBinding item;

        ViewHolder(ItemPlayerQueueSongBinding item) {
            super(item.getRoot());

            this.item = item;

            item.queueSongTitleTextView.setSelected(true);
            item.queueSongSubtitleTextView.setSelected(true);

            itemView.setOnClickListener(v -> onClick());
        }

        public void onClick() {
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(Constants.TRACKS_OBJECT, new ArrayList<>(songs));
            bundle.putInt(Constants.ITEM_POSITION, getBindingAdapterPosition());

            click.onMediaClick(bundle);
        }
    }
}
