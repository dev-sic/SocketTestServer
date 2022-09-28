package com.example.socket_test;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerClass {
    //큐방식 FIFO (자료구조) 으로 쌓아놓는다.
    //스택 공부필요.
    //로드 밸런싱 - 카카오톡
    public void listener() throws IOException {
        ServerSocket serverSocket = new ServerSocket(3333);
        Socket socket = serverSocket.accept();

        InputStream inputStream = socket.getInputStream();

        while(true){
            if(inputStream.available() > 0){
                //클라이언트에서 보낸 hello 문자열을 받기위한 바이트 배열
                /** 문자열의 크기
                 * 한글 : 3byte
                 * 영문,숫자,특수기호 : 1byte
                 */
                byte[] data = new byte[5];
                inputStream.read(data);


            }
        }


    }
}
