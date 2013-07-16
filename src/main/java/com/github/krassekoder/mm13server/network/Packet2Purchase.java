package com.github.krassekoder.mm13server.network;

import com.github.krassekoder.mm13server.Database;
import com.trolltech.qt.core.QByteArray;
import com.trolltech.qt.core.QDataStream;
import com.trolltech.qt.core.QIODevice;

public class Packet2Purchase extends Packet{
    private enum State {
        Length, Data
    }

    private State state = State.Length;
    private int length;
    private String dataInput;

    public Packet2Purchase(Connection c) {
        super(c);
    }

    @Override
    public byte id() {
        return 2;
    }

    @Override
    protected boolean receiveData() throws InvalidPacketException, TimeoutException {
        switch(state) {
            case Length:
                if(socket.bytesAvailable() < 4) //int
                    return false;
                length = data.readInt();
                state = State.Length;
            case Data:
                if(socket.bytesAvailable() < length)
                    return false;
                dataInput = socket.read(length).toString();
        }

        double result = Database.purchase(dataInput);

        QByteArray res = new QByteArray();
        QDataStream s = new QDataStream(res, QIODevice.OpenModeFlag.WriteOnly);

        s.writeDouble(result);

        sendData(res);
        state = State.Length;

        return true;
    }

}
