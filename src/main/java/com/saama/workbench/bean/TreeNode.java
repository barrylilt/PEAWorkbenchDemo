package com.saama.workbench.bean;

import java.util.ArrayList;
import java.util.List;

public class TreeNode {

	public static class Entry {

		
		private String text;

		public Entry(String name) {
			this.text = name;
		}

		public List<Entry> children;

		public void add(Entry node) {
			if (children == null)
				children = new ArrayList<Entry>();
			children.add(node);
		}
		
		
		
		public boolean contains(String cName) {
			if (children != null) {
				for (Entry cEntry : children) {
					if (cEntry.text.trim().equalsIgnoreCase(cName.trim())) {
						return true;
					}
				}
				return false;
			} else {
				return false;
			}
		}
		public static List<Entry> getChildrenByNodeName(List<Entry> cList,String cName){
			if (cList != null) {
				for (Entry cEntry : cList) {
					if (cEntry.text.trim().equalsIgnoreCase(cName.trim())) {
						return cEntry.children;
					}
				}
				
			}
			return null;
		}
		
		public static Entry getNodeByName(List<Entry> cList,String cName){
			if (cList != null) {
				for (Entry cEntry : cList) {
					if (cEntry.text.trim().equalsIgnoreCase(cName.trim())) {
						return cEntry;
					}
				}
				
			}
			return null;
		}

	}
	
}