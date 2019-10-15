package mumble.udp;

import mumble.protobuf.container.Message;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class VoiceConnection implements Runnable {
    private DatagramSocket socket;
    private byte[] data = new byte[ 1024 ];
    private String domain;
    private int port;

    public VoiceConnection(String domain, int port) {
        this.domain = domain;
        this.port = port;

        try {
            socket = new DatagramSocket( port );
        } catch (IOException e) {
            e.printStackTrace();
        }
        new Thread(this).start();
    }

    public void parseUDP(Message e) {
        byte[] raw = e.getRaw();
        int type = raw[0] >> 5 & 3;
        int target = raw[0] & 0x1F;
        System.out.println((raw[1]));
        System.out.println("Type: " + type);
        System.out.println("Target: " + target);
    }

    @Override
    public void run() {
        /*DatagramPacket packet = new DatagramPacket( data, data.length );
        while(true) {
            try {
                socket.receive(packet);
                System.out.println("New Package: " + packet.getLength());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/
    }
}
