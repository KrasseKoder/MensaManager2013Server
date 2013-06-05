package com.github.krassekoder.mm13server;

import com.trolltech.qt.QVariant;
import com.trolltech.qt.core.QSettings;
import com.trolltech.qt.sql.QSqlDatabase;


public class Database extends QSqlDatabase{

    public Database(QSettings settings) {
    }

    private String getHost(QSettings s) {
        Object host = s.value("host");
        if(!QVariant.canConvertToString(host)) {
            System.out.println(String.format(tr("Invalid value for %1$s, should be %2$s!"), "database/host", "String"));
            System.out.println(String.format(tr("Using default value for %1$s: %2$s!"), "database/host", "mm13.db"));
            return "mm13.db";
        }
        return QVariant.toString(host);
    }

    private String getDriver(QSettings s) {
        Object driver = s.value("driver");
        if(!QVariant.canConvertToString(driver)) {
            System.out.println(String.format(tr("Invalid value for %1$s, should be %2$s!"), "database/driver", "String"));
            System.out.println(String.format(tr("Using default value for %1$s: %2$s!"), "database/driver", "SQLITE"));
            return "SQLITE";
        }
        return QVariant.toString(driver);
    }

    private String getUser(QSettings s) {
        Object user = s.value("user");
        if(!QVariant.canConvertToString(user)) {
            System.out.println(String.format(tr("Invalid value for %1$s, should be %2$s!"), "database/user", "String"));
            System.out.println(String.format(tr("Using default value for %1$s: %2$s!"), "database/user", "admin"));
            return "admin";
        }
        return QVariant.toString(user);
    }

    private String getPassword(QSettings s) {
        Object pwd = s.value("password");
        if(!QVariant.canConvertToString(pwd)) {
            System.out.println(String.format(tr("Invalid value for %1$s, should be %2$s!"), "database/password", "String"));
            System.out.println(String.format(tr("Using default value for %1$s: %2$s!"), "database/password", ""));
            return "";
        }
        return QVariant.toString(pwd);
    }
}
