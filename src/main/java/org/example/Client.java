package org.example;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private static Socket socket;

    private static DataInputStream in;

    private static DataOutputStream out;

    public static void main(String[] args) {
        try {
            socket = new Socket("192.168.0.102", 8189);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            Scanner sc = new Scanner(System.in);

            Thread tr = new Thread(new Runnable() {
                @Override
                public void run() {
                   try (FileOutputStream outputStream = new FileOutputStream("history.txt", true)) {
                       while (true) {
                           String s = in.readUTF();
                           System.out.println(s);
                           outputStream.write((s + "\n").getBytes());
                       }
                   } catch (FileNotFoundException e) {
                       System.out.println("Что то не так!");
                       throw new RuntimeException(e);
                   } catch (IOException e) {
                       throw new RuntimeException(e);
                   }
                }
            });
            tr.setDaemon(true);
            tr.start();
            while (true) {
                String s = sc.nextLine();
                if (s.equals("/end")) {
                    out.writeUTF("/end");
                    out.close();
                    in.close();
                    socket.close();
                }
                out.writeUTF(s);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}