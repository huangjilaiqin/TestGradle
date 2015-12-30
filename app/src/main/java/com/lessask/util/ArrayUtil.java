package com.lessask.util;

import java.util.Iterator;
import java.util.List;

/**
 * Created by JHuang on 2015/12/30.
 */
public class ArrayUtil {
    public static String join(List array, String sperator){
        StringBuilder builder = new StringBuilder();
        Iterator iterator = array.iterator();
        while (iterator.hasNext()){
            builder.append(iterator.next());
            if(iterator.hasNext())
                builder.append(sperator);
        }
        return builder.toString();
    }
}
