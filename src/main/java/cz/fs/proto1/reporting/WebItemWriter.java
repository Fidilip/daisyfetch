package cz.fs.proto1.reporting;

import java.util.List;

import javax.batch.api.chunk.AbstractItemWriter;
import javax.inject.Inject;
import javax.inject.Named;

import cz.fs.proto1.model.Web;
import cz.fs.proto1.service.WebManager;

@Named
public class WebItemWriter extends AbstractItemWriter {

	@Inject
	protected WebManager webManager;
	
	@Override
	public void writeItems(List<Object> items) throws Exception {
		for(Object item : items) {
			if(item instanceof Web) {
				Web web = (Web)item;
				webManager.update(web);
			}
		}
	}
	
}
