package pe.socket.test.objects; //pe.socket.test.objects 패키지

import java.nio.ByteBuffer; //ByteBuffer 객체를 사용하기 위한 임포트

public class Header { //Header 클래스
    public static final int HEADER_LENGTH = 10; //헤더 길이 (코드값 char 2, 데이터 길이 int 4, hash 길이 4)
    public char code; //메시지 코드값
    public int length; //데이터의 길이
    public String hash; //해쉬 키값

    //Header를 바이트배열 형식으로 가져오기
    public byte[] getHeader(){
        ByteBuffer byteBuffer = ByteBuffer.allocate(Header.HEADER_LENGTH); //버퍼에 길이 10 할당
        byteBuffer.putChar(this.code); //버퍼에 코드값 추가
        byteBuffer.putInt(this.length); //버퍼에 데이터길이 추가
        byteBuffer.put(this.hash.getBytes()); //버퍼에 해쉬키값 추가
        byteBuffer.position(0); //버퍼 위치를 다시 0으로 설정
        return byteBuffer.array(); //바이트 배열 반환
    }

    //Header 생성
    public Header(byte[] headerData){ //바이트배열로된 headerData를 매개변수로 받는다
        ByteBuffer byteBuffer = ByteBuffer.allocate(Header.HEADER_LENGTH); //버퍼에 길이 10 할당
        byteBuffer.put(headerData); //버퍼에 바이트배열로된 헤더 데이터 추가
        byteBuffer.position(0); //버퍼 위치를 다시 0으로 설정
        this.code = byteBuffer.getChar(); //버퍼에서 코드값 가져오기
        this.length = byteBuffer.getInt(); //버퍼에서 데이터길이 값 가져오기
        byte[] hashByte = new byte[4];
        byteBuffer.get(hashByte); //버퍼에서 hashByte 바이트배열로 4바이트만큼 가져오기
        this.hash = new String(hashByte); //바이트배열을 문자열로 만들어서 해쉬키값 가져오기
    }

    public Header(){

    }
}
