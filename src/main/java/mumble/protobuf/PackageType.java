package mumble.protobuf;

public enum PackageType {
    Version(0),
    UDPTunnel(1),
    Authenticate(2),
    Ping(3),
    Reject(4),
    ServerSync(5),
    ChannelRemove(6),
    ChannelState(7),
    UserRemove(8),
    UserState(9),
    BanList(10),
    TextMessage(11),
    PermissionDenied(12),
    ACL(13),
    QueryUsers(14),
    CryptSetup(15),
    ContextActionModify(16),
    ContextAction(17),
    UserList(18),
    VoiceTarget(19),
    PermissionQuery(20),
    CodecVersion(21),
    UserStats(22),
    RequestBlob(23),
    ServerConfig(24),
    SuggestConfig(25),
    Unknown(9999);

    private final int id;

    PackageType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static PackageType getTypeById(int id) {
        switch (id) {
            case 0: return Version;
            case 1: return Authenticate;
            case 2: return Ping;
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
