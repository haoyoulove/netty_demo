package com.pingan.traffic.test;

import com.pingan.traffic.netty3.ApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yangjing
 */

@RestController
@RequestMapping("/test")
public class TestController {


	@GetMapping("")
	public void testSend(@RequestParam(value = "msg") String msg){

		ApplicationContext.getContext().writeToWeb(msg);

		User user = new User();
		user.setId(1);
	}
}
