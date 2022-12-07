package pe.socket.test.objects;

import java.net.Socket;

public class WorkSocket {
    public boolean isWorking = false;
    public Socket socket = null;

    public WorkSocket(Socket socket){
        this.socket = socket;
    }
}
