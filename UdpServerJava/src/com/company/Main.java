package com.company;

import com.mossonthetree.chatappkotlin.AppMessage;
import com.mossonthetree.chatappkotlin.ClientMessage;
import com.mossonthetree.chatappkotlin.RemoteClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class Main {
    private static boolean isRunning = true;

    private static UdpServer server = null;

    private static Map<String, RemoteClient> clients;

    private static final ReceiveCallback receiveCallback = args -> {
        AppMessage appMessage = args.getAppMessage();
        System.out.printf("%s %s%n", appMessage.getType(), appMessage.getData().toString());
        switch (appMessage.getType()) {
            case 1: {
                ClientMessage msg = (ClientMessage)appMessage.getData();
                if(clients.containsKey(msg.getRecipient())) {
                    System.out.printf("Sending %s to %s%n", msg.getMsg(), msg.getRecipient());
                    server.sendData(new AppMessage(1, msg), clients.get(msg.getRecipient()));
                }
                break;
            }
            case 2: {
                System.out.println("Connection received...");
                String newConnectionName = (String)appMessage.getData();
                RemoteClient newConnection = new RemoteClient(newConnectionName, args.getInetAddress(), args.getPortNumber());
                if(!clients.containsKey(newConnection.getIdentifier())) {
                    clients.put(newConnection.getIdentifier(), newConnection);
                }
                break;
            }
            case 3: {
                System.out.println("Removing connection...");
                String newConnectionName = (String)appMessage.getData();
                clients.remove(newConnectionName);
                break;
            }
            default: {
                System.out.printf("Unknown type %d%n", appMessage.getType());
            }
        }
        return appMessage.getType();
    };

    private static final Runnable thread = () -> {
        while(isRunning) {
            System.out.println("Running...");
            try {
                if(server.isInError()) {
                    isRunning = false;
                    System.out.println("In error");
                }
                server.receiveData(receiveCallback);
            } catch (Exception ex) {

            }
        }
    };

    public static void main(String[] args) {
        clients = new HashMap<>();

        server = new UdpServer((short) 8081);
        Thread serverThread = new Thread(thread);
        serverThread.start();

        InputStreamReader inReader = new InputStreamReader(System.in);
        BufferedReader bufReader = new BufferedReader(inReader);

        while(isRunning) {
            try {
                String input = bufReader.readLine();
                System.out.println(input);
                if(input.equals("-quit")) {
                    isRunning = false;
                }
            } catch (Exception ex) {

            }
        }

        try {
            bufReader.close();
        } catch (Exception ex) {}
        try {
            inReader.close();
        } catch (Exception ex) {}
        server.close();
    }
}
