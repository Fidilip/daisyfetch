package cz.fs.proto1.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.codehaus.jackson.map.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cz.fs.proto1.model.Snippet;
import cz.fs.proto1.model.Web;

@ApplicationScoped
public class FetchService {

	@Inject
	protected WebManager webManager;
	
	protected ObjectMapper mapper = new ObjectMapper();
	
	public String fetch(Web web) throws IOException {
		return getContent(web);
	}
	
	public String getContent(Web web) throws IOException {
		// is caching enabled
		if(web.getCache()) {
			if(isCacheValid(web)) {
				return web._getCachedValue();
			}else {
				String content = fetchContent(web);
				web._setCachedValue(content);
				web.setLastCacheTime(System.currentTimeMillis());
				
				webManager.update(web);
				return content;
			}
		}else {
			return fetchContent(web); 
		}
	}
	
	protected String fetchContent(Web web) throws IOException {
		// TODO: use JAX-RS client instead!!
		return Jsoup.connect(web.getUrl()).get().html();
	}
	
	protected boolean isCacheValid(Web web) {
		return System.currentTimeMillis() <= cacheExpiresAt(web);
	}
	
	protected long cacheExpiresAt(Web web) {
		return web.cacheExpirationTime();
	}

	// TODO: cache json to avoid parsing
	public String getSnippets(Web web) throws IOException {
		Document doc = Jsoup.parse(getContent(web));
	
		Map<String, Object> map = new HashMap<>();
		for(Snippet snippet : web.getSnippets()) {
			Elements els = doc.select(snippet.getSelector());
			
			if(els.size() > 1) {
				List<String> list = new LinkedList<>();
				for(Element el : els) {
					list.add(el.html());
				}
				map.put(snippet.getName(), list);
			}else {
				map.put(snippet.getName(), els.html());
			}
		}
		
		return mapper.writeValueAsString(map);
	}
	
}
