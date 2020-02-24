package com.example.accelerationtest;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class NewGamesAdapter extends RecyclerView.Adapter<NewGamesAdapter.PlayerHolder> {
    List<GameData> gameData;
    Context context;

    public NewGamesAdapter(List<GameData> allData, Context context) {
        gameData = allData;
        this.context = context;


    }

    public void updateData(List<GameData> allData) {
        gameData = allData;

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PlayerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout li = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.multiple_games, parent, false);
        return new PlayerHolder(li);
    }

    @Override
    public void onBindViewHolder(@NonNull final PlayerHolder holder, int position) {
        final GameData gd = gameData.get(position);
        holder.gameCreator.setText(gd.getCreator());
        holder.gameName.setText(gd.getName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               AlertDialog.Builder builder=new AlertDialog.Builder(context);
                builder.setTitle(holder.gameName.getText());

                String st = "";
                for (String s : gd.online.keySet()) {
                    st += s + "\n";
                }
           
                builder.setMessage("Other Users in this game:\n"+st);
                builder.setPositiveButton("Join", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //TODO Add Join
                    }
                });
                AlertDialog dialog =builder.create();
                dialog.show();

            }
        });
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
        return gameData.size();
    }

    class PlayerHolder extends RecyclerView.ViewHolder {
        TextView gameName;
        TextView gameCreator;

        public PlayerHolder(@NonNull View itemView) {
            super(itemView);
            gameName = itemView.findViewById(R.id.gameName);
            gameCreator = itemView.findViewById(R.id.nameOfCreator);

        }
    }


}
