package com.example.rainbowptt;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ale.infra.contact.Contact;
import com.ale.infra.contact.IRainbowContact;
import com.ale.infra.http.adapter.concurrent.RainbowServiceException;
import com.ale.infra.manager.conference.Conference;
import com.ale.infra.manager.conference.ConferenceParticipant;
import com.ale.infra.manager.conference.IConferenceProxy;
import com.ale.infra.manager.room.Room;
import com.ale.infra.proxy.room.IRoomProxy;
import com.ale.listener.IRainbowContactsSearchListener;
import com.ale.rainbow.RBLog;
import com.ale.rainbowsdk.RainbowSdk;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BubbleActivity extends AppCompatActivity {
    RecyclerView participants;
    EditText addparticipant;
    ImageButton back,add;
    Button push, end, addp;
    MyRecyclerViewAdapter adapter;
    List<ConferenceParticipant> participants_l = new ArrayList<>();
    List<String> participants_names = new ArrayList<>();
    String roomId;
    String jingleJid;
    Room myroom;
    static Room mroom;
    Conference conf;
    String name = "";


    List<String> participants_namesfinal = new ArrayList<>();
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bubble);
        roomId = getIntent().getStringExtra(ListOfBubblesActivity.EXTRA_MESSAGE);
        jingleJid = getIntent().getStringExtra(ListOfBubblesActivity.EXTRA_MESSAGEE);
        myroom = RainbowSdk.instance().bubbles().findBubbleById(roomId);
        mroom = myroom;
        push = findViewById(R.id.buttonPush);
        addp = findViewById(R.id.button6);
        end = findViewById(R.id.button5);
        back = findViewById(R.id.imageButton9);
        addparticipant = findViewById(R.id.addparticipant);
        addparticipant.setVisibility(View.INVISIBLE);
        addp.setVisibility(View.INVISIBLE);
        add = findViewById(R.id.addButton);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(BubbleActivity.this, ListOfBubblesActivity.class);
                startActivity(i);
            }
        });


        participants = findViewById(R.id.recyclerview);
        adapter = new MyRecyclerViewAdapter(this, participants_names);
        conf =  myroom.getConference();
        participants.setAdapter(adapter);
        participants.setLayoutManager(new LinearLayoutManager(this));
        updateParticipantsList();
        end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RainbowSdk.instance().bubbles().stopAudioConference(myroom, new IConferenceProxy.IStopAudioConfListener() {
                    @Override
                    public void onStopAudioConfSuccess() {
                        Intent i = new Intent(BubbleActivity.this, ListOfBubblesActivity.class);
                        startActivity(i);
                    }

                    @Override
                    public void onStopAudioConfFailed(IConferenceProxy.ConferenceError error) {

                    }
                });
            }
        });


        push.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RainbowSdk.instance().webRTC().makeConferenceCall(myroom.getConference().getId(), jingleJid);
            }
        });

        push.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                RainbowSdk.instance().bubbles().muteAllParticipants(myroom, true, new IConferenceProxy.IToggleMuteStateParticipantListener() {
                    @SuppressLint("ClickableViewAccessibility")
                    @Override
                    public void onToggleMuteStateParticipantSuccess() {

                    }

                    @Override
                    public void onToggleMuteStateParticipantFailed(IConferenceProxy.ConferenceError error) {

                    }
                });
                return true; //indicate we're done listening to this touch listener
            }
        });

        push.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        for (ConferenceParticipant pt : participants_l){
                            if (!pt.isMuted()){
                                pt.setMuted(true);
                            }
                        }
                        RainbowSdk.instance().bubbles().muteAllParticipants(myroom, true, new IConferenceProxy.IToggleMuteStateParticipantListener() {
                            @SuppressLint("ClickableViewAccessibility")
                            @Override
                            public void onToggleMuteStateParticipantSuccess() {
                            }

                            @Override
                            public void onToggleMuteStateParticipantFailed(IConferenceProxy.ConferenceError error) {

                            }
                        });
                        return true;
                }
                return false;
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addparticipant.setVisibility(View.VISIBLE);
                addp.setVisibility(View.VISIBLE);
            }
        });
        addp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addparticipant.getText().toString().trim().equals("") == false){
                    name = addparticipant.getText().toString().trim();
                    List<String> str_participants = new ArrayList<>();
                    str_participants.add(name);
                    RainbowSdk.instance().bubbles().inviteGuestsToBubble(myroom, str_participants, new IRoomProxy.IInviteToJoinRoom() {
                        @Override
                        public void onSuccess(@Nullable Map<String, String> invalidUsers) {
                            RBLog.warn("LOG_TAG", "TZAAAAAAAAAAAAAAAAAAAAAD");
                        }

                        @Override
                        public void onMaxUsersReached() {

                        }

                        @Override
                        public void onFailure(RainbowServiceException exception) {
                            RBLog.warn("LOG_TAG", "TZAAAAAAAAAAAAAAAAAAAAADCHHHHHHHHHHHHHHHHHHHHHH");
                        }
                    });
                    addparticipant.setVisibility(View.INVISIBLE);
                    addp.setVisibility(View.INVISIBLE);


                }
            }
        });


    }
    private void updateParticipantsList() {
        participants_l.clear();
        participants_names.clear();
        participants_l.addAll(conf.getAllParticipants());
        for (ConferenceParticipant pt : participants_l){
            participants_names.add(pt.getName());
        }
        adapter.notifyDataSetChanged();
    }
    public static void deleteContact(){
        String jID = "";
        List<ConferenceParticipant> searching = mroom.getConference().getAllParticipants();
        RBLog.warn("LOG_TAG", searching.toString());
        for (ConferenceParticipant pt : searching){
            if (pt.getName().equalsIgnoreCase(MyRecyclerViewAdapter.contact)){
                jID = pt.getUserId();
                RBLog.warn("LOG_TAG", "LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL" + jID);
            }
        }
        IRainbowContact cont = RainbowSdk.instance().contacts().getContactFromId(jID);
        RainbowSdk.instance().bubbles().deleteParticipantFromBubble(mroom, cont, false, new IRoomProxy.IDeleteParticipantListener() {
            @Override
            public void onDeleteParticipantSuccess(String roomId, String participantIdDeleted) {
                RBLog.warn("LOG_TAG", "SUCCEEEEESSSSSSSSSSSSSSSSSSSSSSSSSS");
            }

            @Override
            public void onDeleteParticipantFailure() {

            }
        });

    }


}
