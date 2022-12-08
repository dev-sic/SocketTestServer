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

import static pe.socket.test.ServerSocketTest.rooms;
import static pe.socket.test.ServerSocketTest.users;
import static pe.socket.test.objects.Header.*;
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
            System.out.println("WorkThread > executeThread() 실행됨");
            while (true) {
                WorkSocket socket = null;
                synchronized (WORK_SOCKET_LIST) {
                    if (WORK_SOCKET_LIST.size() > 0) {
                        System.out.println("WorkThread > 사이즈 : " + WORK_SOCKET_LIST.size());
                        synchronized (WORK_SOCKET_LIST.get(0)) {
                            socket = WORK_SOCKET_LIST.get(0);
                        }
                        WORK_SOCKET_LIST.remove(0);
                    }
                }

                if (socket != null) {
//                    System.out.println("socket available : " + socket.socket.getInputStream().available());

                    inputStream = socket.socket.getInputStream();
                    outputStream = socket.socket.getOutputStream();

                    System.out.println("WorkThread > inputStream.available() : " + inputStream.available());
                    System.out.println("WorkThread > socket.data : " + socket.data);


                    if (inputStream.available() > 0) {
                        //클라이언트에서 보낸 데이터를 받아온다.
                        byte[] headerByte = new byte[HEADER_LENGTH]; //헤더
                        inputStream.read(headerByte);
                        Header header = new Header(headerByte);

                        byte[] dataByte = new byte[header.length]; //데이터
                        inputStream.read(dataByte);


                        if (header.code == QUIT) {
                            System.out.println("WorkThread > code QUIT");
                            socket.socket.close();
                            socket.isWorking = false;
                            //TODO sockets에서 아예 삭제해야 하는게 좋을지?
                            return;
                        } else if (header.code == CONNECT) {
                            System.out.println("WorkThread > code CONNECT");

                            //json 파싱하여 유저 정보를 유저 리스트에 추가
                            User user = new Gson().fromJson(new String(dataByte, StandardCharsets.UTF_8), User.class);
                            socket.user = user;

                            Header h = new Header();
                            h.code = ROOM_IN;
                            h.length = 0;
                            h.hash = "hash";

                            Room room = new Room();

                            users.forEach(user1 -> {
                                if (user1.room == null) {
                                    System.out.println("WorkThread > forEach");
                                    user1.room = room;
                                    room.users.add(user1);
                                    return;
                                }
                            });
                            users.add(user);



                            if (room.users.size() > 0) {
                                System.out.println("WorkThread > 방 생성!");
                                room.users.add(user);
                                rooms.add(room);
                                user.room = room;
                                // TODO: 2022/11/30 방에 있다는 사실을 프로토콜 정의 클라이언트도 받을 준비
                                // 클라이언트에게 방에 들어왔다는 사실을 알려주는게 핵심.


                                socket.socket.getOutputStream().write(h.getHeader());
                            }
                            synchronized (sockets){
                                socket.isWorking = false;

                                WORK_SOCKET_LIST.forEach(workSocket -> {
                                    System.out.println("WorkThread > WORK_SOCKET_LIST isWorking 확인 : "+workSocket.isWorking);
                                });
                                for(int i = 0; i< sockets.size(); i++){
                                    System.out.println("WorkThread > sockets isWorking 확인 : " + i+1 + "번째 : " + sockets.get(i).isWorking);
                                }
                            }

                            synchronized (sockets) {
                                sockets.forEach(workSocket -> {
                                    if(room.users.size() > 0) {
                                        System.out.println("WorkThread > sockets에서 user를 찾아서 data를 추가한다.");
                                        System.out.println("WorkThread > workSocket.user.name : " + workSocket.user.name);
                                        System.out.println("WorkThread > room.users.get(0).name(룸 첫번째 연결 유저 이름) : " + room.users.get(0).name);
                                        System.out.println("WorkThread > 같은지 여부 : "+ (workSocket.user == room.users.get(0)));
                                        if (workSocket.user == room.users.get(0)) {
                                            System.out.println("WorkThread > 같다!"+ (workSocket.user == room.users.get(0)));
                                            workSocket.data = h.getHeader();
                                        }
                                    }
                                });
                            }
                        } else if (header.code == MESSAGE) {
                            System.out.println("WorkThread > code MESSAGE");
                            synchronized (sockets) {
                                socket.isWorking = false;
                            }
                        }


                    } else if(socket.data != null){
                        System.out.println("WorkThread > write ***");
                        outputStream.write(socket.data);
                        socket.data = null;
                        socket.isWorking = false;
                    } else {
                        System.out.println("WorkThread > 이슈 : " + socket.user);
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
