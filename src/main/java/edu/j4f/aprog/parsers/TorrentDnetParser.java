package edu.j4f.aprog.parsers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.j4f.aprog.entity.ILuceneDocument;
import edu.j4f.aprog.entity.LuceneDocument;

public class TorrentDnetParser extends BaseParser implements IParser {

	private static final Logger logger = LoggerFactory.getLogger(TorrentDnetParser.class);
	
	private final String linksUrl = "http://torrent.dnet/tbdev/browse.php";
	private final String posterBaseUrl = "http://torrent.dnet/tbdev/torrents/images/";
	private final String torrentBaseUrl = "http://torrent.dnet/tbdev/";
	
	private final Pattern linksAreaPattern = Pattern.compile(".*?</tr>.*?<tbody id=\"highlighted\">(.+?)</tbody>.*?<tr>.*?<td class=\"colhead\".*", Pattern.DOTALL);
	private final Pattern linksPattern = Pattern.compile(".*?</a></td>.*?<td align=\"left\"><a href=\"(.+?)&amp;hit=1\"><b>.*?", Pattern.DOTALL);
	private final Pattern titlePattern = Pattern.compile(".*?\\.torrent\"><b>\\s*(.+?)\\s*</b>.*", Pattern.DOTALL);
	private final Pattern descriptionPattern = Pattern.compile(".*?Описание</td>.*?<td valign=\"top\" align=\"left\">\\s*(.+?)\\s*<tr>.*?<td width=\"\" class=\"heading\" valign=\"top\" align=\"right\">.*", Pattern.DOTALL);
	private final Pattern posterPattern = Pattern.compile(".*?<a href=\"viewimage\\.php\\?pic=.+?\\.\\w{0,5}\"><img border=\"0\" src=\"thumbnail\\.php\\?(.+?)\"\\s*?/></a></td>.*?</tr>.*", Pattern.DOTALL);

	public TorrentDnetParser() {
		Map<String, String> cookieMap = new HashMap<String, String>();
		cookieMap.put("uid", "1243");
		cookieMap.put("pass", "97d6aeca8bed8057fc0c227f6b87c930");
		setCookies(cookieMap);
	}

	private List<String> getAllLinks() {
		List<String> links = new ArrayList<String>();
		String content = getContent(linksUrl);

		Matcher matcher = linksAreaPattern.matcher(content);
		if (matcher.matches()) {
			content = matcher.group(1);
		}

		Matcher m = linksPattern.matcher(content);
		while (m.find()) {
			links.add(torrentBaseUrl.concat(m.group(1)));
		}
		return links;
	}

	private String getTitle(String content) {
		Matcher m = titlePattern.matcher(content);
		String title = "";
		if (m.matches()) {
			title = stripTags(m.group(1));
		} else {
			logger.error("No title mathces found");
		}
		return title;
	}

	private String getDescription(String content) {
		Matcher m = descriptionPattern.matcher(content);
		String description = "";
		if (m.matches()) {
			description = stripTags(m.group(1), "br");
			description = normilizeContent(description);
		} else {
			logger.error("No content mathces found");
		}
		return description;
	}

	private String getPosterUrl(String content) {
		Matcher m = posterPattern.matcher(content);
		String posterUrl = "";
		if (m.matches()) {
			posterUrl = stripTags(m.group(1));
			posterUrl = posterBaseUrl.concat(posterUrl);
		} else {
			logger.error("No poster mathces found");
		}
		return posterUrl;
	}

	public List<ILuceneDocument> getDocuments() {
		List<ILuceneDocument> luceneDocuments = new ArrayList<ILuceneDocument>();
		List<String> links = getAllLinks();
		for (String link : links) {
			if (!isIndexed(link)) {
				String content = getContent(link);
				ILuceneDocument luceneDocument = new LuceneDocument();
				luceneDocument.setCdate(System.currentTimeMillis());
				luceneDocument.setContent(getDescription(content));
				luceneDocument.setImgPath(getPosterUrl(content));
				luceneDocument.setTitle(getTitle(content));
				luceneDocument.setUrl(link);
				luceneDocuments.add(luceneDocument);
			}
		}
		return luceneDocuments;
	}

}
