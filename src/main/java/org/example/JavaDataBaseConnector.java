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
    public UserDataTuple selectBylogin(String login) throws SQLException {
        String command = "Select * from newstudents Where login = '"+login+"';";
        ResultSet resultSet = statement.executeQuery(command);
        if (resultSet.getString(1).isEmpty() || resultSet.getString(1) == null){
            return null;
        }
        String curLg = resultSet.getString(1);
        String password = resultSet.getString(2);
        String nickname = resultSet.getString(3);

        return new UserDataTuple(curLg,password,nickname);

    }
    @Override
    public int insertNew(UserDataTuple NeWuser)throws SQLException{
        String command = "Insert into newstudents(login,password,nickname) value"+
                "('"+NeWuser.getLogin()+"',"+
                "('"+NeWuser.getPassword()+"',"+
                "('"+NeWuser.getNick()+"');";
        return statement.executeUpdate(command);
    }

    @Override
    public boolean chngNick(String newNick, String currentNick)throws SQLException {
        String command = "Select count(*) from newstudents Where nick = '"+newNick+"';";
        ResultSet resultSet = statement.executeQuery(command);
        if(resultSet.getInt(1) != 0){
            return false;
        }
        String command2 = "Update newstudents set nick = '"+newNick+"' Where nick = '"+currentNick+"';";
        statement.executeUpdate(command2);
        return true;
    }

}
