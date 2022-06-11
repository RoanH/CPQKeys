package dev.roanh.cpqkeys.algo;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.ProcessBuilder.Redirect;
import java.nio.file.Path;
import java.nio.file.Paths;

import dev.roanh.cpqkeys.Main;

public class Scott{

	
	
	
	
	
	
	public static void test(){
		try{
			startSession();
		}catch(IOException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	private static void startSession() throws IOException{
		Path dir = Paths.get("bindings").resolve("scott");
		ProcessBuilder builder = new ProcessBuilder(Main.PYTHON_COMMAND, dir.resolve("scott.py").toAbsolutePath().toString());
		builder.redirectError(Redirect.INHERIT);
		builder.redirectOutput(Redirect.INHERIT);
		builder.redirectInput(Redirect.PIPE);
		builder.directory(dir.toAbsolutePath().toFile());
		
		Process proc = builder.start();
		PrintWriter out = new PrintWriter(proc.getOutputStream());
		out.println("test 1");
		out.println("test 2");
		out.println("test 3");
		out.println("test 4");
		out.println("test 5");
		out.println("end");
		out.println("test 6");
		out.flush();
		
		try{
			proc.waitFor();
		}catch(InterruptedException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	
}
