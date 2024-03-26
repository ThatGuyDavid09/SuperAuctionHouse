package com.thatguydavid.superauctionhouse.util;

import java.time.Duration;

public class DurationUtils {
    public static String formatDuration(Duration duration) {
        if (duration.toDays() > 1) {
            return String.format("%dd", duration.toDays());
        }

        return String.format("%dh%dm%ds", duration.toHoursPart(), duration.toMinutesPart(), duration.toSecondsPart());
    }
}
