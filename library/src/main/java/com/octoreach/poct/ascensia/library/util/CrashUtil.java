package com.octoreach.poct.ascensia.library.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by dick.wang on 2017/7/5.
 */
public class CrashUtil implements Thread.UncaughtExceptionHandler {

    private static Logger _log = LoggerFactory.getLogger(CrashUtil.class);

    private static volatile CrashUtil instance;
    private Thread.UncaughtExceptionHandler defalutHandler;

    public static CrashUtil getInstance() {
        if (instance == null) {
            synchronized (CrashUtil.class) {
                if (instance == null) {
                    instance = new CrashUtil();
                }
            }
        }
        return instance;
    }

    public CrashUtil() {
        // 获取系统默认的UncaughtException处理器
        defalutHandler = Thread.getDefaultUncaughtExceptionHandler();
        // 设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        _log.error(ex.getLocalizedMessage(), ex);
        defalutHandler.uncaughtException(thread, ex);
    }
}
