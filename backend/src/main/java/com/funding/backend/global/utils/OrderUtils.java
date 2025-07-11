package com.funding.backend.global.utils;

import java.util.UUID;

public class OrderUtils {

    public static String generateOrderId() {
        return "order_" + UUID.randomUUID().toString().substring(0, 8);
    }
}
