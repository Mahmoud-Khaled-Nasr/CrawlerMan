package util;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Channel<E> {

    private BlockingQueue<E> blockingQueue;
    private boolean closed;

    void Channel(){
        closed = false;
        blockingQueue = new LinkedBlockingQueue<>();
    }

    public void put(E e) {
        if(closed) {
            return;
        }
        try {
            blockingQueue.put(e);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
    }

    public E take() {
        try {
            if(closed && blockingQueue.isEmpty()) {
                return null;
            } else {
                E e = blockingQueue.take();
                return e;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException("Error in take function Channel Class");
        }
    }

    public void close() {
        closed = true;
    }

    public boolean isClosed () {
        return closed;
    }

    public void clear() {
        blockingQueue.clear();
    }

}
