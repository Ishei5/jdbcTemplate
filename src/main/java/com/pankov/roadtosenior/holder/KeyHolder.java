package com.pankov.roadtosenior.holder;

import java.util.List;
import java.util.Map;

public interface KeyHolder {
    Number getKey();

    <T> T getKeyAs(Class<T> clazz);

    List<Map<String, Object>> getKeyList();
}
