package AndroidServerTest;

/**
 * Created by ShilleR on 3/5/14.
 */
public class CircularBuffer {
    private int begin;
    private int end;
    private CarAction[] buffer;
    private int bufferSize;
    private int timeWindows;

    public CircularBuffer(int bufferSize, int timeWindows)  {
        this.bufferSize = bufferSize;
        this.timeWindows = timeWindows;
        begin = 0;
        end = 0;
        buffer = new CarAction[bufferSize];
    }

    public synchronized void insert(CarAction action, long time){
        synchronized (this) {
            long minTime = time - timeWindows;
            if (action.getTime() < minTime) {
                return;
            }
            buffer[end] = action;
            end = (end + 1) % bufferSize;
            int i = (end + bufferSize - 1) % bufferSize;
            int j = (end + bufferSize - 2) % bufferSize;
            while (i != begin) {
                if (buffer[i].getTime() < buffer[j].getTime()) {
                    CarAction c = buffer[i];
                    buffer[i] = buffer[j];
                    buffer[j] = c;
                } else {
                    break;
                }
                i = j;
                j = (j + bufferSize - 1) % bufferSize;
            }
        }
    }

    public void clear(long time){
        long minTime = time - timeWindows;
        while(begin!=end) {
            if(buffer[begin].getTime()<minTime){
                begin++;
            }else{
                break;
            }
        }
    }

    public CircularBufferIterator getIterator(){
        return new CircularBufferIterator(begin,end,buffer, bufferSize);
    }



}
