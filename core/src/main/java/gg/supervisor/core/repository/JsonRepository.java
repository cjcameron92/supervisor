package gg.supervisor.core.repository;

/**
 * The {@code JsonRepository} interface represents a specialized version of the {@code Repository}
 * for managing entities in a JSON-based storage system. It extends the general {@code Repository} interface
 * to provide the same CRUD (Create, Read, Update, Delete) functionalities, but specifically leverages
 * JSON as the storage format.
 *
 * <p>This interface acts as a marker for repositories that use JSON for data persistence,
 * allowing the system to easily differentiate between various types of repositories
 * and apply specialized logic where applicable (e.g., different serialization methods or specific storage paths).</p>
 *
 * <p><strong>Key Features:</strong></p>
 * <ul>
 *     <li>Provides all the basic CRUD functionalities through its parent interface {@code Repository}.</li>
 *     <li>Serves as a marker for repositories that handle JSON-based data persistence.</li>
 *     <li>Can be used for dependency injection, allowing for specialized handling of JSON storage in an application context.</li>
 * </ul>
 *
 * @param <T> The type of entities managed by the repository.
 */
public interface JsonRepository<T> extends Repository<T> {
}
