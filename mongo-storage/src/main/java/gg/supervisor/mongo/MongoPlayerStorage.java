package gg.supervisor.mongo;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import gg.supervisor.storage.PlayerStorage;
import gg.supervisor.storage.PlayerStorageCache;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.mongodb.client.model.Filters.eq;
import static gg.supervisor.storage.json.JsonStorage.GSON;

@Deprecated
public class MongoPlayerStorage<T> implements PlayerStorage<T>, PlayerStorageCache<T>, Listener {

    private final Cache<UUID, T> mongoCache = CacheBuilder.newBuilder().build();
    private final Plugin plugin;
    private final MongoCollection<Document> collection;
    private final Class<T> clazz;
    private final Function<Player, T> function;
    private final boolean forceCreate;
    private final boolean verbose;

    public MongoPlayerStorage(Plugin plugin, MongoData mongoData, String databaseName, String collectionName, Class<T> clazz, Function<Player, T> function) {
        this(plugin, mongoData, databaseName, collectionName, clazz, function, true, false);
    }

    public MongoPlayerStorage(Plugin plugin, MongoData mongoData,String databaseName, String collectionName, Class<T> clazz, Function<Player, T> function, boolean verbose) {
        this(plugin, mongoData, databaseName, collectionName, clazz, function, true, verbose);
    }

    public MongoPlayerStorage(Plugin plugin, MongoData mongoData, String databaseName, String collectionName, Class<T> clazz, Function<Player, T> function, boolean forceCreate, boolean verbose) {
        this.plugin = plugin;
        this.collection = mongoData.getMongoClient().getDatabase(databaseName).getCollection(collectionName);
        this.clazz = clazz;
        this.function = function;
        this.forceCreate = forceCreate;
        this.verbose = verbose;

        // register
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public T getPlayer(UUID uuid) {
        final Document document = collection.find(eq("_id", uuid.toString())).first();
        if (document != null) {
            Object o = document.remove("_id");

            if (verbose) {
                Bukkit.getLogger().info("Loaded player " + o.toString() + " from MongoDB collection " + collection.getNamespace().getCollectionName());
            }

            return GSON.fromJson(document.toJson(), clazz);
        }
        return null;
    }

    @Override
    public void savePlayer(UUID uuid) {
        T type = mongoCache.getIfPresent(uuid);
        if (type == null) {
            type = getPlayer(uuid);
        }

        if (type != null) {
            final Document document = Document.parse(GSON.toJson(type)).append("_id", uuid.toString());

            collection.replaceOne(Filters.eq("_id", uuid.toString()), document, new ReplaceOptions().upsert(true));

            mongoCache.put(uuid, type);
            if (verbose) {
                Bukkit.getLogger().info("Saved player " + uuid.toString() + " to MongoDB collection " + collection.getNamespace().getCollectionName());
            }
        }
    }

    @Override
    public void update(UUID uuid, T type) {
        mongoCache.put(uuid, type);
        savePlayer(uuid);
    }

    @Override
    public void update(UUID uuid, Consumer<T> consumer) {
        T type = getPlayer(uuid);
        if (type != null) {
            consumer.accept(type);
            update(uuid, type);
        }
    }

    @Override
    public T getFromCache(UUID uuid) {
        return this.mongoCache.getIfPresent(uuid);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            final Player player = event.getPlayer();
            T type = getPlayer(player.getUniqueId());

            if (type == null && forceCreate) {
                type = function.apply(player);
                final Document document = Document.parse(GSON.toJson(type)).append("_id", player.getUniqueId().toString());
                this.collection.insertOne(document);
                if (verbose) {
                    Bukkit.getLogger().info("Create object for player " + player.getName() + " in MongoDB collection " + collection.getNamespace().getCollectionName());

                }
            }

            if (type != null) {
                this.mongoCache.put(player.getUniqueId(), type);
                if (verbose) {
                    Bukkit.getLogger().info("Cached player " + player.getName() + " from MongoDB collection " + collection.getNamespace().getCollectionName());
                }
            }
        });
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        final Player player = event.getPlayer();

        this.mongoCache.invalidate(event.getPlayer().getUniqueId());

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> savePlayer(player.getUniqueId()));


        if (verbose) {
            Bukkit.getLogger().info("Invalidated cache for " + event.getPlayer().getName() + " from MongoDB collection " + collection.getNamespace().getCollectionName());
        }
    }
}