package mumble.tcp.helper;

import MumbleProto.Mumble;
import com.google.protobuf.MessageLite;
import mumble.protobuf.PackageType;
import mumble.protobuf.container.Message;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MessageSender implements Runnable{
    private OutputStream outputStream;
    private final BlockingQueue<Message> output = new ArrayBlockingQueue<>(20);

    public MessageSender(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void sendPing() {
        Mumble.Ping.Builder ping = Mumble.Ping.newBuilder();
        ping.setTimestamp(System.currentTimeMillis() / 1000);
        addToQueue(PackageType.Ping, ping.build());
    }

    public void sendVersion() {
        Mumble.Version.Builder b = Mumble.Version.newBuilder();
        b.setVersion((1 << 16) | (3 << 8));
        b.setRelease("1.3.0");
        b.setOs("WinDOS");
        b.setOsVersion("11");
        addToQueue(PackageType.Version, b.build());
    }

    public void sendAuth(String username){
        Mumble.Authenticate.Builder auth = Mumble.Authenticate.newBuilder();
        auth.setUsername(username);
        addToQueue(PackageType.Authenticate, auth.build());
    }

    private void addToQueue(PackageType id, MessageLite message) {
        try {
            output.put(new Message(id, message));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private boolean createMessage(MessageLite message, int id) {
        try {
            byte[] bytes = ByteBuffer.allocate(6).putShort((short) id).putInt(message.getSerializedSize()).array();
            outputStream.write(bytes);
            message.writeTo(outputStream);
            outputStream.flush();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    @Override
    public void run() {
        while(true) {
            try {
                Message currentMessage = output.take();
                createMessage(currentMessage.getMessage(), currentMessage.getId());
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }
    }
}
