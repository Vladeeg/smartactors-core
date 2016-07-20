package info.smart_tools.smartactors.plugin.ifieldname;

import info.smart_tools.smartactors.core.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.core.field_name.FieldName;
import info.smart_tools.smartactors.core.iaction.IPoorAction;
import info.smart_tools.smartactors.core.iaction.exception.ActionExecuteException;
import info.smart_tools.smartactors.core.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.core.ifield_name.IFieldName;
import info.smart_tools.smartactors.core.iioccontainer.exception.RegistrationException;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.ikey.IKey;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.iplugin.exception.PluginException;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;
import info.smart_tools.smartactors.core.resolve_by_name_ioc_with_lambda_strategy.ResolveByNameIocStrategy;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.function.Function;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.*;

@PrepareForTest({IOC.class, Keys.class, ResolveByNameIocStrategy.class, IFieldNamePlugin.class, IFieldName.class, Function.class})
@RunWith(PowerMockRunner.class)
public class IFieldNamePluginTest {

    private IFieldNamePlugin plugin;
    private IBootstrap bootstrap;

    @Before
    public void setUp() throws Exception {

        mockStatic(IOC.class);
        mockStatic(Keys.class);

        bootstrap = mock(IBootstrap.class);
        plugin = new IFieldNamePlugin(bootstrap);
    }

    @Test
    public void MustCorrectLoadPlugin() throws Exception {

        BootstrapItem item = mock(BootstrapItem.class);
        whenNew(BootstrapItem.class).withArguments("field_name").thenReturn(item);

        when(item.after(anyString())).thenReturn(item);

        plugin.load();

        verifyNew(BootstrapItem.class).withArguments("field_name");

        verify(item).after("ioc");

        ArgumentCaptor<IPoorAction> iPoorActionArgumentCaptor = ArgumentCaptor.forClass(IPoorAction.class);
        verify(item).process(iPoorActionArgumentCaptor.capture());

        verify(bootstrap).add(item);

        IKey iFieldNameKey = mock(IKey.class);
        when(Keys.getOrAdd(IFieldName.class.getCanonicalName())).thenReturn(iFieldNameKey);

        ArgumentCaptor<ResolveByNameIocStrategy> resolveByNameIocStrategyArgumentCaptor =
                ArgumentCaptor.forClass(ResolveByNameIocStrategy.class);

        iPoorActionArgumentCaptor.getValue().execute();

        verifyStatic();
        Keys.getOrAdd(IFieldName.class.getCanonicalName());

        verifyStatic();
        IOC.register(eq(iFieldNameKey), resolveByNameIocStrategyArgumentCaptor.capture());

        String exampleFieldName = "exampleField";
        FieldName newFieldName = mock(FieldName.class);
        whenNew(FieldName.class).withArguments(exampleFieldName).thenReturn(newFieldName);

        assertTrue("Must return correct value",
                resolveByNameIocStrategyArgumentCaptor.getValue().resolve(exampleFieldName) == newFieldName);

        verifyNew(FieldName.class).withArguments(exampleFieldName);

    }

    @Test
    public void MustInCorrectLoadPluginWhenNewBootstrapItemThrowException() throws Exception {


        whenNew(BootstrapItem.class).withArguments("field_name").thenThrow(new InvalidArgumentException(""));

        try {
            plugin.load();
        } catch (PluginException e) {
            verifyNew(BootstrapItem.class).withArguments("field_name");
            return;
        }
        assertTrue("Must throw exception", false);
    }

    @Test
    public void MustInCorrectExecuteActionWhenKeysThrowException() throws Exception {

        BootstrapItem item = mock(BootstrapItem.class);
        whenNew(BootstrapItem.class).withArguments("field_name").thenReturn(item);

        when(item.after(anyString())).thenReturn(item);

        plugin.load();

        verifyNew(BootstrapItem.class).withArguments("field_name");

        verify(item).after("ioc");

        ArgumentCaptor<IPoorAction> iPoorActionArgumentCaptor = ArgumentCaptor.forClass(IPoorAction.class);
        verify(item).process(iPoorActionArgumentCaptor.capture());

        verify(bootstrap).add(item);

        when(Keys.getOrAdd(IFieldName.class.getCanonicalName())).thenThrow(new ResolutionException(""));

        try {
            iPoorActionArgumentCaptor.getValue().execute();
        } catch (ActionExecuteException e) {

            verifyStatic();
            Keys.getOrAdd(IFieldName.class.getCanonicalName());
            return;
        }
        assertTrue("Must throw exception", false);

    }

    @Test
    public void MustInCorrectExecuteActionWhenNewCreateStrategyThrowException() throws Exception {

        BootstrapItem item = mock(BootstrapItem.class);
        whenNew(BootstrapItem.class).withArguments("field_name").thenReturn(item);

        when(item.after(anyString())).thenReturn(item);

        plugin.load();

        verifyNew(BootstrapItem.class).withArguments("field_name");

        verify(item).after("ioc");

        ArgumentCaptor<IPoorAction> iPoorActionArgumentCaptor = ArgumentCaptor.forClass(IPoorAction.class);
        verify(item).process(iPoorActionArgumentCaptor.capture());

        verify(bootstrap).add(item);

        IKey iFieldNameKey = mock(IKey.class);
        when(Keys.getOrAdd(IFieldName.class.getCanonicalName())).thenReturn(iFieldNameKey);

        whenNew(ResolveByNameIocStrategy.class).withArguments(any()).thenThrow(new InvalidArgumentException(""));

        try {
            iPoorActionArgumentCaptor.getValue().execute();
        } catch (ActionExecuteException e) {

            verifyStatic();
            Keys.getOrAdd(IFieldName.class.getCanonicalName());

            verifyNew(ResolveByNameIocStrategy.class).withArguments(any());
            return;
        }
        assertTrue("Must throw Exception", false);

    }

    @Test
    public void MustInCorrectExecuteActionWhenIOCRegisterThrowException () throws Exception {

        BootstrapItem item = mock(BootstrapItem.class);
        whenNew(BootstrapItem.class).withArguments("field_name").thenReturn(item);

        when(item.after(anyString())).thenReturn(item);

        plugin.load();

        verifyNew(BootstrapItem.class).withArguments("field_name");

        verify(item).after("ioc");

        ArgumentCaptor<IPoorAction> iPoorActionArgumentCaptor = ArgumentCaptor.forClass(IPoorAction.class);
        verify(item).process(iPoorActionArgumentCaptor.capture());

        verify(bootstrap).add(item);

        IKey iFieldNameKey = mock(IKey.class);
        when(Keys.getOrAdd(IFieldName.class.getCanonicalName())).thenReturn(iFieldNameKey);

        ArgumentCaptor<ResolveByNameIocStrategy> resolveByNameIocStrategyArgumentCaptor =
                ArgumentCaptor.forClass(ResolveByNameIocStrategy.class);

        doThrow(new RegistrationException("")).when(IOC.class);
        IOC.register(eq(iFieldNameKey), any());
        try {
            iPoorActionArgumentCaptor.getValue().execute();
        } catch (ActionExecuteException e) {

            verifyStatic();
            Keys.getOrAdd(IFieldName.class.getCanonicalName());

            verifyStatic();
            IOC.register(eq(iFieldNameKey), resolveByNameIocStrategyArgumentCaptor.capture());

            String exampleFieldName = "exampleField";
            FieldName newFieldName = mock(FieldName.class);
            whenNew(FieldName.class).withArguments(exampleFieldName).thenReturn(newFieldName);

            assertTrue("Must return correct value",
                    resolveByNameIocStrategyArgumentCaptor.getValue().resolve(exampleFieldName) == newFieldName);

            verifyNew(FieldName.class).withArguments(exampleFieldName);
            return;
        }
        assertTrue("Must throw exception", false);
    }

    @Test(expected = RuntimeException.class)
    public void MustInCorrectResolveDependencyWhenArgLength0() throws Exception {

        BootstrapItem item = mock(BootstrapItem.class);
        whenNew(BootstrapItem.class).withArguments("field_name").thenReturn(item);

        when(item.after(anyString())).thenReturn(item);

        plugin.load();

        verifyNew(BootstrapItem.class).withArguments("field_name");

        verify(item).after("ioc");

        ArgumentCaptor<IPoorAction> iPoorActionArgumentCaptor = ArgumentCaptor.forClass(IPoorAction.class);
        verify(item).process(iPoorActionArgumentCaptor.capture());

        verify(bootstrap).add(item);

        IKey iFieldNameKey = mock(IKey.class);
        when(Keys.getOrAdd(IFieldName.class.getCanonicalName())).thenReturn(iFieldNameKey);

        ArgumentCaptor<Function<Object[], Object>> targetFuncArgumentCaptor = ArgumentCaptor.forClass((Class) Function.class);

        ResolveByNameIocStrategy resolveByNameIocStrategy = mock(ResolveByNameIocStrategy.class);
        whenNew(ResolveByNameIocStrategy.class).withArguments(targetFuncArgumentCaptor.capture()).thenReturn(resolveByNameIocStrategy);

        iPoorActionArgumentCaptor.getValue().execute();

        verifyStatic();
        Keys.getOrAdd(IFieldName.class.getCanonicalName());

        verifyNew(ResolveByNameIocStrategy.class).withArguments(targetFuncArgumentCaptor.getValue());

        verifyStatic();
        IOC.register(iFieldNameKey, resolveByNameIocStrategy);

        targetFuncArgumentCaptor.getValue().apply(new Object[0]);

    }

    @Test(expected = RuntimeException.class)
    public void MustInCorrectResolveDependencyWhenArg0IsNotString() throws Exception {

        BootstrapItem item = mock(BootstrapItem.class);
        whenNew(BootstrapItem.class).withArguments("field_name").thenReturn(item);

        when(item.after(anyString())).thenReturn(item);

        plugin.load();

        verifyNew(BootstrapItem.class).withArguments("field_name");

        verify(item).after("ioc");

        ArgumentCaptor<IPoorAction> iPoorActionArgumentCaptor = ArgumentCaptor.forClass(IPoorAction.class);
        verify(item).process(iPoorActionArgumentCaptor.capture());

        verify(bootstrap).add(item);

        IKey iFieldNameKey = mock(IKey.class);
        when(Keys.getOrAdd(IFieldName.class.getCanonicalName())).thenReturn(iFieldNameKey);

        ArgumentCaptor<Function<Object[], Object>> targetFuncArgumentCaptor = ArgumentCaptor.forClass((Class) Function.class);

        ResolveByNameIocStrategy createNewInstanceStrategy = mock(ResolveByNameIocStrategy.class);
        whenNew(ResolveByNameIocStrategy.class).withArguments(targetFuncArgumentCaptor.capture()).thenReturn(createNewInstanceStrategy);

        iPoorActionArgumentCaptor.getValue().execute();

        verifyStatic();
        Keys.getOrAdd(IFieldName.class.getCanonicalName());

        verifyNew(ResolveByNameIocStrategy.class).withArguments(targetFuncArgumentCaptor.getValue());

        verifyStatic();
        IOC.register(iFieldNameKey, createNewInstanceStrategy);

        targetFuncArgumentCaptor.getValue().apply(new Object[]{2});

    }

    @Test
    public void MustInCorrectResolveDependencyWhenNewFieldNameThrowException() throws Exception {

        BootstrapItem item = mock(BootstrapItem.class);
        whenNew(BootstrapItem.class).withArguments("field_name").thenReturn(item);

        when(item.after(anyString())).thenReturn(item);

        plugin.load();

        verifyNew(BootstrapItem.class).withArguments("field_name");

        verify(item).after("ioc");

        ArgumentCaptor<IPoorAction> iPoorActionArgumentCaptor = ArgumentCaptor.forClass(IPoorAction.class);
        verify(item).process(iPoorActionArgumentCaptor.capture());

        verify(bootstrap).add(item);

        IKey iFieldNameKey = mock(IKey.class);
        when(Keys.getOrAdd(IFieldName.class.getCanonicalName())).thenReturn(iFieldNameKey);

        ResolveByNameIocStrategy createNewInstanceStrategy = mock(ResolveByNameIocStrategy.class);

        ArgumentCaptor<Function<Object[], Object>> targetFuncArgumentCaptor = ArgumentCaptor.forClass((Class) Function.class);
        whenNew(ResolveByNameIocStrategy.class).withArguments(targetFuncArgumentCaptor.capture()).thenReturn(createNewInstanceStrategy);

        iPoorActionArgumentCaptor.getValue().execute();

        verifyStatic();
        Keys.getOrAdd(IFieldName.class.getCanonicalName());

        verifyNew(ResolveByNameIocStrategy.class).withArguments(targetFuncArgumentCaptor.getValue());

        verifyStatic();
        IOC.register(iFieldNameKey, createNewInstanceStrategy);

        String fieldNameName = "asd";

        whenNew(FieldName.class).withArguments(fieldNameName).thenThrow(new InvalidArgumentException(""));

        try {
            targetFuncArgumentCaptor.getValue().apply(new Object[]{fieldNameName});
        } catch (RuntimeException e) {
            verifyNew(FieldName.class).withArguments(fieldNameName);
        }

    }
}