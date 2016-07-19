/**
 * Contains CreateSessionActor
 */
package info.smart_tools.smartactors.actors.create_session;

import info.smart_tools.smartactors.actors.create_session.exception.CreateSessionException;
import info.smart_tools.smartactors.actors.create_session.wrapper.CreateSessionConfig;
import info.smart_tools.smartactors.actors.create_session.wrapper.CreateSessionMessage;
import info.smart_tools.smartactors.actors.create_session.wrapper.Session;
import info.smart_tools.smartactors.core.db_storage.interfaces.StorageConnection;
import info.smart_tools.smartactors.core.idatabase_task.IDatabaseTask;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.ipool.IPool;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;
import info.smart_tools.smartactors.core.pool_guard.IPoolGuard;
import info.smart_tools.smartactors.core.pool_guard.PoolGuard;
import info.smart_tools.smartactors.core.pool_guard.exception.PoolGuardException;
import info.smart_tools.smartactors.core.wrapper_generator.Field;

import java.util.List;

/**
 * Actor check current session, if she's null create new session
 */
public class CreateSessionActor {
    private String collectionName;
    private IPool connectionPool;
    //TODO:: in future will change dependency from core.db_search_task to core.db_task
    private IObject bufferedQuery;

    private static Field SESSION_ID_F;
    private static Field EQUALS_F;

    static {
        try {
            SESSION_ID_F = IOC.resolve(Keys.getOrAdd(Field.class.toString()), "sessionId");
            EQUALS_F = IOC.resolve(Keys.getOrAdd(Field.class.toString()), "$eq");

        } catch (ResolutionException e) {
            //TODO:: handle exception
        }
    }

    /**
     * Constructor for CreateSessionActor
     * @param config is any configurations
     * @throws CreateSessionException for any occurred error
     */
    public CreateSessionActor(final CreateSessionConfig config) throws CreateSessionException {
        //TODO:: replace CreateSessionConfig to IObject
        try {
            this.collectionName = config.getCollectionName();
            this.connectionPool = config.getConnectionPool();
        } catch (ReadValueException | ChangeValueException e) {
            throw new CreateSessionException("Can't create Actor");
        }
    }

    /**
     * Check current session, if she's null create new session
     * @param inputMessage message for checking
     * @throws CreateSessionException Calling when throws any exception inside CreateSessionActor
     */
    public void resolveSession(final CreateSessionMessage inputMessage) throws CreateSessionException {
        try {
            String sessionId = inputMessage.getSessionId();
            if (sessionId == null || sessionId.equals("")) {
                IObject authInfo = inputMessage.getAuthInfo();
                Session newSession = IOC.resolve(Keys.getOrAdd(Session.class.toString()));
                newSession.setAuthInfo(authInfo);
                inputMessage.setSession(newSession);
            } else {
                try (IPoolGuard poolGuard = new PoolGuard(connectionPool)) {
                    IDatabaseTask searchTask = IOC.resolve(Keys.getOrAdd(IDatabaseTask.class.toString()), "PSQL");
                    IObject searchQuery = IOC.resolve(Keys.getOrAdd(IObject.class.toString()));

                    StorageConnection connection = IOC.resolve(Keys.getOrAdd(StorageConnection.class.toString()), poolGuard.getObject());
                    prepareSearchQuery(searchQuery, inputMessage);

                    searchTask.setConnection(connection);
                    searchTask.prepare(searchQuery);
                    searchTask.execute();

                    Field bufferedQueryF = IOC.resolve(Keys.getOrAdd(Field.class.toString()), "bufferedQuery");
                    try {
                        this.bufferedQuery = bufferedQueryF.in(searchQuery);
                        if (bufferedQuery == null) {
                            throw new CreateSessionException("Search Query is null.Search task didn't returned a buffered query!");
                        }
                    } catch (InvalidArgumentException e) {
                        throw new CreateSessionException("Search task didn't returned a buffered query!", e);
                    }

                    Field countSearchResultF = IOC.resolve(Keys.getOrAdd(Field.class.toString()), "countSearchResult");
                    //TODO:: replace ()
                    if ((Integer)countSearchResultF.in(searchQuery) == 0) {
                        throw new CreateSessionException("Cannot find session by sessionId: "
                                + inputMessage.getSessionId()
                        );
                    }

                    Field searchResultField = IOC.resolve(Keys.getOrAdd(Field.class.toString()), "searchResult");

                    IObject result = ((List<IObject>)searchResultField.in(searchQuery)).get(0);

                    Field sessionF = IOC.resolve(Keys.getOrAdd(Field.class.toString()), "session");
                    Session fromDBSession = sessionF.in(result);
                    if (fromDBSession == null) {
                        throw new CreateSessionException("Find session is null");
                    }
                    inputMessage.setSession(fromDBSession);

                } catch (PoolGuardException e) {
                    throw new CreateSessionException("Cannot get connection from pool.", e);
                } catch (Exception e) {
                    throw new CreateSessionException("Error during find session by sessionId: " + inputMessage.getSessionId(), e);
                }
            }
        } catch (ReadValueException | ChangeValueException e) {
            throw new CreateSessionException("Cannot create or find session by sessionId", e);
        } catch (ResolutionException e) {
            throw new CreateSessionException("Error because cannot resolve Session.class", e);
        }
    }

    private void prepareSearchQuery(final IObject searchQuery, final CreateSessionMessage inputMessage)
            throws ChangeValueException, InvalidArgumentException, ResolutionException, ReadValueException {
        IObject query = IOC.resolve(Keys.getOrAdd(IObject.class.toString()));
        IObject sessionIdObject = IOC.resolve(Keys.getOrAdd(IObject.class.toString()));

        EQUALS_F.out(sessionIdObject, inputMessage.getSessionId());
        SESSION_ID_F.out(query, sessionIdObject);

        Field collectionNameF = IOC.resolve(Keys.getOrAdd(Field.class.toString()), "collectionName");
        collectionNameF.out(searchQuery, this.collectionName);
        Field pageSizeF = IOC.resolve(Keys.getOrAdd(Field.class.toString()), "pageSize");
        pageSizeF.out(searchQuery, 1);
        Field pageNumberF = IOC.resolve(Keys.getOrAdd(Field.class.toString()), "pageNumber");
        pageNumberF.out(searchQuery, 1);
        Field bufferedQueryF = IOC.resolve(Keys.getOrAdd(Field.class.toString()), "bufferedQuery");
        bufferedQueryF.out(searchQuery, this.bufferedQuery);
        Field criteriaF = IOC.resolve(Keys.getOrAdd(Field.class.toString()), "criteria");
        criteriaF.out(searchQuery, query);

    }
}
