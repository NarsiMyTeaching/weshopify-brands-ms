package com.weshopify.platform.features.brands.bean;

import java.io.Serializable;

import lombok.Data;

@Data
public class CategoryBean implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String name;
	
	private String alias;
	
	private String image;
	
	private boolean enabled;
	
	private String allParentIDs;
	
	private CategoryBean parent;
}
