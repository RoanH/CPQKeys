package dev.roanh.cpqkeys;

import java.nio.file.Paths;

import dev.roanh.cpqkeys.algo.Nauty;

public class Main{

	public static void main(String[] args){
		System.out.println("start");
		
		System.load(Paths.get("./native/libnauty.so").toAbsolutePath().toString());
		
		System.out.println("test: " + Nauty.test(10));
		
		Nauty.test();
	}
}
