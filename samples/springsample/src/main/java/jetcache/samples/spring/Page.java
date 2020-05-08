package jetcache.samples.spring;

import com.alicp.jetcache.anno.PageId;

/**
 * jetcache-parent
 *
 * @author atpexgo.wu
 * @date 2020-05-08 14:14
 */
public class Page {

    private Long pageNumber;

    private Long pageSize;

    @PageId
    public Long getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Long pageNumber) {
        this.pageNumber = pageNumber;
    }

    public Long getPageSize() {
        return pageSize;
    }

    public void setPageSize(Long pageSize) {
        this.pageSize = pageSize;
    }
}
