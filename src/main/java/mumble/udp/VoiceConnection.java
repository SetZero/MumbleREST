package mumble.udp;

import mumble.protobuf.container.Message;
import org.concentus.*;
import utils.audio.PCMPlayer;

import java.io.IOException;
import java.net.DatagramSocket;
import java.util.Arrays;

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
        String type = Integer.toBinaryString((raw[0] >> 5) & 4);
        int target = raw[0] & 0x1F;
        int offset = 1;
        PackageInfo info = readVarInt(raw, offset);
        offset += info.readBytes;
        PackageInfo seq = readVarInt(raw, offset);
        offset += seq.readBytes;
        PackageInfo opusHeaderInfo = readVarInt(raw, offset);
        offset += opusHeaderInfo.readBytes; // 16 bit varint
        int opusDataLength = (int) (opusHeaderInfo.value & 0x1FFF);

        byte[] data = new byte[opusDataLength];
        for(int i = offset, j=0; i < opusDataLength + offset; i++, j++) {
            data[j] = raw[i];
        }
        short[] pcmData = decodeOpus(data, 0,data.length);
        PCMPlayer player = new PCMPlayer();
        player.play(ShortToByte_Twiddle_Method(pcmData));
        //System.out.println(Arrays.toString(pcmData));

        /*System.out.println("\nType: " + type);
        System.out.println("Target: " + target);
        System.out.println("User ID: " + info.value);
        System.out.println("Seq Num: " + seq.value);
        System.out.println("Opus Length: " + (opusHeaderInfo.value & 0x1FFF));
        System.out.println("Last Frame: " + ((opusHeaderInfo.value & 0x2000) != 0));*/
    }

    private byte [] ShortToByte_Twiddle_Method(short [] input)
    {
        int short_index, byte_index;
        int iterations = input.length;

        byte [] buffer = new byte[input.length * 2];

        short_index = byte_index = 0;

        for(/*NOP*/; short_index != iterations; /*NOP*/)
        {
            buffer[byte_index]     = (byte) (input[short_index] & 0x00FF);
            buffer[byte_index + 1] = (byte) ((input[short_index] & 0xFF00) >> 8);

            ++short_index; byte_index += 2;
        }

        return buffer;
    }

    private PackageInfo readVarInt(byte[] data, int offset) {
        if ((data[offset] & 0b10000000) >> 7 == 0) {
            return new PackageInfo(
                    unsignedToBytes((byte) (data[offset] & 0b01111111)),
                    1);
        } else if ((data[offset] & 0b11000000) >> 6 == 0b10) {
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

    private short[] decodeOpus(byte[] raw, int offset, int celtDataLength) {
        short[] output = new short[celtDataLength*12];
        try {
            OpusDecoder decoder = new OpusDecoder(48000, 2);
            decoder.decode(raw, offset, celtDataLength, output, 0, output.length, false);
        } catch (OpusException e) {
            e.printStackTrace();
        }
        return output;
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
