package com.depromeet.hangang;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends Activity {
    DownThread mThread;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String serverAddr = "http://hangang.dkserver.wo.tc";
        // "http://hangang.dkserver.wo.tc"
        // {"result":"true","temp":"4.7","time":"2017-01-03 19:52:19"}
        mThread = new DownThread(serverAddr);
        mThread.start();
    }

    class DownThread extends Thread {
        String mAddr;
        String mResult;

        DownThread(String addr) {
            mAddr = addr;
            mResult = "";
        }

        public void run() {
            StringBuilder html = new StringBuilder();
            try {
                URL url = new URL(mAddr);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                if (conn != null) {
                    conn.setConnectTimeout(10000);
                    conn.setUseCaches(false);
                    if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        BufferedReader br = new BufferedReader(
                                new InputStreamReader(conn.getInputStream()));
                        for (;;) {
                            String line = br.readLine();
                            if (line == null) break;
                            html.append(line + '\n');
                        }
                        br.close();
                        mResult = html.toString();
                    }
                    conn.disconnect();
                }
            }
            catch (Exception ex) {;}
            mAfterDown.sendEmptyMessage(0);
        }
    }

    Handler mAfterDown = new Handler() {
        public void handleMessage(Message msg) {
            TextView result = (TextView)findViewById(R.id.result);
            String temperature = "";
            try {
                JSONObject order = new JSONObject(mThread.mResult);
                temperature += order.getString("temp");
                temperature += " ℃";
            } catch(JSONException e) {;}
            result.setText(temperature);
        }
    };
}
