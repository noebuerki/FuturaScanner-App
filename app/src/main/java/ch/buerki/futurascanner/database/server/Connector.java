package ch.buerki.futurascanner.database.server;

import android.app.Activity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

import ch.buerki.futurascanner.database.local.objects.Settings;

public class Connector {

    public void testConnection(Settings settings, Activity activity, ConnectorCallback callback) {
        new Thread(() -> {
            try {
                Socket socket = new Socket();
                socket.connect(new InetSocketAddress(settings.getServerIp(), 9090), 2500);
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                writer.print("100");
                writer.flush();
                int answer = reader.read();
                socket.close();
                if (answer == 49) {
                    activity.runOnUiThread(callback::onSuccess);
                } else {
                    activity.runOnUiThread(callback::onFail);
                }
            } catch (IOException e) {
                activity.runOnUiThread(callback::onFail);
            }
        }).start();
    }

    public void sendString(String message, Settings settings, Activity activity, ConnectorCallback callback) {
        new Thread(() -> {
            try {
                Socket socket = new Socket();
                socket.connect(new InetSocketAddress(settings.getServerIp(), 9090), 3000);
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                writer.print(message);
                writer.flush();
                int answer = reader.read();
                socket.close();
                if (answer == 50) {
                    activity.runOnUiThread(callback::onSuccess);
                } else {
                    activity.runOnUiThread(callback::onFail);
                }
            } catch (IOException e) {
                activity.runOnUiThread(callback::onFail);
            }
        }).start();
    }
}
