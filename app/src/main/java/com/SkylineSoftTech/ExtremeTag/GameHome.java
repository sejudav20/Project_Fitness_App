package com.SkylineSoftTech.ExtremeTag;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.google.android.gms.nearby.connection.Strategy;

import java.util.Map;
import java.util.Stack;

public class GameHome extends AppCompatActivity {

    RecyclerView rv;
    GameAdapter ga;
    AnimationDrawable ad;
    ImageView iv;
    CountDownTimer ct;
    ImageButton tag;
    ImageView wifiStatus;
    ImageView gameStatus;
    ProgressBar progressBar;
    ConnectivityManager cm;
    NearbyCreator nc;
    LiveData<GameData> gd;
    Stack<String> graveStack;
    /// when state=1 run mode| state 2 viewing| state 3 actively tagging| state 4 being tagged|
    // 5 potentially can
    // stage 6 out of wifi| state 7 when user clicked tag too many times
    int state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_home);
        rv = findViewById(R.id.viewTagPlayers);
        //initialize GD from data from the cloud

        ga = new GameAdapter(gd.getValue());
        rv.setAdapter(ga);
        rv.setLayoutManager(new LinearLayoutManager(this));
        gd.observe(this, new Observer<GameData>() {
            @Override
            public void onChanged(GameData gameData) {
                updateData();
            }
        });
        iv = findViewById(R.id.imageView2);
        iv.setBackgroundResource(R.drawable.go321);
        ad = (AnimationDrawable) iv.getBackground();
        gameStatus = findViewById(R.id.gameStatus);
        wifiStatus = findViewById(R.id.wifiStatus);
        tag = findViewById(R.id.tagButton);
        progressBar = findViewById(R.id.progressBar);
        cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        nc = new NearbyCreator(this, "com.example.ProjectFitness", Strategy.P2P_POINT_TO_POINT);

        tag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (state) {
                    case 1:
                        Toast.makeText(GameHome.this, "Can't tag until Run Time is over", Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        Toast.makeText(GameHome.this, "You are Out", Toast.LENGTH_SHORT).show();
                        break;
                    case 3:
                        Toast.makeText(GameHome.this, "Already Tagging", Toast.LENGTH_SHORT).show();
                        break;
                    case 4:
                        Toast.makeText(GameHome.this, "Can't Tag When being Tagged", Toast.LENGTH_SHORT).show();
                        break;
                    case 5:
                        //Actually able to tag
                        state=3;
                        nc.startAdvertising("userName",optionsOfAdvertising);

                        break;
                    case 6:
                        Toast.makeText(GameHome.this, "Can't tag until Wifi connection is back", Toast.LENGTH_SHORT).show();

                        break;
                    case 7:
                        Toast.makeText(GameHome.this, "Tag Time Out please wait 30 seconds", Toast.LENGTH_SHORT).show();

                        break;
                    default:
                        Toast.makeText(GameHome.this, "Error State", Toast.LENGTH_SHORT).show();


                }
            }
        });
    }
    public void updateData(){
        ga.updateData(gd.getValue());
        GameData updated=gd.getValue();
        if(graveStack==null){
            graveStack=new Stack<>();
        }
        Map<String,Boolean> map=gd.getValue().getIsInGame();
        for(String names:map.keySet()){
            if(!map.get(names)){
                if(!graveStack.contains(names)){
                    graveStack.push(names);
                }

            }
        }

        if(map.keySet().size()-1==graveStack.size()){
            AlertDialog.Builder ab= new AlertDialog.Builder(this);
            ab.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                  startActivity(new Intent(GameHome.this,MainActivity.class));

                }
            });
            View v=getLayoutInflater().inflate(R.layout.finalresultsscreen,null);
            ab.setView(v);
            RecyclerView rv=v.findViewById(R.id.rankingRecycler);
            String first="";
            for(String name:map.keySet()){
                if(map.get(first)){
                    first=name;
                    break;
                }
            }
            rv.setAdapter(new RankingAdapter(first,graveStack));
            ab.setTitle("Final Results");

            ab.create();
        }
    }
    public void updateCloudData() {
        //TODO get data from cloud and then update gd
    }

    public void changeWifiState(boolean online) {
        if (online) {
            wifiStatus.setImageResource(R.drawable.wifi);
            tag.setClickable(true);
            //TODO change online database state of user
            state = 5;
        } else {
            wifiStatus.setImageResource(R.drawable.wifi);
            tag.setClickable(true);
            state = 6;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (getIntent().getIntExtra("state", 1) == 1) {
            tag.setClickable(false);

            state = 1;
            gameStatus.setImageResource(R.drawable.run);
            ad.run();
            progressBar.setMax(60);
            progressBar.setProgress(60);
            iv.setVisibility(View.INVISIBLE);
            Toast.makeText(this, "Run away from opponents", Toast.LENGTH_LONG).show();
            ct = new CountDownTimer(57000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {


                    progressBar.incrementProgressBy(-1);
                }

                @Override
                public void onFinish() {
                    iv.setBackgroundResource(R.drawable.go_tag);
                    ((AnimationDrawable) iv.getBackground()).run();
                    tag.setClickable(true);
                    gameStatus.setImageResource(R.drawable.thumb_up);
                    state = 5;
                    nc.startDiscovery("userName",optionsOfDiscovery);
                }
            };
        }

        iv.setVisibility(View.INVISIBLE);

    }

    NearbyCreator.OptionsOfDiscovery optionsOfDiscovery = new NearbyCreator.OptionsOfDiscovery() {
        @Override
        public void OnDiscoverySuccess() {

        }

        @Override
        public void OnDiscoveryFailure(Exception e) {
            ///  something is missing

        }

        @Override
        public void OnStringReceived(String user, String s) {
            //Tagged by user
            Toast.makeText(GameHome.this,"You have been tagged by "+user,Toast.LENGTH_LONG).show();
            state=2;
            gameStatus.setImageResource(R.drawable.eye_view);
            gd.getValue().getIsInGame().put(user,false);
        }

        @Override
        public void OnStringUpdate() {

        }

        @Override
        public void OnConnectionGood(String s) {
            state=4;
            Toast.makeText(GameHome.this,"Run! someone is close",Toast.LENGTH_LONG).show();

        }

        @Override
        public void OnConnectionError() {
            state=5;
            Toast.makeText(GameHome.this,"Close Call! You escaped",Toast.LENGTH_LONG).show();

        }

        @Override
        public void OnConnectionRejected() {
            //something is wrong

            Toast.makeText(GameHome.this,"Close Call! You escaped tag",Toast.LENGTH_LONG).show();

        }

        @Override
        public void OnConnectionDisconnected() {
            state=5;
            Toast.makeText(GameHome.this,"Close Call! You escaped",Toast.LENGTH_LONG).show();

        }

        @Override
        public boolean Authenticated(@NonNull DiscoveredEndpointInfo discoveredEndpointInfo) {
            return false;
        }

        @Override
        public void OnConnectionSuccess() {

        }

        @Override
        public void OnConnectionFailure(Exception e) {
            e.printStackTrace();
        }

        @Override
        public void OnConnectionLost() {
            state=5;
            Toast.makeText(GameHome.this,"Close Call! You escaped",Toast.LENGTH_LONG).show();

        }
    };

    NearbyCreator.OptionsOfAdvertising optionsOfAdvertising= new NearbyCreator.OptionsOfAdvertising() {
        @Override
        public void OnDiscoverySuccess() {
            state=3;
            Toast.makeText(GameHome.this,"Finding people close by",Toast.LENGTH_LONG).show();
            ct=new CountDownTimer(15000,15000) {
                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    state=5;
                    Toast.makeText(GameHome.this,"No nearby Players found",Toast.LENGTH_LONG).show();

                    nc.stopDiscovery();
                }
            };
        }

        @Override
        public void OnDiscoveryFailure(Exception e) {
            e.printStackTrace();
        }

        @Override
        public void OnStringReceived(String user, String s) {

        }

        @Override
        public void OnStringUpdate() {

        }

        @Override
        public void OnConnectionGood(String s) {
           if(gd.getValue().getIsInGame().get(s)){
            ct.cancel();
                nc.sendMessage(s,"1");
           nc.stopAdvertising();
           nc.stopConnection(s);}else{
               state=5;
               Toast.makeText(GameHome.this,"Found User "+s+" who is out",Toast.LENGTH_LONG).show();
            nc.stopConnection(s);
            nc.stopAdvertising();
           }
        }

        @Override
        public void OnConnectionError() {
            state=5;
            Toast.makeText(GameHome.this,"Connection Lost so close",Toast.LENGTH_LONG).show();

            nc.stopAdvertising();
        }

        @Override
        public void OnConnectionRejected() {

        }

        @Override
        public void OnConnectionDisconnected() {
            state=5;
            Toast.makeText(GameHome.this,"Connection Lost so close",Toast.LENGTH_LONG).show();

            nc.stopAdvertising();
        }
    };


}
