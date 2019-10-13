package mumble.tcp.protocol;

import mumble.tcp.container.MumbleOptions;
import mumble.tcp.helper.Connection;
import mumble.tcp.helper.MessageReciever;
import mumble.tcp.helper.MessageSender;

import java.util.Optional;
import java.util.function.BiConsumer;

public class ProtocolHelper {
    public ProtocolHelper() {
    }

    public void connect(String serverIP, Integer serverPort, MumbleOptions options, BiConsumer<Optional<String>, Connection> behaviour) {
        Connection c = new Connection(serverIP, serverPort);
        behaviour.accept(c.getError(), c);
    }
}
