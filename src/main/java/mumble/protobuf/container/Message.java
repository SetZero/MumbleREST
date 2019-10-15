package mumble.protobuf.container;

import com.google.protobuf.MessageLite;
import mumble.protobuf.PackageType;

public class Message {
    private int id;
    private MessageLite message;
    private byte[] raw;

    public Message(PackageType id, MessageLite message) {
        this.id = id.getId();
        this.message = message;
    }

    public Message(PackageType id, MessageLite message, byte[] raw) {
        this.id = id.getId();
        this.message = message;
        this.raw = raw;
    }

    public Message(PackageType id, byte[] raw) {
        this.id = id.getId();
        this.raw = raw;
    }

    public int getId() {
        return id;
    }

    public MessageLite getMessage() {
        return message;
    }

    public byte[] getRaw() {
        return raw;
    }
}
