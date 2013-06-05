package com.github.krassekoder.mm13server;

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

    public static class InputThread extends Thread {
        @Override
        public void run() {
            BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                try {
                    if (r.readLine().toLowerCase().equals("stop")) {
                        System.out.println(QCoreApplication.translate("Server", "Shutting down"));
                        QCoreApplication.quit();
                        return;
                    }
                } catch (IOException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    private ArrayList<Connection> connections;

    public Server(QSettings settings) {
        connections = new ArrayList<Connection>();

        newConnection.connect(this, "establishConnection()");
        settings.beginGroup("server");
        listen(getAdress(settings), getPort(settings));
        settings.endGroup();
    }

    private QHostAddress getAdress(QSettings s) {
        Object address = s.value("ip", "localhost");
        if(!QVariant.canConvertToString(address)) {
            System.out.println(String.format(tr("Invalid value for %1$s, should be %2$s!"), "server/ip", "String"));
            System.out.println(String.format(tr("Using default value for %1$s: %2$s!"), "server/ip", "localhost"));
            return new QHostAddress(QHostAddress.SpecialAddress.LocalHost);
        }
        return new QHostAddress(QVariant.toString(address));
    }

    private int getPort(QSettings s) {
        Object port = s.value("port", 1996);
        if (!QVariant.canConvertToInt(port)){
            System.out.println(String.format(tr("Invalid value for %1$s, should be %2$s!"), "server/port", "int"));
            System.out.println(String.format(tr("Using default value for %1$s: %2$s!"), "server/port", "0"));
            return 1996;
        }
        return QVariant.toInt(port);
    }

    public void establishConnection() {
        connections.add(new Connection(nextPendingConnection()));
    }

    public static String dir() throws URISyntaxException {
        URI path = Server.class.getProtectionDomain().getCodeSource().getLocation().toURI();
        String name = Server.class.getPackage().getName() + ".jar";
        String path2 = path.getRawPath();
        path2 = path2.substring(1);

        if (path2.contains(".jar")) {
            path2 = path2.replace(name, "");
        }
        return path2;
    }

    public static void main(String[] args) throws URISyntaxException {
        QCoreApplication a = new QCoreApplication(args);

        QCoreApplication.setApplicationName("MensaManager2013Server");
        QCoreApplication.setApplicationVersion("Pre-Alpha");
        QCoreApplication.setOrganizationName("KrasseKoder");
        QCoreApplication.setOrganizationDomain("http://www.github.com/KrasseKoder/");

        String jarPath = dir();
        QDir.setCurrent(jarPath);
        QSettings.setPath(QSettings.Format.IniFormat, QSettings.Scope.UserScope, jarPath);

        if (!QFile.exists("mm13server.ini")) {
            System.out.println(QCoreApplication.translate("Server", "Couldn't find configuration."));
            QSettings settings = new QSettings("mm13server.ini", QSettings.Format.IniFormat);

            settings.beginGroup("server");
            settings.setValue("ip", "localhost");
            settings.setValue("port", 1996);
            settings.endGroup();

            settings.beginGroup("database");
            settings.setValue("host", "mm13.db");
            settings.setValue("driver", "SQLITE");
            settings.setValue("user", "admin");
            settings.setValue("password", "");
            settings.endGroup();

            settings.sync();
            return;
        }

        QSettings settings = new QSettings("mm13server.ini", QSettings.Format.IniFormat);
        Server s = new Server(settings);

        (new InputThread()).start();

        QCoreApplication.exec();
    }
}
