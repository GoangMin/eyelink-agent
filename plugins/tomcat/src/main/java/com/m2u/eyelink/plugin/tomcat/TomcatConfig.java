package com.m2u.eyelink.plugin.tomcat;

import java.util.List;

import com.m2u.eyelink.config.ExcludeMethodFilter;
import com.m2u.eyelink.config.ExcludePathFilter;
import com.m2u.eyelink.config.Filter;
import com.m2u.eyelink.config.ProfilerConfig;
import com.m2u.eyelink.config.SkipFilter;

public class TomcatConfig {
    private final boolean tomcatEnable;
    private final List<String> tomcatBootstrapMains;
    private final boolean tomcatConditionalTransformEnable;
    private final boolean tomcatHidePinpointHeader;

    private final boolean tomcatTraceRequestParam;
    private final Filter<String> tomcatExcludeUrlFilter;
    private final String tomcatRealIpHeader;
    private final String tomcatRealIpEmptyValue;
    private final Filter<String> tomcatExcludeProfileMethodFilter;

    // for transform conditional check
    private final List<String> springBootBootstrapMains;

    public TomcatConfig(ProfilerConfig config) {
        if (config == null) {
            throw new NullPointerException("config must not be null");
        }

        // plugin
        this.tomcatEnable = config.readBoolean("profiler.tomcat.enable", true);
        this.tomcatBootstrapMains = config.readList("profiler.tomcat.bootstrap.main");
        this.tomcatConditionalTransformEnable = config.readBoolean("profiler.tomcat.conditional.transform", true);
        this.tomcatHidePinpointHeader = config.readBoolean("profiler.tomcat.hidepinpointheader", true);

        // runtime
        this.tomcatTraceRequestParam = config.readBoolean("profiler.tomcat.tracerequestparam", true);
        final String tomcatExcludeURL = config.readString("profiler.tomcat.excludeurl", "");
        if (!tomcatExcludeURL.isEmpty()) {
            this.tomcatExcludeUrlFilter = new ExcludePathFilter(tomcatExcludeURL);
        } else {
            this.tomcatExcludeUrlFilter = new SkipFilter<String>();
        }
        this.tomcatRealIpHeader = config.readString("profiler.tomcat.realipheader", null);
        this.tomcatRealIpEmptyValue = config.readString("profiler.tomcat.realipemptyvalue", null);

        final String tomcatExcludeProfileMethod = config.readString("profiler.tomcat.excludemethod", "");
        if (!tomcatExcludeProfileMethod.isEmpty()) {
            this.tomcatExcludeProfileMethodFilter = new ExcludeMethodFilter(tomcatExcludeProfileMethod);
        } else {
            this.tomcatExcludeProfileMethodFilter = new SkipFilter<String>();
        }

        this.springBootBootstrapMains = config.readList("profiler.springboot.bootstrap.main");
    }

    public boolean isTomcatEnable() {
        return tomcatEnable;
    }

    public List<String> getTomcatBootstrapMains() {
        return tomcatBootstrapMains;
    }

    public boolean isTomcatConditionalTransformEnable() {
        return tomcatConditionalTransformEnable;
    }

    public boolean isTomcatHidePinpointHeader() {
        return tomcatHidePinpointHeader;
    }

    public boolean isTomcatTraceRequestParam() {
        return tomcatTraceRequestParam;
    }

    public Filter<String> getTomcatExcludeUrlFilter() {
        return tomcatExcludeUrlFilter;
    }

    public String getTomcatRealIpHeader() {
        return tomcatRealIpHeader;
    }

    public String getTomcatRealIpEmptyValue() {
        return tomcatRealIpEmptyValue;
    }

    public Filter<String> getTomcatExcludeProfileMethodFilter() {
        return tomcatExcludeProfileMethodFilter;
    }

    public List<String> getSpringBootBootstrapMains() {
        return springBootBootstrapMains;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TomcatConfig{");
        sb.append("tomcatEnable=").append(tomcatEnable);
        sb.append(", tomcatBootstrapMains=").append(tomcatBootstrapMains);
        sb.append(", tomcatConditionalTransformEnable=").append(tomcatConditionalTransformEnable);
        sb.append(", tomcatHidePinpointHeader=").append(tomcatHidePinpointHeader);
        sb.append(", tomcatTraceRequestParam=").append(tomcatTraceRequestParam);
        sb.append(", tomcatExcludeUrlFilter=").append(tomcatExcludeUrlFilter);
        sb.append(", tomcatRealIpHeader='").append(tomcatRealIpHeader).append('\'');
        sb.append(", tomcatRealIpEmptyValue='").append(tomcatRealIpEmptyValue).append('\'');
        sb.append(", tomcatExcludeProfileMethodFilter=").append(tomcatExcludeProfileMethodFilter);
        sb.append('}');
        return sb.toString();
    }
}
