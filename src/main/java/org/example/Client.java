package org.example;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private static Socket socket;

    private static DataInputStream in;

    private static DataOutputStream out;

    public static void main(String[] args){
        try {
            socket = new Socket("192.168.0.101",8189);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            Scanner sc = new Scanner(System.in);

            Thread tr = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true){
                        try {
                            String s = in.readUTF();
                            System.out.println(s);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }

                }
            });
            tr.setDaemon(true);
            tr.start();
            while (true){
                String s = sc.nextLine();
                if(s.equals("/end")){
                    out.writeUTF("/end");
                    out.close();
                    in.close();
                    socket.close();
                }
                out.writeUTF(s);
            }
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }
}

