package pe.socket.test.util;

import pe.socket.test.objects.WorkSocket;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static pe.socket.test.Main.acceptThreads;
import static pe.socket.test.WorkThread.WORK_SOCKET_LIST;

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
        if (WORK_SOCKET_LIST == null) {
            WORK_SOCKET_LIST = new ArrayList<>();
        }
        while (true) {
            synchronized (sockets) {
                sockets.forEach(work -> {
                    try {
//                        System.out.println("work isworking data size " + work.isWorking + ", " + work.data);
                        //output할 데이터가 있을 경우
                        if (work.socket != null && !work.socket.isClosed()) {
                            if (!work.isWorking && work.data != null) {
                                synchronized (WORK_SOCKET_LIST) {
                                    WORK_SOCKET_LIST.add(work);
                                    System.out.println("InputStreamThread > WORK_SOCKET_LIST.size() : " + WORK_SOCKET_LIST.size());
                                }
                                work.isWorking = true;
                            }

                            //input할 데이터가 있을 경우
                            if (!work.isWorking && work.socket.getInputStream().available() > 0) {
                                System.out.println("InputStreamThread > getInputStream().available() : " + work.socket.getInputStream().available());
                                System.out.println("InputStreamThread > WORK_SOCKET_LIST.size() : " + WORK_SOCKET_LIST.size());
                                System.out.println("InputStreamThread > sockets.size() : " + sockets.size());
                                System.out.println("InputStreamThread > acceptThreads.length : " + acceptThreads.length);
                                synchronized (WORK_SOCKET_LIST) {
                                    WORK_SOCKET_LIST.add(work);
                                }
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
