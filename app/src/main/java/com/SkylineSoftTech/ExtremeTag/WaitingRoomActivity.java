package com.SkylineSoftTech.ExtremeTag;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class WaitingRoomActivity extends AppCompatActivity {
    Button createGame;
    Button leaveGame;
    TextView ptv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_room);
        createGame = findViewById(R.id.StartGameButton);
        leaveGame = findViewById(R.id.leaveGameButton);
        ptv = findViewById(R.id.playerView);
        final String user=getSharedPreferences("userData",MODE_PRIVATE).getString("Username","guest");
     final  Intent i = getIntent();
        final boolean isCreator = i.getBooleanExtra("isCreator", false);

        if (!isCreator) {
            createGame.setVisibility(View.INVISIBLE);
        }
        final DatabaseReference dr = FirebaseDatabase.getInstance().getReference().child(i.getStringExtra("game")).push();
        dr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    Toast.makeText(WaitingRoomActivity.this, "The Host has left Game", Toast.LENGTH_LONG).show();
                    WaitingRoomActivity.this.startActivity(new Intent(WaitingRoomActivity.this, MainActivity.class));
                }
                Map<String, Boolean> names = (Map<String, Boolean>) dataSnapshot.child("online").getValue();
                String n = "";
                for (String name : names.keySet()) {
                    n += name + "\n";
                }
                ptv.setText("Joined:\n" + n);
                if (dataSnapshot.child("status").getValue().toString().equals("playing")) {
                    Intent e= new Intent(WaitingRoomActivity.this,GameHome.class);
                    e.putExtra("game",i.getStringExtra("game"));
                    WaitingRoomActivity.this.startActivity(e);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        leaveGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCreator) {
                    AlertDialog.Builder adb = new AlertDialog.Builder(WaitingRoomActivity.this);
                    adb.setTitle("Are you sure you want to Exit");
                    adb.setMessage("Exiting will delete the Game");
                    adb.setPositiveButton("End Game", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dr.removeValue();
                            WaitingRoomActivity.this.startActivity(new Intent(WaitingRoomActivity.this, MainActivity.class));
                        }
                    });
                } else {
                    dr.push().addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            HashMap<String, Boolean> online = (HashMap<String, Boolean>) dataSnapshot.child("online").getValue();
                            HashMap<String, Boolean> inGame = (HashMap<String, Boolean>) dataSnapshot.child("isInGame").getValue();
                            online.remove(user);
                            inGame.remove(user);
                            dr.child("online").setValue(online);
                             dr.child("isInGame").setValue(inGame);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    WaitingRoomActivity.this.startActivity(new Intent(WaitingRoomActivity.this, MainActivity.class));

                }
            }
        });

        createGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dr.child("status").setValue("playing");
                Intent e= new Intent(WaitingRoomActivity.this,GameHome.class);
                e.putExtra("game",i.getStringExtra("game"));
                WaitingRoomActivity.this.startActivity(e);
            }
        });

    }

}
