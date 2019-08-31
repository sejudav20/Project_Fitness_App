package com.example.accelerationtest;

import android.content.Context;


import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;
import com.google.android.gms.nearby.messages.MessagesClient;
import com.google.android.gms.nearby.messages.PublishOptions;
import com.google.android.gms.nearby.messages.Strategy;
import com.google.android.gms.nearby.messages.SubscribeOptions;

import java.util.ArrayList;

public class NearbyService {
    private Context context;
    private MessagesClient mc;
    private ArrayList<Message> messageList;
    private MessageListener messageListener;
    private NearbyListener nl;
    public int DISTANCE_CLOSE=1;
    public int DISTANCE_FAR=0;


    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    private String gameId = "";

    public NearbyService(Context context) {
        this.context = context;

    }

    public void setOnMessageListener(final NearbyListener nl,int distance) {
        this.nl = nl;
        messageListener = new MessageListener() {
            @Override
            public void onFound(Message message) {
                nl.onMessageReceived(message.getType());
            }

            @Override
            public void onLost(Message message) {
                nl.onLost();
            }
        };
        SubscribeOptions so;
        if(distance==1){
            Strategy bl=new Strategy.Builder().setDistanceType(Strategy.DISTANCE_TYPE_EARSHOT).setDiscoveryMode(Strategy.DISCOVERY_MODE_BROADCAST).build();
            mc.subscribe(messageListener, new SubscribeOptions.Builder().setStrategy(bl).build());
        }




    }

    public interface NearbyListener {
        public void onMessageReceived(String message);

        public void onLost();


    }

    public void callPermissionPrompt() {
        this.mc = Nearby.getMessagesClient(context);
        mc.publish(new Message("#285".getBytes()));

    }

    public void publishMessage(String str) {
        Message o = new Message((gameId + str).getBytes());
        mc.publish(o);
        messageList.add(o);
    }

    public void publishMessage(String str, int distance) {
        Message o = new Message((gameId + str).getBytes());

        messageList.add(o);
        if(distance==1){

            Strategy bl=new Strategy.Builder().setDistanceType(Strategy.DISTANCE_TYPE_EARSHOT).setDiscoveryMode(Strategy.DISCOVERY_MODE_BROADCAST).build();
            mc.publish(o,new PublishOptions.Builder().setStrategy(bl).build());
        }

    }

    public void endConnection() {
        for (Message message : messageList) {
            mc.unpublish(message);
        }
        mc.unsubscribe(messageListener);

    }


}
