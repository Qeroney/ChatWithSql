package org.example;

public interface AuthService {

    boolean authenticate(String login, String password);

    String getNick(String login);

    boolean changeNick(String newNick, ClientHandler client);
    boolean reg(String login,String password,String nickname);

}
