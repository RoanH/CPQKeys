package dev.roanh.cpqkeys;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import dev.roanh.cpqkeys.algo.Nauty;
import dev.roanh.cpqkeys.algo.Nishe;
import dev.roanh.cpqkeys.algo.Traces;

public class Main{

	public static void main(String[] args){
		try{
			loadNatives();
		}catch(IOException | UnsatisfiedLinkError e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//Nauty.test();
		Traces.test();
		
		//System.out.println("=====================================================");
		
		//Nishe.test();

		System.out.println("test");
	}
	
	private static void loadNatives() throws IOException, UnsatisfiedLinkError{
		for(Path lib : Files.newDirectoryStream(Paths.get("native"))){
			System.out.println("Loading native library: " + lib.getFileName());
			System.load(lib.toAbsolutePath().toString());
		}
	}
}
