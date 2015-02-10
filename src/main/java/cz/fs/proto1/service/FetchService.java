package cz.fs.proto1.service;

import java.io.IOException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.jsoup.Jsoup;

import cz.fs.proto1.model.Web;

@ApplicationScoped
public class FetchService {

	@Inject
	protected WebManager webManager;
	
	@Inject
	protected JsoupConverterService jsoupConverter;
	
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
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target(web.getUrl());
		return target.request().get(String.class);
	}
	
	protected boolean isCacheValid(Web web) {
		return System.currentTimeMillis() <= cacheExpiresAt(web);
	}
	
	protected long cacheExpiresAt(Web web) {
		return web.cacheExpirationTime();
	}

	public String fetchSnippets(Web web) throws IOException {
		// is caching enabled
		if(web.getCache()) {
			if(isCacheValid(web)) {
				return web._getCachedJson();
			}else {
				String content = jsoupConverter.htmlToJson(getContent(web), web.getSnippets());
				web._setCachedJson(content);
				webManager.update(web);
				return content;
			}
		}else {
			return fetchContent(web); 
		}
	}
	
}
