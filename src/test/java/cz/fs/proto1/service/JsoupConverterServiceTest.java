package cz.fs.proto1.service;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import cz.fs.proto1.model.Snippet;

public class JsoupConverterServiceTest {

	protected JsoupConverterService jsoupConverter = new JsoupConverterService();

	@Test
	public void testHtmlToJson() throws JsonGenerationException,
			JsonMappingException, IOException {
		String html = "<table><tr><td>cell <b>1</b></td><td>cell 2</td></table>";
		List<Snippet> snippets = new LinkedList<>();
		snippets.add(new Snippet("row", "tr"));

		String json = jsoupConverter.htmlToJson(html, snippets);
		assertEquals(true, isValidJSON(json));
		assertEquals("{\"row\":[\"cell <b>1</b>\",\"cell 2\"]}", json);
	}

	// source: http://stackoverflow.com/a/10227642/663068
	protected boolean isValidJSON(final String json) {
		boolean valid = false;
		try {
			final JsonParser parser = new ObjectMapper().getJsonFactory()
					.createJsonParser(json);
			while (parser.nextToken() != null) {
			}
			valid = true;
		} catch (JsonParseException jpe) {
			jpe.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		return valid;
	}

}
