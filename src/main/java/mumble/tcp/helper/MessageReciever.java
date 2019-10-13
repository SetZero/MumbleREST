package mumble.tcp.helper;

import MumbleProto.Mumble;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageLite;
import mumble.protobuf.PackageType;
import mumble.protobuf.container.Message;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class MessageReciever implements Runnable{
    private InputStream inputStream;
    private final BlockingQueue<Message> input = new ArrayBlockingQueue<>(20);

    public MessageReciever(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    private Message getMessage() throws IOException {
        byte[] metadata = inputStream.readNBytes(6);
        if(metadata.length >= 6) {
            short id = (short) ((metadata[0] << 8) |
                    (metadata[1]));
            int length = (metadata[2] << 24) |
                    (metadata[3] << 16) |
                    (metadata[4] << 8) |
                    metadata[5];
            return parseMessage(inputStream.readNBytes(length), id);
        } else {
            return null;
        }
    }

    private Message parseMessage(byte[] data, short id) throws InvalidProtocolBufferException {
        PackageType mID = PackageType.getTypeById(id);
        MessageLite message = null;

        switch (mID) {
            case Version:
                message = Mumble.Version.parseFrom(data);
                break;
            case UDPTunnel:
                message = Mumble.UDPTunnel.parseFrom(data);
                break;
            case Authenticate:
                message = Mumble.Authenticate.parseFrom(data);
                break;
            case Ping:
                message = Mumble.Ping.parseFrom(data);
                break;
            case Reject:
                message = Mumble.Reject.parseFrom(data);
                break;
            case ServerSync:
                message = Mumble.ServerSync.parseFrom(data);
                break;
            case ChannelRemove:
                message = Mumble.ChannelRemove.parseFrom(data);
                break;
            case ChannelState:
                message = Mumble.ChannelState.parseFrom(data);
                break;
            case UserRemove:
                message = Mumble.UserRemove.parseFrom(data);
                break;
            case UserState:
                message = Mumble.UserState.parseFrom(data);
                break;
            case BanList:
                message = Mumble.BanList.parseFrom(data);
                break;
            case TextMessage:
                message = Mumble.TextMessage.parseFrom(data);
                break;
            case PermissionDenied:
                message = Mumble.PermissionDenied.parseFrom(data);
                break;
            case ACL:
                message = Mumble.ACL.parseFrom(data);
                break;
            case QueryUsers:
                message = Mumble.QueryUsers.parseFrom(data);
                break;
            case CryptSetup:
                message = Mumble.CryptSetup.parseFrom(data);
                break;
            case ContextActionModify:
                message = Mumble.ContextActionModify.parseFrom(data);
                break;
            case ContextAction:
                message = Mumble.ContextAction.parseFrom(data);
                break;
            case UserList:
                message = Mumble.UserList.parseFrom(data);
                break;
            case VoiceTarget:
                message = Mumble.VoiceTarget.parseFrom(data);
                break;
            case PermissionQuery:
                message = Mumble.PermissionQuery.parseFrom(data);
                break;
            case CodecVersion:
                message = Mumble.CodecVersion.parseFrom(data);
                break;
            case UserStats:
                message = Mumble.UserStats.parseFrom(data);
                break;
            case RequestBlob:
                message = Mumble.RequestBlob.parseFrom(data);
                break;
            case ServerConfig:
                message = Mumble.ServerConfig.parseFrom(data);
                break;
            case SuggestConfig:
                message = Mumble.SuggestConfig.parseFrom(data);
                break;
            case Unknown:
                break;
        }
        return new Message(mID, message);
    }

    @Override
    public void run() {
        while (true) {
            try {
                Message msg = getMessage();
                if(msg != null)
                    input.put(msg);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }
    }

    public Message getLastMessage() {
        try {
            return input.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
        return null;
    }
}
