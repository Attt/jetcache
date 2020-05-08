/**
 * Created on  13-09-10 10:33
 */
package com.alicp.jetcache.anno.support;

import java.lang.reflect.Method;
import java.util.function.BiFunction;

/**
 * @author <a href="mailto:areyouok@gmail.com">huangli</a>
 */
public class FirstPageCachedAnnoConfig extends CachedAnnoConfig {

    private String entityLoader;

    private BiFunction<Method, Object[], Object> fetchPageNumber;

    public String getEntityLoader() {
        return entityLoader;
    }

    public void setEntityLoader(String entityLoader) {
        this.entityLoader = entityLoader;
    }

    public BiFunction<Method, Object[], Object> getFetchPageNumber() {
        return fetchPageNumber;
    }

    public void setFetchPageNumber(BiFunction<Method, Object[], Object> fetchPageNumber) {
        this.fetchPageNumber = fetchPageNumber;
    }
}
