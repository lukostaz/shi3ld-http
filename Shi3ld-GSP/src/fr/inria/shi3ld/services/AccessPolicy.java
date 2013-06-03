/**
 * Shi3ld - Shi3ld for HTTP is an access control module for enforcing 
 * authorization on triple stores. Shi3ld for HTTP protects HTTP operations 
 * on Linked Data and relies on attribute-based access policies.
 *
 * Copyright (C) 2013 Luca Costabello, Serena Villata,
 *  Oscar Rodriguez-Rocha, Fabien Gandon - v1.0
 * *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation; either version 2 of the License, or (at your
 * option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see <http://www.gnu.org/licenses/>.
 */


package fr.inria.shi3ld.services;
import java.util.HashSet;
import java.util.Set;
import fr.inria.shi3ld.Config;
import fr.inria.shi3ld.utils.CRUDType;


public class AccessPolicy {
	private ACSType acstype;
	private String uri;
	private Set<String> graphs;
	private Set<CRUDType> privileges = new HashSet<CRUDType>();
	private Set<String> asks = new HashSet<String>();
	
	public AccessPolicy(String uri, String type, Set<String> graphs) {
		this.uri=uri;
		this.graphs=graphs;
		if(type.equals(Config.s4acPrefixURI + "ConjunctiveAccessConditionSet")) 
			this.acstype = ACSType.CONJUNCTIVE;
		else if(type.equals(Config.s4acPrefixURI + "DisjunctiveAccessConditionSet")) 
			this.acstype = ACSType.DISJUNCTIVE;
	}

	public AccessPolicy(String uri, String type) {
		this(uri, type, null);
	}
	
	public ACSType getAcstype() {
		return acstype;
	}

	public Set<String> getGraph() {
		return graphs;
	}

	public Set<CRUDType> getPrivileges() {
		return privileges;
	}

	public Set<String> getAsks() {
		return asks;
	}

	public String getUri() {
		return uri;
	}
	
	public void setAcstype(ACSType acstype) {
		if(this.acstype==null) this.acstype = acstype;
	}

	public void setGraph(Set<String> graphs) {
		if(this.graphs==null) this.graphs = graphs;
	}

	public void setPrivileges(Set<CRUDType> privileges) {
		this.privileges = privileges;
	}
	
	public void addPrivilege(CRUDType privilege) {
		this.privileges.add(privilege);
	}

	public void setAsks(Set<String> asks) {
		this.asks = asks;
	}
	
	public void addAsk(String ask) {
		this.asks.add(ask);
	}
	
	public void setUri(String uri) {
		this.uri = uri;
	}

	public void addGraph(String g) {
		this.graphs.add(g);
		
	}
}
