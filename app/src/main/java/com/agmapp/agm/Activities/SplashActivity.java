package com.agmapp.agm.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.agmapp.agm.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DatabaseReference;

public class SplashActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private SharedPreferences mpref;
    private static final String AGM_PREF = "agm.conf";
    private String userty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mpref = getSharedPreferences(AGM_PREF, Context.MODE_PRIVATE);
        Thread timer = new Thread(){
            @Override
            public void run() {
                try {
                    sleep(4000);
                } catch (InterruptedException e) {
                    FirebaseCrash.log("Splash activity crash: " + e.toString());
                } finally {
                    if (mUser != null){
                        //home activity
                        userty = mpref.getString("user_type","");
                        if (userty.contains("sudo"))
                        {
                            Intent intent = new Intent(SplashActivity.this, AdminActivity.class);
                            startActivity(intent);
                        }
                        else if (userty.contains("member"))
                        {
                            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                        else
                        {
                            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }
                    }
                    else{
                        //login activity
                        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                }
            }
        };timer.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
