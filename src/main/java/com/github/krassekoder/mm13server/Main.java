package com.github.krassekoder.mm13server;

import com.github.krassekoder.mm13server.network.Server;
import com.trolltech.qt.core.QCoreApplication;
import com.trolltech.qt.core.QDir;
import com.trolltech.qt.core.QFile;
import com.trolltech.qt.core.QSettings;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Main {

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

    private static String dir() throws URISyntaxException {
        URI path = Server.class.getProtectionDomain().getCodeSource().getLocation().toURI();
        String name = Server.class.getPackage().getName() + ".jar";
        String path2 = path.getRawPath();
        path2 = path2.substring(1);

        if (path2.contains(".jar")) {
            path2 = path2.replace(name, "");
        }
        return path2;
    }

    private static void prepareSettings() {
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
        }
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

        prepareSettings();

        QSettings settings = new QSettings("mm13server.ini", QSettings.Format.IniFormat);
        Server s = new Server(settings);

        (new InputThread()).start();

        QCoreApplication.exec();
    }
}