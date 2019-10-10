package info.smart_tools.smartactors.scope_plugins.scope_provider_plugin;

import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecutionException;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap_item.IBootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.IPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.exception.PluginException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.scope.iscope.IScope;
import info.smart_tools.smartactors.scope.iscope_provider_container.exception.ScopeProviderException;
import info.smart_tools.smartactors.scope.scope_provider.ScopeProvider;

/**
 *
 */
public class PluginScopeProvider implements IPlugin {
    private IBootstrap<IBootstrapItem<String>> bootstrap;

    /**
     * The constructor.
     *
     * @param bootstrap    the bootstrap
     */
    public PluginScopeProvider(final IBootstrap<IBootstrapItem<String>> bootstrap) {
        this.bootstrap = bootstrap;
    }

    @Override
    public void load() throws PluginException {
        try {
            /* "scope_provider_container" - set container of ScopeProvider */
            IBootstrapItem<String> containerItem = IOC.resolve(
                    Keys.getKeyByName("bootstrap item"),
                    "scope_provider_container"
            );

            containerItem
                    .process(() -> { });

            bootstrap.add(containerItem);

            /* "create_system_scope" - create and set as current the system scope */
            IBootstrapItem<String> systemScopeItem = IOC.resolve(
                    Keys.getKeyByName("bootstrap item"),
                    "create_system_scope"
            );

            systemScopeItem
                    .after("scope_provider_container")
                    .process(() -> {
                        try {
                            Object systemScopeKey = ScopeProvider.createScope(null);
                            IScope scope = ScopeProvider.getScope(systemScopeKey);
                            ScopeProvider.setCurrentScope(scope);
                        } catch (ScopeProviderException e) {
                            throw new ActionExecutionException("ScopeProvider plugin can't load.", e);
                        }
                    });

            bootstrap.add(systemScopeItem);
        } catch (ResolutionException e) {
            throw new PluginException(e);
        }
    }
}
