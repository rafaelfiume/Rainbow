package com.rafaelfiume.rainbow.acceptance;

import static java.lang.System.getenv;

public class SalumeStackHostsResolution {

    public static String supplierBaseUrl() {
        return getenv("SUPPLIER_URL");
    }

}
