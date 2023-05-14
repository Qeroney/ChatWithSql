package org.example;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandller {
    private MyServer server;
    private Socket socket;
    private DataOutputStream out;
    private DataInputStream in;
    private String currentNick;

    public String getCurrentNick() {
        return currentNick;
    }

    public ClientHandller(MyServer server, Socket client) {
        try {
            this.server = server;
            this.socket = client;
            this.in = new DataInputStream(client.getInputStream());
            this.out = new DataOutputStream(client.getOutputStream());

            new Thread(() -> {
                try {
                    authentication();
                    readMessage();
                } catch (IOException e) {
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
                return;
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
        AuthServicce authService = server.getAuthService();
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

    public void authentication() throws IOException {
        AuthServicce authService = server.getAuthService();
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
            }else {
                System.out.println("Команда не распознана.Авторизуйтесь.: /auth login pass");
            }
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
