package org.example;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;

public class ClientHandler {
    private MyServer server;
    private Socket socket;
    private DataOutputStream out;
    private DataInputStream in;
    private String currentNick;

    public String getCurrentNick() {
        return currentNick;
    }

    public ClientHandler(MyServer server, Socket client) {
        try {
            this.server = server;
            this.socket = client;
            this.in = new DataInputStream(client.getInputStream());
            this.out = new DataOutputStream(client.getOutputStream());

            new Thread(() -> {
                try {
                    authentication();
                    readMessage();
                } catch (IOException | SQLException e) {
                    e.printStackTrace();
                } finally {
                    closeConnection();
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readMessage() throws IOException {
        while (true) {
            String clientMessage = in.readUTF();
            if (clientMessage.equals("/end")) {
                closeConnection();
            }else if(clientMessage.startsWith("/changeNick")){
                String[] parts = clientMessage.split(" ");
                String newNick = parts[1];
                changeNick(newNick);
                continue;
            }else if(clientMessage.startsWith("/w")){
                String[] indMess = clientMessage.split(" ",3);
                String nick = indMess[1];
                whisper(nick, indMess[2]);
                continue;
            }
            server.broadcastMsg(currentNick + ":" + clientMessage);
        }
    }
    private void whisper(String nick, String text) {
        if(server.isNickBusy(nick)){
            server.whisper(nick,text);
        }else {
            sendMsg("Данного юзера не существует");
        }
    }

    private void changeNick(String newNick) {
        AuthService authService = server.getAuthService();
        if(authService.changeNick(newNick,this)){
            currentNick = newNick;
            sendMsg("Ник поменян");
            return;
        }
        sendMsg("Что то пошло не так");
    }

    public void sendMsg(String msg) {
        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void authentication() throws IOException, SQLException {
        AuthService authService = server.getAuthService();
        while (true) {
            String s = in.readUTF();
            if (s.startsWith("/auth")) {
                String[] parts = s.split("\\s");
                String login = parts[1];
                String password = parts[2];
                if (authService.authenticate(login, password)) {
                    String nickname = authService.getNick(login);
                    if (server.isNickBusy(nickname)) {
                        sendMsg("Данный ник занят\n");
                        continue;
                    }
                    this.currentNick = nickname;
                    server.subscribe(this);
                    System.out.println("Авторизация прошла успешно");
                    return;
                }
            } else if (s.startsWith("/reg")) {
                String[] parts1 = s.split("\\s");
                String login1 = parts1[1];
                String password1 = parts1[2];
                String nick = parts1[3];
                regis(login1,password1,nick);
            } else {
                out.writeUTF("Команда не распознана.Авторизуйтесь.: /auth login pass или /reg login pass nick(DB)");
            }
        }
    }

    private void regis(String login,String password,String nick) throws IOException {
        if (server.getAuthService().reg(login, password, nick)) {
            out.writeUTF("Регистрация прошла успешно.");
        } else {
            out.writeUTF("Регистрация провалилась, аккаунт с таким логином уже существует \n" +
                        "или произошла ошибка во время попытки регистрации");
        }
    }

    public void closeConnection() {
        server.unsubscribe(this);
        server.broadcastMsg(currentNick + "exit");
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
