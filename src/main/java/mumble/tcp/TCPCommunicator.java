package mumble.tcp;

import MumbleProto.Mumble;
import mumble.protobuf.PackageType;
import mumble.tcp.container.MumbleOptions;
import mumble.tcp.protocol.ProtocolHelper;
import org.apache.logging.log4j.Level;
import utils.logging.Log;

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
            connection.on(PackageType.TextMessage, (textMessage) -> {
                if(textMessage instanceof Mumble.TextMessage) {
                    Mumble.TextMessage message = (Mumble.TextMessage) textMessage;
                    //Log.get().log(Level.WARN, message.getActor() + ": " + message.getMessage());
                    System.out.println(message.getActor() + ": " + message.getMessage());
                }
            });
        });
    }
}
