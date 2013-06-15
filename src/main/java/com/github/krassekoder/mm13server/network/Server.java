package com.github.krassekoder.mm13server.network;

import com.github.krassekoder.mm13server.network.Connection;
import com.trolltech.qt.QVariant;
import com.trolltech.qt.core.QCoreApplication;
import com.trolltech.qt.core.QDir;
import com.trolltech.qt.core.QFile;
import com.trolltech.qt.core.QSettings;
import com.trolltech.qt.network.QHostAddress;
import com.trolltech.qt.network.QTcpServer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server extends QTcpServer {
    private ArrayList<Connection> connections;

    public Server(QSettings settings) {
        connections = new ArrayList<Connection>();

        newConnection.connect(this, "establishConnection()");
        settings.beginGroup("server");
        listen(getAdress(settings), getPort(settings));
        settings.endGroup();
    }

    private QHostAddress getAdress(QSettings s) {
        Object address = s.value("ip");
        if(!QVariant.canConvertToString(address)) {
            System.out.println(String.format(tr("Invalid value for %1$s, should be %2$s!"), "server/ip", "String"));
            System.out.println(String.format(tr("Using default value for %1$s: %2$s!"), "server/ip", "localhost"));
            return new QHostAddress(QHostAddress.SpecialAddress.LocalHost);
        }
        return new QHostAddress(QVariant.toString(address));
    }

    private int getPort(QSettings s) {
        Object port = s.value("port");
        if (!QVariant.canConvertToInt(port)){
            System.out.println(String.format(tr("Invalid value for %1$s, should be %2$s!"), "server/port", "int"));
            System.out.println(String.format(tr("Using default value for %1$s: %2$s!"), "server/port", "1996"));
            return 1996;
        }
        return QVariant.toInt(port);
    }

    public void establishConnection() {
        connections.add(new Connection(nextPendingConnection()));
    }
}
