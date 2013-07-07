package com.github.krassekoder.mm13server;

import com.github.krassekoder.mm13server.network.Server;
import com.trolltech.qt.core.QCoreApplication;
import com.trolltech.qt.core.QDir;
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
            while(true) {
                try {
                    if(r.readLine().toLowerCase().equals("stop")) {
                        System.out.println(QCoreApplication.translate("Server", "Shutting down"));
                        Database.save();
                        QCoreApplication.quit();
                        return;
                    }
                } catch(IOException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    private static void preparePaths() throws URISyntaxException {
        URI path = Server.class.getProtectionDomain().getCodeSource().getLocation().toURI();
        String name = Server.class.getPackage().getName() + ".jar";
        String path2 = path.getRawPath();
        path2 = path2.substring(1);

        if(path2.contains(".jar")) {
            path2 = path2.replace(name, "");
        }

        QDir.setCurrent(path2);
        QSettings.setPath(QSettings.Format.IniFormat, QSettings.Scope.UserScope, path2);
    }

    private static QSettings prepareSettings() {
        System.out.println("Preparing Settings: " + (new QDir()).absoluteFilePath("mm13server.ini"));
        QSettings settings = new QSettings("mm13server.ini", QSettings.Format.IniFormat);

        settings.beginGroup("server");
        settings.setValue("ip", settings.value("ip", "localhost"));
        settings.setValue("port", settings.value("port", 1996));
        settings.endGroup();

        settings.sync();
        return settings;
    }

    public static void main(String[] args) throws URISyntaxException {
        QCoreApplication a = new QCoreApplication(args);

        QCoreApplication.setApplicationName("MensaManager2013Server");
        QCoreApplication.setApplicationVersion("Pre-Alpha");
        QCoreApplication.setOrganizationName("KrasseKoder");
        QCoreApplication.setOrganizationDomain("http://www.github.com/KrasseKoder/");

        preparePaths();

        QSettings settings = prepareSettings();
        Database.init();
        Server s = new Server(settings);

        (new InputThread()).start();

        QCoreApplication.exec();
    }
}
