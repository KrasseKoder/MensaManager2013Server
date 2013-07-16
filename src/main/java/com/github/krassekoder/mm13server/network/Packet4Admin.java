package com.github.krassekoder.mm13server.network;

import com.github.krassekoder.mm13server.Database;
import com.trolltech.qt.core.QByteArray;
import com.trolltech.qt.core.QDataStream;
import com.trolltech.qt.core.QIODevice;

public class Packet4Admin extends Packet{

    public enum State {
        Type, Length, Data
    }

    private static final int PRODUCT = 0, USER = 1, VOUCHER = 2;

    private byte type;
    private int length;
    private String value;
    private State state = State.Type;

    public Packet4Admin(Connection c) {
        super(c);
    }

    @Override
    public byte id() {
        return 4;
    }

    @Override
    protected boolean receiveData() throws InvalidPacketException, TimeoutException {
        switch(state) {
            case Type:
                if(socket.bytesAvailable() < 1)
                    return false;
                type = socket.read(1).at(0);
                state = State.Length;
            case Length:
                if(socket.bytesAvailable() < 4) //int
                    return false;
                length = data.readInt();
                state = State.Data;
            case Data:
                if(socket.bytesAvailable() < length)
                    return false;
                value = socket.read(length).toString();
        }
        state = State.Type;

        if(type == PRODUCT)
            editProduct();
        else if(type == USER)
            editUser();
        else if(type == VOUCHER)
            addVoucher();

        return true;
    }

    private void editProduct() {
        if(((Packet0Login)connection.handler.getById((byte)0)).ensureRights(Packet0Login.ADMIN)) {
            String[] values = value.split("\n");
            Database.editProduct(values[0], values[1], values[2]);
            System.out.println(((Packet0Login)connection.handler.getById((byte)0)).username + " edited product " + values[0] + ": "
                                + values[1] + " " + values[2]);
        } else { System.out.println("Tried editing the database"); }
        sendData(new QByteArray());
    }

    private void editUser() {
        if(((Packet0Login)connection.handler.getById((byte)0)).ensureRights(Packet0Login.ADMIN)) {
            String[] values = value.split("\n");
            Database.editUser(values[0], values[1], values[2]);
            System.out.println(((Packet0Login)connection.handler.getById((byte)0)).username + " edited user " + values[0] + "("
                                + values[2] + "): " + values[1]);
        } else { System.out.println("Tried editing the database"); }
        sendData(new QByteArray());
    }

    private void addVoucher() {
        QByteArray res = new QByteArray();
        QDataStream s = new QDataStream(res, QIODevice.OpenModeFlag.WriteOnly);
        if(((Packet0Login)connection.handler.getById((byte)0)).ensureRights(Packet0Login.TELLER)) {
            QByteArray v = new QByteArray(Database.addVoucher(value));
            s.writeInt(v.length());
            res.append(v);
        } else { System.out.println("Tried creating a voucher"); s.writeInt(0); }
        sendData(res);
    }
}
