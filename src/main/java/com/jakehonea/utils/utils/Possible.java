package com.jakehonea.utils.utils;

import java.util.Optional;

public class Possible {
    public static <T> Optional<T> emptyIfNull(T item) {
        return item == null ? Optional.empty() : Optional.of(item);
    }
}
