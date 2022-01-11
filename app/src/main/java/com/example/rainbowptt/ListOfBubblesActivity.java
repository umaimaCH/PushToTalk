package com.example.rainbowptt;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.ale.infra.manager.conference.IConferenceProxy;
import com.ale.infra.manager.room.Room;
import com.ale.infra.manager.room.RoomParticipant;
import com.ale.infra.proxy.room.IRoomProxy;
import com.ale.rainbow.RBLog;
import com.ale.rainbowsdk.RainbowSdk;

import java.util.ArrayList;
import java.util.List;



public class ListOfBubblesActivity extends Activity {
    ImageButton back, reload, edit;
    ListView activeBubbles;
    ListView nonActiveBubbles;
    public final static String EXTRA_MESSAGE_Name = "MESSAGE";
    public final static String EXTRA_MESSAGEE_JID = "MESSAGEE";



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listofbubbles);
        back = findViewById(R.id.imageButton2);
        edit = findViewById(R.id.edit);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ListOfBubblesActivity.this, MainActivity.class);
                startActivity(i);
            }
        });
        reload = findViewById(R.id.imageButton4);
        reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ListOfBubblesActivity.this, ListOfBubblesActivity.class);
                startActivity(i);
            }
        });


        activeBubbles = findViewById(R.id.listView);
        nonActiveBubbles = findViewById(R.id.listVieww);

        ArrayList<Room> pendings = RainbowSdk.instance().bubbles().getPendingList();
        if (pendings.size() != 0) {
            for(Room pending : pendings){
                RainbowSdk.instance().bubbles().acceptInvitation(pending, new IRoomProxy.IChangeUserRoomDataListener() {
                    @Override
                    public void onChangeUserRoomDataSuccess(RoomParticipant roomParticipant) {
                    }

                    @Override
                    public void onChangeUserRoomDataFailed() {
                    }
                });
            }
        }

        List<Room> rooms = RainbowSdk.instance().bubbles().getAllList();
        List<String> call = new ArrayList<>();
        for(Room room :rooms){
            if(room.getConference() != null){
                if (room.getConference().isConfActive()){
                    call.add(room.getName());
                }
            }

        }
        ArrayList<String> name = new ArrayList<>();
        for(Room room :rooms){
            name.add(room.getName());
        }
        name.removeAll(call);
        ArrayAdapter<String> adapterAB = new ArrayAdapter<String>(ListOfBubblesActivity.this,
                R.layout.listofbubbles_items, R.id.bubble_name, call){
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                View view = super.getView(position, convertView, parent);
                TextView tv = (TextView) view.findViewById(R.id.bubble_name);
                tv.setTextColor(Color.GREEN);
                return view;
            }
        };
        activeBubbles.setAdapter(adapterAB);
        activeBubbles.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        activeBubbles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                String roomPosition = (String) parent.getAdapter().getItem(position);
                Room room= RainbowSdk.instance().bubbles().findBubblesByName(roomPosition).get(0);
                String thisRoomName = room.getId();
                boolean hasMicrophonePermission = hasMicrophonePermission();
                if(room.isUserOwner()){
                    if ( hasMicrophonePermission ) {
                        RainbowSdk.instance().bubbles().startAndJoinConference(room, new IConferenceProxy.IJoinAudioCallListener() {
                            @Override
                            public void onJoinAudioCallSuccess(String jingleJid) {
                                if (room.getConference() != null) {
                                    //RainbowSdk.instance().webRTC().makeConferenceCall(room.getConference().getId(), jingleJid);
                                    String jID = jingleJid;
                                    Intent intent = new Intent(ListOfBubblesActivity.this, BubbleActivity.class);
                                    intent.putExtra(EXTRA_MESSAGE_Name, thisRoomName);
                                    intent.putExtra(EXTRA_MESSAGEE_JID, jID);
                                    startActivity(intent);
                                }

                            }
                            @Override
                            public void onJoinAudioCallFailed(IConferenceProxy.ConferenceError error) {
                                // do something
                            }
                        });
                    }
                    else {
                        ActivityCompat.requestPermissions(ListOfBubblesActivity.this,
                                new String[]{Manifest.permission.RECORD_AUDIO},
                                1);
                    }

                }
                else {

                    if ( hasMicrophonePermission ) {
                        RainbowSdk.instance().bubbles().joinAudioConference(room, null, new IConferenceProxy.IJoinAudioCallListener() {
                            @Override
                            public void onJoinAudioCallSuccess(String jingleJid) {
                                if (room.getConference() != null) {
                                    String jID = jingleJid;
                                    Intent intent = new Intent(ListOfBubblesActivity.this, BubbleActivity.class);
                                    intent.putExtra(EXTRA_MESSAGE_Name, thisRoomName);
                                    intent.putExtra(EXTRA_MESSAGEE_JID, jID);
                                    startActivity(intent);
                                }
                            }

                            @Override
                            public void onJoinAudioCallFailed(IConferenceProxy.ConferenceError error) {
                            }
                        });
                    }
                    else {
                        ActivityCompat.requestPermissions(ListOfBubblesActivity.this,
                                new String[]{Manifest.permission.RECORD_AUDIO},
                                1);
                    }

                }


            }
        });
        ArrayAdapter<String> adapterB = new MyListViewAdapter(this, name);
        nonActiveBubbles.setAdapter(adapterB);
        nonActiveBubbles.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        nonActiveBubbles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                String roomPosition = (String) parent.getAdapter().getItem(position);
                Room room= RainbowSdk.instance().bubbles().findBubblesByName(roomPosition).get(0);
                String thisRoomName = room.getId();
                boolean hasMicrophonePermission = hasMicrophonePermission();
                if(room.isUserOwner()){
                    if ( hasMicrophonePermission ) {
                        RainbowSdk.instance().bubbles().startAndJoinConference(room, new IConferenceProxy.IJoinAudioCallListener() {
                            @Override
                            public void onJoinAudioCallSuccess(String jingleJid) {
                                if (room.getConference() != null) {
                                    String jID = jingleJid;
                                    Intent intent = new Intent(ListOfBubblesActivity.this, BubbleActivity.class);
                                    intent.putExtra( EXTRA_MESSAGE_Name, thisRoomName);
                                    intent.putExtra(EXTRA_MESSAGEE_JID, jID);
                                    startActivity(intent);
                                }

                            }
                            @Override
                            public void onJoinAudioCallFailed(IConferenceProxy.ConferenceError error) {
                            }
                        });
                    }
                    else {
                        ActivityCompat.requestPermissions(ListOfBubblesActivity.this,
                                new String[]{Manifest.permission.RECORD_AUDIO},
                                1);
                    }

                }
                else {

                    if ( hasMicrophonePermission ) {
                        RainbowSdk.instance().bubbles().joinAudioConference(room, null, new IConferenceProxy.IJoinAudioCallListener() {
                            @Override
                            public void onJoinAudioCallSuccess(String jingleJid) {
                                if (room.getConference() != null) {
                                    String jID = jingleJid;
                                    Intent intent = new Intent(ListOfBubblesActivity.this, BubbleActivity.class);
                                    intent.putExtra( EXTRA_MESSAGE_Name, thisRoomName);
                                    intent.putExtra(EXTRA_MESSAGEE_JID, jID);
                                    startActivity(intent);
                                }
                            }

                            @Override
                            public void onJoinAudioCallFailed(IConferenceProxy.ConferenceError error) {
                            }
                        });
                    }
                    else {
                        ActivityCompat.requestPermissions(ListOfBubblesActivity.this,
                                new String[]{Manifest.permission.RECORD_AUDIO},
                                1);
                    }

                }


            }
        });

    }
    public boolean hasMicrophonePermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
    }

    public static void deleteBubble(){
        Room myRoom = RainbowSdk.instance().bubbles().findBubblesByName(MyListViewAdapter.txt).get(0);
        RainbowSdk.instance().bubbles().deleteBubble(myRoom, new IRoomProxy.IDeleteRoomListener() {
            @Override
            public void onRoomDeletedSuccess() {
            }

            @Override
            public void onRoomDeletedFailed() {

            }
        });

    }

    public static void editBubble() {
        String newName = MyListViewAdapter.nameN;
        String oldName = MyListViewAdapter.ancienN;
        Room myRoom = RainbowSdk.instance().bubbles().findBubblesByName(oldName).get(0);
        RainbowSdk.instance().bubbles().changeBubbleData(myRoom,newName,myRoom.getTopic(),true,true,true,myRoom.getAutoRegister(), new IRoomProxy.IChangeRoomDataListener() {

            @Override
            public void onChangeRoomDataSuccess(Room room) {
            }

            @Override
            public void onChangeRoomDataFailed(String roomId) {
            }
        });

    }


}
