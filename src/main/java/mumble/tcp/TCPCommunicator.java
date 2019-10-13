package mumble.tcp;

import MumbleProto.Mumble;
import com.google.protobuf.MessageLite;
import mumble.protobuf.PackageType;
import mumble.tcp.container.MumbleOptions;
import mumble.tcp.helper.Connection;
import mumble.tcp.protocol.ProtocolHelper;
import org.apache.logging.log4j.Level;
import utils.logging.Log;

import java.util.Collection;

public class TCPCommunicator implements Runnable {
    private String domain;
    private int port;

    public TCPCommunicator(String domain, int port) {
        this.domain = domain;
        this.port = port;
    }

    @Override
    public void run() {
        ProtocolHelper helper = new ProtocolHelper();
        helper.connect(domain, port, new MumbleOptions("", ""), (error, connection) -> {
            error.ifPresent(System.err::println);
            connection.authenticate("Y0GURT");
            connection.on(PackageType.TextMessage, (textMessage) -> onTextMessage(connection, textMessage));
            connection.on(PackageType.ServerSync, e -> switchChannel(connection));
        });
    }

    private void switchChannel(Connection connection) {
        Collection<Mumble.UserState> users = connection.getUserManager().getUsers();
        for(Mumble.UserState user : users) {
            connection.getChannelManager().switchChannel(user.getChannelId());
        }
    }

    private void onTextMessage(Connection connection, MessageLite textMessage) {
        if(textMessage instanceof Mumble.TextMessage) {
            Mumble.TextMessage message = (Mumble.TextMessage) textMessage;
            //Log.get().log(Level.WARN, message.getActor() + ": " + message.getMessage());
            System.out.println("Message from: " + message.getActor());
            System.out.println(connection.getUserManager().getUsernameById(message.getActor()) + ": " + message.getMessage() + "(" + message.getChannelIdCount() + ")");
        }
    }
}
