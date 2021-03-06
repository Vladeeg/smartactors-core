package info.smart_tools.smartactors.ioc_strategy_pack_plugins.resolve_standard_types_strategies_plugin;

import info.smart_tools.smartactors.base.interfaces.iresolve_dependency_strategy.IResolveDependencyStrategy;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.base.interfaces.iaction.IPoorAction;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecuteException;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ikey.IKey;
import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.exception.PluginException;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyNew;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@PrepareForTest({IOC.class, Keys.class, ResolveStandardTypesStrategiesPlugin.class, IPoorAction.class})
@RunWith(PowerMockRunner.class)
public class ResolveStandardTypesStrategiesPluginTest {

    private IBootstrap bootstrap;
    private ResolveStandardTypesStrategiesPlugin plugin;

    @Before
    public void setUp() throws Exception {

        mockStatic(IOC.class);
        mockStatic(Keys.class);

        bootstrap = mock(IBootstrap.class);
        plugin = new ResolveStandardTypesStrategiesPlugin(bootstrap);
    }

    @Test
    public void ShouldRegisterStrategiesForStandardTypes() throws Exception {

        BootstrapItem bootstrapItem = mock(BootstrapItem.class);
        whenNew(BootstrapItem.class).withArguments("ResolveStandardTypesStrategiesPlugin").thenReturn(bootstrapItem);
        when(bootstrapItem.after(anyString())).thenReturn(bootstrapItem);
        plugin.load();
        verifyNew(BootstrapItem.class).withArguments("ResolveStandardTypesStrategiesPlugin");

        verify(bootstrapItem).after("IOC");
        ArgumentCaptor<IPoorAction> actionArgumentCaptor = ArgumentCaptor.forClass(IPoorAction.class);
        verify(bootstrapItem).process(actionArgumentCaptor.capture());

        IKey stringConvertKey = mock(IKey.class);
        when(Keys.getOrAdd(String.class.getCanonicalName() + "convert")).thenReturn(stringConvertKey);
        IKey integerConvertKey = mock(IKey.class);
        when(Keys.getOrAdd(Integer.class.getCanonicalName() + "convert")).thenReturn(integerConvertKey);
        IKey bigDecimalConvertKey = mock(IKey.class);
        when(Keys.getOrAdd(BigDecimal.class.getCanonicalName() + "convert")).thenReturn(bigDecimalConvertKey);
        IKey localDateTimeConvertKey = mock(IKey.class);
        when(Keys.getOrAdd(LocalDateTime.class.getCanonicalName() + "convert")).thenReturn(localDateTimeConvertKey);
        IKey listConvertKey = mock(IKey.class);
        when(Keys.getOrAdd(List.class.getCanonicalName() + "convert")).thenReturn(listConvertKey);

        verify(bootstrap).add(eq(bootstrapItem));

        actionArgumentCaptor.getValue().execute();

        verifyStatic();
        Keys.getOrAdd(String.class.getCanonicalName() + "convert");
        verifyStatic();
        Keys.getOrAdd(Integer.class.getCanonicalName() + "convert");
        verifyStatic();
        Keys.getOrAdd(BigDecimal.class.getCanonicalName() + "convert");
        verifyStatic();
        Keys.getOrAdd(LocalDateTime.class.getCanonicalName() + "convert");
        verifyStatic();
        Keys.getOrAdd(List.class.getCanonicalName() + "convert");

        verifyStatic();
        IOC.register(eq(stringConvertKey), any(IResolveDependencyStrategy.class));
        verifyStatic();
        IOC.register(eq(integerConvertKey), any(IResolveDependencyStrategy.class));
        verifyStatic();
        IOC.register(eq(bigDecimalConvertKey), any(IResolveDependencyStrategy.class));
        verifyStatic();
        IOC.register(eq(localDateTimeConvertKey), any(IResolveDependencyStrategy.class));
        verifyStatic();
        IOC.register(eq(listConvertKey), any(IResolveDependencyStrategy.class));
    }

    @Test(expected = PluginException.class)
    public void ShouldThrowPluginException_When_BootstrapErrorIsOccurred() throws Exception {

        whenNew(BootstrapItem.class).withArguments("ResolveStandardTypesStrategiesPlugin").thenThrow(new InvalidArgumentException(""));
        plugin.load();
        fail();
    }

    @Test(expected = ActionExecuteException.class)
    public void ShouldThrowException_When_InternalErrorIsOccurred() throws Exception {

        BootstrapItem bootstrapItem = mock(BootstrapItem.class);
        whenNew(BootstrapItem.class).withArguments("ResolveStandardTypesStrategiesPlugin").thenReturn(bootstrapItem);
        when(bootstrapItem.after(anyString())).thenReturn(bootstrapItem);

        plugin.load();

        ArgumentCaptor<IPoorAction> actionArgumentCaptor = ArgumentCaptor.forClass(IPoorAction.class);
        verify(bootstrapItem).process(actionArgumentCaptor.capture());

        when(Keys.getOrAdd(String.class.getCanonicalName() + "convert")).thenThrow(new ResolutionException(""));
        actionArgumentCaptor.getValue().execute();
        fail();
    }

}
