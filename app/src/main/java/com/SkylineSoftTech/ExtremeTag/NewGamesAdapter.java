package com.SkylineSoftTech.ExtremeTag;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.ColorSpace;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.Query;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class NewGamesAdapter extends FirebaseRecyclerAdapter<GameData,NewGamesAdapter.PlayerHolder> {
    Query gameData;
    Context context;
    String name;

    public NewGamesAdapter(Query query, Context context,FirebaseRecyclerOptions<GameData> options,String name) {
        super(options);
        gameData = query;
        this.context = context;
        this.name=name;

    }

    public void updateData(Query q) {
        gameData = q;

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PlayerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout li = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.multiple_games, parent, false);
        return new PlayerHolder(li);
    }

    @Override
    public void onBindViewHolder(@NonNull final PlayerHolder holder, int position, final GameData gd) {

        holder.gameCreator.setText(gd.getCreator());
        holder.gameName.setText(gd.getName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(gd.gameStatus.equals("join")) {
                   AlertDialog.Builder builder = new AlertDialog.Builder(context);
                   builder.setTitle(holder.gameName.getText());

                   String st = "";
                   for (String s : gd.online.keySet()) {
                       st += s + "\n";
                   }

                   builder.setMessage("Other Users in this game:\n" + st);
                   builder.setPositiveButton("Join", new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialog, int which) {
                           gd.online.put("name",true);
                           gd.isInGame.put("name",true);
                           Intent i=new Intent(context,WaitingRoomActivity.class);
                           i.putExtra("isCreator",false);
                           i.putExtra("game",gd.getName());
                           context.startActivity(i);

                       }
                   });
                   AlertDialog dialog = builder.create();
                   dialog.show();
               }else{
                   Toast.makeText(context,"The game is already underway",Toast.LENGTH_SHORT).show();
               }
            }
        });
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
