package cn.ipman.config.client.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * 提供用于检索类中具有特定注解或满足某些条件的字段的工具方法
 */
public interface FieldUtils {

    /**
     * 查找类中所有被指定注解标注的字段
     *
     * @param aClass     要搜索的类。
     * @param annotationClass 指定的注解类型。
     * @return 所有被指定注解标注的字段列表。
     */
    static List<Field> findAnnotatedField(Class<?> aClass, Class<? extends Annotation> annotationClass) {
        return findField(aClass, f -> f.isAnnotationPresent(annotationClass));
    }

    /**
     * 根据给定的函数条件查找类中所有满足条件的字段
     *
     * @param aClass          要搜索的类。
     * @param function        用于判断字段是否满足条件的函数。
     * @return 所有满足条件的字段列表。
     */
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
