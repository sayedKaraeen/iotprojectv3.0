package com.example.iotprojectv30;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;

public class MainActivity2 extends AppCompatActivity {

    TextView txv_temp_indoor = null;
    TextView txv_humid_indoor = null;
    Button btnUpdateTemp = null;
    String temprature = "";
    String humidity = "";


    public void run (String command) {
        String hostname = "iotpi17.dsv.su.se";
        String username = "pi";
        String password = "IoT@2021";
        StringBuilder output = new StringBuilder();


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        try
        {
            Connection conn = new Connection(hostname); //init connection
            conn.connect(); //start connection to the hostname
            boolean isAuthenticated = conn.authenticateWithPassword(username, password);

            if (!isAuthenticated)
                throw new IOException("Authentication failed.");
            Session sess = conn.openSession();
            sess.execCommand(command);
            InputStream stdout = new StreamGobbler(sess.getStdout());
            BufferedReader br = new BufferedReader(new InputStreamReader(stdout));

            String line = br.readLine();
            output.append(line);
            temprature = line;

            String line2 = br.readLine();
            output.append(line);
            humidity = line2;

            /* Show exit status, if available (otherwise "null") */
            System.out.println("ExitCode: " + sess.getExitStatus());
            sess.close(); // Close this session
            conn.close();
        }
        catch (IOException e)
        { e.printStackTrace(System.err);
            System.exit(2); }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        txv_temp_indoor = (TextView) findViewById(R.id.indoorTempShow);
        txv_temp_indoor.setText("the fetched indoor temp value");

        txv_humid_indoor = (TextView) findViewById(R.id.indoorHumidShow);
        txv_humid_indoor.setText("the fetched indoor humid value");

        btnUpdateTemp = (Button) findViewById(R.id.btnUpdateTemp);

        //button listener
        btnUpdateTemp.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("StaticFieldLeak")
            public void onClick(View v) {
                // Add code to execute on click
                new AsyncTask<Integer, Void, Void>() {
                    @Override
                    protected Void doInBackground(Integer... params) {
                        // Add code to fetch data via SSH
                        run("python listsensorsandvalues.py");
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void v) {
                        // Add code to preform actions after doInBackground
                        txv_temp_indoor.setText(temprature);
                        txv_humid_indoor.setText(humidity);

                    }
                }.execute(1);
            }
        });
    }
}