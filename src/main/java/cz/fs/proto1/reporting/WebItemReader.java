package cz.fs.proto1.reporting;

import java.io.Serializable;
import java.util.LinkedList;

import javax.batch.api.chunk.AbstractItemReader;
import javax.inject.Inject;
import javax.inject.Named;

import cz.fs.proto1.model.Web;
import cz.fs.proto1.service.WebManager;

@Named
public class WebItemReader extends AbstractItemReader {

	@Inject
	protected WebManager webManager;
	
	private LinkedList<Web> items = new LinkedList<>();
	
	@Override
	public void open(Serializable checkpoint) throws Exception {
		items.addAll(webManager.getAllOfAllUsers());
	}

	@Override
	public Object readItem() throws Exception {
		if(!items.isEmpty()) {
			return items.pop();
		}else {
			return null;
		}
	}

}
