package edu.j4f.aprog.parsers;

import java.util.List;

import edu.j4f.aprog.entity.ILuceneDocument;

public interface IParser {

	List<ILuceneDocument> getDocuments();

}
