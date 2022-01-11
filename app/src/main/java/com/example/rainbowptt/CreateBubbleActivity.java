package com.example.rainbowptt;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ale.infra.http.adapter.concurrent.RainbowServiceException;
import com.ale.infra.manager.room.Room;
import com.ale.infra.proxy.room.IRoomProxy;
import com.ale.rainbowsdk.RainbowSdk;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CreateBubbleActivity extends AppCompatActivity {
    Button buttonSubmit, buttonAdd, buttonCancel;
    ListView emailsList;
    EditText nameBubble,description,participants;
    String str_name="";
    String str_description="";
    List<String> str_participants = new ArrayList<>();
    int i = 0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createbubble);
        nameBubble = findViewById(R.id.editTextTextPersonName);
        buttonCancel = findViewById(R.id.buttonCancel);
        description = findViewById(R.id.editTextTextPersonName2);
        participants = findViewById(R.id.editTextTextPersonName3);
        emailsList = findViewById(R.id.listViewEmails);
        buttonSubmit = findViewById(R.id.button3);
        buttonAdd = findViewById(R.id.button4);

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(CreateBubbleActivity.this,MainActivity.class);
                startActivity(i);
            }
        });

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (participants.getText().toString().trim().equals("") == false){
                    String str_participant = participants.getText().toString().trim();
                    str_participants.add(str_participant);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(CreateBubbleActivity.this,
                            android.R.layout.simple_list_item_1, str_participants);
                    emailsList.setAdapter(adapter);
                    buttonSubmit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (nameBubble.getText().toString().trim().equals("") == false
                                    && description.getText().toString().trim().equals("") == false
                                    && participants.getText().toString().trim().equals("") == false) {
                                str_name = nameBubble.getText().toString().trim();
                                str_description = description.getText().toString().trim();
                                RainbowSdk.instance().bubbles().createBubble(str_name, str_description, true, true, true, new IRoomProxy.IRoomCreationListener() {
                                    @Override
                                    public void onCreationSuccess(Room room) {
                                        RainbowSdk.instance().bubbles().inviteGuestsToBubble(room, str_participants, new IRoomProxy.IInviteToJoinRoom() {
                                            @Override
                                            public void onSuccess(@Nullable Map<String, String> invalidUsers) {
                                                Intent i = new Intent(CreateBubbleActivity.this, ListOfBubblesActivity.class);
                                                startActivity(i);
                                            }

                                            @Override
                                            public void onMaxUsersReached() {

                                            }

                                            @Override
                                            public void onFailure(RainbowServiceException exception) {

                                            }
                                        });
                                    }


                                    @Override
                                    public void onCreationFailed(IRoomProxy.RoomCreationError error) {
                                    }
                                });

                            }


                        }
                    });

                }


            }
        });




    }
}
