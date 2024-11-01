# Supervisor Framework

Welcome to the Supervisor Core Framework! This guide provides a hyper-detailed walkthrough on how to set up your Spigot plugin using this framework, leveraging an MVC (Model-View-Controller) design similar to Spring Boot. We will cover how to structure the project, configure build tools, and use the core components effectively for creating commands, listeners, repositories, and services.

## Table of Contents
1. [Project Structure Overview](#project-structure-overview)
2. [Setting Up Build Tools (Maven and Gradle)](#setting-up-build-tools)
3. [MVC Design Pattern Overview](#mvc-design-pattern-overview)
4. [Creating a Spigot Plugin](#creating-a-spigot-plugin)
    - [Models](#models)
    - [Repositories](#repositories)
    - [Controllers and Services](#controllers-and-services)
    - [Commands](#commands)
    - [Event Listeners](#event-listeners)
5. [Testing and Debugging](#testing-and-debugging)

## Project Structure Overview

The structure of your project should follow a well-organized convention similar to a Spring Boot project. This allows for better readability, modularity, and maintainability.

```
project-root/
|-- src/
|   |-- main/
|   |   |-- java/
|   |   |   |-- gg/supervisor/
|   |   |       |-- plugin/
|   |   |       |   |-- adapter/
|   |   |       |   |-- api/
|   |   |       |   |-- controller/
|   |   |       |   |-- model/
|   |   |       |   |-- listener/
|   |   |       |   |-- service/
|   |   |       |   |-- repository/
|   |   |       |   |-- util/
|   |-- resources/
|   |   |-- plugin.yml
|-- build.gradle (or pom.xml)
```

- **model/**: Represents data entities, similar to DTOs in a Spring Boot project.
- **repository/**: Handles persistence of models.
- **service/**: Contains business logic.
- **controller/**: Handles interactions between the server and services.
- **listener/**: Contains event listeners, equivalent to event-driven controllers.
- **util/**: Utility classes for supporting tasks.

## Setting Up Build Tools

### Maven Setup

Add the following dependencies to your `pom.xml`:

```xml
<dependencies>
    <dependency>
        <groupId>gg.supervisor</groupId>
        <artifactId>core</artifactId>
        <version>2.0.0</version>
        <scope>provided</scope>
    </dependency>
    <!-- Add other necessary dependencies for Supervisor Core Framework -->
</dependencies>
```

### Gradle Setup

Add the following dependencies to your `build.gradle` file:

```groovy
repositories {
    maven {
        url = "https://maven.vertmix.com/"
    }
}
dependencies {
    implementation 'gg.supervisor:core:2.0.0'
    // Add other necessary dependencies for Supervisor Core Framework
}
```

```kts
repositories {
    maven("https://maven.vertmix.com/")
}
dependencies {
    implementation("gg.supervisor:core:2.0.0")
    // Add other necessary dependencies for Supervisor Core Framework
}
```

## MVC Design Pattern Overview

The MVC pattern is a powerful way to separate concerns:

1. **Model**: Represents the data structure (e.g., player data or game entities).
2. **View**: For a Spigot plugin, views could be commands, in-game messages, or GUIs that present data.
3. **Controller**: Coordinates interaction between the user, the system, and the business logic.

This separation helps with testability, maintainability, and scalability.

## Creating a Spigot Plugin

### 1. Models

The model represents your data, such as information about players, in-game objects, or settings.

Example model:
```java
package gg.supervisor.dummy.plugin.model;

import org.bukkit.Location;

public class Dog {
    public String name;
    public Location location;

    public Dog(String name, Location location) {
        this.name = name;
        this.location = location;
    }
}
```

### 2. Repositories

Repositories are responsible for data persistence. With the Supervisor Core Framework, you can use `JsonRepository` or `JsonPlayerRepository`.

Example repository:
```java
package gg.supervisor.dummy.plugin.repository;

import gg.supervisor.core.annotation.Component;
import gg.supervisor.core.repository.JsonRepository;
import gg.supervisor.dummy.plugin.model.Dog;

@Component
public interface DogRepository extends JsonRepository<Dog> {
}
```

### 3. Controllers and Services

Controllers handle requests, while services contain the business logic.

Example service:
```java
package gg.supervisor.dummy.plugin.service;

import gg.supervisor.core.annotation.Component;
import gg.supervisor.dummy.plugin.model.Dog;
import gg.supervisor.dummy.plugin.repository.DogRepository;
import org.bukkit.Bukkit;
import org.bukkit.Location;

@Component
public class DogService {
    private final DogRepository dogRepository;

    public DogService(DogRepository repository) {
        this.dogRepository = repository;
        if (repository.values().isEmpty()) {
            repository.save("ralph", new Dog("Ralph", new Location(Bukkit.getWorld("world"), 0, 0, 0)));
            Bukkit.getLogger().info("Saved Ralph!");
        }
    }

    public void listDogs() {
        dogRepository.values().forEach(dog -> Bukkit.getLogger().info("Found a dog named " + dog.name));
    }
}
```

### 4. Commands

Commands act as user interfaces (the "View" in MVC). Use commands to interact with your plugin.

Example command:
```java
package gg.supervisor.dummy.plugin.command;

import gg.supervisor.dummy.plugin.service.DogService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

@Component
public class DogCommand implements CommandExecutor {
    private final DogService dogService;

    public DogCommand(DogService dogService) {
        this.dogService = dogService;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("listdogs")) {
            dogService.listDogs();
            return true;
        }
        return false;
    }
}
```

To register the command in your `JavaPlugin` class:
```java
@Override
public void onEnable() {
    SuperviserLoader.register(this);
}
```

### 5. Event Listeners

Listeners respond to events in the game, providing another way for users to interact.

Example listener:
```java
package gg.supervisor.dummy.plugin.listener;

import gg.supervisor.dummy.plugin.service.DogService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

@Component
public class PlayerJoinListener implements Listener {
    private final DogService dogService;

    public PlayerJoinListener(DogService dogService) {
        this.dogService = dogService;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        dogService.listDogs();
    }
}
```