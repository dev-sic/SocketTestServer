package pe.socket.test;

import pe.socket.test.objects.Room;
import pe.socket.test.objects.User;
import pe.socket.test.util.InputStreamThread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ServerSocketTest {
//    private List<Room> list = new ArrayList<>();
    public static ArrayList<User> users = new ArrayList<>();
    public static ArrayList<Room> rooms = new ArrayList<>();
    public void listener(InputStreamThread inputStreamThread) throws IOException {
        ServerSocket serverSocket = new ServerSocket(3333);
        /**
         * 첫번째 스레드 (메인 스레드)
         */
        while (true){
            Socket socket = serverSocket.accept();
            inputStreamThread.addSocket(socket);
            System.out.println("someone connected");
        }
    }


}
