package com.github.krassekoder.mm13server;

import com.trolltech.qt.core.QDir;
import com.trolltech.qt.core.QFile;
import com.trolltech.qt.core.QTextStream;
import com.trolltech.qt.xml.QDomDocument;
import com.trolltech.qt.xml.QDomNodeList;

public final class Database {
    private static QDomDocument db;

    private static void newDb() {
        QFile.copy("classpath:com/github/krassekoder/default.xml", "mm13server.db.xml");
        openDb();
    }

    private static void openDb() {
        QFile f = new QFile("mm13server.db.xml");
        f.open(QFile.OpenModeFlag.ReadOnly);
        db.setContent(f);
        f.close();
    }

    public static void init() {
        System.out.println("Preparing database: " + (new QDir()).absoluteFilePath("mm13server.db.xml"));
        db = new QDomDocument("mm13server");
        if(QFile.exists("mm13server.db.xml"))
            openDb();
        else
            newDb();
    }

    public static void save() {
        QFile f = new QFile("mm13server.db.xml");
        f.open(QFile.OpenModeFlag.WriteOnly);
        db.save(new QTextStream(f), 4);
        f.close();
    }


    public static byte hasUser(String username, String password) {
        QDomNodeList users = db.documentElement().firstChildElement("users").childNodes();
        for(int i = 0; i < users.length(); i++) {
            if(users.at(i).isElement()) {
                if(users.at(i).toElement().attribute("name").equals(username)) {
                    if(users.at(i).toElement().attribute("password").equals(password)) {
                        return Byte.parseByte(users.at(i).toElement().attribute("rights"));
                    }
                }
            }
        }
        return 0;
    }
}
