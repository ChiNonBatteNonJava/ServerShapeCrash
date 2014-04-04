import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

/**
 * Created by matteopiergiovanni on 04.04.14.
 */
public class AlphaClient {
    private SocketChannel sock;


    //constructor
    public AlphaClient() throws IOException {
        this.sock = SocketChannel.open();



    }


    public void socketConnection(String ip, int gate) throws IOException {
        this.sock.connect(new InetSocketAddress(ip, gate));

    }

    public void listRoom(){


    }

    public void joinRoom(){


    }

    public void createRoom(){


    }

    public void manageRoom(){


    }

    public void leftRoom(){


    }

    public void startPlay(){


    }

    public void action(){


    }

    public void endPlay(){


    }

    public void exit(){


    }

}
