package edu.j4f.aprog.web;

import javax.faces.bean.ManagedBean;

import edu.j4f.aprog.parsers.IndexParser;


@ManagedBean(name = "indexer")
public class IndexerBean {

	public String getIndexAll() {
		try {
			IndexParser ip = new IndexParser();
			ip.parse();
			return "Ok: parsers successfully finished.";
		} catch (Exception e) {
			return "Something went wrong: see logs for details";
		}
	}

}
