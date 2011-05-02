package edu.j4f.aprog.utils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Pager {

	private static final Logger logger = LoggerFactory.getLogger(Pager.class);
	private int pagesPerView;
	private int recordsCount;
	private int recordsPerPage;
	private int currentPage;

	public int getPagesPerView() {
		return pagesPerView;
	}

	public void setPagesPerView(int pagesPerView) {
		this.pagesPerView = pagesPerView;
	}

	public int getRecordsCount() {
		return recordsCount;
	}

	public void setRecordsCount(int recordsCount) {
		this.recordsCount = recordsCount;
	}

	public int getRecordsPerPage() {
		return recordsPerPage;
	}

	public void setRecordsPerPage(int recordsPerPage) {
		this.recordsPerPage = recordsPerPage;
	}

	public int getCurrentPage() {
		if (currentPage < getFirstPage()) {
			logger.info("Get current page: current page is less than first page");
			currentPage = getFirstPage();
		} else if (currentPage > getLastPage()) {
			logger.info("Get current page: current page is grater than last page");
			currentPage = getLastPage();
		}
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public int getFirstPage() {
		return 1;
	}

	public int getLastPage() {
		return new BigDecimal(1.0 * getRecordsCount() / getRecordsPerPage())
				.setScale(0, BigDecimal.ROUND_UP).intValue();
	}

	public int getPrevPage() {
		return Math.max(0, getCurrentPage() - 1);
	}

	public int getNextPage() {
		return Math.min(getLastPage(), getCurrentPage() + 1);
	}

	public List<Integer> getPages() {
		int lastPage = getLastPage();
		pagesPerView = pagesPerView > lastPage ? lastPage : pagesPerView;
		int middle = new BigDecimal(1.0 * getPagesPerView() / 2).setScale(0,
				BigDecimal.ROUND_DOWN).intValue();

		int start = getCurrentPage() - middle;
		start = start <= 0 ? 0 : start;
		if (lastPage - start < getPagesPerView()) {
			start = lastPage - getPagesPerView();
		}

		int end = getCurrentPage() + middle;
		end = end > lastPage ? lastPage : end;
		if (end - start < getPagesPerView()) {
			end = getPagesPerView();
		}

		logger.debug("Get pages with start: " + start + ", end: " + end);

		List<Integer> result = new ArrayList<Integer>();
		for (int i = start; i < end; i++) {
			result.add(i + 1);
		}
		return result;
	}

}
