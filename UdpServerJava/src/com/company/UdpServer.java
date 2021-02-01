package com.company;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import com.mossonthetree.chatappkotlin.AppMessage;
import com.mossonthetree.chatappkotlin.RemoteClient;

public class UdpServer implements Closeable, AutoCloseable {
    private boolean inError;

    private DatagramSocket socket;

    public boolean isInError() {
        return inError;
    }

    public UdpServer(short port) {
        try {
            socket = new DatagramSocket(port);
            inError = false;
        } catch (Exception ex) {
            inError = true;
        }
    }

    public void sendData(AppMessage appMessage, RemoteClient client) {
        try {
            ByteArrayOutputStream bOutStream = new ByteArrayOutputStream(2048);
            ObjectOutputStream objOutStream = new ObjectOutputStream(bOutStream);
            objOutStream.writeObject(appMessage);
            objOutStream.close();
            byte[] data = bOutStream.toByteArray();
            DatagramPacket res = new DatagramPacket(data, 0, data.length, client.getIpAddress(), client.getPort());
            bOutStream.close();
            socket.send(res);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void receiveData(ReceiveCallback callback) {
        if(inError) {
            return;
        }
        byte[] buffer = new byte[2048];
        DatagramPacket req = new DatagramPacket(buffer, 0, buffer.length);
        try {
            socket.receive(req);
            ByteArrayInputStream byteStream = new ByteArrayInputStream(req.getData(), 0, req.getLength());
            ObjectInputStream objStream = new ObjectInputStream(byteStream);
            AppMessage appMessage = (AppMessage)(objStream.readObject());
            ReceiveArgs args = new ReceiveArgs(appMessage, req.getAddress(), req.getPort());
            callback.handleData(args);
            objStream.close();
            byteStream.close();
        } catch (Exception ex) {
            System.out.println(ex);
            inError = true;
        }
    }

    @Override
    public void close() {
        try {
            socket.close();
        } catch (Exception ex) {
            inError = true;
        }
    }
}
