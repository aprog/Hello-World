package edu.j4f.aprog.parsers;

import java.util.ArrayList;
import java.util.List;

import edu.j4f.aprog.service.LuceneService;


public class IndexParser {

	private LuceneService luceneService = new LuceneService();

	private List<IParser> getParsers() {
		List<IParser> parsers = new ArrayList<IParser>();
		parsers.add(new TorrentDnetParser());
		return parsers;
	}

	public void parse() {
		List<IParser> parsers = getParsers();
		for (IParser parser : parsers) {
			luceneService.addDocuments(parser.getDocuments());
		}
	}

}
