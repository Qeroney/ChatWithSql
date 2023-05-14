package org.example;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MyServer {

    private final int PORT = 8189;

    private List<ClientHandller> clients;
    private AuthServicce authService;

    public AuthServicce getAuthService() {
        return authService;
    }


    public MyServer() {
        try (ServerSocket server = new ServerSocket(PORT)) {
            authService = new InMemoryAuth();
            clients = new ArrayList<>();
            while (true) {
                System.out.println("Сервер ожидает подключения");
                Socket socket = server.accept();
                System.out.println("Клиент подключился: " + socket.getInetAddress());
                new ClientHandller(this, socket);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Ошибка в работе сервера");
        }
    }

    public synchronized boolean isNickBusy(String nickname) {
        for (ClientHandller client : clients) {
            if (client.getCurrentNick().equals(nickname)) {
                return true;
            }
        }
        return false;
    }


    public synchronized void broadcastMsg(String msg){
        for (ClientHandller client : clients) {
            client.sendMsg(msg);
        }
    }

    public synchronized void unsubscribe (ClientHandller client){
        clients.remove(client);
    }

    public synchronized void subscribe (ClientHandller client){
        clients.add(client);
    }

    public synchronized void whisper(String nick, String text) {
        for (ClientHandller client : clients) {
            if (client.getCurrentNick().equals(nick)){
                client.sendMsg(text);
            }
        }
    }
}