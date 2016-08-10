package info.smart_tools.smartactors.core.postgres_create_task;

import info.smart_tools.smartactors.core.db_storage.utils.CollectionName;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;

/**
 * A query message to be passed to {@link PostgresCreateTask#prepare(IObject)}.
 */
public interface CreateCollectionMessage {

    /**
     * Returns the name of the collection to create
     * @return the collection name
     * @throws ReadValueException if the value cannot be read from underlying IObject
     */
    CollectionName getCollectionName() throws ReadValueException;

    /**
     * Returns the document describing options of the collection creation.
     * @return the options as IObject
     * @throws ReadValueException if the value cannot be read from underlying IObject
     */
    IObject getOptions() throws ReadValueException;

}
