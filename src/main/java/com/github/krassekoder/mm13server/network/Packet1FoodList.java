package com.github.krassekoder.mm13server.network;

import com.github.krassekoder.mm13server.Database;
import com.trolltech.qt.core.QByteArray;
import com.trolltech.qt.core.QDataStream;
import com.trolltech.qt.core.QIODevice;

public class Packet1FoodList extends Packet{
    private enum State {
        Length, Request
    }

    private int length;
    private String request;
    private State state = State.Length;

    public Packet1FoodList(Connection c) {
        super(c);
    }

    @Override
    public byte id() {
        return 1;
    }

    @Override
    protected boolean receiveData() throws InvalidPacketException, TimeoutException {
        switch(state) {
            case Length:
                if(socket.bytesAvailable() < 4)
                    return false;
                length = data.readInt();
                state = State.Request;
            case Request:
                if(socket.bytesAvailable() < length)
                    return false;
                request = socket.read(length).toString();
        }

        QByteArray meals = new QByteArray(Database.getProducts(request));

        QByteArray res = new QByteArray();
        QDataStream s = new QDataStream(res, QIODevice.OpenModeFlag.WriteOnly);
        s.writeInt(meals.length());
        res.append(meals);

        sendData(res);
        state = State.Length;
        return true;
    }


}
