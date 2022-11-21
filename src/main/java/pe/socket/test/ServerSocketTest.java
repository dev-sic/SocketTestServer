package pe.socket.test;

import pe.socket.test.objects.User;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ServerSocketTest {
//    private List<Room> list = new ArrayList<>();
    public static ArrayList<User> users = new ArrayList<>();
    public void listener() throws IOException {
        ServerSocket serverSocket = new ServerSocket(3333);
        /**
         * 첫번째 스레드 (메인 스레드)
         */
        while (true){
            Socket socket = serverSocket.accept();
            new AcceptThread(socket).start();
            System.out.println("someone connected");
        }
    }
}
