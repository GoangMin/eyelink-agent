package com.m2u.eyelink.context;

public interface ParsingResult {
    int ID_NOT_EXIST = 0;

    String getSql();

    String getOutput();

    int getId();
}
