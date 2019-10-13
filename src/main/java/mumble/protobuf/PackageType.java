package mumble.protobuf;

import MumbleProto.Mumble;

public enum PackageType {
    Version(0, Mumble.Version.class),
    UDPTunnel(1, Mumble.UDPTunnel.class),
    Authenticate(2, Mumble.Authenticate.class),
    Ping(3, Mumble.Ping.class),
    Reject(4, Mumble.Reject.class),
    ServerSync(5, Mumble.ServerSync.class),
    ChannelRemove(6, Mumble.ChannelRemove.class),
    ChannelState(7, Mumble.ChannelState.class),
    UserRemove(8, Mumble.UserRemove.class),
    UserState(9, Mumble.UserState.class),
    BanList(10, Mumble.BanList.class),
    TextMessage(11, Mumble.TextMessage.class),
    PermissionDenied(12, Mumble.PermissionDenied.class),
    ACL(13, Mumble.ACL.class),
    QueryUsers(14, Mumble.QueryUsers.class),
    CryptSetup(15, Mumble.CryptSetup.class),
    ContextActionModify(16, Mumble.ContextActionModify.class),
    ContextAction(17, Mumble.ContextAction.class),
    UserList(18, Mumble.UserList.class),
    VoiceTarget(19, Mumble.VoiceTarget.class),
    PermissionQuery(20, Mumble.PermissionQuery.class),
    CodecVersion(21, Mumble.CodecVersion.class),
    UserStats(22, Mumble.UserStats.class),
    RequestBlob(23, Mumble.RequestBlob.class),
    ServerConfig(24, Mumble.ServerConfig.class),
    SuggestConfig(25, Mumble.SuggestConfig.class),
    Unknown(9999, Object.class);

    private final int id;
    private final Class<?> clazz;

    PackageType(int id, Class<?> clazz) {
        this.id = id;
        this.clazz = clazz;
    }

    public int getId() {
        return id;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public static PackageType getTypeById(int id) {
        switch (id) {
            case 0: return Version;
            case 1: return UDPTunnel;
            case 2: return Authenticate;
            case 3: return Ping;
            case 4: return Reject;
            case 5: return ServerSync;
            case 6: return ChannelRemove;
            case 7: return ChannelState;
            case 8: return UserRemove;
            case 9: return UserState;
            case 10: return BanList;
            case 11: return TextMessage;
            case 12: return PermissionDenied;
            case 13: return ACL;
            case 14: return QueryUsers;
            case 15: return CryptSetup;
            case 16: return ContextActionModify;
            case 17: return ContextAction;
            case 18: return UserList;
            case 19: return VoiceTarget;
            case 20: return PermissionQuery;
            case 21: return CodecVersion;
            case 22: return UserStats;
            case 23: return RequestBlob;
            case 24: return ServerConfig;
            case 25: return SuggestConfig;
            default: return Unknown;
        }
    }
}
