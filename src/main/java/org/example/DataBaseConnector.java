package org.example;

import java.sql.SQLException;

public interface DataBaseConnector  {

    UserDataTuple selectByLogin(String login) throws SQLException;

    int insertNew(UserDataTuple NewUser) throws SQLException;

    boolean chnNick(String newNick, String currentNick) throws SQLException;

}
