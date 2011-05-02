package edu.j4f.aprog.parsers;

import java.io.IOException;
import java.util.Map;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.j4f.aprog.service.LuceneService;


public abstract class BaseParser {

	private static final Logger logger = LoggerFactory.getLogger(BaseParser.class);
	private Map<String, String> cookieMap;
	private LuceneService luceneService;

	public BaseParser() {
		luceneService = new LuceneService();
	}
	
	public void setCookies(Map<String, String> cookieMap) {
		this.cookieMap = cookieMap;
	}

	public String getContent(String url) {
		Connection connection = Jsoup.connect(url).timeout(3000);
		for (Map.Entry<String, String> cookie : cookieMap.entrySet()) {
			connection.cookie(cookie.getKey(), cookie.getValue());
		}
		String content = null;
		try {
			content = connection.get().html();
		} catch (IOException e) {
			logger.error("Could not get content", e);
		}
		return content;
	}

	public String stripTags(String text, String... allowedTags) {
		Whitelist wl = new Whitelist();
		wl.addTags(allowedTags);
		return Jsoup.clean(text, wl);
	}

	public String normilizeContent(String content) {
		content = content.replaceAll("(?s)\\n+", " ");
		content = content.replaceAll("(?s)\\s{2,}", " ");
		content = content.replaceAll("(?s)(&nbsp;){2,}", "&nbsp;");
		content = content.replaceAll("(?s)&nbsp;\\s*&nbsp;", "&nbsp;");
		content = content.replaceAll("(?s)&nbsp;\\s+", "&nbsp;");
		content = content.replaceAll("(?s)<br\\s*/>.{0,3}<br\\s*/>(.{0,3}<br\\s*/>)*", "<br />");
		return content;
	}

	public boolean isIndexed(String url) {
		boolean isIndexed = luceneService.isIndexed(url);
		if (isIndexed) {
			logger.debug("Document with url: " + url + " is indexed");
		} else {
			logger.debug("Document with url: " + url + " is not indexed");
		}
		return isIndexed;
	}

}
