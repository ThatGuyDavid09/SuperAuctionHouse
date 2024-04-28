package com.highmarsorbit.superauctionhouse.config;

import redempt.redlib.config.annotations.Comment;
import redempt.redlib.config.annotations.ConfigPath;

public class Config {
    public static boolean log_fine = true;

    public static String fee_eqn_readme =
        "The equation to calculate the auction fee. Use placeholders like d, h, m, s, for total days, hours, etc" +
        "in the auction duration, and p for initial price. Supports most math operations and functions." +
        "Parentheses are also allowed. Rounded to nearest cent.";
    public static String fee_equation = "p * 0.06 + h";
}
