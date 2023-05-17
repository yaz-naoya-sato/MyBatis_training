package com.example.mybatis.common;

import java.util.logging.Logger;

public class LogUtil {

    private static final Logger log = getLogger();

    public static Logger getLogger() {
        return Logger.getLogger(new Throwable().getStackTrace()[1].getClassName());
    }
}
