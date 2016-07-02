package com.legend.netty.quickstart.common;

import java.util.regex.Pattern;

/**
 * Created by allen on 6/30/16.
 */
public interface Constants {
    public static final String CORRECT_QUERY_TIME_ORDER = "QUERY TIME ORDER";
    public static final String BAD_QUERY_TIME_ORDER = "BAD ORDER";

    public static final String DOLLAR_DELIMITER = "$_";

    public static final String ACCEPTED_USER_NAME = "Allen";

    public static final Pattern INSECURE_URI = Pattern.compile(".*[<>&\"].*");
    public static final Pattern ALLOWED_FILE_NAME = Pattern.compile("[A-Za-z0-9][-_A-Za-z0-9\\.]*");
}
