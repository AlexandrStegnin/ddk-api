package com.ddkolesnik.ddkapi.util;

/**
 * @author Alexandr Stegnin
 */

public class Constant {

    // Security
    public static final String AUTHORITIES_KEY = "authorities";

    public static final String API_INFO_URL = "/info";

    public static final String BASE_URL = "/";

    public static final String ROLE_PREFIX = "ROLE_";

    // PATHS

    public static final String VERSION = "v1";

    public static final String PATH_MONIES = VERSION + "/monies";

    // MESSAGES
    public static final String UNKNOWN_FACILITY = "Неизвестный объект";

    public static final String UNKNOWN_INVESTOR = "Неизвестный инвестор";

    public static final String[] ALL_HTTP_MATCHERS = {
            "/VAADIN/**", "/HEARTBEAT/**", "/UIDL/**", "/resources/**",
            "/manifest.json", "/icons/**", "/images/**",
            // (development mode) static resources
            "/frontend/**",
            // (development mode) webjars
            "/webjars/**",
            // (development mode) H2 debugging console
            "/h2-console/**",
            // (production mode) static resources
            "/frontend-es5/**", "/frontend-es6/**"
    };

    public static final String[] ALL_SWAGGER_MATCHERS = {"/v3/api-docs*", "/configuration/**", "/swagger*/**", "/webjars/**", "/", "/info", "/api-info.html"};

}
