package com.pe.lima.sg.presentacion.util;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;


public class PageWrapper<T> {
    public static final int MAX_PAGE_ITEM_DISPLAY = 5;
    private Page<T> page;
    private List<PageItem> items;
    private int currentNumber;
    private String url;
    private String operacion;
    private int pageActual;
    private int pageMaximo;
    private long total;
    private static final Logger LOGGER = LoggerFactory.getLogger(PageWrapper.class);

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public PageWrapper(Page<T> page, String url, PageableSG pageable){
        this.page = page;
        this.url = url;
        items = new ArrayList<PageItem>();

        //currentNumber = page.getNumber() + 1; //start from 1 to match page.page
        currentNumber = pageable.getOffset() + 1;
        operacion = pageable.getOperacion();
        
        pageActual = pageable.getOffset()==0?1:pageable.getOffset()/pageable.getLimit()+1;
        pageMaximo = page.getTotalPages();

        currentNumber = pageActual;
        
        total = page.getTotalElements();
        int start, size;
        if (page.getTotalPages() <= MAX_PAGE_ITEM_DISPLAY){
            start = 1;
            size = page.getTotalPages();
        } else {
            if (currentNumber <= MAX_PAGE_ITEM_DISPLAY - MAX_PAGE_ITEM_DISPLAY/2){
                start = 1;
                size = MAX_PAGE_ITEM_DISPLAY;
            } else if (currentNumber >= page.getTotalPages() - MAX_PAGE_ITEM_DISPLAY/2){
                start = page.getTotalPages() - MAX_PAGE_ITEM_DISPLAY + 1;
                size = MAX_PAGE_ITEM_DISPLAY;
            } else {
                start = currentNumber - MAX_PAGE_ITEM_DISPLAY/2;
                size = MAX_PAGE_ITEM_DISPLAY;
            }
        }

        for (int i = 0; i<size; i++){
            items.add(new PageItem(start+i, (start+i)==pageActual));
        }
    }

    public List<PageItem> getItems(){
        return items;
    }

    public int getNumber(){
        return currentNumber;
    }

    public List<T> getContent(){
        return page.getContent();
    }

    public int getSize(){
        return page.getSize();
    }

    public int getTotalPages(){
        return page.getTotalPages();
    }

    public boolean isFirstPage(){
        //return page.isFirst();
    	//return operacion.equals("F");
    	return currentNumber == 1;
    }

    public boolean isLastPage(){
        //return page.isLast();
    	//return operacion.equals("L");
    	return pageActual == pageMaximo;
    }

    public boolean isHasPreviousPage(){
        //return page.hasPrevious();
    	//return operacion.equals("P");
    	return currentNumber == 1;
    }

    public boolean isHasNextPage(){
        //return page.hasNext();
    	return (pageActual >= pageMaximo);
    }

    public class PageItem {
        private int number;
        private boolean current;
        public PageItem(int number, boolean current){
            this.number = number;
            this.current = current;
        }

        public int getNumber(){
            return this.number;
        }

        public boolean isCurrent(){
            return this.current;
        }
    }

	public String getOperacion() {
		return operacion;
	}

	public void setOperacion(String operacion) {
		this.operacion = operacion;
	}

	public int getPageActual() {
		return pageActual;
	}

	public void setPageActual(int pageActual) {
		this.pageActual = pageActual;
	}

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

}
