package info.smart_tools.smartactors.actors;

import info.smart_tools.smartactors.actors.exception.SampleDBException;
import info.smart_tools.smartactors.actors.wrapper.SampleGetByIdWrapper;
import info.smart_tools.smartactors.actors.wrapper.SampleUpsertWrapper;
import info.smart_tools.smartactors.core.iaction.IAction;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.core.iobject.exception.SerializeException;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.ipool.IPool;
import info.smart_tools.smartactors.core.itask.ITask;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;
import info.smart_tools.smartactors.core.pool_guard.PoolGuard;
import info.smart_tools.smartactors.core.postgres_connection.wrapper.ConnectionOptions;

/**
 * Sample actor which upserts and retreives the document from database.
 * NOTE: IT'S NOT COMMON TO PUT ALL DB OPERATIONS INTO SINGLE ACTOR, HERE IT'S DONE ONLY FOR EXAMPLE.
 * Also it's not common to actor to access any collection, typically the actor works only with one collection.
 */
public class SampleDBActor {

    /**
     * Pool of DB connections.
     */
    private final IPool pool;

    /**
     * Constructs the actor. Resolves connection pool here.
     */
    public SampleDBActor() throws SampleDBException {
        try {
            ConnectionOptions options = IOC.resolve(Keys.getOrAdd("PostgresConnectionOptions"));
            pool = IOC.resolve(Keys.getOrAdd("PostgresConnectionPool"), options);
        } catch (ResolutionException e) {
            throw new SampleDBException("Cannot create actor", e);
        }
    }

    public void upsertDocument(SampleUpsertWrapper wrapper) throws SampleDBException {
        String collectionName = null;
        IObject document = null;
        try {
            collectionName = wrapper.getCollectionName();
            document = wrapper.getDocument();

            try (PoolGuard guard = new PoolGuard(pool)) {
                ITask task = IOC.resolve(
                        Keys.getOrAdd("db.collection.upsert"),
                        guard.getObject(),
                        collectionName,
                        document
                );
                task.execute();
            }

            wrapper.setDocument(document);
        } catch (Exception e) {
            try {
                throw new SampleDBException("Failed to upsert document " + document.serialize() + " into " + collectionName, e);
            } catch (SerializeException e1) {
                throw new SampleDBException("Failed to upsert unserializable document into " + collectionName, e);
            }
        }
    }

    public void getDocumentById(SampleGetByIdWrapper wrapper)
            throws SampleDBException {
        String collectionName = null;
        Object id = null;
        try {
            collectionName = wrapper.getCollectionName();
            id = wrapper.getDocumentId();

            try (PoolGuard guard = new PoolGuard(pool)) {
                ITask task = IOC.resolve(
                        Keys.getOrAdd("db.collection.getbyid"),
                        guard.getObject(),
                        wrapper.getCollectionName(),
                        id,
                        (IAction<IObject>) doc -> {
                            try {
                                wrapper.setDocument(doc);
                            } catch (ChangeValueException e) {
                                throw new RuntimeException(e);
                            }
                        }
                );
                task.execute();
            }
        } catch (Exception e) {
            throw new SampleDBException("Failed to get document " + id + " in " + collectionName, e);
        }
    }
}
