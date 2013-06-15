package com.github.krassekoder.mm13server.network;

import com.trolltech.qt.core.QObject;
import com.trolltech.qt.network.QTcpSocket;

public class Connection extends QObject{

    QTcpSocket socket;

    public Connection(QTcpSocket socket) {
        this.socket = socket;
        socket.readyRead.connect(this, "processBytes()");
    }

    public void processBytes() {

    }
}
