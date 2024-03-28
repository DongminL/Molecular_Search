package com.example.molecularsearch.jwt;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.security.Key;

@Component
public class JwtProvider implements InitializingBean {

    private Key key;

    @Override
    public void afterPropertiesSet() throws Exception {

    }
}
