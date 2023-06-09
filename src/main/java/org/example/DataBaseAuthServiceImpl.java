package org.example;

import java.sql.SQLException;

public class DataBaseAuthServiceImpl implements AuthService {
    public DataBaseConnector connector;

    public DataBaseAuthServiceImpl(DataBaseConnector connector) {
        this.connector = connector;
    }

    @Override
    public boolean authenticate(String login, String password) {

        if(login.isEmpty()){
            return false;
        }
        if (password.isEmpty()){
            return false;
        }
        UserDataTuple user = null;
        try {
            user = connector.selectByLogin(login);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        if(user==null){
            return false;
        }
        if(user.getPassword().equals(password)){
            return true;
        }
        return false;
    }


    @Override
    public String getNick(String login) {
        try {
            UserDataTuple userDataTuple = connector.selectByLogin(login);
            return userDataTuple.getNick();
        }catch (SQLException e){
            e.printStackTrace();
            return " ";
        }
    }

    @Override
    public boolean changeNick(String newNick, ClientHandler client) {
        String currentNick1 = client.getCurrentNick();
        try {
            if (currentNick1.equals(newNick)) {
                return false;
            }
            connector.chnNick(newNick,currentNick1);
        }catch (SQLException e){
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean reg(String login, String password, String nickname) {
        try {
            UserDataTuple userDataTuple = connector.selectByLogin(login);
            if(userDataTuple !=null){
                return false;
            }
            UserDataTuple NewUser = new UserDataTuple(login,password,nickname);
            int i = connector.insertNew(NewUser);
            if(i!=1){
                return false;
            }
            return true;

        }catch (SQLException e){
            e.printStackTrace();
            return false;
        }

    }
}
