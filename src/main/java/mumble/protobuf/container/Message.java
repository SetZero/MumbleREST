package mumble.protobuf.container;

import com.google.protobuf.MessageLite;
import mumble.protobuf.PackageType;

public class Message {
    private int id;
    private MessageLite message;

    public Message(PackageType id, MessageLite message) {
        this.id = id.getId();
        this.message = message;
    }

    public int getId() {
        return id;
    }

    public MessageLite getMessage() {
        return message;
    }
}
