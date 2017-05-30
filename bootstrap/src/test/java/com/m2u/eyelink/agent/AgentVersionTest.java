package com.m2u.eyelink.agent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Test;

public class AgentVersionTest {

    private final Pattern versionPattern = Pattern.compile(AgentDirBaseClassPathResolver.VERSION_PATTERN);

    @Test
    public void testVersion() {

        assertVersion("-1.6.0");
        assertVersion("-2.1.0");
        assertVersion("-20.10.99");

        assertVersion("-1.6.0-SNAPSHOT");

        assertVersion("-1.6.0-RC1");
        assertVersion("-1.6.0-RC0");
        assertVersion("-1.6.0-RC11");


    }

    @Test
    public void testVersion_fail() {

        assertFalseVersion("-1.6.0-RC");
        assertFalseVersion("-2.1.0-SNAPSHOT-RC1");
    }

    private void assertVersion(String versionString) {
        Matcher matcher = this.versionPattern.matcher(versionString);
        Assert.assertTrue(matcher.matches());
    }

    private void assertFalseVersion(String versionString) {
        Matcher matcher = this.versionPattern.matcher(versionString);
        Assert.assertFalse(matcher.matches());
    }

}
