package edu.j4f.aprog.utils;

import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;

public class SearchPager extends Pager {

	private String requestPhrase; 

	public SearchPager(String requestPhrase) {
		this.requestPhrase = requestPhrase;
	}

	public String getPrevPageLink() {
		String url = FacesContext.getCurrentInstance().getExternalContext().encodeResourceURL("/index.jsf?requestPhrase=" + requestPhrase + "&start=" + getPrevPage());
		return url;
	}

	public String getNextPageLink() {
		String url = FacesContext.getCurrentInstance().getExternalContext().encodeResourceURL("/index.jsf?requestPhrase=" + requestPhrase + "&start=" + getNextPage());
		return url;
	}

	public List<PageRecord> getPagesLinks() {
		List<PageRecord> pagesLinks = new ArrayList<PageRecord>();
		List<Integer> pages = getPages();
		String url = "";
		for (Integer page : pages) {
			url = FacesContext.getCurrentInstance().getExternalContext().encodeResourceURL("/index.jsf?requestPhrase=" + requestPhrase + "&start=" + page);
			PageRecord pr = new PageRecord();
			pr.setUrl(url);
			pr.setName(page);
			pagesLinks.add(pr);
		}
		return pagesLinks;
	}
	
}
