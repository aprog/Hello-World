package edu.j4f.aprog.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.TokenSources;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.j4f.aprog.entity.ILuceneDocument;
import edu.j4f.aprog.entity.LuceneDocument;
import edu.j4f.aprog.utils.PropertyManager;

public class LuceneService {

	private static final Logger logger = LoggerFactory.getLogger(LuceneService.class);

	private Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_30);
	private Directory directory = null;

	public LuceneService() {
		try {
			ClassLoader loader = this.getClass().getClassLoader();
			String indexDirectory = loader.getResource("/").getFile() + "luceneindex/";
			directory = FSDirectory.open(new File(indexDirectory));
		} catch (IOException e) {
			logger.error("Failed to open FSDirectory", e);
		}
	}

	private IndexWriter getIndexWriter() {
		IndexWriter iwriter = null;
		try {
			logger.debug("Get index writer");
			iwriter = new IndexWriter(directory, analyzer, false,
					IndexWriter.MaxFieldLength.LIMITED);
		} catch (IOException e) {
			logger.error("Failed to create IndexWriter", e);
		}
		return iwriter;
	}

	public void addDocument(ILuceneDocument doc) {
		logger.debug("Add new document");
		if (doc == null) {
			throw new IllegalArgumentException("Document is null");
		}
		if (!doc.isValid()) {
			throw new IllegalArgumentException("Document is invalid");
		}

		IndexWriter iwriter = getIndexWriter();
		try {
			iwriter.addDocument(doc.getDocument());
			iwriter.close();
		} catch (IOException e) {
			logger.error("Failed to add Lucene document", e);
		}
	}

	public void addDocuments(List<ILuceneDocument> docs) {
		logger.debug("Add new documents");
		IndexWriter iwriter = getIndexWriter();
		try {
			for (ILuceneDocument document : docs) {
				if (document == null) {
					throw new IllegalArgumentException("Document is null");
				}
				if (!document.isValid()) {
					throw new IllegalArgumentException("Document is invalid");
				}
				iwriter.addDocument(document.getDocument());
			}
			iwriter.close();
		} catch (IOException e) {
			logger.error("Failed to add Lucene documents", e);
		}
	}

	private Query getQuery(String searchPhrase) {
		Query query = null;
		QueryParser parser = new QueryParser(Version.LUCENE_30, "content", analyzer);
		try {
			query = parser.parse(searchPhrase);
		} catch (ParseException e) {
			logger.error("Failed to parse searchPhrase", e);
		}
		return query;
	}

	public int getTotalHitsCount(String searchPhrase) {
		Query query = getQuery(searchPhrase);
		IndexSearcher searcher = null;
		int numTotalHits = 0;
		try {
			searcher = new IndexSearcher(directory);
			TopScoreDocCollector collector = TopScoreDocCollector.create(10, false);
			searcher.search(query, collector);
			numTotalHits = collector.getTotalHits();
		} catch (CorruptIndexException e) {
			logger.error("Failed while working with Lucene index", e);
		} catch (IOException e) {
			logger.error("Failed to get total hits count", e);
		}
		logger.debug("For query '" + searchPhrase + "' total hits: " + numTotalHits);
		return numTotalHits;
	}

	public List<ILuceneDocument> search(String searchPhrase, int start, int hitsPerPage) {
		logger.debug("Search request with search phrase: " + searchPhrase + ", start: " + start + ", hitsPerPage: " + hitsPerPage);
		IndexSearcher searcher = null;
		Query query = null;
		List<ILuceneDocument> docs = new ArrayList<ILuceneDocument>();
		try {
			searcher = new IndexSearcher(directory);
			query = getQuery(searchPhrase);

			int numTotalHits = getTotalHitsCount(searchPhrase);

			TopScoreDocCollector collector = TopScoreDocCollector.create(numTotalHits, false);
			/*
			Sort sort = new Sort(new SortField("cdate", SortField.LONG, true));
			TopFieldCollector collector = TopFieldCollector.create(sort, numTotalHits, true, false, false, false);
			*/
			searcher.search(query, collector);
			ScoreDoc[] hits = collector.topDocs().scoreDocs;

			SimpleHTMLFormatter htmlFormatter = new SimpleHTMLFormatter();
			Highlighter highlighter = new Highlighter(htmlFormatter,
					new QueryScorer(query));

			docs = getDocs(hits, searcher, highlighter, start, hitsPerPage);
		} catch (IOException e) {
			logger.error("Failed to complete search request", e);
		}
		return docs;
	}

	private List<ILuceneDocument> getDocs(ScoreDoc[] hits,
			IndexSearcher searcher, Highlighter highlighter, int start, int hitsPerPage) {
		start = Math.max(0, start);
		int end = Math.min(hits.length, start + hitsPerPage);
		logger.debug("Get documents from: " + start + " to: " + end + " hits");
		List<ILuceneDocument> docs = new ArrayList<ILuceneDocument>();
		for (int i = start; i < end; i++) {
			int docId = hits[i].doc;
			Document doc = null;
			try {
				doc = searcher.doc(docId);
				String content = doc.get("content");

				ILuceneDocument tdoc = new LuceneDocument();
				tdoc.fillEntity(doc);

				TokenStream tokenStream = null;

				tokenStream = TokenSources.getAnyTokenStream(searcher.getIndexReader(), docId, "content", analyzer);
				String fragments = null;

				fragments = highlighter.getBestFragments(tokenStream, content, 3, "...");
				if (fragments.isEmpty()) {
					fragments = getShortContent(content);
				}
				tdoc.setContent(fragments);
				tdoc.setId(docId);
				docs.add(tdoc);
			} catch (CorruptIndexException e) {
				logger.error("Failed while working with Lucene index", e);
			} catch (IOException e) {
				logger.error("Failed when work with token stream or best fragments", e);
			} catch (InvalidTokenOffsetsException e) {
				logger.error("Failed when work with best fragments");
			}
		}
		return docs;
	}

	private String getShortContent(String content) {
		String shortContent = "";
		String[] fragments = content.split(" ");
		int length = PropertyManager.getInstance().getIntegerProperty("search.record.maxwords");
		int count = Math.min(fragments.length, length);
		for (int k = 0; k < count; k++) {
			shortContent = shortContent.concat(" ".concat(fragments[k]));
		}
		if (fragments.length > count) {
			shortContent = shortContent.concat("...");
		}
		return shortContent;
	}

	public boolean isIndexed(String url) {
		url = QueryParser.escape(url);
		int hits = getTotalHitsCount("url:".concat(url).concat("*"));
		return hits > 0 ? true : false;
	}

}
