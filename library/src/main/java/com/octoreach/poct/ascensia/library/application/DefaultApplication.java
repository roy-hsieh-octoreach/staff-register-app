package com.octoreach.poct.ascensia.library.application;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.support.multidex.MultiDexApplication;

import com.octoreach.poct.ascensia.library.util.CrashUtil;

import org.slf4j.LoggerFactory;

import java.io.File;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.android.LogcatAppender;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.filter.ThresholdFilter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;

public class DefaultApplication extends MultiDexApplication {

    private static org.slf4j.Logger _log = LoggerFactory.getLogger(DefaultApplication.class);

    @Override
    public void onCreate() {
        super.onCreate();

        configureLogbackDirectly();
        CrashUtil.getInstance();

        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                _log.trace("onActivityCreated : " + activity.getLocalClassName());
            }

            @Override
            public void onActivityStarted(Activity activity) {
                _log.trace("onActivityStarted : " + activity.getLocalClassName());

            }

            @Override
            public void onActivityResumed(Activity activity) {
                _log.trace("onActivityResumed : " + activity.getLocalClassName());

            }

            @Override
            public void onActivityPaused(Activity activity) {
                _log.trace("onActivityPaused : " + activity.getLocalClassName());


            }

            @Override
            public void onActivityStopped(Activity activity) {
                _log.trace("onActivityStopped : " + activity.getLocalClassName());

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
                _log.trace("onActivitySaveInstanceState : " + activity.getLocalClassName());

            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                _log.trace("onActivityDestroyed : " + activity.getLocalClassName());
            }
        });
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        lc.stop();
    }

    private void configureLogbackDirectly() {
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        lc.reset();

//        final String LOG_DIR = getExternalFilesDir(null).getAbsolutePath();
        final String LOG_DIR = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
        final String LOG_File = "domain.log"; // change to your log file name
        final String LOG_ROLLING_FILE = "domain.%d.log"; // change to your log file name

        RollingFileAppender<ILoggingEvent> rollingFileAppender = new RollingFileAppender<>();
        rollingFileAppender.setAppend(true);
        rollingFileAppender.setContext(lc);
        rollingFileAppender.setFile(LOG_DIR + File.separator + LOG_File);

        TimeBasedRollingPolicy<ILoggingEvent> rollingPolicy = new TimeBasedRollingPolicy<>();
        rollingPolicy.setFileNamePattern(LOG_DIR + File.separator + LOG_ROLLING_FILE);
        rollingPolicy.setMaxHistory(7);
        rollingPolicy.setParent(rollingFileAppender);
        rollingPolicy.setContext(lc);

        SizeAndTimeBasedFNATP<ILoggingEvent> sizeAndTimeBasedFNATP = new SizeAndTimeBasedFNATP<>();
        sizeAndTimeBasedFNATP.setMaxFileSize("5MB");
        sizeAndTimeBasedFNATP.setContext(lc);
        sizeAndTimeBasedFNATP.setTimeBasedRollingPolicy(rollingPolicy);

        rollingPolicy.setTimeBasedFileNamingAndTriggeringPolicy(sizeAndTimeBasedFNATP);
        rollingPolicy.start();
        sizeAndTimeBasedFNATP.start();
        rollingFileAppender.setRollingPolicy(rollingPolicy);

        PatternLayoutEncoder rollingEncoder = new PatternLayoutEncoder();
        rollingEncoder.setPattern("%d{yyyy-MM-dd HH:mm:ss} - [%-5level][%file:%M:%line] - %msg%n");
        rollingEncoder.setContext(lc);
        rollingEncoder.start();

        ThresholdFilter filter = new ThresholdFilter();
        filter.setLevel(Level.INFO.toString());
        filter.start();

        rollingFileAppender.addFilter(filter);
        rollingFileAppender.setEncoder(rollingEncoder);
        rollingFileAppender.start();

        // logcat 看訊息用
        PatternLayoutEncoder logcatEncoder = new PatternLayoutEncoder();
        logcatEncoder.setPattern("[%thread][%-5level][:%M:%line] - %msg%n");
        logcatEncoder.setContext(lc);
        logcatEncoder.start();

        LogcatAppender logcatAppender = new LogcatAppender();
        logcatAppender.setEncoder(logcatEncoder);
        logcatAppender.start();

        ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.TRACE);
        root.addAppender(logcatAppender);
//        root.addAppender(rollingFileAppender);
    }
}

