package com.gradlic.fts.erp;

import com.gradlic.fts.erp.utils.SmsUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication( exclude = {SecurityAutoConfiguration.class})
//@SpringBootApplication
public class ErpApplication {
	private static final int STRENGTH = 12;

	public static void main(String[] args) {
		SpringApplication.run(ErpApplication.class, args);
		//SmsUtils.sendSMS("8723992495", "Hello Workd");
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder(){
		return new BCryptPasswordEncoder(STRENGTH);
	}

}
