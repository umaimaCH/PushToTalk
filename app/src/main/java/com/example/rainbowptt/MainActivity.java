package com.example.rainbowptt;

import static com.ale.rainbowsdk.Infrastructure.instance;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ale.infra.contact.IRainbowContact;
import com.ale.infra.manager.room.Room;
import com.ale.listener.SigninResponseListener;
import com.ale.rainbow.RBLog;
import com.ale.rainbowsdk.RainbowSdk;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private Button listOfBubbles_button,createBubble_button;
    private List<Room> roomList = new ArrayList<>();
    private List<IRainbowContact> contactList = new ArrayList<>();
    private IRainbowContact contact;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listOfBubbles_button = findViewById(R.id.button);
        createBubble_button = findViewById(R.id.button2);
        listOfBubbles_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, ListOfBubblesActivity.class);
                startActivity(i);
            }
        });
        createBubble_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this,CreateBubbleActivity.class);
                startActivity(i);

            }
        });

    }
}