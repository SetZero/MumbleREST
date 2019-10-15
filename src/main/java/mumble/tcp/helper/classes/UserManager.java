package mumble.tcp.helper.classes;

import MumbleProto.Mumble;
import com.google.protobuf.MessageLite;
import mumble.protobuf.PackageType;
import mumble.protobuf.container.Message;
import mumble.tcp.helper.Connection;
import mumble.tcp.helper.MessageSender;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class UserManager {
    private final MessageSender sender;
    private final Connection connection;
    private Map<Integer, Mumble.UserState> userMap = new HashMap<>();
    private int mySessionID;

    public UserManager(MessageSender sender, Connection connection) {
        this.sender = sender;
        this.connection = connection;
    }

    public void acceptStateChange(Message e) {
        if(e.getMessage() instanceof Mumble.UserState) {
            Mumble.UserState userState = (Mumble.UserState) e.getMessage();
            System.out.println("User: " + userState.getSession() + ", " + userState.getName());
            userMap.put(userState.getSession(), userState);
            //TODO: This can also be a mute / etc.
            //System.out.println("User Changed State: " + userState.getName() + ", " + userState.getSession());
        }
    }

    public void acceptUserRemove(Message e) {
        if(e.getMessage() instanceof Mumble.UserRemove) {
            Mumble.UserRemove userRemove = (Mumble.UserRemove) e.getMessage();
            userMap.remove(userRemove.getSession());
        }
    }

    public String getUsernameById(int id) {
        Mumble.UserState user = userMap.get(id);
        if(user != null)
            return user.getName();
        else
            return null;
    }

    public Collection<Mumble.UserState> getUsers() {
        return userMap.values();
    }

    public void acceptServerSync(Message e) {
        if(e.getMessage() instanceof Mumble.ServerSync) {
            Mumble.ServerSync serverSync = (Mumble.ServerSync) e.getMessage();
            mySessionID = serverSync.getSession();
        }
    }

    public void selfMute() {
        Mumble.UserState.Builder userState = Mumble.UserState.newBuilder();
        userState.setSelfMute(true);
        userState.setActor(getMySessionID());
        sender.addToQueue(PackageType.UserState, userState.build());
    }

    public void selfDeaf() {
        Mumble.UserState.Builder userState = Mumble.UserState.newBuilder();
        userState.setSelfDeaf(true);
        userState.setActor(getMySessionID());
        sender.addToQueue(PackageType.UserState, userState.build());
    }

    public int getMySessionID() {
        return mySessionID;
    }
}
