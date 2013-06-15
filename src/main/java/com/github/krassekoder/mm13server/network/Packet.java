package com.github.krassekoder.mm13server.network;

import com.trolltech.qt.core.QByteArray;
import com.trolltech.qt.network.QTcpSocket;


public abstract class Packet {

    public static class InvalidPacketException extends Exception {}
    public static class TimeoutException extends Exception {}

    private static Packet[] packets = new Packet[128];
    private static QTcpSocket socket;

    public Packet() {
        packets[id()] = this;
    }

    public abstract byte id();
    public static Packet getById(byte id) {
        return packets[id];
    }

    protected void sendData(QByteArray data) {
        QByteArray out = new QByteArray();
        out.append(id());
        out.append(data);
        socket.write(out);
    }
    protected abstract void receiveData(QTcpSocket socket) throws Packet.InvalidPacketException, Packet.TimeoutException;


    public static void init(QTcpSocket socket) {
        Packet.socket = socket;
        //initialize packets here
    }
    public static void receive() throws Packet.InvalidPacketException, Packet.TimeoutException {
        QByteArray id = socket.read(1);
        Packet packet = getById(id.at(0));
        if(packet == null)
            throw new Packet.InvalidPacketException();
        packet.receiveData(socket);
    }
}
