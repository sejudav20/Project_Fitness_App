package com.example.accelerationtest;

import android.content.Context;


import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;
import com.google.android.gms.nearby.messages.MessagesClient;

import java.util.ArrayList;

public class NearbyService {
    private Context context;
    private MessagesClient mc;
    private ArrayList<Message> messageList;
    private MessageListener messageListener;
    private NearbyListener nl;

    public NearbyService(Context context) {
        this.context = context;

    }

    public void setOnMessageListener(final NearbyListener nl) {
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
        Message o = new Message(str.getBytes());
        mc.publish(o);
        messageList.add(o);
    }

    public void endConnection() {
        for (Message message : messageList) {
            mc.unpublish(message);
        }

    }


}
