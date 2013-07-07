package com.github.krassekoder.mm13server.network;

import com.trolltech.qt.QVariant;
import com.trolltech.qt.core.QSettings;
import com.trolltech.qt.network.QHostAddress;
import com.trolltech.qt.network.QTcpServer;

public final class Server{
    public static QTcpServer server;

    public Server(QSettings s) {
        server = new QTcpServer();
        server.newConnection.connect(this, "establishConnection()");
        s.beginGroup("server");
        if(!server.listen(new QHostAddress(QVariant.toString(s.value("ip"))), QVariant.toInt(s.value("port")))) {
            System.out.println("Error listening at " + server.serverAddress() + ":" + server.serverPort());
            System.out.println(server.errorString());
        } else {
            System.out.println("Listening at " + server.serverAddress() + ":" + server.serverPort());
        }
        s.endGroup();
    }

    public void establishConnection() {
        while(server.hasPendingConnections())
            new Connection(server.nextPendingConnection());
    }
}
