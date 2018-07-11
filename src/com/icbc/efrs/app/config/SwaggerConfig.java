package com.icbc.efrs.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
@EnableWebMvc
@ComponentScan(basePackages = { "com.icbc.efrs.app.controller" })
public class SwaggerConfig {
	@Bean
	public Docket customDocket() {
		//
		return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo());
	}

	private ApiInfo apiInfo() {
		Contact contact = new Contact("efrs",
				"https://efrs.icbc.com.cn/icbc/efrs", "efrs@icbc.com.cn");
		return new ApiInfo("EFRS移动端API接口",// 大标题 title
				"EFRS移动端API接口",// 小标题
				"0.0.1",// 版本
				"efrs",// termsOfServiceUrl
				contact,// 作者
				"",// 链接显示文字
				""// 网站链接
		);
	}
	
}