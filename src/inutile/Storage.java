package inutile;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;

/**
 * Created by ShilleR on 21/03/14.
 */
public class Storage {
    private static Storage s;

    private Selector selector;
    private ArrayList<SelectionKey> keys;

    private Storage(){
        try {
            selector = Selector.open();
        } catch (IOException e) {
            e.printStackTrace();
        }
        keys = new ArrayList<SelectionKey>();

    }

    public static Storage getInstance(){
        if(s==null){
            s= new Storage();
        }
        return s;
    }

    public void addSocketChannel(SocketChannel sc){
        try{
            System.out.println("asdfasdfasdf");
            sc.configureBlocking(false);
            System.out.println("qwerqwerqwer");
            selector.wakeup();
            sc.register(selector, SelectionKey.OP_READ);
            System.out.println("poiupoiu");
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public ArrayList<SelectionKey> getKeys(){
        return  keys;
    }

    public Selector getSelector(){
        return selector;
    }
}
