package com.example.mahmud.mplayer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongHolder> {
    Context context;
    ArrayList<SongInfo> songs;

    public SongAdapter(Context context, ArrayList<SongInfo> songs) {
        this.context = context;
        this.songs = songs;
    }
    OnitemClickListener onitemClickListener;

    public interface OnitemClickListener{
        void onItemClick(Button b,View v,SongInfo obj,int position);
    }
    public void setOnitemClickListener(OnitemClickListener onitemClickListener){
        this.onitemClickListener = onitemClickListener;
    }

    @NonNull
    @Override
    public SongHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View MyView = LayoutInflater.from(context).inflate(R.layout.row_song,viewGroup,false);
        return new SongHolder(MyView);
    }

    @Override
    public void onBindViewHolder(@NonNull final SongHolder songHolder, final int i) {
        final SongInfo songInfo = songs.get(i);
        songHolder.songName.setText(songInfo.songName);
        songHolder.artistName.setText(songInfo.artistName);
        songHolder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onitemClickListener!=null){
                    onitemClickListener.onItemClick(songHolder.button,v,songInfo,i);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    public class SongHolder extends RecyclerView.ViewHolder {
        TextView songName;
        TextView artistName;
        Button button;
        public SongHolder(@NonNull View itemView) {
            super(itemView);
            songName = itemView.findViewById(R.id.nameTextViewId);
            artistName = itemView.findViewById(R.id.artistTextViewId);
            button = itemView.findViewById(R.id.buttonId);
        }
    }
}
