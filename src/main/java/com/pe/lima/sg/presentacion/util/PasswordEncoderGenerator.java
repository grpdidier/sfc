package com.pe.lima.sg.presentacion.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncoderGenerator {
	
	public static void generar(String[] args) {

		int i = 0;
		while (i < 5) {
			String password = "123456";
			BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
			String hashedPassword = passwordEncoder.encode(password);

			System.out.println(hashedPassword);
			i++;
		}
		
		@SuppressWarnings("unused")
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

	  }
}
