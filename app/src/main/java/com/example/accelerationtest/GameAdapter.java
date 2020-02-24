package com.example.accelerationtest;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;

public class GameAdapter extends RecyclerView.Adapter<GameAdapter.PlayerHolder> {
    GameData gameData;
    List<String> people;
    public GameAdapter(GameData allData){
        gameData=allData;
        people=new ArrayList<>();
        for(String name:gameData.isInGame.keySet()){

            if(gameData.isInGame.get(name)){
                people.add(name);

            }

        }

    }
    public void updateData(GameData allData){
        gameData=allData;
        for(String name:gameData.isInGame.keySet()){

            if(gameData.isInGame.get(name)){
                people.add(name);

            }

        }
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public PlayerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout li=(LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_people_view,parent,false);
        return new PlayerHolder(li);
    }

    @Override
    public void onBindViewHolder(@NonNull PlayerHolder holder, int position) {
        String name=people.get(position);

            if(gameData.isInGame.get(name)){
                holder.person.setText(name);
                if(gameData.online.get(name)){
                    holder.status.setImageResource(R.drawable.wifi);
                    holder.itemView.setBackgroundColor(Color.WHITE);
                }else{
                    holder.status.setImageResource(R.drawable.wifi);
                    holder.itemView.setBackgroundColor(Color.GRAY);
                }

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
        return people.size();
    }

    class PlayerHolder extends RecyclerView.ViewHolder{
        TextView person;
        ImageView status;

        public PlayerHolder(@NonNull View itemView) {
            super(itemView);
            person=itemView.findViewById(R.id.personName);
            status=itemView.findViewById(R.id.statusImage);

        }
    }



}
