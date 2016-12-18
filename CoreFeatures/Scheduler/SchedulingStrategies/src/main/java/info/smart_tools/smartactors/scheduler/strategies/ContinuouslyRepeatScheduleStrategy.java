package info.smart_tools.smartactors.scheduler.strategies;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.scheduler.interfaces.ISchedulerEntry;
import info.smart_tools.smartactors.scheduler.interfaces.ISchedulingStrategy;
import info.smart_tools.smartactors.scheduler.interfaces.exceptions.EntryScheduleException;
import info.smart_tools.smartactors.scheduler.interfaces.exceptions.EntryStorageAccessException;
import info.smart_tools.smartactors.scheduler.interfaces.exceptions.SchedulingStrategyExecutionException;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAmount;


/**
 * {@link ISchedulingStrategy Scheduling strategy} that schedules entry with some fixed interval.
 *
 * <p>
 * Expected configuration arguments are:
 * </p>
 *
 * <ul>
 *     <li>{@code "start"} - date and time (ISO 8601) when the entry should/could be executed first time</li>
 *     <li>{@code "interval"} - interval in ISO-8601 format (e.g. {@code "PT8H10M42.36S"})</li>
 * </ul>
 */
public class ContinuouslyRepeatScheduleStrategy implements ISchedulingStrategy {
    private final IFieldName startFieldName;
    private final IFieldName intervalFieldName;

    private long nextTime(final LocalDateTime startTime, final TemporalAmount period, final long now) {
        long lStartTime = datetimeToMillis(startTime);

        if (lStartTime >= now) {
            return lStartTime;
        }

        if (period instanceof Duration) {
            long lPeriod = ((Duration) period).toMillis();
            long lNextTime = lStartTime + lPeriod * ((now - lStartTime) / lPeriod);
            return (lNextTime >= now) ? lNextTime : (lNextTime + lPeriod);
        }

        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plus(period);
        long lPeriod = datetimeToMillis(end) - datetimeToMillis(start);
        long lNextTime = lStartTime + lPeriod * ((now - lStartTime) / lPeriod);
        return (lNextTime >= now) ? lNextTime : (lNextTime + lPeriod);
    }

    private long datetimeToMillis(final LocalDateTime localDateTime) {
        return localDateTime.atZone(ZoneOffset.UTC).toInstant().toEpochMilli();
    }

    /**
     * The constructor.
     *
     * @throws ResolutionException if fails resolving dependencies
     */
    public ContinuouslyRepeatScheduleStrategy()
            throws ResolutionException {
        startFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "start");
        intervalFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "interval");
    }

    @Override
    public void init(final ISchedulerEntry entry, final IObject args) throws SchedulingStrategyExecutionException {
        try {
            String start = (String) args.getValue(startFieldName);
            LocalDateTime startTime;
            TemporalAmount interval = parseInterval((String) args.getValue(intervalFieldName));
            LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);

            if (start == null) {
                startTime = now;
            } else {
                startTime = LocalDateTime.parse(start);
            }

            entry.getState().setValue(startFieldName, startTime.toString());
            entry.getState().setValue(intervalFieldName, interval.toString());

            entry.save();

            entry.scheduleNext(nextTime(startTime, interval, datetimeToMillis(now)));
        } catch (ReadValueException | InvalidArgumentException | EntryScheduleException | EntryStorageAccessException
                | ChangeValueException e) {
            throw new SchedulingStrategyExecutionException("Error occurred initializing scheduler entry.", e);
        }
    }

    @Override
    public void postProcess(final ISchedulerEntry entry) throws SchedulingStrategyExecutionException {
        try {
            TemporalAmount interval = parseInterval((String) entry.getState().getValue(intervalFieldName));
            if (interval instanceof Duration) {
                entry.scheduleNext(entry.getLastTime() + ((Duration) interval).toMillis());
            }
            if (interval instanceof Period) {
                LocalDateTime start = LocalDateTime.now();
                LocalDateTime end = start.plus(interval);
                long millis = datetimeToMillis(end) - datetimeToMillis(start);
                entry.scheduleNext(entry.getLastTime() + millis);
            }
        } catch (ReadValueException | InvalidArgumentException | EntryScheduleException e) {
            throw new SchedulingStrategyExecutionException("Error occurred rescheduling scheduler entry.", e);
        }
    }

    @Override
    public void restore(final ISchedulerEntry entry) throws SchedulingStrategyExecutionException {
        try {
            TemporalAmount interval = parseInterval((String) entry.getState().getValue(intervalFieldName));
            LocalDateTime startTime = LocalDateTime.parse((String) entry.getState().getValue(startFieldName));

            long nextTime = nextTime(startTime, interval, System.currentTimeMillis());

            entry.scheduleNext(nextTime);
        } catch (ReadValueException | InvalidArgumentException | EntryScheduleException e) {
            throw new SchedulingStrategyExecutionException("Error occurred restoring scheduler entry.", e);
        }
    }

    @Override
    public void processException(final ISchedulerEntry entry, final Throwable e) throws SchedulingStrategyExecutionException {
        try {
            entry.cancel();
        } catch (EntryStorageAccessException | EntryScheduleException ee) {
            throw new SchedulingStrategyExecutionException("Error occurred cancelling failed scheduler entry.", ee);
        }
    }

    private TemporalAmount parseInterval(final String intervalString) {
        TemporalAmount interval;
        try {
            interval = Duration.parse(intervalString);
        } catch (DateTimeParseException e) {
            interval = Period.parse(intervalString);
        }
        return interval;
    }
}
