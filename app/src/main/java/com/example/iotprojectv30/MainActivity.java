package com.example.iotprojectv30;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity {
    //private TextView txv_rgb;
    //private TextView txv_light;
    private TextView txv_proximity;
    private Button btn_color;
    private MqttAndroidClient client;
    private static final String SERVER_URI = "tcp://test.mosquitto.org:1883";
    private static final String TAG = "MainActivity";

    ArrayList<Integer> collectionofNumbers = new ArrayList<>(10);



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //txv_rgb = findViewById(R.id.txv_rgbValue);
        //txv_light = findViewById(R.id.txv_lightValue);
        txv_proximity = findViewById(R.id.txv_proximityValue);
        btn_color = findViewById(R.id.btnColor);


        btn_color.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                openMainActivity2();
            }
        });

        connect();

        client.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
                subscribe("otter");
                //subscribe("otter2");

                if (reconnect) {
                    System.out.println("Reconnected to : " + serverURI);
                    // Re-subscribe as we lost it due to new session
                    subscribe("otter");
                //    subscribe("otter2");

                } else {
                    System.out.println("Connected to: " + serverURI);
                    subscribe("otter");
                //    subscribe("otter2");

                }
            }
            @Override
            public void connectionLost(Throwable cause) {
                System.out.println("The Connection was lost.");
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws
                    Exception {
                String newMessage = new String(message.getPayload());
                System.out.println("Incoming message: " + newMessage);

                /* add code here to interact with elements (text views, buttons) using data from newMessage */

                String count;
                count = newMessage;
                int myNum = Integer.parseInt(count);

                    /* Do something */

                    /* Do something */

                while(myNum > 3000 && myNum < 3500 ){

                    collectionofNumbers.remove(1);
                    txv_proximity.setText("Attendees inside = " + collectionofNumbers.size());
                    System.out.println(collectionofNumbers);
                    System.out.println("Incoming message: " + newMessage + "second otter is here");
                    break;
                }

                while(myNum < 2700 && myNum > 2500){

                    collectionofNumbers.add(myNum);

                    System.out.println(collectionofNumbers);

                    txv_proximity.setText("Attendees inside = " + collectionofNumbers.size());
                    System.out.println("Incoming message: " + newMessage + "             first otter is here");
                    break;
                }
            }


                //collectionofNumbers.clear();



            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
            }
        });
    }

    public void openMainActivity2(){
        Intent intent = new Intent(this, MainActivity2.class);
        startActivity(intent);

    }




    private void connect(){
        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(this.getApplicationContext(), SERVER_URI,
                        clientId);
        try {
            IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Log.d(TAG, "onSuccess");
                    System.out.println(TAG + " Success. Connected to " + SERVER_URI);
                }
                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception)
                {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Log.d(TAG, "onFailure");
                    System.out.println(TAG + " Oh no! Failed to connect to " +
                            SERVER_URI);
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void subscribe(String otter) {
        final String topic = otter;


        int qos = 1;
        try {
            IMqttToken subToken = client.subscribe(topic, qos);
            subToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    System.out.println("Subscription successful to topic: " + topic);
                }
                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                    System.out.println("Failed to subscribe to topic: " + topic);
                    // The subscription could not be performed, maybe the user was not
                    // authorized to subscribe on the specified topic e.g. using wildcards
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }



}