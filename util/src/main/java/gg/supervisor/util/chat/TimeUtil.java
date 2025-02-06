package gg.supervisor.util.chat;

import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TimeUtil {

    public static final long FULL_DAY_IN_SECONDS = TimeUnit.DAYS.toSeconds(1);

    public static long getLocalTimeInSeconds() {
        return LocalTime.now().toSecondOfDay();
    }

    public static long getLocalTimeInSeconds(ZoneId id) {
        return LocalTime.now(id).toSecondOfDay();
    }

    public static long getRemainingSecondsUntil(long current, long seconds) {
        return (FULL_DAY_IN_SECONDS - current + seconds) % FULL_DAY_IN_SECONDS;
    }

    public static long getRemainingSecondsUntil(long seconds) {
        return getRemainingSecondsUntil(getLocalTimeInSeconds(), seconds);
    }

    public static long getRemainingSecondsUntil(ZoneId id, long seconds) {
        return getRemainingSecondsUntil(getLocalTimeInSeconds(id), seconds);
    }

    public static String toString(long duration) {
        return toString(duration, true, List.of(TimeUnits.WEEKS));
    }

    public static String toString(long duration, boolean shortVersion) {
        return toString(duration, shortVersion, List.of(TimeUnits.WEEKS));
    }

    public static String toString(long duration, boolean shortVersion, Collection<TimeUnits> ignoreUnits) {

        StringBuilder res = new StringBuilder();

        for (TimeUnits current : TimeUnits.values()) {
            if (ignoreUnits.contains(current))
                continue;
            long temp = duration / current.getSeconds();

            if (temp > 0) {

                if (!res.isEmpty())
                    res.append(" ");

                res.append(temp).append(" ").append(current.getName()).append(temp != 1 ? "s" : "");
                if (shortVersion)
                    break;
                else
                    duration -= temp * current.getSeconds();
            }
        }

        if ("".contentEquals(res))
            return "0 seconds";
        else
            return res.toString();
    }

    public enum TimeUnits {

        YEARS(TimeUnit.DAYS.toSeconds(365), "year"),
        MONTHS(TimeUnit.DAYS.toSeconds(30), "month"),
        WEEKS(TimeUnit.DAYS.toSeconds(7), "week"),
        DAYS(TimeUnit.DAYS.toSeconds(1), "day"),
        HOURS(TimeUnit.HOURS.toSeconds(1), "hour"),
        MINUTES(TimeUnit.MINUTES.toSeconds(1), "minute"),
        SECONDS(TimeUnit.SECONDS.toSeconds(1), "second");

        private final Long seconds;
        private final String name;

        TimeUnits(Long seconds, String name) {
            this.seconds = seconds;
            this.name = name;
        }

        public Long getSeconds() {
            return seconds;
        }

        public String getName() {
            return name;
        }

    }

}
