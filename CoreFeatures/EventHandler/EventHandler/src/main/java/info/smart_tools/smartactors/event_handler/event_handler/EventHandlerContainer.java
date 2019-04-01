package info.smart_tools.smartactors.event_handler.event_handler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * The default implementation of {@link EventHandlerContainer} which store instances of {@link IEventHandler} in the {@link LinkedList}.
 * Also, this container implements a sequential call of registered instances of {@link IEventHandler} from latest to early.
 * The sequential call will be stopped after the first successful execution of the next handler.
 * This container expanded by interface {@link IExtendedEventHandlerContainer} that provides to add custom implementations of {@link IEventHandler}
 * and remove handler by its key.
 */
public class EventHandlerContainer implements IEventHandlerContainer, IExtendedEventHandlerContainer {

    private LinkedList<IEventHandler> handlers = new LinkedList<IEventHandler>(
            Arrays.asList(
                    new PrintToFileEventHandler("fileLogger"),
                    new PrintToConsoleEventHandler("consoleLogger")
            )
    );

    @Override
    public void handle(final IEvent event) {
        List<IEvent> events = new ArrayList<>();
        events.add(event);
        for (IEventHandler handler : handlers) {
            try {
                for (IEvent e: events) {
                    handler.handle(e);
                }
                break;
            } catch (Exception e) {
                events = new ArrayList<>();
                events.add(event);
                events.add(
                    Event
                        .builder()
                        .message(
                            String.format(
                                "Exception on executing 'handle' method of '%s' event handler.",
                                    handler.getEventHandlerKey()
                            )
                        )
                        .body(e)
                        .build()
                );
            }
        }
    }

    @Override
    public void register(final IEventHandler eventHandler) {
        if (eventHandler != null) {
            handlers.push(eventHandler);
        }
    }

    @Override
    public IEventHandler unregister(final String eventHandlerKey) {
        if (null != eventHandlerKey) {
            handlers.removeIf((IEventHandler handler) -> handler.getEventHandlerKey().equals(eventHandlerKey));
        } else {
            if (handlers.size() > 0) {
                return handlers.pollFirst();
            }
        }

        return null;
    }
}