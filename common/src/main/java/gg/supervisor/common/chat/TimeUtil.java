package gg.supervisor.common.chat;

import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TimeUtil {

    public static final int FULL_DAY_IN_SECONDS = 86400;
    private static final List<String> timesString = Arrays.asList("year", "month", "day", "hour", "minute", "second");
    private static final List<Long> times = Arrays.asList(
            TimeUnit.DAYS.toMillis(365),
            TimeUnit.DAYS.toMillis(30),
            TimeUnit.DAYS.toMillis(1),
            TimeUnit.HOURS.toMillis(1),
            TimeUnit.MINUTES.toMillis(1),
            TimeUnit.SECONDS.toMillis(1));
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("A");

    public static int getLocalTimeInSeconds() {
        return Math.floorDiv(Integer.parseInt(LocalTime.now().format(formatter)), 1000);
    }

    public static int getLocalTimeInSeconds(ZoneId id) {
        return Math.floorDiv(Integer.parseInt(LocalTime.now(id).format(formatter)), 1000);
    }

    public static int getRemainingSecondsUntil(int current, int seconds) {
        return (FULL_DAY_IN_SECONDS - current + seconds) % FULL_DAY_IN_SECONDS;
    }

    public static int getRemainingSecondsUntil(int seconds) {
        return (FULL_DAY_IN_SECONDS - getLocalTimeInSeconds() + seconds) % FULL_DAY_IN_SECONDS;
    }

    public static int getRemainingSecondsUntil(ZoneId id, int seconds) {
        return (FULL_DAY_IN_SECONDS - getLocalTimeInSeconds(id) + seconds) % FULL_DAY_IN_SECONDS;
    }

    public static String toString(long duration) {
        return toString(duration, true);
    }

    public static String toString(long duration, boolean shortVersion) {

        StringBuilder res = new StringBuilder();

        duration = duration * 1000;

        for (int i = 0; i < TimeUtil.times.size(); i++) {

            Long current = TimeUtil.times.get(i);

            long temp = duration / current;

            if (temp > 0) {

                if (!res.isEmpty())
                    res.append(" ");

                res.append(temp).append(" ").append(TimeUtil.timesString.get(i)).append(temp != 1 ? "s" : "");
                if (shortVersion)
                    break;
                else
                    duration -= temp * current;
            }
        }


        if ("".contentEquals(res))
            return "0 seconds";
        else
            return res.toString();
    }

}
