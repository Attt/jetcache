package com.alicp.jetcache.anno;

import java.lang.annotation.*;

/**
 * jetcache-parent
 *
 * @author atpexgo.wu
 * @date 2020-05-08 12:35
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD})
public @interface PageId {
}
