package com.xiaobei.stickynavlayout;


public class Category {
	private String name;
	private String id;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Category() {
		super();
	}
	public Category(String name, String id) {
		super();
		this.name = name;
		this.id = id;
	}
}
