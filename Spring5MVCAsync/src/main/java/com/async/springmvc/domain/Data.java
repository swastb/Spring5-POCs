package com.async.springmvc.domain;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Data {

	@Id
	private String id;
	private String dataKey;
	private String value;

	public Data() {
	}

	public Data(String id, String dataKey, String value) {
		this.id = id;
		this.dataKey = dataKey;
		this.value = value;

	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getKey() {
		return dataKey;
	}

	public void setKey(String key) {
		this.dataKey = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		String s = "Data: Key - " + getKey() + ", value - " + getValue();
		return s;
	}
}