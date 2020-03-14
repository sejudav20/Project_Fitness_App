package com.SkylineSoftTech.ExtremeTag;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

public class LoadingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        Toast.makeText(this,"This Game requires Location to Work read ",Toast.LENGTH_LONG).show();

        NearbyCreator.getPermissionToUseNearby(this);
            if(!NearbyCreator.hasPermissionToUseNearby(this)){
                Dialog d=new Dialog(this);
                d.setTitle("Location Use Policy");
                d.setContentView(R.layout.loactionpolicy);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    this.finishAffinity();
                }else{
                    System.exit(0);
                }
            }else{
                //TODO Sam make this go to your login activity or use the activity to sign in automatically
                startActivity(new Intent(this,MainActivity.class));}

    }
}
