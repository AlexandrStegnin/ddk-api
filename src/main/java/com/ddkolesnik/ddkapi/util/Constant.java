package com.ddkolesnik.ddkapi.util;

/**
 * @author Alexandr Stegnin
 */

public class Constant {

    public static final String CREATOR_1C = "API 1C";

    // Security
    public static final String AUTHORITIES_KEY = "authorities";

    public static final String API_INFO_URL = "/info";

    public static final String BASE_URL = "/";

    public static final String ROLE_PREFIX = "ROLE_";

    public static final String INVALID_APP_TOKEN = "Неверный ключ приложения.";

    // PATHS

    public static final String KEY_PATH = "/{token}";

    public static final String VERSION = "v1";

    // MONEY PATHS
    public static final String PATH_MONIES = BASE_URL + VERSION + KEY_PATH + "/monies";

    public static final String PATH_INVESTOR_CASH = BASE_URL + VERSION + KEY_PATH + "/cash";

    public static final String PATH_INVESTOR_CASH_UPDATE = "/update";

    // MESSAGES
    public static final String UNKNOWN_FACILITY = "Неизвестный объект";

    public static final String UNKNOWN_INVESTOR = "Неизвестный инвестор";

    // USERS PATH
    public static final String USERS = BASE_URL + VERSION + KEY_PATH + "/users";

    public static final String UPDATE_USER = "/update";

    public static final String UPDATE_USER_JSON = "/update.json";

    public static final String USER_LOGIN = "/{login}";

    // BITRIX CONTACT PATHS

    public static final String BITRIX_CONTACT = BASE_URL + VERSION + KEY_PATH + "/bitrix";

    public static final String BITRIX_CONTACTS_MERGE = "/merge";

    public static final String BITRIX_CONTACT_UPDATE_URL = "http://bitrixflows.jelastic.regruhosting.ru/update";

    // API PATHS
    public static final String[] API_HTTP_MATCHERS = {
            PATH_MONIES,
            USERS + "/**",
            BITRIX_CONTACT + "/**",
            PATH_INVESTOR_CASH + "/**"
    };

    // IGNORING access to Spring Security

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
