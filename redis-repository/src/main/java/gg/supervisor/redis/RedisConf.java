package gg.supervisor.redis;


public class RedisConf {

    private final String host;
    private final int port;
    private final String username;
    private final String password;

    public RedisConf(String host, int port, String username, String password) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
