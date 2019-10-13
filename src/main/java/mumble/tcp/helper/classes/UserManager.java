package mumble.tcp.helper.classes;

import MumbleProto.Mumble;
import com.google.protobuf.MessageLite;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class UserManager {
    private Map<Integer, Mumble.UserState> userMap = new HashMap<>();
    private int mySessionID;

    public void acceptStateChange(MessageLite e) {
        if(e instanceof Mumble.UserState) {
            Mumble.UserState userState = (Mumble.UserState) e;
            userMap.put(userState.getSession(), userState);
            //TODO: This can also be a mute / etc.
            //System.out.println("User Changed State: " + userState.getName() + ", " + userState.getSession());
        }
    }

    public void acceptUserRemove(MessageLite e) {
        if(e instanceof Mumble.UserRemove) {
            Mumble.UserRemove userRemove = (Mumble.UserRemove) e;
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

    public void acceptServerSync(MessageLite e) {
        if(e instanceof Mumble.ServerSync) {
            Mumble.ServerSync serverSync = (Mumble.ServerSync) e;
            mySessionID = serverSync.getSession();
        }
    }

    public int getMySessionID() {
        return mySessionID;
    }
}
