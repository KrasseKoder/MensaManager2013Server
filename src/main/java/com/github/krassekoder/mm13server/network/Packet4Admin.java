package com.github.krassekoder.mm13server.network;

import com.github.krassekoder.mm13server.Database;
import com.trolltech.qt.core.QByteArray;

public class Packet4Admin extends Packet{

    public enum State {
        Type, Length, Data
    }

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

        if(((Packet0Login)connection.handler.getById((byte)0)).rights < 2) {
            System.out.println(((Packet0Login)connection.handler.getById((byte)0)).username + " tried editing the database.");
            sendData(new QByteArray());
            return true;
        }

        if(type == 0)
            editProduct();
        else if(type == 1)
            editUser();

        sendData(new QByteArray());

        state = State.Type;
        return true;
    }

    private void editProduct() {
        String[] values = value.split("\n");
        Database.editProduct(values[0], values[1], values[2]);
        System.out.println(((Packet0Login)connection.handler.getById((byte)0)).username + " edited product " + values[0] + ": "
                            + values[1] + " " + values[2]);
    }

    private void editUser() {
        String[] values = value.split("\n");

    }
}
