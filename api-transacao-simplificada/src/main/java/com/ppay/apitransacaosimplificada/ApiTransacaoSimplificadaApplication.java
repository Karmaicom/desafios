package com.ppay.apitransacaosimplificada;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ApiTransacaoSimplificadaApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiTransacaoSimplificadaApplication.class, args);
	}

}
