package mumble.protobuf.container;

import com.google.protobuf.MessageLite;

public class Message {
    private int id;
    private int length;
    private MessageLite message;

    public Message(int id, int length, MessageLite message) {
        this.id = id;
        this.length = length;
        this.message = message;
    }

    public int getId() {
        return id;
    }

    public int getLength() {
        return length;
    }

    public MessageLite getMessage() {
        return message;
    }
}
