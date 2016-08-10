package info.smart_tools.smartactors.core.in_memory_db_section_processing_strategy;

import info.smart_tools.smartactors.core.create_new_instance_strategy.CreateNewInstanceStrategy;
import info.smart_tools.smartactors.core.ds_object.DSObject;
import info.smart_tools.smartactors.core.field_name.FieldName;
import info.smart_tools.smartactors.core.iconfiguration_manager.exceptions.ConfigurationProcessingException;
import info.smart_tools.smartactors.core.idatabase.exception.IDataBaseException;
import info.smart_tools.smartactors.core.ifield_name.IFieldName;
import info.smart_tools.smartactors.core.iioccontainer.exception.RegistrationException;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.in_memory_database.InMemoryDatabase;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.iscope.IScope;
import info.smart_tools.smartactors.core.iscope_provider_container.exception.ScopeProviderException;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;
import info.smart_tools.smartactors.core.resolve_by_name_ioc_strategy.ResolveByNameIocStrategy;
import info.smart_tools.smartactors.core.scope_provider.ScopeProvider;
import info.smart_tools.smartactors.core.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.core.strategy_container.StrategyContainer;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.FileHandler;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class InMemoryDBSectionProcessingStrategyTest {
    InMemoryDatabase inMemoryDatabase;
    IObject mockObject;

    @Before
    public void setUp() throws ScopeProviderException, RegistrationException, ResolutionException, InvalidArgumentException {
        inMemoryDatabase = mock(InMemoryDatabase.class);
        mockObject = mock(IObject.class);
        ScopeProvider.subscribeOnCreationNewScope(
                scope -> {
                    try {
                        scope.setValue(IOC.getIocKey(), new StrategyContainer());
                    } catch (Exception e) {
                        throw new Error(e);
                    }
                }
        );

        Object keyOfMainScope = ScopeProvider.createScope(null);
        IScope mainScope = ScopeProvider.getScope(keyOfMainScope);
        ScopeProvider.setCurrentScope(mainScope);
        IOC.register(
                IOC.getKeyForKeyStorage(),
                new ResolveByNameIocStrategy()
        );
        IOC.register(Keys.getOrAdd(IFieldName.class.getCanonicalName()), new CreateNewInstanceStrategy(
                        (args) -> {
                            try {
                                return new FieldName((String) args[0]);
                            } catch (InvalidArgumentException e) {
                            }
                            return null;
                        }
                )
        );

        IOC.register(Keys.getOrAdd(DSObject.class.getCanonicalName()), new SingletonStrategy(mockObject));
        IOC.register(Keys.getOrAdd(InMemoryDatabase.class.getCanonicalName()), new SingletonStrategy(inMemoryDatabase));

    }

    @Test
    public void testLoadingConfig() throws InvalidArgumentException, ResolutionException, ConfigurationProcessingException, ChangeValueException, IDataBaseException {
        List<String> iObjects = new LinkedList<>();
        iObjects.add("{\"foo\": \"bar\"}");
        iObjects.add("{\"foo1\": \"bar1\"}");
        IObject inMemoryDatabaseConfig = new DSObject("{\"name\":\"my_collection_name\"}");
        List<IObject> inMemoryDb = new ArrayList<>();
        DSObject config = new DSObject();
        inMemoryDatabaseConfig.setValue(new FieldName("documents"), iObjects);
        inMemoryDb.add(inMemoryDatabaseConfig);
        InMemoryDBSectionProcessingStrategy sectionProcessingStrategy = new InMemoryDBSectionProcessingStrategy();
        config.setValue(new FieldName("inMemoryDb"), inMemoryDb);
        sectionProcessingStrategy.onLoadConfig(config);
        verify(inMemoryDatabase).createCollection("my_collection_name");
        verify(inMemoryDatabase, times(2)).insert(mockObject, "my_collection_name");
    }
}

