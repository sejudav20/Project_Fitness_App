package com.example.accelerationtest;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;
import java.util.Stack;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RankingAdapter extends RecyclerView.Adapter<RankingAdapter.PlayerHolder> {
    Stack<String> gameData;
    String first;

    public RankingAdapter(String first, Stack<String> allData) {
        gameData = allData;
        this.first=first;


    }



    @NonNull
    @Override
    public PlayerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout li = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.rankingitem, parent, false);
        return new PlayerHolder(li);
    }

    @Override
    public void onBindViewHolder(@NonNull final PlayerHolder holder, int position) {
        if(position==0){
            holder.person.setText(first);
            holder.rank.setText("1: ");
        }else{
            holder.person.setText(gameData.pop());
            holder.rank.setText(position+1+":");
        }
    }


    @Override
    public int getItemCount() {
//       int i=0;
//        for(String name:gameData.isInGame.keySet()){
//
//            if(gameData.isInGame.get(name)){
//                i++;
//
//            }
//
//        }
        return gameData.size()+1;
    }

    class PlayerHolder extends RecyclerView.ViewHolder {
        TextView rank;
        TextView person;

        public PlayerHolder(@NonNull View itemView) {
            super(itemView);
            rank = itemView.findViewById(R.id.placeNumber);
            person = itemView.findViewById(R.id.textView8);

        }
    }


}
