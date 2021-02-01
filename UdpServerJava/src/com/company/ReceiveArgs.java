package com.company;

import com.mossonthetree.chatappkotlin.AppMessage;

import java.net.InetAddress;

public class ReceiveArgs {
    private final AppMessage appMessage;

    private final InetAddress inetAddress;

    private final int portNumber;

    public ReceiveArgs(AppMessage appMessage, InetAddress inetAddress, int portNumber) {
        this.appMessage = appMessage;
        this.inetAddress = inetAddress;
        this.portNumber = portNumber;
    }

    public AppMessage getAppMessage() {
        return appMessage;
    }

    public InetAddress getInetAddress() {
        return inetAddress;
    }

    public int getPortNumber() {
        return portNumber;
    }
}
