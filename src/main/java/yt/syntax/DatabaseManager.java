package yt.syntax;

import com.mongodb.ConnectionString;
import com.mongodb.MongoException;
import com.mongodb.async.client.MongoClient;
import com.mongodb.async.client.MongoClients;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import lombok.NonNull;
import lombok.var;
import org.bson.Document;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * created on 17.02.19 / 13:25
 *
 * @author Daniel Riegler
 */
@SuppressWarnings("WeakerAccess")
public class DatabaseManager implements Closeable {

    private final static String COLLECTION = "redirects";

    private final MongoClient client;

    private final MongoCollection<Document> collection;

    public DatabaseManager(final ConnectionString string) throws MongoException {
        this.client = MongoClients.create(string);

        final var database = this.client.getDatabase(!StringUtil.isEmpty(string.getDatabase()) ? string.getDatabase() : "admin");
        this.collection = database.getCollection(COLLECTION);
    }

    public CompletableFuture<Document> find(final String key) {
        if (StringUtil.isEmpty(key)) return CompletableFuture.completedFuture(null);
        final var future = new CompletableFuture<Document>();
        this.collection.find(Filters.eq("key", key)).first((doc, x) -> {
            if (x != null) future.completeExceptionally(x);
            else future.complete(doc);
        });
        return future;
    }

    public CompletableFuture<Boolean> has(final String key) {
        final var future = new CompletableFuture<Boolean>();
        this.find(key).whenComplete((doc, x) -> future.complete(x == null && doc != null));
        return future;
    }

    public CompletableFuture<DeleteResult> delete(final String key) {
        final var future = new CompletableFuture<DeleteResult>();
        this.has(key).thenAccept(bool -> {
            if (!bool) future.completeExceptionally(new MongoException("The issued document does not exist!"));
            else this.collection.deleteOne(Filters.eq("key", key), (res, x) -> {
                if (x != null) future.completeExceptionally(x);
                else future.complete(res);
            });
        });
        return future;
    }

    public CompletableFuture insertOrUpdate(final String key, @NonNull final Document document) {
        final var future = new CompletableFuture<>();
        if (StringUtil.isEmpty(key)) {
            future.completeExceptionally(new MongoException("Key must not be empty!"));
            return future;
        }
        this.has(key).thenAccept(bool -> {
            if (!bool)
                this.collection.insertOne(document, (v, x) -> {
                    if (x != null) future.completeExceptionally(x);
                    else future.complete(v);
                });
            else this.collection.updateOne(Filters.eq("key", key), document, (res, x) -> {
                if (x != null) future.completeExceptionally(x);
                else future.complete(res);
            });
        });
        return future;
    }

    public CompletableFuture<List<Document>> list() {
        final var future = new CompletableFuture<List<Document>>();
        final var list = new ArrayList<Document>();
        this.collection.find().forEach(list::add, (v, x) -> {
            if (x != null) future.completeExceptionally(x);
            else future.complete(list);
        });
        return future;
    }

    @Override
    public void close() throws IOException {
        if (this.client == null) return;
        try {
            this.client.close();
        } catch (final MongoException e) {
            throw new IOException(e);
        }
    }
}
