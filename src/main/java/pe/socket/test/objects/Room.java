package pe.socket.test.objects;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Room {
    public String subTitle;
    public String title;
    public int roomIndex;


    public List<User> users = new ArrayList<>();

    public Room(){
    }
}
