package mumble.tcp.protocol;

import mumble.tcp.container.MumbleOptions;
import mumble.tcp.helper.Connection;

import java.util.Optional;
import java.util.function.BiConsumer;

public class ProtocolHelper {
    public ProtocolHelper() {
    }

    public void connect(String serverIP, Integer serverPort, MumbleOptions options, BiConsumer<Optional<String>, Connection> behaviour) {
        Connection c = new Connection(serverIP, serverPort);
        behaviour.accept(c.getError(), c);
        new Thread(() -> {
            while (true) {
                c.sendPingIfNeeded();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
