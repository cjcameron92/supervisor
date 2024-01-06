## Deploy your Minecraft plugin faster


### Getting Started
```java
import gg.supervisor.loader.SupervisorLoader;
import org.bukkit.plugin.java.JavaPlugin;

public class TeamPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        SupervisorLoader.register(this);
    }
}
```

### Configuration
```java
import gg.supervisor.api.Configuration;
import gg.supervisor.configuration.AbstractConfig;
import gg.supervisor.configuration.yaml.YamlConfigService;

import java.util.HashMap;
import java.util.Map;

@Configuration(fileName = "database.yml", service = YamlConfigService.class)
public class MySQLConfig extends AbstractConfig {

    public String hostName = "0.0.0.0";
    public int port = 3306;

    public String username = "admin";
    public String password = "l0calhost";

    public String database = "admin";

    public Map<String, String> properties = new HashMap<>() {{
        put("cachePrepStmts", "true");
        put("prepStmtCacheSize", "250");
        put("prepStmtCacheSqlLimit", "2048");
    }};
}
```
### Storage Registry
```java
import gg.supervisor.api.Component;
import gg.supervisor.storage.json.JsonStorageRegistry;
import org.bukkit.plugin.Plugin;

import java.io.File;

@Component
public class DoubleStorage extends JsonStorageRegistry<Double> {

    public DoubleStorage(Plugin plugin) {
        super(Double.class, new File(plugin.getDataFolder(), "storage"));
    }
}
```

### Storage
```java
import gg.supervisor.storage.json.JsonStorage;
import org.bukkit.plugin.Plugin;

import java.io.File;

@Component
public class DemoStd extends JsonStorage<String> {

    public DemoStd(Plugin plugin) {
        super(String.class, new File(plugin.getDataFolder(), "demo.json"));
    }
}
```

### Player Storage
```java
import gg.supervisor.storage.json.JsonPlayerStorage;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class TeamPlayerStorage extends JsonPlayerStorage<TeamPlayerStorage.Team> {

    public TeamPlayerStorage(Plugin plugin) {
        super(Team.class, new File(plugin.getDataFolder(), "storage/teams/"),  player -> new Team(player.getName(), Collections.singletonList(player.getUniqueId().toString())));
    }

    record Team(String teamName, List<String> ids) {
    }
}
```
### Menu
```java
    final MenuBuilder menuBuilder = MenuBuilder.newBuilder().add(4, ItemBuilder.newMenuItem(Material.EGG).addInventoryClickListener(e -> e.getWhoClicked().sendMessage("lol")).build());
    final player.openInventory(menuBuilder.build(LegacyComponentSerializer.legacyAmpersand().deserialize("demo"), 9).getInventory());
```

### Items
```java
   final Item item = ItemBuilder.newBuilder(Material.STICK, "demo").addDropListener(e -> System.out.println("e")).build();
   final ItemStack itemStack = item.buildItem(1);

   player.getInventory().addItem(itemStack);
```