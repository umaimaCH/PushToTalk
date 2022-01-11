package com.example.rainbowptt;

import static com.ale.rainbowsdk.Infrastructure.instance;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.ale.listener.SigninResponseListener;
import com.ale.rainbowsdk.RainbowSdk;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String applicationId = "";
        String applicationSecret = "";
        String userId = "";
        String userSecret = "";
        instance().initialize(this, applicationId, applicationSecret);
        instance().connection().signin(userId, userSecret, "sandbox.openrainbow.com", new SigninResponseListener() {
            @Override
            public void onRequestFailed(RainbowSdk.ErrorCode errorCode, String err) {

            }

            @Override
            public void onSigninSucceeded() {
                Intent i = new Intent(LoginActivity.this,MainActivity.class);
                startActivity(i);

            }
        });


    }
}
