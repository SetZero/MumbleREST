package mumble.tcp.helper;

import MumbleProto.Mumble;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageLite;
import mumble.protobuf.PackageType;
import mumble.protobuf.container.Message;
import org.apache.logging.log4j.Level;
import utils.logging.Log;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class MessageReciever implements Runnable{
    private InputStream inputStream;
    private final BlockingQueue<Message> input = new ArrayBlockingQueue<>(20);

    public MessageReciever(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public static int unsignedToBytes(byte b) {
        return b & 0xFF;
    }

    private Message getMessage() throws IOException {
        byte[] metadata = inputStream.readNBytes(6);


        if(metadata.length >= 6) {
            short id = (short) ((metadata[0] << 8) |
                    (metadata[1]));
            int length = (unsignedToBytes(metadata[2]) << 24) |
                            (unsignedToBytes(metadata[3]) << 16) |
                            (unsignedToBytes(metadata[4]) << 8) |
                            unsignedToBytes(metadata[5]);
            return parseMessage(inputStream.readNBytes(length), id);
        } else {
            return null;
        }
    }

    private Message parseMessage(byte[] data, short id) {
        PackageType mID = PackageType.getTypeById(id);
        MessageLite message;

        try {
            Method method = mID.getClazz().getMethod("parseFrom", byte[].class);
            Object obj = method.invoke(null, data);
            if(obj instanceof MessageLite) {
                message = (MessageLite) obj;
                return new Message(mID, message);
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            Log.get().log(Level.WARN, "MESSAGE: " + mID + ", ID: " + id);
        }
        return new Message(mID, null);
    }

    @Override
    public void run() {
        while (true) {
            try {
                Message msg = getMessage();
                if(msg != null)
                    input.put(msg);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }
    }

    public Message getLastMessage() {
        try {
            return input.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
        return null;
    }
}
