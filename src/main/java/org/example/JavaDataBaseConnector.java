package org.example;

import java.sql.*;

public class JavaDataBaseConnector implements DataBaseConnector{
    private final Connection connection;
    private final Statement statement;
    public JavaDataBaseConnector() throws SQLException{
        connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1/new_schema", "root", "vvT54R9v");
        statement = connection.createStatement();
    }


    @Override
    public UserDataTuple selectByLogin(String login) throws SQLException {
        ResultSet resultSet = statement.executeQuery("select * from notstudents where login='"+login+"';");
        if (!resultSet.next()){
            return null;
        }
        String curLg = resultSet.getString(1);
        String password = resultSet.getString(2);
        String nickname = resultSet.getString(3);

        return new UserDataTuple(curLg,password,nickname);

    }
    @Override
    public int insertNew(UserDataTuple NewUser) throws SQLException{
        return statement.executeUpdate( "Insert into notstudents(login,password,nick) value"+
                "('"+ NewUser.getLogin()+"',"+ "'"+ NewUser.getPassword()+"',"+ "'"+ NewUser.getNick()+"');");
    }

    @Override
    public boolean chnNick(String newNick, String currentNick) throws SQLException {
        ResultSet resultSet = statement.executeQuery ( "Select count(*) from notstudents Where nick = '"+newNick+"';");
        if(resultSet.getInt(1) != 0){
            return false;
        }
        statement.executeUpdate ( "Update notstudents set nick = '"+newNick+"' Where nick = '"+currentNick+"';");
        return true;
    }
}
