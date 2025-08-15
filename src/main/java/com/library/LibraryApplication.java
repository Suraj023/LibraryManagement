package com.library;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class LibraryApplication {

	public static void main(String[] args) {
		try {
			SpringApplication.run(LibraryApplication.class, args);
			System.out.println("Main Methode exicuted------"); 
		}catch(Exception e){
			System.out.println("Exceptions Occured "+e.getMessage()); 
		} 
		
	}

}
