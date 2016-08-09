package info.smart_tools.smartactors.plugin.get_header_from_request_rule;

import info.smart_tools.smartactors.core.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.core.create_new_instance_strategy.CreateNewInstanceStrategy;
import info.smart_tools.smartactors.core.iaction.IPoorAction;
import info.smart_tools.smartactors.core.iaction.exception.ActionExecuteException;
import info.smart_tools.smartactors.core.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.ikey.IKey;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.iplugin.exception.PluginException;
import info.smart_tools.smartactors.core.iresolve_dependency_strategy.IResolveDependencyStrategy;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;
import info.smart_tools.smartactors.transformation_rules.get_header_from_request.GetHeaderFromRequestRule;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.function.Function;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doThrow;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyNew;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@PrepareForTest({IOC.class, Keys.class, GetHeaderFromRequestRulePlugin.class, CreateNewInstanceStrategy.class})
@RunWith(PowerMockRunner.class)
public class GetHeaderFromRequestRulePluginTest {
    private GetHeaderFromRequestRulePlugin plugin;
    private IBootstrap bootstrap;

    @Before
    public void setUp() throws Exception {

        mockStatic(IOC.class);
        mockStatic(Keys.class);

        bootstrap = mock(IBootstrap.class);
        plugin = new GetHeaderFromRequestRulePlugin(bootstrap);
    }

    @Test
    public void MustCorrectLoadPlugin() throws Exception {

        BootstrapItem bootstrapItem = mock(BootstrapItem.class);
        whenNew(BootstrapItem.class).withArguments("GetHeaderFromRequestRulePlugin").thenReturn(bootstrapItem);

        when(bootstrapItem.after(anyString())).thenReturn(bootstrapItem);
        when(bootstrapItem.before(anyString())).thenReturn(bootstrapItem);

        plugin.load();

        verifyNew(BootstrapItem.class).withArguments("GetHeaderFromRequestRulePlugin");

        ArgumentCaptor<IPoorAction> actionArgumentCaptor = ArgumentCaptor.forClass(IPoorAction.class);

        verify(bootstrapItem).after("IOC");
        verify(bootstrapItem).process(actionArgumentCaptor.capture());

        verify(bootstrap).add(bootstrapItem);

        IKey ruleKey = mock(IKey.class);
        when(Keys.getOrAdd(IResolveDependencyStrategy.class.getCanonicalName())).thenReturn(ruleKey);

        actionArgumentCaptor.getValue().execute();

        verifyStatic();
        Keys.getOrAdd(IResolveDependencyStrategy.class.getCanonicalName());

        verifyStatic();
        IOC.resolve(eq(ruleKey), eq("getHeaderFromRequestRule"), any(GetHeaderFromRequestRule.class));
    }

    @Test
    public void MustInCorrectLoadNewIBootstrapItemThrowException() throws Exception {

        whenNew(BootstrapItem.class).withArguments("GetHeaderFromRequestRulePlugin").thenThrow(new InvalidArgumentException(""));

        try {
            plugin.load();
        } catch (PluginException e) {

            verifyNew(BootstrapItem.class).withArguments("GetHeaderFromRequestRulePlugin");
            return;
        }
        assertTrue("Must throw exception, but was not", false);
    }

    @Test
    public void MustInCorrectExecuteActionWhenIOCRegisterThrowException() throws Exception {

        BootstrapItem bootstrapItem = mock(BootstrapItem.class);
        whenNew(BootstrapItem.class).withArguments("GetHeaderFromRequestRulePlugin").thenReturn(bootstrapItem);

        when(bootstrapItem.after(anyString())).thenReturn(bootstrapItem);
        when(bootstrapItem.before(anyString())).thenReturn(bootstrapItem);

        plugin.load();

        verifyNew(BootstrapItem.class).withArguments("GetHeaderFromRequestRulePlugin");

        ArgumentCaptor<IPoorAction> actionArgumentCaptor = ArgumentCaptor.forClass(IPoorAction.class);

        verify(bootstrapItem).after("IOC");
        verify(bootstrapItem).process(actionArgumentCaptor.capture());

        verify(bootstrap).add(bootstrapItem);

        IKey ruleKey = mock(IKey.class);
        when(Keys.getOrAdd(IResolveDependencyStrategy.class.getCanonicalName())).thenReturn(ruleKey);

        ArgumentCaptor<Function<Object[], Object>> targetFuncArgumentCaptor = ArgumentCaptor.forClass((Class) Function.class);

        doThrow(new ResolutionException("")).when(IOC.class);
        IOC.resolve(eq(ruleKey), eq("getHeaderFromRequestRule"), any(GetHeaderFromRequestRule.class));

        whenNew(CreateNewInstanceStrategy.class).withArguments(targetFuncArgumentCaptor.capture())
                .thenReturn(mock(CreateNewInstanceStrategy.class));//the method which was used for constructor is importantly

        try {
            actionArgumentCaptor.getValue().execute();
        } catch (ActionExecuteException e) {

            verifyStatic();
            Keys.getOrAdd(IResolveDependencyStrategy.class.getCanonicalName());

            verifyStatic();
            IOC.resolve(eq(ruleKey), eq("getHeaderFromRequestRule"), any(GetHeaderFromRequestRule.class));

            return;
        }
        assertTrue("Must throw exception", false);
    }
}
