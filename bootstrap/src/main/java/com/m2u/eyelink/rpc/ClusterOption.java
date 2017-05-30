package com.m2u.eyelink.rpc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.m2u.eyelink.sender.Role;

public class ClusterOption {
    public static final ClusterOption DISABLE_CLUSTER_OPTION = new ClusterOption(false, "", Collections.EMPTY_LIST);

    private final boolean enable;
    private final String id;
    private final List<Role> roles;

    public ClusterOption(boolean enable, String id, String role) {
        this(enable, id, Role.getValue(role));
    }

    public ClusterOption(boolean enable, String id, Role role) {
        this(enable, id, Arrays.asList(role));
    }

    public ClusterOption(boolean enable, String id, List<Role> roles) {
        this.enable = enable;
        this.id = id;
        this.roles = roles;
    }

    public boolean isEnable() {
        return enable;
    }

    public String getId() {
        return id;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public Map<String, Object> getProperties() {
        if (!enable) {
            return Collections.emptyMap();
        }

        Map<String, Object> clusterProperties = new HashMap<String, Object>(2);
        clusterProperties.put("id", id);

        List<String> roleList = new ArrayList<String>(roles.size());
        for (Role role : roles) {
            roleList.add(role.name());
        }
        clusterProperties.put("roles", roleList);

        return clusterProperties;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ClusterOption{");
        sb.append("enable=").append(enable);
        sb.append(", id='").append(id).append('\'');
        sb.append(", roles=").append(roles);
        sb.append('}');
        return sb.toString();
    }
}
