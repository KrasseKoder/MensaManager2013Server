package com.github.krassekoder.mm13server.network;

import com.github.krassekoder.mm13server.Database;
import com.trolltech.qt.core.QByteArray;

public class Packet2EntryEdit extends Packet{

    public enum State {
        IdLength, NameLength, PriceLength, Id, Name, Price
    }

    private int idLength, nameLength, priceLength;
    private String id, name, price;
    private State state = State.IdLength;

    public Packet2EntryEdit(Connection c) {
        super(c);
    }

    @Override
    public byte id() {
        return 2;
    }

    @Override
    protected boolean receiveData() throws InvalidPacketException, TimeoutException {
        switch(state) {
            case IdLength:
                if(socket.bytesAvailable() < 4) //int
                    return false;
                idLength = data.readInt();
                state = State.NameLength;
            case NameLength:
                if(socket.bytesAvailable() < 4) //int
                    return false;
                nameLength = data.readInt();
                state = State.PriceLength;
            case PriceLength:
                if(socket.bytesAvailable() < 4) //int
                    return false;
                priceLength = data.readInt();
                state = State.Id;
            case Id:
                if(socket.bytesAvailable() < idLength)
                    return false;
                id = socket.read(idLength).toString();
                state = State.Name;
            case Name:
                if(socket.bytesAvailable() < nameLength)
                    return false;
                name = socket.read(nameLength).toString();
                state = State.Price;
            case Price:
                if(socket.bytesAvailable() < priceLength)
                    return false;
                price = socket.read(priceLength).toString();
        }

        if(((Packet0Login)connection.handler.getById((byte)0)).rights < 2) {
            System.out.println(((Packet0Login)connection.handler.getById((byte)0)).username + " tried editing " + id);
            sendData(new QByteArray());
            return true;
        }

        Database.editMeal(id, name, price);
        System.out.println(((Packet0Login)connection.handler.getById((byte)0)).username + " edited entry " + id);

        sendData(new QByteArray());

        state = State.IdLength;
        return true;
    }

}
