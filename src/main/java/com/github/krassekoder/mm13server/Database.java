package com.github.krassekoder.mm13server;

import com.trolltech.qt.QVariant;
import com.trolltech.qt.core.QCoreApplication;
import com.trolltech.qt.core.QSettings;
import com.trolltech.qt.sql.QSqlDatabase;

public final class Database {
    public static QSqlDatabase db;
    public static void init(QSettings s) {
        s.beginGroup("database");
        db = QSqlDatabase.addDatabase(QVariant.toString(s.value("driver")));
        db.setHostName(QVariant.toString(s.value("host")));
        db.setDatabaseName(QVariant.toString(s.value("name")));
        db.setUserName(QVariant.toString(s.value("user")));
        db.setPassword(QVariant.toString(s.value("password")));
        if(!db.open()) {
            System.out.println(QCoreApplication.translate("Database", "Couldn't connect to database"));
            QCoreApplication.quit();
        }
        s.endGroup();
    }
}
