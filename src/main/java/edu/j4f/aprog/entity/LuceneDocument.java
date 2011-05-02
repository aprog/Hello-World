package edu.j4f.aprog.entity;

import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

public class LuceneDocument implements ILuceneDocument {

	private Field url;
	private Field title;
	private Field cdate;
	private Field content;
	private Field imgPath;
	private int id;

	public LuceneDocument() {
		this.url = new Field("url", "", Field.Store.YES,
				Field.Index.NOT_ANALYZED);
		this.title = new Field("title", "", Field.Store.YES,
				Field.Index.ANALYZED);
		this.cdate = new Field("cdate", "", Field.Store.YES,
				Field.Index.NOT_ANALYZED);
		this.content = new Field("content", "", Field.Store.YES,
				Field.Index.ANALYZED);
		this.imgPath = new Field("imgPath", "", Field.Store.YES,
				Field.Index.NOT_ANALYZED);
	}

	public String getUrl() {
		return url.stringValue();
	}

	public void setUrl(String url) {
		this.url.setValue(url);
	}

	public String getTitle() {
		return title.stringValue();
	}

	public void setTitle(String title) {
		this.title.setValue(title);
	}

	public Long getCdate() {
		return Long.parseLong(cdate.stringValue());
	}

	public void setCdate(Long time) {
		this.cdate.setValue(DateTools.timeToString(time,
				DateTools.Resolution.MILLISECOND));
	}

	public String getContent() {
		return content.stringValue();
	}

	public void setContent(String content) {
		this.content.setValue(content);
	}

	public String getImgPath() {
		return imgPath.stringValue();
	}

	public void setImgPath(String imgPath) {
		this.imgPath.setValue(imgPath);
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return this.id;
	}

	public Document getDocument() {
		Document doc = new Document();
		doc.add(url);
		doc.add(title);
		doc.add(cdate);
		doc.add(content);
		doc.add(imgPath);
		return doc;
	}

	public void fillEntity(Document doc) {
		setCdate(Long.parseLong(doc.get("cdate")));
		setContent(doc.get("content"));
		setImgPath(doc.get("imgPath"));
		setTitle(doc.get("title"));
		setUrl(doc.get("url"));
	}
	
	public boolean isValid() {
		try {
			checkEmptyString(getUrl());
			checkEmptyString(getTitle());
			checkEmptyString(getContent());
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private void checkEmptyString(String string) throws IllegalArgumentException {
		if (string == null || string.trim().equals("")) {
			throw new IllegalArgumentException("String is empty");
		}
	}

}
