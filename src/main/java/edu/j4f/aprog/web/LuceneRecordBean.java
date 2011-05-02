package edu.j4f.aprog.web;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.j4f.aprog.entity.ILuceneDocument;
import edu.j4f.aprog.service.LuceneService;
import edu.j4f.aprog.utils.GetParameter;
import edu.j4f.aprog.utils.Pager;
import edu.j4f.aprog.utils.PropertyManager;
import edu.j4f.aprog.utils.SearchPager;

@ManagedBean(name = "luceneRecord")
public class LuceneRecordBean {

	private static final Logger logger = LoggerFactory.getLogger(LuceneRecordBean.class);

	private String requestPhrase;
	private int startPage;
	private int hitsCount = -1;

	private LuceneService luceneDao;

	public LuceneRecordBean() {
		luceneDao = new LuceneService();
	}

	public void setRequestPhrase(String request) {
		this.requestPhrase = request;
	}

	public String getRequestPhrase() {
		if (requestPhrase == null) {
			requestPhrase = GetParameter.get("requestPhrase");
		}
		return requestPhrase;
	}

	public List<ILuceneDocument> getAllRecords() {
		if (getRequestPhrase() == null || getRequestPhrase().isEmpty()) {
			return new ArrayList<ILuceneDocument>();
		}

		int perPage = PropertyManager.getInstance().getIntegerProperty("search.record.perpage");
		int start = (getStartPage() - 1) * perPage;
		List<ILuceneDocument> res = null;
		try {
			 res = luceneDao.search(getRequestPhrase(), start, perPage);
		} catch (Exception e) {
			
		}
		return res;
	}

	public int getStartPage() {
		logger.debug("Get start page from ExternalContext");
		startPage = GetParameter.getIntegerValue("start");
		if (startPage > getLastPage()) {
			startPage = getLastPage();
		}
		startPage = startPage < 1 ? 1 : startPage;
		logger.debug("Start page: " + startPage);
		return startPage;
	}

	public int getRecordsCount() {
		if ((hitsCount == -1) && (!getRequestPhrase().isEmpty())) {
			try {
				hitsCount = luceneDao.getTotalHitsCount(getRequestPhrase());
			} catch (Exception e) {
				
			}
		}
		return hitsCount;
	}

	public int getPagesPerView() {
		return PropertyManager.getInstance().getIntegerProperty("search.pages.perview");
	}

	public int getRecordsPerPage() {
		return PropertyManager.getInstance().getIntegerProperty("search.record.perpage");
	}

	public int getLastPage() {
		return new BigDecimal(1.0 * getRecordsCount() / getRecordsPerPage())
				.setScale(0, BigDecimal.ROUND_UP).intValue();
	}

	public Pager getPager() {
		SearchPager pager = new SearchPager(getRequestPhrase());
		pager.setCurrentPage(getStartPage());
		pager.setPagesPerView(getPagesPerView());
		pager.setRecordsCount(getRecordsCount());
		pager.setRecordsPerPage(getRecordsPerPage());
		return pager;
	}

	public String getContextPath() {
		return FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();
	}

	public void searchRedirect() {
		String url = FacesContext.getCurrentInstance().getExternalContext().encodeResourceURL("/index.jsf");
		try {
			if (!getRequestPhrase().isEmpty()) {
				url = FacesContext.getCurrentInstance().getExternalContext().encodeResourceURL("/index.jsf?requestPhrase=" + getRequestPhrase() + "&start=1");
			}
			logger.debug("Search redirect with request phrase: " + getRequestPhrase() + " and url: " + url);
		} catch (IllegalArgumentException e) {
			logger.error("Failed to encode url: " + url + " with request phrase: " + getRequestPhrase(), e);
		}
		try {
			FacesContext.getCurrentInstance().getExternalContext().redirect(url);
		} catch (IOException e) {
			logger.error("Failed search redirect to: " + url, e);
		}
	}

}
