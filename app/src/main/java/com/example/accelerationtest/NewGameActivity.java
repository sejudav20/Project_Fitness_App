package com.example.accelerationtest;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class NewGameActivity extends AppCompatActivity {
RecyclerView rv;
Button createGame;
NewGamesAdapter nga;
LiveData<List<GameData>> liveData;
String user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game);
        rv=findViewById(R.id.recyclerViewGames);
        createGame=findViewById(R.id.createGameNew);
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
                        GameData gd= new GameData(Calendar.getInstance().getTime(),user,edt.getText().toString(),false,new HashMap<String, Boolean>(),new HashMap<String, Boolean>(),"join");
                             //TODO add wait until enough players were added


                    }
                });
                adb.create();
            }
        });
        //TODO set live data value from cloud
        liveData.observe(this, new Observer<List<GameData>>() {
            @Override
            public void onChanged(List<GameData> gameData) {
                nga.updateData(gameData);
            }
        });
        nga=new NewGamesAdapter(liveData.getValue(),this);
        rv.setAdapter(nga);
        rv.setLayoutManager(new LinearLayoutManager(this));


    }
}
