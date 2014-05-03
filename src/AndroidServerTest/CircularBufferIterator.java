package AndroidServerTest;

/**
 * Created by ShilleR on 3/5/14.
 */
public class CircularBufferIterator {
    private int end;
    private CarAction[] buffer;
    private int index;
    private int bufferSize;

    public CircularBufferIterator(int begin, int end, CarAction[] buffer, int bufferSize) {
        this.index = begin-1;
        this.end = end;
        this.buffer = buffer;
        this.bufferSize = bufferSize;
    }

    public boolean hasNext(){
        if(index!=(end+bufferSize-1)%bufferSize){
            return true;
        }
        return false;
    }

    public CarAction next(){
        index = (index+1)%bufferSize;
        return buffer[index];
    }
}
