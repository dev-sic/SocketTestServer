package pe.socket.test;

import com.google.gson.Gson;
import pe.socket.test.objects.Header;
import pe.socket.test.objects.Room;
import pe.socket.test.objects.User;
import pe.socket.test.objects.WorkSocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static pe.socket.test.ServerSocketTest.rooms;
import static pe.socket.test.ServerSocketTest.users;
import static pe.socket.test.util.InputStreamThread.sockets;

public class WorkThread extends Thread {
    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;

    public static List<WorkSocket> WORK_SOCKET_LIST;

    public WorkThread() {

    }

    public WorkThread(Socket socket) {
        this.socket = socket;
    }

    public void excuteThread() {
        try {
            System.out.println("executeThread() 실행됨");
            while (true) {
                WorkSocket socket = null;
                synchronized (WORK_SOCKET_LIST) {
                    //TODO 각 WorkThread가 WORK_SOCKET_LIST를 바라보고 있는데, 겹치지 않고 일을 할수 있는지? -> synchronized로 가능한것인지?
                    if (WORK_SOCKET_LIST.size() > 0) {
                        System.out.println("사이즈 : " + WORK_SOCKET_LIST.size());
                        synchronized (WORK_SOCKET_LIST.get(0)) {
                            socket = WORK_SOCKET_LIST.get(0);
                        }
                        WORK_SOCKET_LIST.remove(0);
                    }
                }

                if (socket != null) {
                    System.out.println("socket available : " + socket.socket.getInputStream().available());

                    inputStream = socket.socket.getInputStream();
                    outputStream = socket.socket.getOutputStream();



                    if (inputStream.available() > 0) {
                        //클라이언트에서 보낸 데이터를 받아온다.
                        byte[] headerByte = new byte[Header.HEADER_LENGTH]; //헤더
                        inputStream.read(headerByte);
                        Header header = new Header(headerByte);

                        byte[] dataByte = new byte[header.length]; //데이터
                        inputStream.read(dataByte);


                        if (header.code == Header.QUIT) {
                            socket.socket.close();
                            socket.isWorking = false;
                            return;
                        } else if (header.code == Header.CONNECT) {

                            //json 파싱하여 유저 정보를 유저 리스트에 추가
                            User user = new Gson().fromJson(new String(dataByte, StandardCharsets.UTF_8), User.class);
                            socket.user = user;

                            Header h = new Header();
                            h.code = Header.ROOM_IN;
                            h.length = 0;
                            h.hash = "hash";

                            Room room = new Room();

                            users.forEach(user1 -> {
                                if (user1.room == null) {
                                    System.out.println("forEach");
                                    user1.room = room;
                                    room.users.add(user1);
                                    return;
                                }
                            });
                            users.add(user);



                            if (room.users.size() > 0) {
                                System.out.println("방 생성!");
                                room.users.add(user);
                                rooms.add(room);
                                user.room = room;
                                // TODO: 2022/11/30 방에 있다는 사실을 프로토콜 정의 클라이언트도 받을 준비
                                // 클라이언트에게 방에 들어왔다는 사실을 알려주는게 핵심.


                                socket.socket.getOutputStream().write(h.getHeader());
                            }
                            socket.isWorking = false;

                            synchronized (sockets) {
                                sockets.forEach(workSocket -> {

                                    if(room.users.size() > 0) {
                                        System.out.println("workSocket User name: " + workSocket.user.name);
                                        System.out.println("클라이언트로 신호 전송" + room.users.get(0).name);
                                        System.out.println("같은지 여부 : "+ (workSocket.user == room.users.get(0)));
                                        if (workSocket.user == room.users.get(0)) {
                                            workSocket.data = h.getHeader();
                                        }
                                    }
                                });
                            }
                        } else if (header.code == Header.MESSAGE) {

                        }


                    } else if(socket.data != null){
                        System.out.println("write ***");
                        outputStream.write(socket.data);
                        socket.isWorking = false;
                    } else {
                        System.out.println("이슈 : " + socket.user);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        super.run();
        excuteThread();
    }
}
