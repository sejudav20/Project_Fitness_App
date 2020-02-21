package com.example.accelerationtest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.google.android.gms.nearby.connection.Strategy;

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
    /// when state=1 run mode state 2 viewing state 3 actively tagging state 4 being tagged 5 potentially can
    // stage 6 out of wifi state 7 when user clicked tag too many times
    int state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_home);
        rv = findViewById(R.id.viewTagPlayers);
        ga = new GameAdapter(null);
        rv.setAdapter(ga);
        rv.setLayoutManager(new LinearLayoutManager(this));
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

        }

        @Override
        public void OnStringReceived(String user, String s) {

        }

        @Override
        public void OnStringUpdate() {

        }

        @Override
        public void OnConnectionGood(String s) {

        }

        @Override
        public void OnConnectionError() {

        }

        @Override
        public void OnConnectionRejected() {

        }

        @Override
        public void OnConnectionDisconnected() {

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

        }

        @Override
        public void OnConnectionLost() {

        }
    };


}
