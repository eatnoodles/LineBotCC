/**
 * 
 */
package com.cc.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * @author Caleb Cheng
 *
 */
@Entity
@Table(name = "WOW_CHARACTER_MAPPING")
public class WoWCharacterMapping {

	@Id
	@Column(name = "LINE_ID")
	private String lineId;

	@NotNull
	@Column(name = "NAME")
	private String name;

	@NotNull
	@Column(name = "REALM")
	private String realm;

	@NotNull
	@Column(name = "LOCATION")
	private String location;
	
	@Column(name = "LAST_MDFY_DTTM")
	private String lastMdfyDttm;

	public String getLineId() {
		return lineId;
	}

	public void setLineId(String lineId) {
		this.lineId = lineId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRealm() {
		return realm;
	}

	public void setRealm(String realm) {
		this.realm = realm;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getLastMdfyDttm() {
		return lastMdfyDttm;
	}

	public void setLastMdfyDttm(String lastMdfyDttm) {
		this.lastMdfyDttm = lastMdfyDttm;
	}
	
}
