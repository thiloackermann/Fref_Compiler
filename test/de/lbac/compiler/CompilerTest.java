package de.lbac.compiler;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

import jasmin.ClassFile;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class CompilerTest {
	private Path tempDir;
	
	@BeforeMethod
	public void createTempDir() throws IOException{
		tempDir = Files.createTempDirectory("compilerTest");
	}
	
	@AfterMethod
	public void deleteTempDir(){
		deleteRecursive(tempDir.toFile());
	}
	
	private void deleteRecursive(File file) {
		if(file.isDirectory()){
			for(File child : file.listFiles()){
				deleteRecursive(child);
			}
		}
		if(!file.delete()){
			throw new Error("Could not Delete file <" + file + ">");
		}
	}

@Test(dataProvider = "provide_code_expectedText")
  public void runningCode_outputExpectedText(String code, String expectedText) throws Exception {
	  // execution
	  String actualOutput = compileAndRun(code);
	  
	  //evaluation
	  Assert.assertEquals(actualOutput, expectedText);
	  
  }

	@DataProvider
	private Object[][] provide_code_expectedText(){
		return new Object[][]{
				{"Number b=1+2!" +
					"Out<<Number b>>!","3" + System.lineSeparator()},
				{"Number b=1+2*3!" +
					"Out<<Number b>>!","7" + System.lineSeparator()},
					{"Number b=(1+2)*3!" +
							"Out<<Number b>>!","9" + System.lineSeparator()},
					{"Number b!" + "b = 42!" +
					"Out<<Number b>>!","42" + System.lineSeparator()},
					{"Number b=1*2/4+1!" +
					"Out<<Number b>>!","1" + System.lineSeparator()},
					{"Number b=8/2*4!" +
					"Out<<Number b>>!","16" + System.lineSeparator()},
					{"Number b=1+2!" +
					"Out<<Number b>>!","3" + System.lineSeparator()},
					{"Number b=1+2!" +
				"Out<<Number b>>!","3" + System.lineSeparator()},
					
				{"Number a=1!",""}
		};
	}

	private String compileAndRun(String code) throws Exception {
		code = Main.compile(new ANTLRInputStream(code));
		ClassFile classFile = new ClassFile();
		classFile.readJasmin(new StringReader(code), "", false);
		Path outputPath = tempDir.resolve(classFile.getClassName() + ".class");
		try(OutputStream out = Files.newOutputStream(outputPath)) {
			classFile.write(out);
		}
		return runJavaClass(tempDir, classFile.getClassName());
	}

	private String runJavaClass(Path dir, String className) throws Exception {
		Process process = Runtime.getRuntime().exec(new String[]{"java", "-cp", dir.toString(), className});
		try(InputStream in = process.getInputStream()){
			Scanner s = new Scanner(in);
			if(s.hasNext())
				return s.useDelimiter("\\A").next();
			return "";
		}
	}
}
