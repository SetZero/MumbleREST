package mumble.tcp.helper.classes;

import MumbleProto.Mumble;
import com.google.protobuf.MessageLite;
import mumble.protobuf.PackageType;
import mumble.protobuf.container.Message;
import mumble.tcp.helper.Connection;
import mumble.tcp.helper.MessageSender;

import java.util.HashMap;
import java.util.Map;

public class ChannelManager {
    private final MessageSender sender;
    private final Connection connection;
    private Map<Integer, Mumble.ChannelState> channelMap = new HashMap<>();

    public ChannelManager(MessageSender sender, Connection connection) {
        this.sender = sender;
        this.connection = connection;
    }

    public void acceptStateChange(Message e) {
        if(e.getMessage() instanceof Mumble.ChannelState) {
            Mumble.ChannelState channelState = (Mumble.ChannelState) e.getMessage();

            channelMap.put(channelState.getChannelId(), channelState);
            System.out.println("New Channel: " + channelState.getName() + "(" + channelState.getChannelId() + ")");
        }
    }

    public void switchChannel(int channel) {
        Mumble.UserState.Builder userState = Mumble.UserState.newBuilder();
        userState.setChannelId(channel);
        userState.setActor(connection.getUserManager().getMySessionID());
        //userState.setSession(connection.getUserManager().getMySessionID());
        //userState.setMute(true);
        //userState.setDeaf(true);
        sender.addToQueue(PackageType.UserState, userState.build());
        System.out.println("Switching Channel: " + channel + ", " + connection.getUserManager().getMySessionID());
    }
}
