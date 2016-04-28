package info.smart_tools.smartactors.core.ioc;

import info.smart_tools.smartactors.core.iresolve_dependency_strategy.IResolveDependencyStrategy;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


/**
 * Tests for IOC
 */
public class IocTest {

    @Test
    public void checkResolution()
            throws Exception {
        Integer value = 1;
        Object[] args = new Object[]{};
        IContainer container = mock(IContainer.class);
        IKey<Integer> key = mock(IKey.class);
        when(container.resolve(key, args)).thenReturn(value);

        Field field = IOC.class.getDeclaredField("container");
        field.setAccessible(true);
        field.set(null, container);
        field.setAccessible(false);

        Integer result = IOC.resolve(key, args);
        verify(container, times(1)).resolve(key, args);
        assertEquals(result, value);
    }

    @Test
    public void checkRegistration()
            throws Exception {
        final Map<IKey, IResolveDependencyStrategy> testMap = new HashMap<IKey, IResolveDependencyStrategy>();
        IResolveDependencyStrategy strategy = mock(IResolveDependencyStrategy.class);
        IContainer container = mock(IContainer.class);
        IKey<Integer> key = mock(IKey.class);
        doAnswer(new Answer() {
            private Map<IKey, IResolveDependencyStrategy> map = new HashMap<IKey, IResolveDependencyStrategy>();
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                IKey key = (IKey)invocationOnMock.getArguments()[0];
                IResolveDependencyStrategy strategy = (IResolveDependencyStrategy)invocationOnMock.getArguments()[1];
                testMap.put(key, strategy);
                return null;
            }
        }).when(container).register(key, strategy);

        Field field = IOC.class.getDeclaredField("container");
        field.setAccessible(true);
        field.set(null, container);
        field.setAccessible(false);

        IOC.register(key, strategy);
        verify(container, times(1)).register(key, strategy);
        assertEquals(testMap.get(key), strategy);
    }
}
