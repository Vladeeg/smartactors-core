package info.smart_tools.smartactors.plugin.create_session;

import info.smart_tools.smartactors.actors.create_session.CreateSessionActor;
import info.smart_tools.smartactors.actors.create_session.wrapper.CreateSessionConfig;
import info.smart_tools.smartactors.core.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.core.create_new_instance_strategy.CreateNewInstanceStrategy;
import info.smart_tools.smartactors.core.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.core.ibootstrap_item.IBootstrapItem;
import info.smart_tools.smartactors.core.iioccontainer.exception.RegistrationException;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.ikey.IKey;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.iplugin.IPlugin;
import info.smart_tools.smartactors.core.iplugin.exception.PluginException;
import info.smart_tools.smartactors.core.ipool.IPool;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;
import info.smart_tools.smartactors.core.wrapper_generator.Field;

/**
 * Plugin for register actors for create session
 */
public class CreateSessionPlugin implements IPlugin {

    private final IBootstrap<IBootstrapItem<String>> bootstrap;

    /**
     * Constructor
     * @param bootstrap the bootstrap
     */
    public CreateSessionPlugin(final IBootstrap<IBootstrapItem<String>> bootstrap) {
        this.bootstrap = bootstrap;
    }

    @Override
    public void load() throws PluginException {
        try {
            IKey createSessionActorKey = Keys.getOrAdd(CreateSessionActor.class.toString());
            IBootstrapItem<String> item = new BootstrapItem("CreateSessionActorPlugin");

            item.process(() -> {
                try {
                    IPool connectionPool = IOC.resolve(Keys.getOrAdd(IPool.class.toString()));
                    IOC.register(createSessionActorKey, new CreateNewInstanceStrategy(
                            (args) -> {
                                try {
                                    IObject iObject = IOC.resolve(Keys.getOrAdd(IObject.class.toString()));
                                    Field<String> collectionNameF = IOC.resolve(Keys.getOrAdd(Field.class.toString()), "collectionName");
                                    collectionNameF.in(iObject, (String) args[0]);
                                    Field<IPool> connectionPoolF = IOC.resolve(Keys.getOrAdd(Field.class.toString()), "connectionPool");
                                    connectionPoolF.in(iObject, connectionPool);

                                    CreateSessionConfig param = IOC.resolve(Keys.getOrAdd(CreateSessionConfig.class.toString()), iObject);
                                    return new CreateSessionActor(param);
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                            }
                    ));
                } catch (RegistrationException | ResolutionException | InvalidArgumentException e) {
                    throw new RuntimeException(e);
                }
            });
            bootstrap.add(item);
        } catch (ResolutionException | InvalidArgumentException e) {
            throw new RuntimeException(e);
        }
    }
}