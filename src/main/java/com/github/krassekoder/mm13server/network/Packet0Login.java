package com.github.krassekoder.mm13server.network;

import com.github.krassekoder.mm13server.Database;
import com.trolltech.qt.core.QByteArray;

public class Packet0Login extends Packet{

    private enum State {
        UsernameLength, PasswordLength, Username, Password, LoggedIn
    }

    private int usernameLength, passwordLength;
    /*packet*/ String username, password;
    private State state = State.UsernameLength;
    /*package*/ byte rights = 0;

    public Packet0Login(Connection c) {
        super(c);
    }

    @Override
    public byte id() {
        return 0;
    }

    @Override
    protected boolean receiveData() throws InvalidPacketException, TimeoutException {
        switch(state) {
            case UsernameLength:
                if(socket.bytesAvailable() < 4) //int
                    return false;
                usernameLength = data.readInt();
                state = State.PasswordLength;
            case PasswordLength:
                if(socket.bytesAvailable() < 4) //int
                    return false;
                passwordLength = data.readInt();
                state = State.Username;
            case Username:
                if(socket.bytesAvailable() < usernameLength)
                    return false;
                username = socket.read(usernameLength).toString();
                state = State.Password;
            case Password:
                if(socket.bytesAvailable() < passwordLength)
                    return false;
                password = socket.read(passwordLength).toString();
                state = State.LoggedIn;
        }

        QByteArray res = new QByteArray();
        res.append(rights = Database.hasUser(username, password));
        sendData(res);

        if(rights > 0)
            System.out.println(username + " logged in");
        else
            System.out.println("Failed to log in as " + username);

        return true;
    }

}
