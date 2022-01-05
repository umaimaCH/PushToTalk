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
    ImageButton back, reload, delete,edit;
    static String text;
    ListView bubbles;
    ListView bubbless;
    public final static String EXTRA_MESSAGE = "MESSAGE";
    public final static String EXTRA_MESSAGEE = "MESSAGEE";



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


        bubbles = findViewById(R.id.listView);
        bubbless = findViewById(R.id.listVieww);
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
                    RBLog.warn("LOGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG_TAG", call.toString());
                }
            }

        }
        ArrayList<String> name = new ArrayList<>();
        for(Room room :rooms){
            name.add(room.getName());
        }
        name.removeAll(call);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(ListOfBubblesActivity.this,
                R.layout.listofbubbles_items, R.id.bubble_name, call){
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                View view = super.getView(position, convertView, parent);
                TextView tv = (TextView) view.findViewById(R.id.bubble_name);
                tv.setTextColor(Color.GREEN);
                return view;
            }
        };
        bubbles.setAdapter(adapter);
        bubbles.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        bubbles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                String roomposition = (String) parent.getAdapter().getItem(position);
                Room room= RainbowSdk.instance().bubbles().findBubblesByName(roomposition).get(0);
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
                                    intent.putExtra(EXTRA_MESSAGE, thisRoomName);
                                    intent.putExtra(EXTRA_MESSAGEE, jID);
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
                                    intent.putExtra(EXTRA_MESSAGE, thisRoomName);
                                    intent.putExtra(EXTRA_MESSAGEE, jID);
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
        ArrayAdapter<String> adapterr = new MyListViewAdapter(this, name);
        //adapterr.notifyDataSetInvalidated();
        bubbless.setAdapter(adapterr);
        bubbless.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        bubbless.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                String roomposition = (String) parent.getAdapter().getItem(position);
                Room room= RainbowSdk.instance().bubbles().findBubblesByName(roomposition).get(0);
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
                                    intent.putExtra(EXTRA_MESSAGE, thisRoomName);
                                    intent.putExtra(EXTRA_MESSAGEE, jID);
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
                                    intent.putExtra(EXTRA_MESSAGE, thisRoomName);
                                    intent.putExtra(EXTRA_MESSAGEE, jID);
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



        /*delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView txt = v.findViewById(R.id.bubble_name);
                String text = txt.toString().trim();
                Room roomyy = RainbowSdk.instance().bubbles().findBubblesByName(text).get(0);
                RainbowSdk.instance().bubbles().deleteBubble(roomyy, new IRoomProxy.IDeleteRoomListener() {
                    @Override
                    public void onRoomDeletedSuccess() {
                        RBLog.warn("LOGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG_TAG", "bubble deleted");
                    }

                    @Override
                    public void onRoomDeletedFailed() {
                        RBLog.warn("LOGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG_TAG", "nop");

                    }
                });
            }
        });*/


/*
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/


    }
    public boolean hasMicrophonePermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
    }

    public static void deleteBubble(){
        Room roomyy = RainbowSdk.instance().bubbles().findBubblesByName(MyListViewAdapter.txt).get(0);
        RainbowSdk.instance().bubbles().deleteBubble(roomyy, new IRoomProxy.IDeleteRoomListener() {
            @Override
            public void onRoomDeletedSuccess() {
                RBLog.warn("LOGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG_TAG", "bubble deleted");
            }

            @Override
            public void onRoomDeletedFailed() {
                RBLog.warn("LOGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG_TAG", "nop");

            }
        });

    }

    public static void editBubble() {
        String neew = MyListViewAdapter.nameN;
        String old = MyListViewAdapter.ancienN;
        Room roomyy = RainbowSdk.instance().bubbles().findBubblesByName(old).get(0);
        RainbowSdk.instance().bubbles().changeBubbleData(roomyy,neew,roomyy.getTopic(),true,true,true, roomyy.getAutoRegister(), new IRoomProxy.IChangeRoomDataListener() {

            @Override
            public void onChangeRoomDataSuccess(Room room) {

            }

            @Override
            public void onChangeRoomDataFailed(String roomId) {

            }
        });

    }


}
