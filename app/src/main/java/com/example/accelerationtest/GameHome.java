package com.example.accelerationtest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

public class GameHome extends AppCompatActivity {

    RecyclerView rv;
    GameAdapter ga;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_home);
        rv=findViewById(R.id.viewTagPlayers);
        ga=new GameAdapter(null);
        rv.setAdapter(ga);
        rv.setLayoutManager(new LinearLayoutManager(this));
    }
}
