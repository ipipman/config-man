package cn.ipman.config.client.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public interface FieldUtils {

    static List<Field> findAnnotatedField(Class<?> aClass, Class<? extends Annotation> annotationClass) {
        return findField(aClass, f -> f.isAnnotationPresent(annotationClass));
    }

    static List<Field> findField(Class<?> aClass, Function<Field, Boolean> function) {
        List<Field> result = new ArrayList<>();
        while (aClass != null) {
            Field[] fields = aClass.getDeclaredFields();
            for (Field f : fields) {
                if (function.apply(f)) {
                    result.add(f);
                }
            }
            // spring中有些类会被CGLIB代理,所以需要通过父类获取Field
            aClass = aClass.getSuperclass();
        }
        return result;
    }

}
