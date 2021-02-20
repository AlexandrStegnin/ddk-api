package com.ddkolesnik.ddkapi.util;

import java.math.BigDecimal;

/**
 * @author Alexandr Stegnin
 */

public class Constant {

    private static final String WILD_CARD = "/**";

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

    // FACILITIES PATH
    public static final String FACILITIES = BASE_URL + VERSION + KEY_PATH + "/facilities";

    public static final String UPDATE_FACILITY = "/update";

    // BITRIX CONTACT PATHS

    public static final String BITRIX_CONTACT = BASE_URL + VERSION + KEY_PATH + "/bitrix";

    public static final String BITRIX_CONTACTS_MERGE = "/merge";

    public static final String BITRIX_CONTACT_UPDATE_URL = "http://bitrixflows.jelastic.regruhosting.ru/update";

    public static final String PATH_USER_AGREEMENT = BASE_URL + VERSION + KEY_PATH + "/agreement";

    public static final String PATH_USER_AGREEMENT_UPDATE = "/update";

    // API PATHS
    public static final String[] API_HTTP_MATCHERS = {
            PATH_MONIES,
            USERS + WILD_CARD,
            BITRIX_CONTACT + WILD_CARD,
            PATH_INVESTOR_CASH + WILD_CARD,
            FACILITIES + WILD_CARD,
            PATH_USER_AGREEMENT + WILD_CARD
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

    public static final String INVESTOR_PREFIX = "investor";

    public static final String UNDER_FACILITY_SUFFIX = "_Целиком";

    public static final Long DDK_USER_ID = 322L;

    public static final BigDecimal COMMISSION_RATE = new BigDecimal("0.01010101");
}
