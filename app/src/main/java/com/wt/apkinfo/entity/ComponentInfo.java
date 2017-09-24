package com.wt.apkinfo.entity;

/**
 * Created by kenumir on 20.09.2017.
 *
 */

public class ComponentInfo {

	public String name;
	public String className;

	public ComponentInfo() {}

	public ComponentInfo(String n, String v) {
		name = n;
		className = v;
	}

	@Override
	public String toString() {
		return "{name=" + name + ", class=" + className + "}";
	}
}
