package mumble.tcp.container;

public class MumbleOptions {
    private String key;
    private String cert;

    public MumbleOptions(String key, String cert) {
        this.key = key;
        this.cert = cert;
    }

    public String getKey() {
        return key;
    }

    public String getCert() {
        return cert;
    }
}
