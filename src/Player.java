import java.nio.channels.SocketChannel;

/**
 * @author Matteo Piergiovanni
 * @version Alfa
 */

public class Player
{
    private String name;
    private SocketChannel socket;
    private int color;
    private Point2f position;
    private float direction;                        //?
    private float speed;                              //?
    private int id;


    public Player(SocketChannel sc, int id){
        this.id = id;
        socket = sc;
    }

    public SocketChannel getSocket(){
        return socket;
    }

    public int getId(){
        return id;
    }
}
