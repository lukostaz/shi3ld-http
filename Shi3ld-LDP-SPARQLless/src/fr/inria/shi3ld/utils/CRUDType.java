package fr.inria.shi3ld.utils;

import fr.inria.shi3ld.Config;

public enum CRUDType {
	CREATE(Config.s4acPrefixURI + "Create"),
	READ(Config.s4acPrefixURI + "Read"),
	UPDATE(Config.s4acPrefixURI + "Update"),
	DELETE(Config.s4acPrefixURI + "Delete"),
	UNKNOWN("");
	
	private String privilegeURI;
	
	CRUDType(String privilegeURI){
		this.privilegeURI = privilegeURI;
	}
	
	public String getPrivilegeURI(){
		return this.privilegeURI;
	}
}
