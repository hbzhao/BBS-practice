package dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Page implements Serializable {
    private static int DEFAULT_PAGE_SIZE = 20;
    private int pageSize = DEFAULT_PAGE_SIZE;
    private long start;
    private List Data;
    private long totalCount;


    public Page(int pageSize, long start, List data, long totalCount) {
        this.pageSize = pageSize;
        this.start = start;
        Data = data;
        this.totalCount = totalCount;
    }

    //    空白页的构造方法
    public Page() {
        this(0, 0, new ArrayList(), DEFAULT_PAGE_SIZE);
    }

    public int getPageSize() {
        return pageSize;
    }

    public long getStart() {
        return start;
    }

    public List getData() {
        return Data;
    }

    public long getTotalCount() {
        return totalCount;
    }

//    取当前页码
    public long getCurrentPageNo(){
        return start/pageSize+1;
    }

//    判断是否为第一页
    public boolean isFirstPage(){
        return this.getCurrentPageNo()>1;
    }

//    获取每一个分页的第一条数据的位置
    public static int getStartOfPage(int pageNo,int pageSize){
        return (pageNo-1)*pageSize;
    }

    public static int getStartOfPage(int pageNo){
        return (pageNo-1)*DEFAULT_PAGE_SIZE;
    }
}
