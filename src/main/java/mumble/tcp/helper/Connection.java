package mumble.tcp.helper;

import MumbleProto.Mumble;
import org.checkerframework.checker.nullness.Opt;

import javax.net.ssl.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Function;

public class Connection {
    private SSLSocket socket;
    private MessageReciever reciever;
    private MessageSender sender;
    private String error = null;
    private Lock pingLock = new ReentrantLock();
    private long lastPing = 0;
    private List<Consumer<Mumble.TextMessage>> textMessageListener = new ArrayList<>();

    public Connection(String domain, int port) {
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() { return null; }
            public void checkClientTrusted(X509Certificate[] certs, String authType) {}
            public void checkServerTrusted(X509Certificate[] certs, String authType) {}
        } };

        try {
            SSLParameters params = new SSLParameters();
            params.setProtocols(new String[] {"TLSv1"});

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            final SSLSocketFactory factory = sc.getSocketFactory();

            socket = (SSLSocket) factory.createSocket(domain, port);
            socket.setSSLParameters(params);
            socket.startHandshake();

            reciever = new MessageReciever(socket.getInputStream());
            sender = new MessageSender(socket.getOutputStream());
            new Thread(reciever).start();
            new Thread(sender).start();

            sender.sendVersion();
        } catch (IOException | NoSuchAlgorithmException | KeyManagementException e) {
            error = e.toString();
        }
    }

    public Optional<String> getError() {
        if(error != null) {
            return Optional.of(error);
        } else {
            return Optional.empty();
        }
    }

    public void authenticate(String username) {
        sender.sendAuth(username);
    }

    public void sendPingIfNeeded() {
        try {
            pingLock.lock();
            long currentTime = System.currentTimeMillis() / 1000;
            if (currentTime > lastPing + 20) {
                System.out.println("send ping: " + currentTime + " / " + lastPing);
                sender.sendPing();
                lastPing = currentTime;
            }
        } finally {
            pingLock.unlock();
        }
    }

    public void addTextMessageListener(Consumer<Mumble.TextMessage> messageListener) {
        textMessageListener.add(messageListener);
    }
}
