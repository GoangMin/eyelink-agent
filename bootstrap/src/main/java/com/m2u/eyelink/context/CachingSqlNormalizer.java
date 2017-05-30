package com.m2u.eyelink.context;


public interface CachingSqlNormalizer {
    ParsingResult wrapSql(String sql);

    boolean normalizedSql(ParsingResult sql);
}
