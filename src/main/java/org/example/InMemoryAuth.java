package org.example;

import java.util.ArrayList;
import java.util.List;

public class InMemoryAuth implements AuthService {


    private final List<UserDataTuple> userData = new ArrayList<>(List.of(
            new UserDataTuple("user1", "pass1", "Bedolaga"),
            new UserDataTuple("user2", "pass2", "YourBunnyWrote"),
            new UserDataTuple("user3", "pass3", "AgentBob")
    ));

    @Override
    public boolean authenticate(String login, String password) {
        if (login.isEmpty()) {
            return false;
        }
        if (password.isEmpty()) {
            return false;
        }
        for (UserDataTuple user : userData) {
            if (login.equals(user.getLogin())) {
                if (password.equals(user.getPassword())) {
                    return true;
                }
            }
        }
        return false;
    }


    @Override
    public String getNick(String login) {
        for (UserDataTuple user : userData) {
            if (login.equals(user.getLogin())) {
                return user.getNick();
            }
        }
        return null;
    }
    @Override
    public boolean changeNick(String newNick, ClientHandler client) {
        for (UserDataTuple userDatum : userData) {
            if(userDatum.getNick().equals(newNick)){
                return false;
            }
        }
        for (UserDataTuple userDatum : userData) {
            if(userDatum.getNick().equals(client.getCurrentNick())){
                userDatum.setNickname(newNick);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean reg(String login, String password, String nickname) {
        for(UserDataTuple userDataTuple : userData){
            if(userDataTuple.getLogin().equals(login)) {
                return false;
            }
            if (userDataTuple.getNick().equals(nickname)){
                return false;
            }
        }
        UserDataTuple userDataTuple = new UserDataTuple(login, password, nickname);
        userData.add(userDataTuple);
        return true;
    }

}
