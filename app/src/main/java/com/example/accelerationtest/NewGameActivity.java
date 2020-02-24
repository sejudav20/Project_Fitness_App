package com.example.accelerationtest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.List;

public class NewGameActivity extends AppCompatActivity {
RecyclerView rv;
Button createGame;
NewGamesAdapter nga;
LiveData<List<GameData>> liveData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game);
        rv=findViewById(R.id.recyclerViewGames);
        createGame=findViewById(R.id.createGameNew);
        createGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
