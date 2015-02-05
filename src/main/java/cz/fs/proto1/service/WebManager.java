package cz.fs.proto1.service;

import java.io.Serializable;
import java.security.Principal;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;

import org.hibernate.validator.constraints.NotEmpty;
import org.infinispan.Cache;
import org.infinispan.tree.Node;
import org.infinispan.tree.TreeCache;
import org.infinispan.tree.TreeCacheFactory;

import cz.fs.proto1.infinispan.CacheContainerProvider;
import cz.fs.proto1.model.Web;
import cz.fs.proto1.qualifiers.PicketLinkPrincipal;

@SessionScoped
public class WebManager implements Serializable {

	private static final long serialVersionUID = -6880838017179741130L;
	
	public static final String CACHE_NAME = "WebCache";
	
	protected static final String MSG_NO_KEY = "Could not remove non-existing item with key: '%s'";
	
	@Inject @PicketLinkPrincipal
	protected Principal principal;
	
	@Inject
	protected CacheContainerProvider cacheProvider;
	
	public Web add(@Valid Web web) {
		return add(principal.getName(), web);
	}
	
	public Web add(String user, @Valid Web web) {				
		web.setUser(user);
		
		// store the web
		getTreeCache().put(user, web.getName(), web);

		return web;
	}
	
	/** Returns Web named 'name' for currently logged user 
	 * 	@throws NotFoundException when web does not exist
	 * */
	public Web get(@NotEmpty String name) {
		return get(principal.getName(), name);
	}
	
	/** Returns Web for user and name 
	 * 	@throws NotFoundException when web does not exist
	 * */
	public Web get(@NotEmpty String user, @NotEmpty String name) {
		Web web = getTreeCache().get(user, name);
		if(web == null) {
			throw new NotFoundException(String.format(MSG_NO_KEY, name));
		}
		return web;
	}
	
	public void update(@Valid Web web) {
		add(web.getUser(), web);
	}
	
	public void update(String user, @Valid Web web) {
		add(user, web);	// update operation is the same as add
	}
	
	public void remove(@NotEmpty String name) {
		if(getTreeCache().remove(principal.getName(), name) == null) {
			throw new NotFoundException(String.format(MSG_NO_KEY, name));
		}
	}
	
	public Collection<Web> getAll() {
		Node<String, Web> usersData = getTreeCache().getNode(principal.getName());
		if(usersData == null) {
			return new LinkedList<Web>();
		}
		return usersData.getData().values();
	}
	
	public Collection<Web> getAllOfAllUsers() {
		List<Web> webs = new LinkedList<>();
		for(Node<String, Web> node : getTreeCache().getRoot().getChildren()) {
			webs.addAll( node.getData().values() );
		}
		return webs;
	}
	
	public void clearAll() {
		getTreeCache().getRoot().clearData();
	}

	protected Cache<String, Web> getCache() {
		return (Cache<String, Web>) cacheProvider.getCacheContainer().<String, Web>getCache();
	}	
	
	protected TreeCache<String, Web> getTreeCache() {
		TreeCacheFactory tcf = new TreeCacheFactory();
		return tcf.createTreeCache(getCache());
	}
}
