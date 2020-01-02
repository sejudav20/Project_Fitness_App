package com.example.accelerationtest;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.se.omapi.SEService;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.ConnectionsClient;
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes;
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.google.android.gms.nearby.connection.DiscoveryOptions;
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;
import com.google.android.gms.nearby.connection.Strategy;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


public class NearbyCreator {
    public boolean haveConnections = false;
    private Context context;
    private String id;
    private Strategy strategy;
    private ArrayList<String> connections;

    //Creates a new NearbyCreator with an id that will be shared among all connections and a strategy
    public NearbyCreator(Context context, String id, Strategy strategy) throws PermissionDeniedException {
        this.context = context;
        this.id = id;
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            throw new PermissionDeniedException("ACCESS_COARSE_LOCATION");
        }
        connections = new ArrayList<>();
        this.strategy = strategy;
    }

    public boolean disconnect(String s) {
        if (connections.contains(s)) {
            connections.remove(s);
            Nearby.getConnectionsClient(context).disconnectFromEndpoint(s);
            return true;
        }
        return false;

    }

    //this method gets the original nearby class
    public ConnectionsClient getNearbyClient() {
        return Nearby.getConnectionsClient(context);
    }

    //stops trying to find new clients
    public void stopDiscovery() {

        Nearby.getConnectionsClient(context).stopDiscovery();

    }

    //stops trying to find new clients
    public void stopAdvertising() {

        Nearby.getConnectionsClient(context).stopAdvertising();

    }

    public boolean sendMessage(List<String> client, String message) {
        if (!connections.containsAll(client)) {
            return false;
        }
        Nearby.getConnectionsClient(context).sendPayload(client, Payload.fromBytes(message.getBytes()));
        return true;
    }

    public boolean sendMessage(String client, String message) {
        if (!connections.contains(client)) {
            return false;
        }
        Nearby.getConnectionsClient(context).sendPayload(client, Payload.fromBytes(message.getBytes()));
        return true;
    }

    public void startAdvertising(String name, final OptionsOfAdvertising optionsOfAdvertising) {
        startAdvertising(name, new OnAdvertisingTry() {
            @Override
            public void OnSuccess() {
                optionsOfAdvertising.OnDiscoverySuccess();
            }

            @Override
            public void OnFailure() {
                optionsOfAdvertising.OnDiscoveryFailure();
            }
        }, new ConnectionResult() {
            @Override
            public void ConnectionGood() {
                optionsOfAdvertising.OnConnectionGood();
            }

            @Override
            public void ConnectionRejected() {
                optionsOfAdvertising.OnConnectionRejected();
            }

            @Override
            public void ConnectionError() {
                optionsOfAdvertising.OnConnectionError();
            }

            @Override
            public void OnDisconnected() {
                optionsOfAdvertising.OnConnectionDisconnected();
            }
        }, new StringReceived() {
            @Override
            public void OnReceived(String s) {
                optionsOfAdvertising.OnStringReceived();
            }

            @Override
            public void OnUpdate() {
                optionsOfAdvertising.OnStringUpdate();
            }
        });

    }


    public void startAdvertising(String advertiserName, final OnAdvertisingTry options, final ConnectionResult connectionOptions, final StringReceived stringOptions) {

        final PayloadCallback payloadCallback = new PayloadCallback() {
            @Override
            public void onPayloadReceived(@NonNull String s, @NonNull Payload payload) {
                String es = new String(payload.asBytes());
                stringOptions.OnReceived(es);
            }

            @Override
            public void onPayloadTransferUpdate(@NonNull String s, @NonNull PayloadTransferUpdate payloadTransferUpdate) {
                stringOptions.OnUpdate();

            }
        };

        final ConnectionLifecycleCallback connectionLifecycleCallback = new ConnectionLifecycleCallback() {
            @Override
            public void onConnectionInitiated(@NonNull String s, @NonNull ConnectionInfo connectionInfo) {
                Nearby.getConnectionsClient(context).acceptConnection(s, payloadCallback);
            }

            @Override
            public void onConnectionResult(@NonNull String s, @NonNull ConnectionResolution result) {
                switch (result.getStatus().getStatusCode()) {
                    case ConnectionsStatusCodes.STATUS_OK:
                        // We're connected! Can now start sending and receiving data.
                        connections.add(s);
                        connectionOptions.ConnectionGood();
                        break;
                    case ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED:
                        // The connection was rejected by one or both sides.
                        connectionOptions.ConnectionRejected();
                        break;
                    case ConnectionsStatusCodes.STATUS_ERROR:
                        // The connection broke before it was able to be accepted.
                        connectionOptions.ConnectionError();
                        break;
                    default:
                        // Unknown status code
                }
            }

            @Override
            public void onDisconnected(@NonNull String s) {
                connections.remove(s);
                connectionOptions.OnDisconnected();
            }
        };


        AdvertisingOptions advertisingOptions = new AdvertisingOptions.Builder().setStrategy(strategy).build();
        Nearby.getConnectionsClient(context)
                .startAdvertising(advertiserName, id, connectionLifecycleCallback, advertisingOptions)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        options.OnSuccess();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                options.OnSuccess();
            }
        });

    }

    public void startDiscovery(String name, final OptionsOfDiscovery optionsOfDiscovery) {
        startDiscovery(name, new OnDiscoveryTry() {
            @Override
            public void OnSuccess() {
                optionsOfDiscovery.OnDiscoverySuccess();
            }

            @Override
            public void OnFailure() {
                optionsOfDiscovery.OnDiscoveryFailure();
            }
        }, new OnEndPointFound() {
            @Override
            public boolean Authenticated(@NonNull DiscoveredEndpointInfo discoveredEndpointInfo) {
                return optionsOfDiscovery.Authenticated(discoveredEndpointInfo);
            }

            @Override
            public void OnConnectionSuccess() {
                optionsOfDiscovery.OnConnectionSuccess();

            }

            @Override
            public void OnConnectionFailure() {
                optionsOfDiscovery.OnConnectionFailure();
            }

            @Override
            public void onConnectionLost() {
                optionsOfDiscovery.OnConnectionLost();
            }
        }, new ConnectionResult() {
            @Override
            public void ConnectionGood() {
                optionsOfDiscovery.OnConnectionGood();
            }

            @Override
            public void ConnectionRejected() {
                optionsOfDiscovery.OnConnectionRejected();
            }

            @Override
            public void ConnectionError() {
                optionsOfDiscovery.OnConnectionError();
            }

            @Override
            public void OnDisconnected() {
                optionsOfDiscovery.OnConnectionDisconnected();
            }
        }, new StringReceived() {
            @Override
            public void OnReceived(String s) {
                optionsOfDiscovery.OnStringReceived();
            }

            @Override
            public void OnUpdate() {
                optionsOfDiscovery.OnStringUpdate();
            }
        });

    }


    public void startDiscovery(final String name, final OnDiscoveryTry options, final OnEndPointFound endPointOption, final ConnectionResult connectionOptions, final StringReceived stringOptions) {


        final PayloadCallback payloadCallback = new PayloadCallback() {
            @Override
            public void onPayloadReceived(@NonNull String s, @NonNull Payload payload) {
                String es = new String(payload.asBytes());
                stringOptions.OnReceived(es);
            }

            @Override
            public void onPayloadTransferUpdate(@NonNull String s, @NonNull PayloadTransferUpdate payloadTransferUpdate) {
                stringOptions.OnUpdate();

            }
        };

        final ConnectionLifecycleCallback connectionLifecycleCallback = new ConnectionLifecycleCallback() {
            @Override
            public void onConnectionInitiated(@NonNull String s, @NonNull ConnectionInfo connectionInfo) {
                Nearby.getConnectionsClient(context).acceptConnection(s, payloadCallback);
            }

            @Override
            public void onConnectionResult(@NonNull String s, @NonNull ConnectionResolution result) {
                switch (result.getStatus().getStatusCode()) {
                    case ConnectionsStatusCodes.STATUS_OK:
                        // We're connected! Can now start sending and receiving data.
                        connectionOptions.ConnectionGood();
                        break;
                    case ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED:
                        // The connection was rejected by one or both sides.
                        connectionOptions.ConnectionRejected();
                        break;
                    case ConnectionsStatusCodes.STATUS_ERROR:
                        // The connection broke before it was able to be accepted.
                        connectionOptions.ConnectionError();
                        break;
                    default:
                        // Unknown status code
                }
            }

            @Override
            public void onDisconnected(@NonNull String s) {
                connectionOptions.OnDisconnected();
            }
        };


        EndpointDiscoveryCallback endpointDiscoveryCallback = new EndpointDiscoveryCallback() {
            @Override
            public void onEndpointFound(@NonNull String s, @NonNull final DiscoveredEndpointInfo discoveredEndpointInfo) {

                Nearby.getConnectionsClient(context)
                        .requestConnection(name, s, connectionLifecycleCallback)
                        .addOnSuccessListener(
                                new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        if (endPointOption.Authenticated(discoveredEndpointInfo)) {
                                            endPointOption.OnConnectionSuccess();
                                        }

                                    }
                                })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        endPointOption.OnConnectionFailure();
                                    }
                                });
            }

            @Override
            public void onEndpointLost(@NonNull String s) {
                endPointOption.onConnectionLost();
            }
        };


        DiscoveryOptions discoveryOptions = new DiscoveryOptions.Builder().setStrategy(strategy).build();
        Nearby.getConnectionsClient(context).startDiscovery(id, endpointDiscoveryCallback, discoveryOptions
        ).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                options.OnSuccess();
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                options.OnFailure();

            }
        });

    }

    private interface OptionsOfAdvertising {
        void OnDiscoverySuccess();

        void OnDiscoveryFailure();

        void OnStringReceived();

        void OnStringUpdate();

        void OnConnectionGood();

        void OnConnectionError();

        void OnConnectionRejected();

        void OnConnectionDisconnected();
    }


    //This interface is a combination of the ones below
    private interface OptionsOfDiscovery {
        void OnDiscoverySuccess();

        void OnDiscoveryFailure();

        void OnStringReceived();

        void OnStringUpdate();

        void OnConnectionGood();

        void OnConnectionError();

        void OnConnectionRejected();

        void OnConnectionDisconnected();

        boolean Authenticated(@NonNull DiscoveredEndpointInfo discoveredEndpointInfo);

        void OnConnectionSuccess();

        void OnConnectionFailure();

        //If connection is lost
        void OnConnectionLost();
    }


    //this interface is called when the app tries to start discovery
    private interface OnDiscoveryTry {
        void OnSuccess();

        void OnFailure();
    }

    //this interface is called when the app advertises
    private interface OnAdvertisingTry {
        void OnSuccess();

        void OnFailure();
    }

    // this is called when a string is sent
    private interface StringReceived {
        void OnReceived(String s);

        //When string is succesfully transfered
        void OnUpdate();

    }

    //called when a connection is tried
    private interface ConnectionResult {
        //connection is met and you can start sending
        void ConnectionGood();

        //Connection is rejected by one side
        void ConnectionRejected();

        //connection lost before acceptance
        void ConnectionError();

        //connection is lost after acceptance
        void OnDisconnected();
    }

    //This interface is called when another app is found that is advertising
    private interface OnEndPointFound {
        //A method that is called first to confirm a connection
        boolean Authenticated(@NonNull DiscoveredEndpointInfo discoveredEndpointInfo);

        void OnConnectionSuccess();

        void OnConnectionFailure();

        //If connection is lost
        void onConnectionLost();

    }

    //This error is thrown when permission is not given
    private class PermissionDeniedException extends Exception {
        public PermissionDeniedException(String permission) {
            super("The permission " + permission + " was not given. Check if it is a permission the user has to allow");
        }


    }


}
