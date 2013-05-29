package com.github.krassekoder.mm13server;

import com.trolltech.qt.core.QCoreApplication;

public class Main{
    public static void main(String[] args) {
        QCoreApplication a = new QCoreApplication(args);
        
        QCoreApplication.setApplicationName("MensaManager2013Server");
        QCoreApplication.setApplicationVersion("Pre-Alpha");
        QCoreApplication.setOrganizationName("KrasseKoder");
        QCoreApplication.setOrganizationDomain("http://www.github.com/KrasseKoder/");

        System.out.println("MensaManager2013 Server - Starting up");
    }
}
