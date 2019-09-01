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


    /***
     * NearbyService provides the basic structure for communications between nearby devices.
     * Nearby: A google api that communicates between devices through a combination of bluetooth,wifi and ultrasound
     * This does not need wifi but just user permission
     * When the method callPermisionPrompt is called the app will display a popup asking for the user's permission
     * the programmer must be explicit about what is being shared and how. It is recommended to use nearby only with the user's control
     * Nearby takes up three times the battery life a regular activity does so make sure  to turn it off.
     *
     * Methods:
     *  public void callPermissionPrompt(): calls the permission prompt with a broadcasted message
     *
     *  setOnMessageListener(final NearbyListener nl, int distance): Please set this up if you want to receive messages
     *  as well. NearbyListener is a class interface that contains onMessageReceived and onLost (meaning the message was not receieved but went out of range)
     *  distance can be set using the class constants such as Distance_Close: a distance of 5 m or Distance_far which is 30 m
     *
     *  public void publishMessage(String str, int distance) any app whithin the specified location will get a message
     *  #privacy matters
     *
     *  public void endConnection() ends broadcast of messages and no app can see them. Use this often to save battery if nearby is not in use
     *  also use this in the stop method of an activity for the same purpose
     *
     *  Be aware this takes up battery life!!!
     *
     * **/


    private Context context;
    private MessagesClient mc;
    private ArrayList<Message> messageList;
    private MessageListener messageListener;
    private NearbyListener nl;
    public int DISTANCE_CLOSE = 1;
    public int DISTANCE_FAR = 0;


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

    public void setOnMessageListener(final NearbyListener nl, int distance) {
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
        if (distance == 1) {
            Strategy bl = new Strategy.Builder().setDistanceType(Strategy.DISTANCE_TYPE_EARSHOT).setDiscoveryMode(Strategy.DISCOVERY_MODE_BROADCAST).build();
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
        if (distance == 1) {

            Strategy bl = new Strategy.Builder().setDistanceType(Strategy.DISTANCE_TYPE_EARSHOT).setDiscoveryMode(Strategy.DISCOVERY_MODE_BROADCAST).build();
            mc.publish(o, new PublishOptions.Builder().setStrategy(bl).build());
        }

    }

    public void unsubscribe() {
        mc.unsubscribe(messageListener);
    }

    public void endConnection() {
        for (Message message : messageList) {
            mc.unpublish(message);
        }
        mc.unsubscribe(messageListener);

    }


}
