package com.github.krassekoder.mm13server;

import com.trolltech.qt.core.QCoreApplication;
import com.trolltech.qt.core.QDir;
import com.trolltech.qt.core.QSettings;
import com.trolltech.qt.network.QHostAddress;
import com.trolltech.qt.network.QTcpServer;

public class Server extends QTcpServer {

    public Server(QHostAddress address, int port) {
        newConnection.connect(this, "establishConnection()");
        listen(address, port);
    }

    public void establishConnection() {
    }

    public static void main(String[] args) {
        QCoreApplication a = new QCoreApplication(args);

        QCoreApplication.setApplicationName("MensaManager2013Server");
        QCoreApplication.setApplicationVersion("Pre-Alpha");
        QCoreApplication.setOrganizationName("KrasseKoder");
        QCoreApplication.setOrganizationDomain("http://www.github.com/KrasseKoder/");

        QDir.setCurrent(QCoreApplication.applicationDirPath());

        QSettings settings = new QSettings("mm13server.ini", QSettings.Format.IniFormat);
        Server s = new Server(new QHostAddress(settings.value("server-ip", "localhost").toString()),
                Integer.parseInt(settings.value("server-port", "0").toString()));

        //QCoreApplication.exec(); //No way of quitting
    }
}
