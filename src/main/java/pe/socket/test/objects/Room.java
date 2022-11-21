package pe.socket.test.objects;

import pe.socket.test.objects.User;

import java.util.ArrayList;
import java.util.List;

public class Room {
    public String subTitle;
    public String title;
    public int roomIndex;

    public List<User> users = new ArrayList<>();


    Room(){
    }
}
