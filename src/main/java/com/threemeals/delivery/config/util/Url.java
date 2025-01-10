package com.threemeals.delivery.config.util;


import org.springframework.util.PatternMatchUtils;

public class Url {

    private Url() {
    }

    public static final String[] WHITE_LIST = {
        "/signup", "/signup/**", "/login", "/users",
        "/oauth/**","/*.ico"
    };

    public static final String COOKIE_PATH = "/api";


    public static boolean isIncludedInWhiteList(String requestUrl) {
        return PatternMatchUtils.simpleMatch(WHITE_LIST, requestUrl);
    }

}
