package ch.noebuerki.futurascanner.database.server;

import android.app.Activity;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

import ch.noebuerki.futurascanner.database.local.objects.Settings;

public class Connector {

    public void testConnection(Settings settings, Activity activity, ConnectorCallback callback) {
        new Thread(() -> {
            try {
                Socket socket = new Socket();
                socket.connect(new InetSocketAddress(settings.getServerIp(), 9090), 2500);
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                writer.print("ConnectionCheck-Request");
                writer.flush();
                String answer = reader.readLine();
                socket.close();
                if (answer.equals("ConnectionCheck-Reply")) {
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
                int messageLength = StringUtils.countMatches(message, "\n");
                int expectedDeliveries = (messageLength + 99) / 100;

                Socket socket = new Socket();
                socket.connect(new InetSocketAddress(settings.getServerIp(), 9090), 3000);
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                writer.write("DeliveryStart");
                writer.flush();

                for (int deliveries = 1; deliveries <= expectedDeliveries; deliveries++) {
                    String currentMessage;
                    if (deliveries == 1) {
                        currentMessage = message.substring(0, StringUtils.ordinalIndexOf(message, "\n", deliveries * 100));
                    } else if (deliveries == expectedDeliveries) {
                        currentMessage = message.substring(StringUtils.ordinalIndexOf(message, "\n", (deliveries - 1) * 100));
                    } else {
                        currentMessage = message.substring(StringUtils.ordinalIndexOf(message, "\n", (deliveries - 1) * 100), StringUtils.ordinalIndexOf(message, "\n", deliveries * 100));
                    }

                    writer.write(currentMessage);
                    writer.flush();

                    String answer = reader.readLine();

                    if (!answer.equals("Delivery:" + deliveries)) {
                        activity.runOnUiThread(() -> callback.onFail());
                        break;
                    }
                }

                writer.write("DeliveryReport");
                writer.flush();

                String answer = reader.readLine();
                socket.close();

                int deliveredDeliveries = Integer.parseInt(answer.split(";")[0]);
                int deliveredItems = Integer.parseInt(answer.split(";")[1]);
                if (deliveredDeliveries == expectedDeliveries && deliveredItems == messageLength) {
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
