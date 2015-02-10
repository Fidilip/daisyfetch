package cz.fs.proto1.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cz.fs.proto1.model.Snippet;

@ApplicationScoped
public class JsoupConverterService {

	protected ObjectMapper mapper = new ObjectMapper();
	
	public String htmlToJson(String html, List<Snippet> snippets) throws JsonGenerationException, JsonMappingException, IOException {
		Document doc = Jsoup.parse(html);
		
		Map<String, Object> jsonMap = new HashMap<>();
		
		// select each snippet
		for(Snippet snippet : snippets) {
			Elements els = doc.select(snippet.getSelector());
			jsonMap.put(snippet.getName(), processElements(els));
		}
		
		return mapper.writeValueAsString(jsonMap);
	}
	
	protected Object processElements(Elements els) {
		if(els.size() > 1) {
			List<Object> list = new LinkedList<>();
			for(Element el : els) {
				list.add( processElements(el) );
			}
			return list;
		}else {
			return processElements(els.get(0));
		}
	}
	
	protected Object processElements(Element el) {
		Elements children = el.children();
		if(children.size() > 1) {
			return processElements(children);
		}else {
			return el.html();
		}
	}
	
}
