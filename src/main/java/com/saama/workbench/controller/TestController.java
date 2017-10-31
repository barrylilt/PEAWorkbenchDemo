package com.saama.workbench.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.saama.workbench.bean.MapBean;

@Controller
public class TestController {
	
	@RequestMapping("/welcome")
	public String welcome() {
		return "Welcome";
	}
	
	@RequestMapping("/get")
	public @ResponseBody List<MapBean> get() {
		List<MapBean> a = new ArrayList<MapBean>();
		
		for (int i=0; i < 10; i++) {
			MapBean b = new MapBean();
			b.setKey(""+i);
			b.setValue(""+i);
			a.add(b);
		}
		return a;
		
	}

}
