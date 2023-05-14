package org.example;

import java.sql.SQLException;

public interface DataBaseConnector  {

    UserDataTuple selectBylogin(String login) throws SQLException;

    int insertNew(UserDataTuple NeWuser) throws SQLException;

    boolean chngNick(String newNick,String currentNick) throws SQLException;
}
