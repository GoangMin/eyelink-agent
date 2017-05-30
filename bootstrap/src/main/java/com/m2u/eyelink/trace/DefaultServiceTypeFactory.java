package com.m2u.eyelink.trace;

public class DefaultServiceTypeFactory extends ServiceTypeFactory {

    DefaultServiceTypeFactory() {
    }


    @Override
    public ServiceType createServiceType(int code, String name, String desc, ServiceTypeProperty... properties) {
        return new DefaultServiceType(code, name, desc, properties);
    }

}
