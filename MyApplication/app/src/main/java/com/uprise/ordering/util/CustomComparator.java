package com.uprise.ordering.util;

import java.util.Comparator;

/**
 * Created by cicciolina on 10/22/16.
 */

public class CustomComparator implements Comparator<String> {
    @Override
    public int compare(String o1, String o2) {
        return o1.compareTo(o2);
    }
}
