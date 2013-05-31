package com.github.krassekoder.mm13server;

import com.trolltech.qt.core.QCoreApplication;
import com.trolltech.qt.core.QDir;
import com.trolltech.qt.core.QFile;
import com.trolltech.qt.core.QSettings;
import com.trolltech.qt.gui.QApplication;
import com.trolltech.qt.network.QHostAddress;
import com.trolltech.qt.network.QTcpServer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server extends QTcpServer {
    
    public static class InputThread extends Thread{
        @Override
        public void run() {
            BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
            while(true) {
                try {
                    if(r.readLine().equals("stop")) {
                        QCoreApplication.quit();
                        return;
                    }
                } catch (IOException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public Server(QHostAddress address, int port) {
        newConnection.connect(this, "establishConnection()");
        listen(address, port);
    }

    public void establishConnection() {
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
            System.out.println(QApplication.translate("Server", "Couldn't find configuration."));
            QSettings settings = new QSettings("mm13server.ini", QSettings.Format.IniFormat);

            settings.beginGroup("server");
            settings.setValue("ip", "localhost");
            settings.setValue("port", 0);
            settings.endGroup();

            settings.beginGroup("database");
            settings.setValue("host", "db");
            settings.setValue("driver", "SQLITE");
            settings.setValue("user", "admin");
            settings.setValue("password", "");
            settings.endGroup();

            settings.sync();
            return;
        }

        QSettings settings = new QSettings("mm13server.ini", QSettings.Format.IniFormat);
        Server s = new Server(new QHostAddress(settings.value("server-ip", "localhost").toString()),
                Integer.parseInt(settings.value("server-port", "0").toString()));

        (new InputThread()).start();
        
        QCoreApplication.exec(); //No way of quitting
    }
}
