package com.rafaelfiume.rainbow.support;

import static java.lang.System.getenv;

public class SalumeStackHostsResolution {

    public static String supplierBaseUrl() {
        return getenv("SUPPLIER_URL");
    }

}
