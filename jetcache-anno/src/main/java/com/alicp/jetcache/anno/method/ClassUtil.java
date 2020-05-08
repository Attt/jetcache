/**
 * Created on  13-09-09 17:20
 */
package com.alicp.jetcache.anno.method;

import com.alicp.jetcache.anno.PageId;
import com.alicp.jetcache.anno.PageParameter;
import org.springframework.asm.Type;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collections;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @author <a href="mailto:areyouok@gmail.com">huangli</a>
 */
public class ClassUtil {

    private static ConcurrentHashMap<Method, String> methodSigMap = new ConcurrentHashMap();


    public static String getShortClassName(String className) {
        if (className == null) {
            return null;
        }
        String[] ss = className.split("\\.");
        StringBuilder sb = new StringBuilder(className.length());
        for (int i = 0; i < ss.length; i++) {
            String s = ss[i];
            if (i != ss.length - 1) {
                sb.append(s.charAt(0)).append('.');
            } else {
                sb.append(s);
            }
        }
        return sb.toString();
    }

    public static Class<?>[] getAllInterfaces(Object obj) {
        Class<?> c = obj.getClass();
        HashSet<Class<?>> s = new HashSet<>();
        do {
            Class<?>[] its = c.getInterfaces();
            Collections.addAll(s, its);
            c = c.getSuperclass();
        } while (c != null);
        return s.toArray(new Class<?>[s.size()]);
    }

    private static void getMethodSig(StringBuilder sb, Method m) {
        sb.append(m.getName());
        sb.append(Type.getType(m).getDescriptor());
    }

    public static String getMethodSig(Method m) {
        String sig = methodSigMap.get(m);
        if (sig != null) {
            return sig;
        } else {
            StringBuilder sb = new StringBuilder();
            getMethodSig(sb, m);
            sig = sb.toString();
            methodSigMap.put(m, sig);
            return sig;
        }
    }

    public static BiFunction<Method, Object[], Object> fetchPageNumberByAnnotation(){

        return (method, arguments)->{
            try {
                for (int i = 0; i < method.getParameters().length; i++) {
                    Parameter parameter = method.getParameters()[i];
                    PageId pageId = parameter.getDeclaredAnnotation(PageId.class);
                    if (pageId != null) {
                        pageIdTypeCheck(parameter.getType());
                        return arguments[i];
                    }

                    PageParameter pageParameter = parameter.getDeclaredAnnotation(PageParameter.class);
                    if (pageParameter != null) {
                        Class<?> clazz = parameter.getType();
                        for (Field declaredField : clazz.getDeclaredFields()) {
                            PageId pageId1 = declaredField.getDeclaredAnnotation(PageId.class);
                            if (pageId1 != null) {
                                pageIdTypeCheck(declaredField.getType());
                                return declaredField.get(arguments[i]);
                            }
                        }

                        for (Method declaredMethod : clazz.getDeclaredMethods()) {
                            PageId pageId1 = declaredMethod.getDeclaredAnnotation(PageId.class);
                            if (pageId1 != null) {
                                if (declaredMethod.getParameterCount() != 0) {
                                    throw new RuntimeException("Multi parameters detected, @PageId should be used for 'get' method where No parameter provided.");
                                }
                                pageIdTypeCheck(declaredMethod.getReturnType());
                                return declaredMethod.invoke(arguments[i]);
                            }
                        }
                    }
                }
            }catch (Throwable t){
                throw new RuntimeException(t.toString());
            }
            return null;
        };


    }

    private static void pageIdTypeCheck(Class<?> type){
        if(!type.equals(Long.class) && !type.equals(Integer.class)
                && !type.equals(Long.TYPE) && !type.equals(Integer.TYPE)){
            throw new RuntimeException("Unsupported @PageId type, only [java.lang.Long, long, java.lang.Integer, int] supported");
        }
    }
}
