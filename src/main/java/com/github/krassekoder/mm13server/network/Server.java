package com.github.krassekoder.mm13server.network;

import com.trolltech.qt.QVariant;
import com.trolltech.qt.core.QSettings;
import com.trolltech.qt.network.QHostAddress;
import com.trolltech.qt.network.QTcpServer;
import java.util.ArrayList;

public final class Server {
    public static QTcpServer server;
    private ArrayList<Connection> connections;

    public Server(QSettings s) {
        this.connections = new ArrayList<Connection>();

        server = new QTcpServer();
        server.newConnection.connect(this, "establishConnection()");
        s.beginGroup("server");
        server.listen(new QHostAddress(QVariant.toString(s.value("address"))),
               QVariant.toInt(s.value("port")));

        s.endGroup();
    }

    public void establishConnection() {
        connections.add(new Connection(server.nextPendingConnection()));
    }
}
