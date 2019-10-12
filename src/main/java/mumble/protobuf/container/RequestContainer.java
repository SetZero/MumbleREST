package mumble.protobuf.container;

import org.apache.logging.log4j.Level;
import utils.logging.Log;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class RequestContainer {
    private final BlockingQueue<Message> input;
    private final BlockingQueue<Message> output;

    public RequestContainer(int size) {
        this.input = new ArrayBlockingQueue<>(size);
        this.output = new ArrayBlockingQueue<>(size);
    }

    public void addToSend(Message wrapper) {
        try {
            output.add(wrapper);
        } catch (IllegalStateException e) {
            Log.getInstance().getLogger().log(Level.INFO, "A Client Thread might not be responding fast enough...");
        }
    }

    public void addToRead(Message event) {
        try {
            input.add(event);
        } catch (IllegalStateException e) {
            //Silently drop package
        }
    }

    public Message takeFromSend() {
        try {
            return output.take();
        } catch (InterruptedException e) {
            Log.getInstance().getLogger().log(Level.INFO, "Interrupt request while waiting for send...");
            Thread.currentThread().interrupt();
        }
        return null;
    }

    public Message takeFromRead() {
        try {
            return input.take();
        } catch (InterruptedException e) {
            Log.getInstance().getLogger().log(Level.INFO, "Interrupt request while waiting for read...");
            Thread.currentThread().interrupt();
        }
        return null;
    }

    public int sizeOfRead() {
        return input.size();
    }

    public int sizeOfSend() {
        return output.size();
    }
}
