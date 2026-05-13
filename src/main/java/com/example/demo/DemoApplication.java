package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Lớp khởi chạy chính của hệ thống Quản lý Đào tạo.
 * Sử dụng Spring Boot framework để khởi tạo Application Context và Embedded Server (Tomcat).
 */
@SpringBootApplication
public class DemoApplication {
	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}
}
