### Installation
Ensure you have [required Java version] installed. Add the following dependency to your build script using Gradle:

```kts
// this example
implementation("gg.supervisor:yaml-configuration:[Latest Version]")
// other uses
implementation("gg.supervisor:[configuration-type]:[Latest Version]")
```
Replace __[Latest Version]__ with the current version of the library

**Example**: MySQL Configuration
This example demonstrates how to set up a MySQL configuration using our library. The configuration includes standard connection details and additional properties to enhance performance.

```java
@Configuration(fileName = "database.yml", service = YamlConfigService.class)
public class MySQLConfig extends AbstractConfig {

    // Connection details
    public String hostName = "0.0.0.0";
    public int port = 3306;
    public String username = "admin";
    public String password = "l0calhost";
    public String database = "admin";

    // Additional properties to optimize connection
    public Map<String, String> properties = new HashMap<>() {{
        put("cachePrepStmts", "true");
        put("prepStmtCacheSize", "250");
        put("prepStmtCacheSqlLimit", "2048");
    }};
}
```
### Output File
`database.yml` 
```yaml
hostName: 0.0.0.0
port: 3306
username: admin
password: l0calhost
database: admin
properties:
  prepStmtCacheSqlLimit: 2048
  cachePrepStmts: "true"
  prepStmtCacheSize: 250

```
This configuration class sets up a MySQL connection with specified host, port, and authentication details. The properties map includes settings to cache prepared statements for improved performance.
