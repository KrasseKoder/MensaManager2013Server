package com.github.krassekoder.mm13server.network;

import com.github.krassekoder.mm13server.Database;
import com.trolltech.qt.core.QByteArray;
import com.trolltech.qt.core.QDataStream;
import com.trolltech.qt.core.QIODevice;

public class Packet3Data extends Packet{

    public static final int SALES = 0;

    public Packet3Data(Connection c) {
        super(c);
    }

    @Override
    public byte id() {
        return 3;
    }

    @Override
    protected boolean receiveData() throws InvalidPacketException, TimeoutException {
        if(socket.bytesAvailable() < 4) // int
            return false;
        int type = data.readInt();

        QByteArray res = new QByteArray();
        QDataStream s = new QDataStream(res, QIODevice.OpenModeFlag.WriteOnly);
        QByteArray d = new QByteArray(Database.composeData(type));

        s.writeInt(d.length());
        res.append(d);

        sendData(res);
        return true;
    }

}
