package br.com.dev.lukas.TodoList.utils;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;


import java.beans.PropertyDescriptor;
import java.util.HashSet;
import java.util.Set;

public class Utils {

    public static void copyNonNullProperties(Object source, Object target){
        BeanUtils.copyProperties(source, target , getNullPropertyNames(source));
    }

    public static String[] getNullPropertyNames (Object source) {
        final BeanWrapper beanWrapper = new BeanWrapperImpl(source);

        PropertyDescriptor[] pds = beanWrapper.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<String>();
        for (PropertyDescriptor pd : pds) {
           Object srcValue = beanWrapper.getPropertyValue(pd.getName());
           if (srcValue == null){
               emptyNames.add(pd.getName());
           }
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }
}
