package mumble.tcp.helper;

import com.google.protobuf.MessageLite;
import mumble.protobuf.PackageType;
import mumble.protobuf.container.Message;
import mumble.tcp.helper.classes.ChannelManager;
import mumble.tcp.helper.classes.TextManager;
import mumble.tcp.helper.classes.UserManager;

import javax.net.ssl.*;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

public class Connection implements Runnable {
    private SSLSocket socket;
    private MessageReciever reciever;
    private MessageSender sender;
    private String error = null;
    private Lock pingLock = new ReentrantLock();
    private long lastPing = 0;
    private Map<PackageType, List<Consumer<MessageLite>>> textMessageListener = new ConcurrentHashMap<>();

    private ChannelManager channelManager;
    private UserManager userManager;
    private TextManager textManager;

    public Connection(String domain, int port) {
        try {
            connect(domain, port);

            sender.sendVersion();

            on(PackageType.ChannelState, e -> channelManager.acceptStateChange(e));
            on(PackageType.UserState, e -> userManager.acceptStateChange(e));
            on(PackageType.UserRemove, e -> userManager.acceptUserRemove(e));
            on(PackageType.ServerSync, e -> userManager.acceptServerSync(e));

        } catch (IOException | NoSuchAlgorithmException | KeyManagementException e) {
            error = e.toString();
        }
        new Thread(this).start();
    }

    private void connect(String domain, int port) throws NoSuchAlgorithmException, KeyManagementException, IOException {
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() { return null; }
            public void checkClientTrusted(X509Certificate[] certs, String authType) {}
            public void checkServerTrusted(X509Certificate[] certs, String authType) {}
        } };

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

        channelManager =  new ChannelManager(sender, this);
        textManager =  new TextManager(sender, this);
        userManager = new UserManager();

        new Thread(reciever).start();
        new Thread(sender).start();
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
                sender.sendPing();
                lastPing = currentTime;
            }
        } finally {
            pingLock.unlock();
        }
    }

    public void on(PackageType type, Consumer<MessageLite> messageListener) {
        textMessageListener.computeIfAbsent(type, k -> new ArrayList<>());
        textMessageListener.get(type).add(messageListener);
    }

    @Override
    public void run() {
        while(true) {
            Message lastMessage = reciever.getLastMessage();
            System.out.println("Broadcasting: " + PackageType.getTypeById(lastMessage.getId()));
            List<Consumer<MessageLite>> list = textMessageListener.get(PackageType.getTypeById(lastMessage.getId()));
            if (list != null) {
                list.forEach(e -> e.accept(lastMessage.getMessage()));
            }
        }
    }

    public ChannelManager getChannelManager() {
        return channelManager;
    }

    public UserManager getUserManager() {
        return userManager;
    }

    public TextManager getTextManager() {
        return textManager;
    }
}
