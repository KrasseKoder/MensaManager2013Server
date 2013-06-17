package com.github.krassekoder.mm13server.network;

import static com.trolltech.qt.QSignalEmitter.signalSender;
import com.trolltech.qt.QtJambiObject;
import com.trolltech.qt.core.QByteArray;
import com.trolltech.qt.network.QTcpSocket;


public abstract class Packet extends QtJambiObject{

    public static class InvalidPacketException extends Exception {}
    public static class TimeoutException extends Exception {}

    private static Packet[] packets = new Packet[128];

    public Packet() {
        packets[id()] = this;
    }

    public abstract byte id();
    public static Packet getById(byte id) {
        return packets[id];
    }

    protected void sendData(QByteArray data, QTcpSocket socket) {
        QByteArray out = new QByteArray();
        out.append(id());
        out.append(data);
        socket.write(out);
    }

    protected final void receiveSlot() throws InvalidPacketException, TimeoutException{
        QTcpSocket socket = (QTcpSocket)signalSender();
        if(receiveData(socket)) {
            disconnect(socket);
        }
    }
    /*
     * This function is called every time, new data is available for the Packet.
     * To reimplement: Return false until all bytes are received. When all bytes were read, send the answer via sendData and return true.
     * Make sure, not to read too many bytes.
     */
    protected abstract boolean receiveData(QTcpSocket socket) throws Packet.InvalidPacketException, Packet.TimeoutException;


    public static void init() {
        //initialize packets here
    }

    public static void receive(QTcpSocket socket) throws Packet.InvalidPacketException, Packet.TimeoutException {
        QByteArray id = socket.read(1);
        Packet packet = getById(id.at(0));
        if(packet == null)
            throw new Packet.InvalidPacketException();
        if(!packet.receiveData(socket))
            socket.readyRead.connect(packet, "receiveSlot()");
    }
}
