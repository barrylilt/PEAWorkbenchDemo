package com.saama.workbench.bean;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

public class HierarchyNodeAjaxBean {
	private CommonsMultipartFile photo;
	private String nodeId;
	private String name;
	private String code;

	public CommonsMultipartFile getPhoto() {
		return photo;
	}

	public void setPhoto(CommonsMultipartFile photo) {
		this.photo = photo;
	}

	public String getNodeId() {
		return nodeId;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

}
