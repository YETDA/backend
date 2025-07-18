package com.funding.backend.global.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class CreateRandomNumber {

    private static final Random random = new Random();

    public static String randomNumber(){
        return UUID.randomUUID().toString().substring(0, 10);
    }

    public static String randomNumberSix() {
        Random random = new Random();
        int number = 100000 + random.nextInt(900000); // 100000 ~ 999999
        return String.valueOf(number);
    }


    //날짜 + 시간 + 랜덤번호
    public static String timeBasedRandomName() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String uuidPart = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        return timestamp + "_" + uuidPart;
    }

    public static <T> T randomFromList(List<T> list) {
        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("List cannot be null or empty");
        }
        return list.get(random.nextInt(list.size()));
    }
}
