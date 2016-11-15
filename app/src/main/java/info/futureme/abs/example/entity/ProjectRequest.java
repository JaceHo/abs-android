package info.futureme.abs.example.entity;

/**
 * Created by Jeffrey on 2016/5/9.
 */
public class ProjectRequest {
    private String engineerid;
    private String filter;
    private String sort;
    private int page;
    private int limit;

    public String getEngineerid() {
        return engineerid;
    }

    public void setEngineerid(String engineerid) {
        this.engineerid = engineerid;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }
}
