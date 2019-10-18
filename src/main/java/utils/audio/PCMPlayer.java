package utils.audio;

import org.apache.commons.lang3.ArrayUtils;

import javax.sound.sampled.*;
import java.util.List;

public class PCMPlayer {
    private AudioFormat af;
    private SourceDataLine line;

    public PCMPlayer() {
        try {
            af = new AudioFormat(48000, 16, 2, true, false);
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, af);
            line = (SourceDataLine) AudioSystem.getLine(info);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public void play(byte[] buffer) {
        // select audio format parameters
        // prepare audio output
        try {
            line.open(af, 2048);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
        line.start();
        // output wave form repeatedly
        line.write(buffer, 0, buffer.length);
        // shut down audio
        //line.drain();
        //line.stop();
        //line.close();
    }
}
