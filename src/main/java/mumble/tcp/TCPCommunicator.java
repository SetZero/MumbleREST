package mumble.tcp;

import MumbleProto.Mumble;
import com.google.protobuf.MessageLite;
import mumble.protobuf.PackageType;
import mumble.tcp.container.MumbleOptions;
import mumble.tcp.protocol.ProtocolHelper;

import javax.net.ssl.*;
import java.io.*;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.PriorityQueue;
import java.util.Queue;

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
            error.ifPresent(System.out::println);
            connection.authenticate("Y0GURT");
            while (true) {
                connection.sendPingIfNeeded();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
