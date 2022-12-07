package pe.socket.test;

import pe.socket.test.util.InputStreamThread;

import java.io.IOException;

public class Main {
    private static WorkThread[] acceptThreads;
    public static void main(String[] args) {
        System.out.println("main 클래스 실행");

        ServerSocketTest serverSocketTest = new ServerSocketTest();
        try {
            InputStreamThread inputStreamThread = new InputStreamThread();
            inputStreamThread.start();

            int coreNum = Runtime.getRuntime().availableProcessors() * 2;
            acceptThreads = new WorkThread[coreNum-2];

            for(int i=0; i < coreNum - 2; i++){
                acceptThreads[i] = new WorkThread();
                acceptThreads[i].start();
            }

            serverSocketTest.listener(inputStreamThread);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}