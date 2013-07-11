package com.github.krassekoder.mm13server;

import com.trolltech.qt.core.QDir;
import com.trolltech.qt.core.QFile;
import com.trolltech.qt.core.QTextStream;
import com.trolltech.qt.xml.QDomDocument;
import com.trolltech.qt.xml.QDomElement;
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


    public static byte login(String username, String password) {
        QDomNodeList users = db.documentElement().firstChildElement("users").elementsByTagName("user");
        for(int i = 0; i < users.length(); i++) {
            if(users.at(i).toElement().attribute("name").equals(username)) {
                if(users.at(i).toElement().attribute("password").equals(password)) {
                    return Byte.parseByte(users.at(i).toElement().attribute("rights"));
                }
            }
        }
        return 0;
    }

    private static QDomElement findUser(String name) {
        QDomNodeList meals = db.documentElement().firstChildElement("users").elementsByTagName("user");
        for(int i = 0; i < meals.length(); i++) {
            if(meals.at(i).toElement().attribute("name").equals(name)) {
                return meals.at(i).toElement();
            }
        }
        return null;
    }

    public static void editUser(String name, String password, String rights) {
       QDomElement e = findUser(name);

       if(rights.equals("0")) {
           if(e != null)
                e.parentNode().removeChild(e);
           else
               return;
       }

       if(e == null) {
           e = db.createElement("user");
           e.setAttribute("name", name);
           db.documentElement().firstChildElement("users").appendChild(e);
       }

       e.setAttribute("password", password);
       e.setAttribute("rights", rights);
    }

    public static String getProducts(String request) {
        QDomNodeList meals = db.documentElement().firstChildElement("products").elementsByTagName("product");
        StringBuilder res = new StringBuilder();
        for(int i = 0; i < meals.length(); i++) {
            if(meals.at(i).toElement().attribute("id").matches(request)) {
                StringBuilder buffer = res;
                res = new StringBuilder();
                res.append(meals.at(i).toElement().attribute("id"));
                res.append("\n");
                res.append(meals.at(i).toElement().attribute("name"));
                res.append("\n");
                res.append(meals.at(i).toElement().attribute("price"));
                res.append("\n\n");
                res.append(buffer);
            } else if(meals.at(i).toElement().attribute("id").startsWith(request) ||
               meals.at(i).toElement().attribute("name").contains(request)) {
                    res.append(meals.at(i).toElement().attribute("id"));
                    res.append("\n");
                    res.append(meals.at(i).toElement().attribute("name"));
                    res.append("\n");
                    res.append(meals.at(i).toElement().attribute("price"));
                    res.append("\n\n");
            }
        }
        return res.toString();
    }

    private static QDomElement findProduct(String id){
        QDomNodeList meals = db.documentElement().firstChildElement("products").elementsByTagName("product");
        for(int i = 0; i < meals.length(); i++) {
            if(meals.at(i).toElement().attribute("id").equals(id)) {
                return meals.at(i).toElement();
            }
        }
        return null;
    }

    public static void editProduct(String id, String name, String price) {
       QDomElement e = findProduct(id);

       if(name.isEmpty()) {
           if(e != null)
                e.parentNode().removeChild(e);
           else
               return;
       }

       if(e == null) {
           e = db.createElement("product");
           e.setAttribute("id", id);
           db.documentElement().firstChildElement("products").appendChild(e);
       }

       e.setAttribute("name", name);
       e.setAttribute("price", price);
    }
}
