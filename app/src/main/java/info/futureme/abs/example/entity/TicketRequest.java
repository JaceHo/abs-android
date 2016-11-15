package info.futureme.abs.example.entity;

/**
 * Created by Jeffrey on 2016/4/29.
 */
public class TicketRequest {
    private int page;
    private int limit;
    private TicketFilter filter = new TicketFilter();
    private String sort;
    private String includes;

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

    public TicketFilter getFilter() {
        return filter;
    }

    public void setFilter(TicketFilter filter) {
        if(filter != null)
            this.filter = filter;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getIncludes() {
        return includes;
    }

    public void setIncludes(String includes) {
        this.includes = includes;
    }

    public void clear() {
        setFilter(new TicketFilter());
        setSort(null);
        setLimit(0);
        setPage(0);
        setIncludes(null);
    }
}
