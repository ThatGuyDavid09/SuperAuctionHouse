package com.highmarsorbit.superauctionhouse.util;

import org.apache.commons.lang.StringUtils;

import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DurationUtils {
    public static String formatDuration(Duration duration) {
        return formatDuration(duration, true);
    }

    public static String formatDuration(Duration duration, boolean shortenToDay) {
        if (shortenToDay) {
            if (duration.toDays() >= 1) {
                return String.format("%dd", duration.toDays());
            }

            return String.format("%dh%dm%ds", duration.toHoursPart(), duration.toMinutesPart(), duration.toSecondsPart());
        }

        return String.format("%dd%dh%dm%ds", duration.toDaysPart(), duration.toHoursPart(), duration.toMinutesPart(), duration.toSecondsPart());
    }

    public static Duration fromString(String durString) throws IllegalArgumentException {
        // Each of these match a string of digits, then optionally a space, then "d", "h", "m", or "s"
        Matcher dayMatcher = Pattern.compile("\\d+ ?d").matcher(durString);
        Matcher hourMatcher = Pattern.compile("\\d+ ?h").matcher(durString);
        Matcher minMatcher = Pattern.compile("\\d+ ?m").matcher(durString);
        Matcher secMatcher = Pattern.compile("\\d+ ?s").matcher(durString);

        long seconds = 0;

        boolean foundMatch = false;

        if (dayMatcher.find()) {
            foundMatch = true;
            int days = Integer.parseInt(StringUtils.chop(dayMatcher.group()));
            seconds += (long) days * 24 * 60 * 60;
        }

        if (hourMatcher.find()) {
            foundMatch = true;
            int hours = Integer.parseInt(StringUtils.chop(hourMatcher.group()));
            seconds += (long) hours * 60 * 60;
        }

        if (minMatcher.find()) {
            foundMatch = true;
            int mins = Integer.parseInt(StringUtils.chop(minMatcher.group()));
            seconds += (long) mins * 60;
        }

        if (secMatcher.find()) {
            foundMatch = true;
            int secs = Integer.parseInt(StringUtils.chop(secMatcher.group()));
            seconds += (long) secs;
        }

        if (!foundMatch) {
            // If could not find days, minutes, hours, or seconds, is an illegal format
            throw new IllegalArgumentException(String.format("Illegal duration format \"%s\"", durString));
        }

        return Duration.ofSeconds(seconds);
    }
}
