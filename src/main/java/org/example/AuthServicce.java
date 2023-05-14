package org.example;

import java.util.List;

public interface AuthServicce {

    boolean authenticate(String login, String password);

    String getNick(String login);

    boolean changeNick(String newNick,ClientHandller client);
    boolean reg(String login,String password,String nickname);

}
