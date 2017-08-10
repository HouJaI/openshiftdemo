package com.maxis.legacyRest;

import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Handles requests for the application home page.
 */
@RestController
public class HomeController {

	@RequestMapping("/HomeController")
	public Greet sayHello() {
		
		ArrayList list = new ArrayList();
		
		HashMap abc =new HashMap();
		HashMap bcd =new HashMap();
		
		abc.put("micro", "micro1");
		abc.put("micro2", "micro2");
		abc.put("micro3", "micro3");
		
		bcd.put("service1", "service1");
		bcd.put("service2", "service2");
		bcd.put("service3", "service3");
		
		list.add(abc);
		list.add(bcd);
		
		return new Greet(list);
	}
}

	class Greet {
	public ArrayList message;

	public Greet(ArrayList message) {
		this.message = message;
	}
	// add getter and setter
	
	
}