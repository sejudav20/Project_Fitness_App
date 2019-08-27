package com.example.accelerationtest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toolbar;
import com.example.accelerationtest.ProfileFragment;
import com.example.accelerationtest.ChatFragment;
import com.example.accelerationtest.RecordsFragment;
import com.example.accelerationtest.HomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class MainActivity extends AppCompatActivity implements HomeFragment.OnFragmentInteractionListener,ChatFragment.OnFragmentInteractionListener,ProfileFragment.OnFragmentInteractionListener,RecordsFragment.OnFragmentInteractionListener {
    private ActionBar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        toolbar=getSupportActionBar();
        toolbar.setTitle("Home");

        loadFragment(new HomeFragment());

    }

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.MainFrame, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.homeActionButton:
                    toolbar.setTitle("Home");
                    loadFragment(new HomeFragment());
                    return true;
                case R.id.profileActionButton:
                    toolbar.setTitle("Profile");
                    loadFragment(new ProfileFragment());
                    return true;
                case R.id.chatActionButton:
                    toolbar.setTitle("Chat");
                    loadFragment(new ChatFragment());
                    return true;
                case R.id.recordsActionButton:
                    toolbar.setTitle("Records");
                    loadFragment(new RecordsFragment());
                    return true;
            }
            return false;
        }
    };






    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
