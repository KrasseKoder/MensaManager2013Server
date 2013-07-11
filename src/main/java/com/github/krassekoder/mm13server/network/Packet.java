package com.github.krassekoder.mm13server.network;

import com.trolltech.qt.QtJambiObject;
import com.trolltech.qt.core.QByteArray;
import com.trolltech.qt.core.QDataStream;
import com.trolltech.qt.network.QTcpSocket;

/**
 * A Packet encapsulates one set of data to be sent through the network.
 * Each implementation of Packet needs its own id wich will be automatically prepended to the stream by this class.
 * On the server, each {@see Connection} will have it's own set of Packets. This adds the ability to store data per {@see Connection}.
 */
public abstract class Packet extends QtJambiObject{

    public static class InvalidPacketException extends Exception {}
    public static class TimeoutException extends Exception {}

    public static class PacketHandler {
        private Connection c;
        private Packet[] packets = new Packet[128];
        private Packet current;

        public PacketHandler(Connection c) {
            this.c = c;
            //initialize packets here
            packets[0] = new Packet0Login(c);
            packets[1] = new Packet1FoodList(c);
            packets[4] = new Packet4Admin(c);
        }

        public Packet getById(byte id) {
            return packets[id];
        }

        public void distribute() throws InvalidPacketException, TimeoutException {
            if(current != null) {
                if(current.receiveData())
                    current = null;
            }

            QByteArray id = c.socket.read(1);
            Packet packet = getById(id.at(0));
            if(packet == null)
                throw new Packet.InvalidPacketException();
            if(!packet.receiveData()){
                current = packet;
        }
        }
    }

    protected QTcpSocket socket;
    protected Connection connection;
    protected QDataStream data;

    public Packet(Connection c) {
        connection = c;
        socket = c.socket;
        data = new QDataStream(socket);
    }

    /**
     * Overwrite: return &lt;number&gt;;
     */
    public abstract byte id();

    /**
     * Call this function to send your data.
     * The packet id is automatically prepended.
     */
    protected void sendData(QByteArray data) {
        QByteArray out = new QByteArray();
        out.append(id());
        out.append(data);
        socket.write(out);
    }

    /**
     * This function is called every time, new data is available for the Packet.
     * To reimplement: Return false until all bytes are received. When all bytes were read, send the answer via sendData and return true.
     * Make sure, not to read too many bytes.
     */
    protected abstract boolean receiveData() throws Packet.InvalidPacketException, Packet.TimeoutException;
}
