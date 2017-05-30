package com.m2u.eyelink.context;


public interface ParsingResultInternal extends ParsingResult {


    String getOriginalSql();

    boolean setId(int id);

    void setSql(String sql);

    void setOutput(String output);

}
