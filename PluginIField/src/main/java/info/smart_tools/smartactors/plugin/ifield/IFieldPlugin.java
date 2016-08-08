package info.smart_tools.smartactors.plugin.ifield;

import info.smart_tools.smartactors.core.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.core.field.Field;
import info.smart_tools.smartactors.core.iaction.exception.ActionExecuteException;
import info.smart_tools.smartactors.core.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.core.ibootstrap_item.IBootstrapItem;
import info.smart_tools.smartactors.core.ifield.IField;
import info.smart_tools.smartactors.core.ifield_name.IFieldName;
import info.smart_tools.smartactors.core.iioccontainer.exception.RegistrationException;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.ikey.IKey;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.iplugin.IPlugin;
import info.smart_tools.smartactors.core.iplugin.exception.PluginException;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;
import info.smart_tools.smartactors.core.resolve_by_name_ioc_with_lambda_strategy.ResolveByNameIocStrategy;

/**
 * Plugin for registration of IOC strategy for Field
 */
public class IFieldPlugin implements IPlugin {

    private final IBootstrap<IBootstrapItem<String>> bootstrap;

    /**
     * Constructor
     * @param bootstrap bootstrap element
     */
    public IFieldPlugin(final IBootstrap<IBootstrapItem<String>> bootstrap) {
        this.bootstrap = bootstrap;
    }

    @Override
    public void load() throws PluginException {

        try {
            BootstrapItem item = new BootstrapItem("IFieldPlugin");
            item
                .after("IOC")
                .after("IFieldNamePlugin")
                .process(() -> {
                    try {
                        IKey fieldKey = Keys.getOrAdd(IField.class.getCanonicalName());
                        IOC.register(fieldKey, new ResolveByNameIocStrategy(
                            (args) -> {
                                String fieldName = String.valueOf(args[0]);
                                try {
                                    return new Field(IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), fieldName));
                                } catch (InvalidArgumentException | ResolutionException e) {
                                    throw new RuntimeException("Can't resolve IField: ", e);
                                }
                            }));
                    } catch (ResolutionException e) {
                        throw new ActionExecuteException("IField plugin can't load: can't get IField key", e);
                    } catch (InvalidArgumentException e) {
                        throw new ActionExecuteException("IField plugin can't load: can't create strategy", e);
                    } catch (RegistrationException e) {
                        throw new ActionExecuteException("IField plugin can't load: can't register new strategy", e);
                    }
                });
            bootstrap.add(item);
        } catch (InvalidArgumentException e) {
            throw new PluginException("Can't load IField plugin", e);
        }
    }
}
