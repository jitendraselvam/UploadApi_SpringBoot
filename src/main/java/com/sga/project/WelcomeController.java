package com.sga.project;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WelcomeController {

	@RequestMapping("/welcome")
	public String Welcome()
	{
		return "Welcome to UploadAPI";
	}
}
