package com.m2u.eyelink.agent;

import org.junit.Assert;
import org.junit.Test;

public class LoadStateTest {
    @Test
    public void testStart() throws Exception {
        LoadState loadState = new LoadState();

        Assert.assertTrue(loadState.start());
        Assert.assertTrue(loadState.getState());

        Assert.assertFalse(loadState.start());

    }
}
