package mumble.tcp;

import MumbleProto.Mumble;
import com.google.protobuf.MessageLite;
import mumble.protobuf.PackageType;

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
    private static final SSLSocketFactory FACTORY =
            (SSLSocketFactory)SSLSocketFactory.getDefault();
    private OutputStream outputStream;
    private InputStream inputStream;
    private SSLSocket socket;

    public TCPCommunicator(String domain, int port) {
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(X509Certificate[] certs,
                                           String authType) {
            }

            public void checkServerTrusted(X509Certificate[] certs,
                                           String authType) {
            }
        } };

        try {
            SSLParameters params = new SSLParameters();
            params.setProtocols(new String[] {"TLSv1"});



            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            final SSLSocketFactory factory = sc.getSocketFactory();

            socket = (SSLSocket) factory.createSocket(domain, port);
            socket.setSSLParameters(params);
            socket.setReceiveBufferSize(1024);
            socket.setSendBufferSize(1024);
            socket.setWantClientAuth(false);
            socket.setKeepAlive(true);
            socket.startHandshake();

            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
        } catch (IOException | NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            Mumble.Version.Builder b = Mumble.Version.newBuilder();
            b.setVersion((1 << 16) | (3 << 8));
            b.setRelease("1.3.0");
            b.setOs("WinDOS");
            b.setOsVersion("11");
            createMessage(b.build(), PackageType.Version);

            byte[] data = getMessage();
            Mumble.Version v = Mumble.Version.parseFrom(data);
            System.out.println("Version: " + v.getVersion());
            System.out.println("Release: " + v.getRelease());
            System.out.println("OS: " + v.getOs());
            System.out.println("OS Version: " + v.getOsVersion());

            Mumble.Authenticate.Builder auth = Mumble.Authenticate.newBuilder();
            auth.setUsername("FarmerYogurt9999");
            createMessage(auth.build(), PackageType.Authenticate);

            data = getMessage();
            Mumble.CryptSetup crypt = Mumble.CryptSetup.parseFrom(data);
            System.out.println("Key: " + crypt.getKey());
            System.out.println("Client Nonce: " + crypt.getClientNonce());
            System.out.println("Server Nonce: " + crypt.getServerNonce());

            data = getMessage();
            Mumble.CodecVersion codec = Mumble.CodecVersion.parseFrom(data);
            System.out.println("Opus:" + codec.getOpus());

            data = getMessage();
            Mumble.ChannelState state = Mumble.ChannelState.parseFrom(data);
            System.out.println("Name: " + state.getName());
            System.out.println("Description: " + state.getDescription());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private byte[] getMessage() throws IOException {
        byte[] metadata = inputStream.readNBytes(6);
        short id = (short)((metadata[0] << 8) |
                    (metadata[1]));
        int length = (metadata[2] << 24) |
                        (metadata[3] << 16) |
                        (metadata[4] << 8) |
                        metadata[5];
        System.out.println("MessageName:" + PackageType.getTypeById(id) + ", " +  id);
        return inputStream.readNBytes(length);
    }

    private void createMessage(MessageLite message, PackageType type) throws IOException {
        byte[] bytes = ByteBuffer.allocate(6).putShort((short)type.getId()).putInt(message.getSerializedSize()).array();
        outputStream.write(bytes);
        message.writeTo(outputStream);
        outputStream.flush();
    }
}
