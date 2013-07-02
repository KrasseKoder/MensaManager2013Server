package com.github.krassekoder.mm13server.network;

public class Packet0Login extends Packet{

    private int uNameLen = -1, pwdLen = -1;
    private String uName, pwd;

    public Packet0Login(Connection c) {
        super(c);
    }

    @Override
    public byte id() {
        return 0;
    }

    @Override
    protected boolean receiveData() throws InvalidPacketException, TimeoutException {
        if(uNameLen == -1)
            if(socket.bytesAvailable() >= 4) //int
                uNameLen = data.readInt();
            else
                return false;
        if(pwdLen == -1)
            if(socket.bytesAvailable() >= 4) //int
                pwdLen = data.readInt();
            else
                return false;
        if(uNameLen != -2) //reading username
            if(socket.bytesAvailable() >= uNameLen)
                uName = socket.read(uNameLen).toString();
            else
                return false;
        else //reading password
            if(socket.bytesAvailable() >= pwdLen)
                pwd = socket.read(pwdLen).toString();
            else
                return false;
        uNameLen = pwdLen = 0;
        return true;
    }

}
