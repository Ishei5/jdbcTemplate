package com.pankov.roadtosenior.holder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class GeneratedKeyHolder implements KeyHolder {

    private List<Map<String, Object>> keyList;

    public GeneratedKeyHolder() {
        keyList = new ArrayList<>(1);
    }

    @Override
    public Number getKey() {
        return getKeyAs(Number.class);
    }

    @Override
    public <T> T getKeyAs(Class<T> clazz) {
        if (keyList.isEmpty()) {
            return null;
        } else {
            Iterator<Object> keyIter = ((Map) this.keyList.get(0)).values().iterator();
            if (keyIter.hasNext()) {
                Object key = keyIter.next();
                if (key != null && clazz.isAssignableFrom(key.getClass())) {
                    return clazz.cast(key);
                } else throw new RuntimeException("Cannot cast key to generated type");
            } else {
                throw new RuntimeException("Cannot get generated key");
            }
        }
    }

    @Override
    public List<Map<String, Object>> getKeyList() {
        return keyList;
    }
}
