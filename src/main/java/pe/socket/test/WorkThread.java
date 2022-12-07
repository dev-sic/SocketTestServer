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

public class WorkThread extends Thread{
    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;

    public static List<WorkSocket> WORK_SOCKET_LIST;

    public WorkThread(){

    }
    public WorkThread(Socket socket){
        this.socket = socket;
    }
    public void excuteThread(){
        try {
            System.out.println("executeThread() 실행됨");
            while (true) {
                synchronized (WORK_SOCKET_LIST) {
                    //TODO 각 WorkThread가 WORK_SOCKET_LIST를 바라보고 있는데, 겹치지 않고 일을 할수 있는지? -> synchronized로 가능한것인지?
                    if (WORK_SOCKET_LIST.size() > 0) {
                        System.out.println("사이즈 : " + WORK_SOCKET_LIST.size());
                        Socket socket = WORK_SOCKET_LIST.get(0).socket;
                        WORK_SOCKET_LIST.remove(0);

                        System.out.println("socket available : " + socket.getInputStream().available());

                        inputStream = socket.getInputStream();
                        outputStream = socket.getOutputStream();

                        if (inputStream.available() > 0) {
                            //클라이언트에서 보낸 데이터를 받아온다.
                            byte[] headerByte = new byte[Header.HEADER_LENGTH]; //헤더
                            inputStream.read(headerByte);
                            Header header = new Header(headerByte);

                            byte[] dataByte = new byte[header.length]; //데이터
                            inputStream.read(dataByte);


                            if (header.code == Header.QUIT) {
                                socket.close();
                                return;
                            } else if (header.code == Header.CONNECT) {

                                //json 파싱하여 유저 정보를 유저 리스트에 추가
                                User user = new Gson().fromJson(new String(dataByte, StandardCharsets.UTF_8), User.class);
                                user.socket = socket;
                                Room room = new Room();

                                users.forEach(user1 -> {
                                    if(user1.room == null){
                                        System.out.println("forEach");
                                        user1.room = room;
                                        room.users.add(user1);
                                        return;
                                    }
                                });
                                users.add(user);
                                if(room.users.size() > 0){
                                    System.out.println("방 생성!");
                                    room.users.add(user);
                                    rooms.add(room);
                                    user.room = room;
                                    // TODO: 2022/11/30 방에 있다는 사실을 프로토콜 정의 클라이언트도 받을 준비
                                    // 클라이언트에게 방에 들어왔다는 사실을 알려주는게 핵심.

                                    for (User user1 : room.users) {
                                        Header h = new Header();
                                        h.code = Header.ROOM_IN;
                                        h.length = 0;
                                        h.hash = "hash";
                                        user1.socket.getOutputStream().write(h.getHeader());
                                    }
                                    //매칭된 유저에게도 신호를 보내줘야함.

                                }

                            } else if (header.code == Header.MESSAGE) {

                            }

                        } else {
                            //클라이언트로 보낼때
                        }

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
