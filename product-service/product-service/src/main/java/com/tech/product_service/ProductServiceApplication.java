package com.tech.product_service;

import java.math.BigDecimal;
import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.reactive.function.client.WebClient;

import com.tech.product_service.model.Product;
import com.tech.product_service.repository.ProductRepository;

@SpringBootApplication
public class ProductServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProductServiceApplication.class, args);
	}
	
	 @Bean
	    public WebClient.Builder webClientBuilder() {
	        return WebClient.builder();
	    }
	 /*
	 @Bean
	 public CommandLineRunner loadProductData(ProductRepository productRepository) {
	     return args -> {

	         Product p1 = new Product();
	         p1.setId("P1003");
	         p1.setName("Laptop");
	         p1.setDescription("High performance laptop");
	         p1.setPrice(new BigDecimal("79999"));
	         p1.setSkuCode("SKU789");
	         productRepository.save(p1);

	         Product p2 = new Product();
	         p2.setId("P1004");
	         p2.setName("Basic Smartphone");
	         p2.setDescription("Simple smartphone for daily use");
	         p2.setPrice(new BigDecimal("19999"));
	         p2.setSkuCode("SKU778");
	         productRepository.save(p2);

	         Product p3 = new Product();
	         p3.setId("P1005");
	         p3.setName("Wireless Earbuds");
	         p3.setDescription("Compact wireless earbuds");
	         p3.setPrice(new BigDecimal("1499"));
	         p3.setSkuCode("SKU555");
	         productRepository.save(p3);

	         Product p4 = new Product();
	         p4.setId("P1006");
	         p4.setName("Bluetooth Earphones");
	         p4.setDescription("Neckband style earphones");
	         p4.setPrice(new BigDecimal("1299"));
	         p4.setSkuCode("SKU321");
	         productRepository.save(p4);

	         Product p5 = new Product();
	         p5.setId("P1007");
	         p5.setName("Fast Charger");
	         p5.setDescription("USB fast charging adapter");
	         p5.setPrice(new BigDecimal("999"));
	         p5.setSkuCode("SKU111");
	         productRepository.save(p5);

	         Product p6 = new Product();
	         p6.setId("P1008");
	         p6.setName("Power Bank");
	         p6.setDescription("10000mAh portable power bank");
	         p6.setPrice(new BigDecimal("1299"));
	         p6.setSkuCode("SKU987");
	         productRepository.save(p6);

	         Product p7 = new Product();
	         p7.setId("P1009");
	         p7.setName("Smartwatch");
	         p7.setDescription("Basic fitness smartwatch");
	         p7.setPrice(new BigDecimal("2499"));
	         p7.setSkuCode("SKU654");
	         productRepository.save(p7);
	         
	         Product p8 = new Product();
	         p8.setId("P1010");
	         p8.setName("USB Cable");
	         p8.setDescription("Standard USB charging cable");
	         p8.setPrice(new BigDecimal("299"));
	         p8.setSkuCode("SKU852");
	         productRepository.save(p8);


	         System.out.println("Sample products added to database!");
	     };
	 }

*/
	
}


 