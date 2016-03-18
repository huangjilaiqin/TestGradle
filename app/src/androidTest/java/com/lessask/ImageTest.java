package com.lessask;

import android.test.InstrumentationTestCase;

/**
 * Created by huangji on 2016/3/18.
 */
public class ImageTest extends InstrumentationTestCase {
    public void test() throws Exception {
        final int expected = 1;
        final int reality = 5;
        assertEquals(expected, reality);
    }
}
