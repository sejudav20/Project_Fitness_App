package com.SkylineSoftTech.ExtremeTag;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewGameActivity extends AppCompatActivity {
RecyclerView rv;
Button createGame;
NewGamesAdapter nga;
DatabaseReference games;
LiveData<List<GameData>> liveData;
String user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game);
        rv=findViewById(R.id.recyclerViewGames);
        createGame=findViewById(R.id.createGameNew);
        games= FirebaseDatabase.getInstance().getReference().child("games").push();
        user= getSharedPreferences("userData",MODE_PRIVATE).getString("Username","guest");
        createGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder adb= new AlertDialog.Builder(NewGameActivity.this);
                adb.setTitle("New Game");
                adb.setNegativeButton("Cancel",null);
                View aView=getLayoutInflater().inflate(R.layout.create_new_game_alert_dialog,null);
                final EditText edt=aView.findViewById(R.id.editNewName);
                adb.setView(aView);

                adb.setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                       final GameData gd= new GameData(Calendar.getInstance().getTime().toString(),user,edt.getText().toString(),false,new HashMap<String, Boolean>(),new HashMap<String, Boolean>(),"join");

                        games.child(gd.name).setValue(gd);
                        gd.setGameStatus("join");
                        Intent ed=new Intent(NewGameActivity.this,WaitingRoomActivity.class);
                        ed.putExtra("isCreator",true);
                        ed.putExtra("game",gd.getName());
                        NewGameActivity.this.startActivity( ed);

                    }
                });
                adb.create();
            }
        });

        Query query = FirebaseDatabase.getInstance()
                .getReference().orderByChild("games");
        final FirebaseRecyclerOptions<GameData> options =
                new FirebaseRecyclerOptions.Builder<GameData>()
                        .setQuery(query, new SnapshotParser<GameData>() {
                            @NonNull
                            @Override
                            public GameData parseSnapshot(@NonNull DataSnapshot snapshot) {
                                return new GameData(snapshot.child("d").getValue().toString(),snapshot.child("creator").getValue().toString(),snapshot.child("name").getValue().toString(),
                                        (Boolean) snapshot.child("isGameDone").getValue(),(Map<String, Boolean>) snapshot.child("online").getValue(),(Map<String, Boolean>) snapshot.child("isInGame").getValue(),
                                         snapshot.child("gameStatus").getValue().toString());
                            }
                        })
                        .build();
        nga=new NewGamesAdapter(query,this,options,user);

        rv.setAdapter(nga);
        rv.setLayoutManager(new LinearLayoutManager(this));
        nga.startListening();

    }

    @Override
    protected void onStop() {
        super.onStop();
        nga.stopListening();
    }
}
