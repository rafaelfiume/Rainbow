package com.rafaelfiume.raibow.acceptance;

import static java.lang.System.getenv;

public class SalumeStackHostsResolution {

    public static String supplierServer() {
        return getenv("SUPPLIER_URL");
    }

}
