package com.github.krassekoder.mm13server;

import com.trolltech.qt.core.QDateTime;
import com.trolltech.qt.core.QDir;
import com.trolltech.qt.core.QFile;
import com.trolltech.qt.core.QTextStream;
import com.trolltech.qt.xml.QDomDocument;
import com.trolltech.qt.xml.QDomElement;
import com.trolltech.qt.xml.QDomNodeList;
import java.util.UUID;

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
               meals.at(i).toElement().attribute("name").toLowerCase().contains(request.toLowerCase())) {
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

    private static QDomElement getSale(String input) {
        String[] items = input.split("\n");

        QDomElement e = db.createElement("sale");
        db.documentElement().firstChildElement("sales").appendChild(e);

        e.setAttribute("type", items[0]);
        e.setAttribute("date", QDateTime.currentDateTime().toString());
        if(items.length >= 2)
            e.setAttribute("data1", items[1]);
        if(items.length >= 3)
            e.setAttribute("data2", items[2]);

        return e;
    }

    public static double getPrice(String id) {
        return Double.parseDouble(findProduct(id).attribute("price"));
    }

    public static double purchase(String data) {
        String[] parts = data.split("\n\n");
        QDomElement e = getSale(parts[0]);

        double sum = 0;

        String[] products = parts[1].split("\n");
        for(int i = 0; i < products.length; i++) {
            QDomElement product = db.createElement("product");
            e.appendChild(product);
            String[] items = products[i].split("\t");
            product.setAttribute("id", items[0]);
            product.setAttribute("count", items[1]);
            sum += getPrice(items[0]) * Integer.parseInt(items[1]);
        }

        e.setAttribute("sum", sum);

        if(e.attribute("type").equals("01")) {
            System.out.println("Sale: Cash(" + e.attribute("data1") + ")");
            e.setAttribute("money", e.attribute("data1"));
            sum = Double.parseDouble(e.attribute("data1")) - sum;
        } else if(e.attribute("type").equals("02")) {
            System.out.println("Sale: Account(" + e.attribute("data1") + ")");
            e.setAttribute("user", e.attribute("data1"));
            //charge user, calc remaining
        } else if(e.attribute("type").equals("03")) {
            System.out.println("Sale: Voucher(" + e.attribute("data1") + ")");
            e.setAttribute("id", e.attribute("data1"));
            double newsum = chargeVoucher(e.attribute("data1"), sum);
            if(newsum > 0)
                sum = newsum;
        }
        e.removeAttribute("data1");
        e.removeAttribute("data2");
        return sum;
    }

    private static QDomElement findVoucher(String id) {
        QDomNodeList meals = db.documentElement().firstChildElement("vouchers").elementsByTagName("voucher");
        for(int i = 0; i < meals.length(); i++) {
            if(meals.at(i).toElement().attribute("id").equals(id)) {
                return meals.at(i).toElement();
            }
        }
        return null;
    }

    public static double chargeVoucher(String id, double sum) {
        QDomElement e = findVoucher(id);
        if(e == null)
            return -1;

        double value = Double.parseDouble(e.attribute("value"));
        if(value < sum) {
            e.setAttribute("value", "0");
            return sum - value;
        } else {
            e.setAttribute("value", value - sum);
            return 0;
        }
    }

    public static String addVoucher(String value) {
        String uuid = UUID.randomUUID().toString();
        while(chargeVoucher(uuid, 0) >= 0)
            uuid = UUID.randomUUID().toString();

        QDomElement e = db.createElement("voucher");
        e.setAttribute("voucher-id", uuid);
        e.setAttribute("value", value);
        db.documentElement().firstChildElement("vouchers").appendChild(e);

        System.out.println("New Voucher(" + value + "): " + uuid);

        return uuid;
    }
}
