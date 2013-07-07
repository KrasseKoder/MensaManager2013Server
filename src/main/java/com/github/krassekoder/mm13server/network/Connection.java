package com.github.krassekoder.mm13server.network;

import com.trolltech.qt.core.QObject;
import com.trolltech.qt.network.QTcpSocket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Connection extends QObject{

    /*package*/ final QTcpSocket socket;
    Packet.PacketHandler handler;

    public Signal0 readyRead = new Signal0();

    public Connection(QTcpSocket socket) {
        this.socket = socket;
        socket.error.connect(this, "error()");
        handler = new Packet.PacketHandler(this);
        socket.readyRead.connect(this, "processBytes()");
        System.out.println(socket + " connected");
    }

    private void processBytes() {
        try {
            handler.distribute();
        } catch (Packet.InvalidPacketException ex) {
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Packet.TimeoutException ex) {
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void error() {
        System.out.println(socket.errorString());
    }
}
