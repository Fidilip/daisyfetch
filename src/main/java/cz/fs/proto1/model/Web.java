package cz.fs.proto1.model;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@SuppressWarnings("serial")
@JsonIgnoreProperties
// TODO: validations
public class Web implements Serializable {

	public static final int DAY = 24;

	// TODO: pattern
	protected String name;
	protected String url;

	protected String user;
	
	// snippets
	protected boolean snippetsEnabled = false;
	protected List<Snippet> snippets = new LinkedList<>();
	
	// cache
	protected boolean cache = true;
	protected int cacheLeaseTime = 60;	// minutes

	// reporting
	protected boolean reporting = false;
	protected String reportUrl = "";
	protected int reportInterval = DAY;	// hours

	// -------------------------------
	@JsonIgnore
	private String cachedValue = null;
	protected long lastCacheTime = 0L;

	protected long lastReported = 0L;
	
	public Web() {}

	public Web(String name, String url) {
		super();
		this.name = name;
		this.url = url;
	}
	
	public String getUser() {
		return user;
	}

	@JsonProperty
	public void setUser(String user) {
		this.user = user;
	}

	/** 
	 * @return time in millis when cache entry will expire
	 * */
	@JsonIgnore
	public long cacheExpirationTime() {
		// TODO: magic numbers
		return getLastCacheTime() + getCacheLeaseTime() * 60L * 1000L;
	}
	
	@JsonProperty
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@JsonProperty
	public boolean getSnippetsEnabled() {
		return snippetsEnabled;
	}

	public void setSnippetsEnabled(boolean snippetsEnabled) {
		this.snippetsEnabled = snippetsEnabled;
	}
	
	@JsonProperty
	public List<Snippet> getSnippets() {
		return snippets;
	}

	public void setSnippets(List<Snippet> snippets) {
		this.snippets = snippets;
	}

	@JsonProperty
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@JsonProperty
	public boolean getCache() {
		return cache;
	}

	public void setCache(boolean cache) {
		this.cache = cache;
	}

	/** Lease time in minutes
	 *  denotes how long it takes for data to be refreshed in cache */
	@JsonProperty
	public int getCacheLeaseTime() {
		return cacheLeaseTime;
	}

	public void setCacheLeaseTime(int cacheLeaseTime) {
		this.cacheLeaseTime = cacheLeaseTime;
	}

	@JsonProperty
	public boolean getReporting() {
		return reporting;
	}

	public void setReporting(boolean reporting) {
		this.reporting = reporting;
	}

	@JsonProperty
	public String getReportUrl() {
		return reportUrl;
	}

	public void setReportUrl(String reportUrl) {
		this.reportUrl = reportUrl;
	}

	@JsonProperty
	public int getReportInterval() {
		return reportInterval;
	}

	public void setReportInterval(int reportInterval) {
		this.reportInterval = reportInterval;
	}

	@JsonProperty
	public long getLastCacheTime() {
		return lastCacheTime;
	}

	public void setLastCacheTime(long lastCacheTime) {
		this.lastCacheTime = lastCacheTime;
	}

	// Do not send this to client!
	@JsonIgnore
	public String _getCachedValue() {
		return cachedValue;
	}

	@JsonIgnore
	public void _setCachedValue(String cachedValue) {
		this.cachedValue = cachedValue;
	}
	
	@JsonProperty
	public long getLastReported() {
		return lastReported;
	}

	public void setLastReported(long lastReported) {
		this.lastReported = lastReported;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Web other = (Web) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Web [name=" + name + ", url=" + url + "]";
	}

}
