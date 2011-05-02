package edu.j4f.aprog.entity;

import org.apache.lucene.document.Document;

public interface ILuceneDocument {

	public Document getDocument();

	public void fillEntity(Document doc);

	public int getId();

	public void setId(int id);

	public String getTitle();

	public void setTitle(String title);

	public String getContent();

	public void setContent(String content);

	public String getUrl();

	public void setUrl(String content);

	public Long getCdate();

	public void setCdate(Long cdate);

	public String getImgPath();

	public void setImgPath(String imgPath);

	public boolean isValid();

}
