package com.zizibujuan.updatemanager.test;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.MediumTest;

import com.zizibujuan.updatemanager.MainActivity;
import com.zizibujuan.updatemanager.VersionInfo;

import junit.runner.Version;

/**
 * Created by jzw on 14-3-7.
 */
public class HelloWorldTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private final static String TAG = HelloWorldTest.class.getName();

    private MainActivity mainActivity;

    public HelloWorldTest() {
        super(MainActivity.class);
    }

    public HelloWorldTest(Class<MainActivity> activityClass) {
        super(activityClass);
    }
    @MediumTest
    public void testGetServerVersion() throws Throwable {
        mainActivity = getActivity();
//        VersionInfo versionInfo = mainActivity.getServerVersion();
//        assertEquals(1, versionInfo.getVersionCode());
//        assertEquals("1.0.0", versionInfo.getVersionName());
//        assertEquals("app", versionInfo.getAppName());
//        assertEquals("apk", versionInfo.getApkName());
    }
}
