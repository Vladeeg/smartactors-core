package info.smart_tools.smartactors.plugin.check_user_by_email_actor;

import info.smart_tools.smartactors.actors.check_user_by_email.CheckUserByEmailActor;
import info.smart_tools.smartactors.actors.check_user_by_email.wrapper.ActorParams;
import info.smart_tools.smartactors.core.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.core.create_new_instance_strategy.CreateNewInstanceStrategy;
import info.smart_tools.smartactors.core.iaction.IPoorAction;
import info.smart_tools.smartactors.core.iaction.exception.ActionExecuteException;
import info.smart_tools.smartactors.core.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.core.iioccontainer.exception.RegistrationException;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.ikey.IKey;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.iplugin.exception.PluginException;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;
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

@PrepareForTest({IOC.class, Keys.class, CheckUserByEmailActorPlugin.class, CreateNewInstanceStrategy.class})
@RunWith(PowerMockRunner.class)
public class CheckUserByEmailActorPluginTest {
    private CheckUserByEmailActorPlugin plugin;
    private IBootstrap bootstrap;

    @Before
    public void setUp() throws Exception {

        mockStatic(IOC.class);
        mockStatic(Keys.class);

        bootstrap = mock(IBootstrap.class);
        plugin = new CheckUserByEmailActorPlugin(bootstrap);
    }

    @Test
    public void MustCorrectLoadPlugin() throws Exception {

        BootstrapItem bootstrapItem = mock(BootstrapItem.class);
        whenNew(BootstrapItem.class).withArguments("CheckUserByEmailActorPlugin").thenReturn(bootstrapItem);

        when(bootstrapItem.after(anyString())).thenReturn(bootstrapItem);

        plugin.load();

        verifyNew(BootstrapItem.class).withArguments("CheckUserByEmailActorPlugin");

        ArgumentCaptor<IPoorAction> actionArgumentCaptor = ArgumentCaptor.forClass(IPoorAction.class);

        verify(bootstrapItem).after("IOC");
        verify(bootstrapItem).process(actionArgumentCaptor.capture());

        verify(bootstrap).add(bootstrapItem);

        IKey checkUserByEmailActorKey = mock(IKey.class);
        when(Keys.getOrAdd(CheckUserByEmailActor.class.getCanonicalName())).thenReturn(checkUserByEmailActorKey);

        actionArgumentCaptor.getValue().execute();

        verifyStatic();
        Keys.getOrAdd(CheckUserByEmailActor.class.getCanonicalName());

        ArgumentCaptor<CreateNewInstanceStrategy> createNewInstanceStrategyArgumentCaptor = ArgumentCaptor.forClass(CreateNewInstanceStrategy.class);

        verifyStatic();
        IOC.register(eq(checkUserByEmailActorKey), createNewInstanceStrategyArgumentCaptor.capture());

        IObject arg = mock(IObject.class);

        CheckUserByEmailActor actor = mock(CheckUserByEmailActor.class);
        ActorParams actorParams = mock(ActorParams.class);
        IKey actorParamsIKey = mock(IKey.class);
        when(Keys.getOrAdd(ActorParams.class.getCanonicalName())).thenReturn(actorParamsIKey);
        when(IOC.resolve(actorParamsIKey, arg)).thenReturn(actorParams);
        whenNew(CheckUserByEmailActor.class).withArguments(actorParams).thenReturn(actor);

        assertTrue("Objects must return correct object", createNewInstanceStrategyArgumentCaptor.getValue().resolve(arg) == actor);

        verifyStatic();
        Keys.getOrAdd(ActorParams.class.getCanonicalName());

        verifyStatic();
        IOC.resolve(actorParamsIKey, arg);

        verifyNew(CheckUserByEmailActor.class).withArguments(actorParams);
    }

    @Test
    public void MustInCorrectLoadNewIBootstrapItemThrowException() throws Exception {

        whenNew(BootstrapItem.class).withArguments("CheckUserByEmailActorPlugin").thenThrow(new InvalidArgumentException(""));

        try {
            plugin.load();
        } catch (PluginException e) {

            verifyNew(BootstrapItem.class).withArguments("CheckUserByEmailActorPlugin");
            return;
        }
        assertTrue("Must throw exception, but was not", false);
    }

    @Test
    public void MustInCorrectExecuteActionWhenKeysThrowException() throws Exception {

        BootstrapItem bootstrapItem = mock(BootstrapItem.class);
        whenNew(BootstrapItem.class).withArguments("CheckUserByEmailActorPlugin").thenReturn(bootstrapItem);

        when(bootstrapItem.after(anyString())).thenReturn(bootstrapItem);

        plugin.load();

        verifyNew(BootstrapItem.class).withArguments("CheckUserByEmailActorPlugin");

        ArgumentCaptor<IPoorAction> actionArgumentCaptor = ArgumentCaptor.forClass(IPoorAction.class);

        verify(bootstrapItem).after("IOC");
        verify(bootstrapItem).process(actionArgumentCaptor.capture());

        verify(bootstrap).add(bootstrapItem);

        when(Keys.getOrAdd(CheckUserByEmailActor.class.getCanonicalName())).thenThrow(new ResolutionException(""));

        try {
            actionArgumentCaptor.getValue().execute();
        } catch (ActionExecuteException e) {
            verifyStatic();
            Keys.getOrAdd(CheckUserByEmailActor.class.getCanonicalName());
            return;
        }
        assertTrue("Must throw exception", false);
    }

    @Test
    public void MustInCorrectExecuteActionWhenIOCRegisterThrowException() throws Exception {

        BootstrapItem bootstrapItem = mock(BootstrapItem.class);
        whenNew(BootstrapItem.class).withArguments("CheckUserByEmailActorPlugin").thenReturn(bootstrapItem);

        when(bootstrapItem.after(anyString())).thenReturn(bootstrapItem);

        plugin.load();

        verifyNew(BootstrapItem.class).withArguments("CheckUserByEmailActorPlugin");

        ArgumentCaptor<IPoorAction> actionArgumentCaptor = ArgumentCaptor.forClass(IPoorAction.class);

        verify(bootstrapItem).after("IOC");
        verify(bootstrapItem).process(actionArgumentCaptor.capture());

        verify(bootstrap).add(bootstrapItem);

        IKey createAsyncOpKey = mock(IKey.class);
        when(Keys.getOrAdd(CheckUserByEmailActor.class.getCanonicalName())).thenReturn(createAsyncOpKey);

        ArgumentCaptor<Function<Object[], Object>> targetFuncArgumentCaptor = ArgumentCaptor.forClass((Class) Function.class);

        doThrow(new RegistrationException("")).when(IOC.class);
        IOC.register(eq(createAsyncOpKey), any());

        whenNew(CreateNewInstanceStrategy.class).withArguments(targetFuncArgumentCaptor.capture())
                .thenReturn(mock(CreateNewInstanceStrategy.class));//the method which was used for constructor is importantly

        try {
            actionArgumentCaptor.getValue().execute();
        } catch (ActionExecuteException e) {

            verifyStatic();
            Keys.getOrAdd(CheckUserByEmailActor.class.getCanonicalName());

            verifyNew(CreateNewInstanceStrategy.class).withArguments(targetFuncArgumentCaptor.getValue());

            verifyStatic();
            IOC.register(eq(createAsyncOpKey), any(CreateNewInstanceStrategy.class));

            IObject arg = mock(IObject.class);

            CheckUserByEmailActor actor = mock(CheckUserByEmailActor.class);
            ActorParams actorParams = mock(ActorParams.class);
            IKey actorParamsIKey = mock(IKey.class);
            when(Keys.getOrAdd(ActorParams.class.getCanonicalName())).thenReturn(actorParamsIKey);
            when(IOC.resolve(actorParamsIKey, arg)).thenReturn(actorParams);
            whenNew(CheckUserByEmailActor.class).withArguments(actorParams).thenReturn(actor);

            assertTrue("Objects must return correct object", targetFuncArgumentCaptor.getValue().apply(new Object[]{arg}) == actor);

            verifyStatic();
            Keys.getOrAdd(ActorParams.class.getCanonicalName());

            verifyStatic();
            IOC.resolve(actorParamsIKey, arg);

            verifyNew(CheckUserByEmailActor.class).withArguments(actorParams);
            return;
        }
        assertTrue("Must throw exception", false);
    }

    @Test
    public void MustInCorrectExecuteActionWhenNewCreateInstanceThrowException() throws Exception {

        BootstrapItem bootstrapItem = mock(BootstrapItem.class);
        whenNew(BootstrapItem.class).withArguments("CheckUserByEmailActorPlugin").thenReturn(bootstrapItem);

        when(bootstrapItem.after(anyString())).thenReturn(bootstrapItem);

        plugin.load();

        verifyNew(BootstrapItem.class).withArguments("CheckUserByEmailActorPlugin");

        ArgumentCaptor<IPoorAction> actionArgumentCaptor = ArgumentCaptor.forClass(IPoorAction.class);

        verify(bootstrapItem).after("IOC");
        verify(bootstrapItem).process(actionArgumentCaptor.capture());

        verify(bootstrap).add(bootstrapItem);

        IKey createAsyncOpKey = mock(IKey.class);
        when(Keys.getOrAdd(CheckUserByEmailActor.class.getCanonicalName())).thenReturn(createAsyncOpKey);

        ArgumentCaptor<Function<Object[], Object>> targetFuncArgumentCaptor = ArgumentCaptor.forClass((Class) Function.class);

        whenNew(CreateNewInstanceStrategy.class).withArguments(targetFuncArgumentCaptor.capture())
                .thenThrow(new RegistrationException(""));//the method which was used for constructor is importantly

        try {
            actionArgumentCaptor.getValue().execute();
        } catch (ActionExecuteException e) {

            verifyStatic();
            Keys.getOrAdd(CheckUserByEmailActor.class.getCanonicalName());

            verifyNew(CreateNewInstanceStrategy.class).withArguments(targetFuncArgumentCaptor.getValue());

            IObject arg = mock(IObject.class);

            CheckUserByEmailActor actor = mock(CheckUserByEmailActor.class);
            ActorParams actorParams = mock(ActorParams.class);
            IKey actorParamsIKey = mock(IKey.class);
            when(Keys.getOrAdd(ActorParams.class.getCanonicalName())).thenReturn(actorParamsIKey);
            when(IOC.resolve(actorParamsIKey, arg)).thenReturn(actorParams);
            whenNew(CheckUserByEmailActor.class).withArguments(actorParams).thenReturn(actor);

            assertTrue("Objects must return correct object", targetFuncArgumentCaptor.getValue().apply(new Object[]{arg}) == actor);

            verifyStatic();
            Keys.getOrAdd(ActorParams.class.getCanonicalName());

            verifyStatic();
            IOC.resolve(actorParamsIKey, arg);

            verifyNew(CheckUserByEmailActor.class).withArguments(actorParams);
            return;
        }
        assertTrue("Must throw exception", false);
    }

    @Test
    public void MustInCorrectResolveWhenKeysGetOrAddActorParamKey() throws Exception {

        BootstrapItem bootstrapItem = mock(BootstrapItem.class);
        whenNew(BootstrapItem.class).withArguments("CheckUserByEmailActorPlugin").thenReturn(bootstrapItem);

        when(bootstrapItem.after(anyString())).thenReturn(bootstrapItem);

        plugin.load();

        verifyNew(BootstrapItem.class).withArguments("CheckUserByEmailActorPlugin");

        ArgumentCaptor<IPoorAction> actionArgumentCaptor = ArgumentCaptor.forClass(IPoorAction.class);

        verify(bootstrapItem).after("IOC");
        verify(bootstrapItem).process(actionArgumentCaptor.capture());

        verify(bootstrap).add(bootstrapItem);

        IKey checkUserByEmailActorKey = mock(IKey.class);
        when(Keys.getOrAdd(CheckUserByEmailActor.class.getCanonicalName())).thenReturn(checkUserByEmailActorKey);

        ArgumentCaptor<Function> targetFuncArgumentCaptor = ArgumentCaptor.forClass((Class) Function.class);

        CreateNewInstanceStrategy createNewInstanceStrategy = mock(CreateNewInstanceStrategy.class);
        whenNew(CreateNewInstanceStrategy.class)
                .withArguments(targetFuncArgumentCaptor.capture())
                .thenReturn(createNewInstanceStrategy);

        actionArgumentCaptor.getValue().execute();

        verifyStatic();
        Keys.getOrAdd(CheckUserByEmailActor.class.getCanonicalName());

        verifyStatic();
        IOC.register(checkUserByEmailActorKey, createNewInstanceStrategy);

        IObject arg = mock(IObject.class);

        when(Keys.getOrAdd(ActorParams.class.getCanonicalName())).thenThrow(new ResolutionException(""));

        try {
            targetFuncArgumentCaptor.getValue().apply(new Object[]{arg});
        } catch (RuntimeException e) {

            verifyStatic();
            Keys.getOrAdd(ActorParams.class.getCanonicalName());
            return;
        }
        assertTrue("Must throw exception", false);
    }

    @Test
    public void MustInCorrectResolveWhenIOCResolveActorParamsThrowException() throws Exception {

        BootstrapItem bootstrapItem = mock(BootstrapItem.class);
        whenNew(BootstrapItem.class).withArguments("CheckUserByEmailActorPlugin").thenReturn(bootstrapItem);

        when(bootstrapItem.after(anyString())).thenReturn(bootstrapItem);

        plugin.load();

        verifyNew(BootstrapItem.class).withArguments("CheckUserByEmailActorPlugin");

        ArgumentCaptor<IPoorAction> actionArgumentCaptor = ArgumentCaptor.forClass(IPoorAction.class);

        verify(bootstrapItem).after("IOC");
        verify(bootstrapItem).process(actionArgumentCaptor.capture());

        verify(bootstrap).add(bootstrapItem);

        IKey checkUserByEmailActorKey = mock(IKey.class);
        when(Keys.getOrAdd(CheckUserByEmailActor.class.getCanonicalName())).thenReturn(checkUserByEmailActorKey);

        ArgumentCaptor<Function> targetFuncArgumentCaptor = ArgumentCaptor.forClass((Class) Function.class);

        CreateNewInstanceStrategy createNewInstanceStrategy = mock(CreateNewInstanceStrategy.class);
        whenNew(CreateNewInstanceStrategy.class)
                .withArguments(targetFuncArgumentCaptor.capture())
                .thenReturn(createNewInstanceStrategy);

        actionArgumentCaptor.getValue().execute();

        verifyStatic();
        Keys.getOrAdd(CheckUserByEmailActor.class.getCanonicalName());

        verifyStatic();
        IOC.register(checkUserByEmailActorKey, createNewInstanceStrategy);

        IObject arg = mock(IObject.class);

        IKey actorParamsIKey = mock(IKey.class);
        when(Keys.getOrAdd(ActorParams.class.getCanonicalName())).thenReturn(actorParamsIKey);
        when(IOC.resolve(actorParamsIKey, arg)).thenThrow(new ResolutionException(""));

        try {
            targetFuncArgumentCaptor.getValue().apply(new Object[]{arg});
        } catch (RuntimeException e) {

            verifyStatic();
            Keys.getOrAdd(ActorParams.class.getCanonicalName());

            verifyStatic();
            IOC.resolve(actorParamsIKey, arg);
            return;
        }
        assertTrue("Must throw exception", false);
    }

    @Test
    public void MustInCorrectResolveWhenNewCheckUserByEmailActorThrowException() throws Exception {

        BootstrapItem bootstrapItem = mock(BootstrapItem.class);
        whenNew(BootstrapItem.class).withArguments("CheckUserByEmailActorPlugin").thenReturn(bootstrapItem);

        when(bootstrapItem.after(anyString())).thenReturn(bootstrapItem);

        plugin.load();

        verifyNew(BootstrapItem.class).withArguments("CheckUserByEmailActorPlugin");

        ArgumentCaptor<IPoorAction> actionArgumentCaptor = ArgumentCaptor.forClass(IPoorAction.class);

        verify(bootstrapItem).after("IOC");
        verify(bootstrapItem).process(actionArgumentCaptor.capture());

        verify(bootstrap).add(bootstrapItem);

        IKey checkUserByEmailActorKey = mock(IKey.class);
        when(Keys.getOrAdd(CheckUserByEmailActor.class.getCanonicalName())).thenReturn(checkUserByEmailActorKey);

        ArgumentCaptor<Function> targetFuncArgumentCaptor = ArgumentCaptor.forClass((Class) Function.class);

        CreateNewInstanceStrategy createNewInstanceStrategy = mock(CreateNewInstanceStrategy.class);
        whenNew(CreateNewInstanceStrategy.class)
                .withArguments(targetFuncArgumentCaptor.capture())
                .thenReturn(createNewInstanceStrategy);

        actionArgumentCaptor.getValue().execute();

        verifyStatic();
        Keys.getOrAdd(CheckUserByEmailActor.class.getCanonicalName());

        verifyStatic();
        IOC.register(checkUserByEmailActorKey, createNewInstanceStrategy);

        IObject arg = mock(IObject.class);

        ActorParams actorParams = mock(ActorParams.class);
        IKey actorParamsIKey = mock(IKey.class);
        when(Keys.getOrAdd(ActorParams.class.getCanonicalName())).thenReturn(actorParamsIKey);
        when(IOC.resolve(actorParamsIKey, arg)).thenReturn(actorParams);
        whenNew(CheckUserByEmailActor.class).withArguments(actorParams).thenThrow(new InvalidArgumentException(""));

        try {
            targetFuncArgumentCaptor.getValue().apply(new Object[]{arg});
        } catch (RuntimeException e) {

            verifyStatic();
            Keys.getOrAdd(ActorParams.class.getCanonicalName());

            verifyStatic();
            IOC.resolve(actorParamsIKey, arg);

            verifyNew(CheckUserByEmailActor.class).withArguments(actorParams);
            return;
        }
        assertTrue("Must throw exception", false);
    }
}