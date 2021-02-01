package com.company;

import com.mossonthetree.chatappkotlin.AppMessage;

public interface ReceiveCallback {
    int handleData(ReceiveArgs appMessage);
}
