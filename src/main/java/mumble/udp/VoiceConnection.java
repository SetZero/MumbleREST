package mumble.udp;

import mumble.protobuf.PackageType;
import mumble.protobuf.container.Message;
import org.restcomm.media.codec.opus.OpusJni;

import java.io.IOException;
import java.net.DatagramSocket;

public class VoiceConnection implements Runnable {
    private DatagramSocket socket;
    private byte[] data = new byte[1024];
    private String domain;
    private int port;

    public VoiceConnection(String domain, int port) {
        this.domain = domain;
        this.port = port;

        try {
            socket = new DatagramSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        new Thread(this).start();
    }

    public static long unsignedToBytes(byte b) {
        return b & 0xFF;
    }

    public void parseUDP(Message e) {
        byte[] raw = e.getRaw();
        int type = (raw[0] >> 5) & 3;
        int target = raw[0] & 0x1F;
        int offset = 1;
        PackageInfo info = readVarInt(raw, offset);
        offset += info.readBytes;
        PackageInfo seq = readVarInt(raw, offset);
        offset += seq.readBytes;
        long OpusHeaderSize = unsignedToBytes(raw[offset]);

        System.out.println("Type: " + type);
        System.out.println("Target: " + target);
        System.out.println("User ID: " + info.value);
        System.out.println("Seq Num: " + seq.value);
        System.out.println("Opus Length: " + OpusHeaderSize);
    }

    private PackageInfo readVarInt(byte[] data, int offset) {
        if ((data[offset] & 0b10000000) >> 7 == 0) {
            return new PackageInfo(
                    unsignedToBytes((byte) (data[offset] & 0b01111111)),
                    1);
        } else if ((data[offset] & 0b11000000) >> 6 == 0b10) {
            System.out.println("1 byte");
            return new PackageInfo(
                    unsignedToBytes((byte) (data[offset] & 0b00111111)) << 8 |
                            unsignedToBytes(data[1 + offset]),
                    2);
        } else if ((data[offset] & 0b11100000) >> 5 == 0b110) {
            return new PackageInfo(
                    unsignedToBytes((byte) (data[offset] & 0b00011111)) << 16 |
                            unsignedToBytes(data[1 + offset]) << 8 |
                            unsignedToBytes(data[2 + offset]),
                    3);
        } else if ((data[offset] & 0b11110000) >> 4 == 0b1110) {
            return new PackageInfo(
                    unsignedToBytes((byte) (data[offset] & 0b00001111)) << 24 |
                            unsignedToBytes(data[1 + offset]) << 16 |
                            unsignedToBytes(data[2 + offset]) << 8 |
                            unsignedToBytes(data[3 + offset]),
                    4);
        } else if ((data[offset] & 0b11111100) >> 2 == 0b111100) {
            return new PackageInfo(
                    unsignedToBytes(data[1 + offset]) << 24 |
                            unsignedToBytes(data[2 + offset]) << 16 |
                            unsignedToBytes(data[3 + offset]) << 8 |
                            unsignedToBytes(data[4 + offset]),
                    5);
        } else if ((data[offset] & 0b11111100) >> 2 == 0b111110) {
            System.out.println("64bit");
            return new PackageInfo(
                    (long) data[1 + offset] << 56L |
                            (long) data[2 + offset] << 48L |
                            (long) data[3 + offset] << 40L |
                            (long) data[4 + offset] << 32L |
                            (long) data[5 + offset] << 24L |
                            (long) data[6 + offset] << 16L |
                            (long) data[7 + offset] << 8L |
                            data[8 + offset],
                    9);
        }
        //TODO
        return new PackageInfo(0, 0);
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

    private static class PackageInfo {
        public long value;
        public int readBytes;

        public PackageInfo(long value, int read) {
            this.value = value;
            this.readBytes = read;
        }
    }
}
