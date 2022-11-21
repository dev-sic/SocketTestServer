package pe.socket.test;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");

        ServerSocketTest serverSocketTest = new ServerSocketTest();
        try {
            serverSocketTest.listener();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}