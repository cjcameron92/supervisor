package gg.supervisor.api.template;

import java.util.HashMap;
import java.util.Map;

public class SQLConfig {

    public String jdbcUrl = "jdbc:mariadb://localhost:5432/yourdatabase";
    public String username = "admin123";
    public String password = "p@ssw0rd";
    public Map<String, String> cacheProps = new HashMap<>();
}
