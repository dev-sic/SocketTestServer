package com.example.socket_test;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Thread(()-> {
            //Socket
            Socket socket = null;
            //try catch 로 IOException
            try {
                //Socket IP와 Port 설정.
                socket = new Socket("localhost", 3333);
                //InputStream으로 데이터를 받아온다.
                InputStream inputStream = socket.getInputStream();

                //OutputStream으로 데이터를 보낸다.( write() 함수로 OutputStream에 데이터를 실어서 보낸다. )
                OutputStream outputStream = socket.getOutputStream();
                outputStream.write("hello".getBytes(StandardCharsets.UTF_8));

                //스레드 할당
                //while(true){

                while(true){
                    //inputStream에 available() 함수로 이용가능한 데이터가 있는지 확인하고, 있을경우 실행한다.
                    if(inputStream.available() > 0){
                        //데이터를 통신할때 첫 부분에 header가 담겨져 오고, header에서 데이터의 크기(length) 또한 확인할수 있다.
                        byte[] header = new byte[21];
                        inputStream.read(header);

                        //byteBuffer - 버퍼를 allocate() 함수로 지정한 크기만큼 만들어 놓는다.
                        ByteBuffer byteBuffer = ByteBuffer.allocate(21);
                        //만든 버퍼에 inputStream으로 받았던 데이터인 header 바이트 배열을 담는다.
                        byteBuffer.put(header);
                        byteBuffer.position(0);
                        byteBuffer.get(); //하나의 비트를 가져온다.
                        int length = byteBuffer.getInt(); //length, 4개의 비트를 가져온다.

                        // length 찰때까지 반복.
                        ByteBuffer dataByteBuffer = ByteBuffer.allocate(length);

                        while( dataByteBuffer.position() < length){
                            byte[] dataAvailable = new byte[inputStream.available()];
                            inputStream.read(dataAvailable);
                            dataByteBuffer.put(dataAvailable);
                        }
                        outputStream.write("end".getBytes(StandardCharsets.UTF_8));
                    }

                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();

    }
}