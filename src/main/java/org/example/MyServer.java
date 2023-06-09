package org.example;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MyServer {

    private final int PORT = 8189;

    private List<ClientHandler> clients;
    private AuthService authService;

    public AuthService getAuthService() {
        return authService;
    }


    public MyServer(){
        try (ServerSocket server = new ServerSocket(PORT)) {
            DataBaseConnector connector = new JavaDataBaseConnector();
            authService = new DataBaseAuthServiceImpl(connector);
            clients = new ArrayList<>();
            while (true) {
                System.out.println("Сервер ожидает подключения");
                Socket socket = server.accept();
                System.out.println("Клиент подключился: " + socket.getInetAddress());
                new ClientHandler(this, socket);
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
            System.out.println("Ошибка в работе сервера");
        }
    }

    public synchronized boolean isNickBusy(String nickname) {
        for (ClientHandler client : clients) {
            if (client.getCurrentNick().equals(nickname)) {
                return true;
            }
        }
        return false;
    }

    public synchronized void broadcastMsg(String msg){
        for (ClientHandler client : clients) {
            client.sendMsg(msg);
        }
    }

    public synchronized void unsubscribe (ClientHandler client){
        clients.remove(client);
    }

    public synchronized void subscribe (ClientHandler client){
        clients.add(client);
    }

    public synchronized void whisper(String nick, String text) {
        for (ClientHandler client : clients) {
            if (client.getCurrentNick().equals(nick)){
                client.sendMsg(text);
            }
        }
    }
}