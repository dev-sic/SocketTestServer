package pe.socket.test.util;

import pe.socket.test.WorkThread;
import pe.socket.test.objects.WorkSocket;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class InputStreamThread extends Thread {
    public static List<WorkSocket> sockets = new ArrayList<>();

    public void addSocket(Socket socket) {
        synchronized (sockets) {
            sockets.add(new WorkSocket(socket));
        }
    }

    @Override
    public void run() {
        super.run();
        if (WorkThread.WORK_SOCKET_LIST == null) {
            WorkThread.WORK_SOCKET_LIST = new ArrayList<>();
        }
        while (true) {
            synchronized (sockets) {
                //TODO isWorking을 다시 false로 만들어야 하는지?
                sockets.forEach(work -> {
                    try {
                        System.out.println("work isworking data size " + work.isWorking + ", " + work.data);

                        if (!work.isWorking && work.data != null) {
                            synchronized (WorkThread.WORK_SOCKET_LIST) {
                                WorkThread.WORK_SOCKET_LIST.add(work);
                                System.out.println("WORK Socket list : " + WorkThread.WORK_SOCKET_LIST.size());
                            }
                            synchronized (work){
                                work.isWorking = true;
                            }
                        }
                        if (!work.isWorking && work.socket.getInputStream().available() > 0) {
                            System.out.println("available() : " + work.socket.getInputStream().available());
                            synchronized (WorkThread.WORK_SOCKET_LIST) {
                                WorkThread.WORK_SOCKET_LIST.add(work);
                            }
                            synchronized (work){
                                work.isWorking = true;
                            }
                        }

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        }
    }
}
