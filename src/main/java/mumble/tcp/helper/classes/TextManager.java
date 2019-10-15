package mumble.tcp.helper.classes;

import MumbleProto.Mumble;
import mumble.protobuf.PackageType;
import mumble.tcp.helper.Connection;
import mumble.tcp.helper.MessageSender;

public class TextManager {
    private final MessageSender sender;
    private final Connection connection;

    public TextManager(MessageSender sender, Connection connection) {
        this.connection = connection;
        this.sender = sender;
    }

    public void writeMessageToUser(int user, String message) {
        Mumble.TextMessage.Builder textMesage = Mumble.TextMessage.newBuilder();
        textMesage.setMessage(message);
        textMesage.setActor(connection.getUserManager().getMySessionID());
        textMesage.addSession(user);
        sender.addToQueue(PackageType.TextMessage, textMesage.build());
    }
}
