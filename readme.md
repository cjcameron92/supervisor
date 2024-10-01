# Supervisor 
[![Paper Build Status](https://img.shields.io/github/actions/workflow/status/PaperMC/Paper/build.yml?branch=master)](https://github.com/PaperMC/Paper/actions)
[![Discord](https://img.shields.io/discord/289587909051416579.svg?label=&logo=discord&logoColor=ffffff&color=7389D8&labelColor=6A7EC2)](https://discord.gg/papermc)
[![GitHub Sponsors](https://img.shields.io/github/sponsors/papermc?label=GitHub%20Sponsors)](https://github.com/sponsors/cjcameron92)
# Supervisor Framework for Minecraft Spigot Plugins

Inspired by Spring Boot, the Supervisor Framework is designed to be both expandable and modular, making it the perfect choice for developing robust Minecraft Spigot plugins.

## Table of Contents
- [Downloads](#downloads)
- [User Guide](https://github.com/cjcameron92/supervisor/wiki/User-Guide)
- [Wiki](https://github.com/cjcameron92/supervisor/wiki)
- [Community and Support](#community-and-support)
- [Quick Start](#quick-start)
  - [Maven Setup](#maven-setup)
  - [Gradle Setup](#gradle-setup)
    - [Groovy](#groovy)
    - [Kotlin DSL](#kotlin-dsl)
- [Example](#example)

## Downloads

**Core**

[`supervisor-loader`](https://repo.world/cjcameron92/downloads/supervisor-loader.jar)

[`supervisor-bundle`](https://repo.world/cjcameron92/downloads/supervisor-bundle.jar)

[`supervisor-api`](https://repo.world/cjcameron92/downloads/supervisor-api.jar)

**Configuration**

[`supervisor-yaml`](https://repo.world/cjcameron92/downloads/supervisor-yaml.jar)

[`supervisor-json`](https://repo.world/cjcameron92/downloads/supervisor-json.jar)

[`supervisor-toml`](https://repo.world/cjcameron92/downloads/supervisor-toml.jar)

**Storage**

[`supervisor-mongo`](https://repo.world/cjcameron92/downloads/supervisor-mongo.jar)

[`supervisor-sql`](https://repo.world/cjcameron92/downloads/supervisor-sql.jar)

[`supervisor-storage`](https://repo.world/cjcameron92/downloads/supervisor-storage.jar)

## Community and Support
- [Discord](https://discord.gg/vertmix)

## Quick Start

### Latest Releases
- **Stable Release**: `0.0.4`
- **Dev Release**: `0.0.5-dev`

### Maven Setup
Add the repository and dependency to your `pom.xml`:
```xml
<repository>
  <url>https://cjcameron92.repo.world</url>
</repository>

<dependency>
  <groupId>gg.supervisor</groupId>
  <artifactId>bundle</artifactId>
  <version>LATEST</version>
</dependency>
```

### Gradle Setup
#### Groovy
Add the repository and dependency to your `build.gradle`:
```groovy
maven {
  url = "https://cjcameron92.repo.world"
}

implementation("gg.supervisor:bundle:VERSION")
```

#### Kotlin DSL
Add the repository and dependency to your `build.gradle.kts`:

```kts
maven("https://cjcameron92.repo.world")

implementation("gg.supervisor:bundle:VERSION")
```

### Local Setup
```bash
git clone https://github.com/cjcameron92/supervisor.git
./setup.sh
./gradlew shadowJar
```

## Example

```java
package com.cjcameron92.clearchat;

import co.aikar.commands.PaperCommandManager;
import gg.supervisor.core.loader.SupervisorLoader;
import org.bukkit.plugin.java.JavaPlugin;

public class ClearChatPlugin extends JavaPlugin {

  @Override
  public void onEnable() {
    SupervisorLoader.register(this, new PaperCommandManager(this));
  }
}
```
```java
package com.cjcameron92.clearchat.config;

import gg.supervisor.core.annotation.Configuration;
import gg.supervisor.configuration.yaml.YamlConfigService;

@Configuration(fileName = "config.yml", service = YamlConfigService.class)
public class ClearChatConfig {

    public String permissionRequired = "clearchat.use";
    public String deniedPermissionMessage = "&cYou do not have permission to use this command.";
    public String chatClearedMessage = "&e(!) Chat has been cleared by %player%.";
}
```

```java
package com.cjcameron92.clearchat.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.PaperCommandManager;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import com.cjcameron92.clearchat.config.ClearChatConfig;
import gg.supervisor.util.chat.Text;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

@CommandAlias("clearchat:cc")
public class ClearChatCommand extends BaseCommand {

  private final ClearChatConfig clearChatConfig;

  public ClearChatCommand(PaperCommandManager commandManager, ClearChatConfig clearChatConfig) {
    commandManager.registerCommand(this);
    this.clearChatConfig = clearChatConfig;
  }

  @Default
  public void onChatCleared(CommandSender sender) {
    if (!sender.hasPermission(clearChatConfig.permissionRequired)) {
      sender.sendMessage(Text.translate(clearChatConfig.deniedPermissionMessage));
      return;
    }

    for (int i = 0; i < 50; i++)
      Bukkit.broadcast(Text.translate("\n"));

    Bukkit.broadcast(Text.translate(clearChatConfig.chatClearedMessage.replaceAll("%player%", sender.getName())));
  }
}
```

## Support and Contributions
For support, join our Discord or consult the Wiki for troubleshooting tips. Contributions to the project are always welcome!


## Contributors 
[Hadi Mafhouz](https://github.com/Hadimhz)  
[Cameron Carvalho](https://github.com/cjcameron92)


