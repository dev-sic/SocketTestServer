package pe.socket.test;

import com.google.gson.Gson;
import pe.socket.test.objects.Header;
import pe.socket.test.objects.User;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import static pe.socket.test.ServerSocketTest.users;

public class AcceptThread extends Thread{
    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;

    public AcceptThread(Socket socket){
        this.socket = socket;
    }
    public void excuteThread(){
        try {
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();

            while (true) {
                if(!socket.isClosed()) {
                    int receiveNum = inputStream.available();
                    if(receiveNum > 0) {
                        System.out.println("receive num :" +receiveNum);
                        /** 문자열의 크기
                         * 한글 : 3byte
                         * 영문,숫자,특수기호 : 1byte
                         */

                        byte[] headerByte = new byte[Header.HEADER_LENGTH];
                        inputStream.read(headerByte);
                        Header header = new Header(headerByte);
                        if(header.code == 'Q'){
                            socket.close();

                            return;
                        }

                        byte[] dataByte = new byte[header.length];
                        System.out.println("available num : " + inputStream.available());
                        inputStream.read(dataByte);


                        System.out.println("data length : " + dataByte.length);

                        ByteBuffer byteBuffer = ByteBuffer.allocate(dataByte.length);
                        byteBuffer.put(dataByte);
                        byteBuffer.position(0);
                        System.out.println(new String(byteBuffer.array(), StandardCharsets.UTF_8));

                        if(header.code == 'C') {
                            //json 파싱하여 유저 정보를 유저 리스트에 추가
                            User user = new Gson().fromJson(new String(byteBuffer.array(), StandardCharsets.UTF_8), User.class);
                            users.add(user);
                            System.out.println("size : " + users.size() + ", " + users);
                            for (User u : users) {
                                System.out.println(u.name);
                            }
                        }

                        if(header.code != 'M') {
                            //연결되었다는 신호를 클라이언트에 전달
                            Header resHeader = new Header();
                            resHeader.code = 'R';
                            resHeader.length = 0;
                            resHeader.hash = "hash";

                            outputStream.write(resHeader.getHeader());
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
