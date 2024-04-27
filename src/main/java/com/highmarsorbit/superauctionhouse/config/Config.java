package com.highmarsorbit.superauctionhouse.config;

import redempt.redlib.config.annotations.Comment;
import redempt.redlib.config.annotations.ConfigPath;

public class Config {
    public static boolean log_fine = true;

    @Comment("The equation to calculate the auction fee. Use placeholders like d, h, m, s, for total days, hours, etc")
    @Comment("in the auction time, and p for price. Supports most math operations and functions.")
    @Comment("Parentheses are also allowed. Rounded to nearest cent.")
    public static String fee_equation = "p * 0.06 + h";
}
