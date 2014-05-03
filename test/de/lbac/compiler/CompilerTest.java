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
				{"#Main<<Number x>><<Void>>\n"
						+ "Number b=1+2!\n" 
						+ "Out<<b>>!\n"
						+ "#!","3" + System.lineSeparator()},
				{"#Main<<Number x>><<Void>>\n"
						+ "Number b=1+2*3!\n" 
						+ "Out<<b>>!\n"
						+ "#!","7" + System.lineSeparator()},
				{"#Main<<Number x>><<Void>>\n"
						+ "Number b=(1+2)*3!\n" 
						+ "Out<<b>>!\n"
						+ "#!","9" + System.lineSeparator()},
				{"#Main<<Number x>><<Void>>\n"
						+ "Number b!\n" 
						+ "b = 42!\n" 
						+ "Out<<b>>!\n"
						+ "#!","42" + System.lineSeparator()},
				{"#Main<<Number x>><<Void>>\n"
						+ "Number b=1*2/4+1!\n"
						+ "Out<<b>>!\n"
						+ "#!","1" + System.lineSeparator()},
				{"#Main<<Number x>><<Void>>\n"
						+ "Number b=8/2*4!\n" 
						+ "Out<<b>>!\n"
						+ "#!","16" + System.lineSeparator()},
				{"#Main<<Number x>><<Void>>\n"
						+ "Number b=1+2!\n" 
						+ "Out<<b>>!"
						+ "#!","3" + System.lineSeparator()},
				{"#Main<<Number x>><<Void>>\n"
						+ "Number b=1+2!\n"
						+ "Out<<b>>!\n"
						+ "#!","3" + System.lineSeparator()},
				{"#Main<<Number x>><<Void>>\n"
						+ "Number b = 3!\n"
						+ "if<<b == 3>> \n"
						+ "then: Out<<b>>!\n"
						+ "fi!\n"
						+ "#!", "3" + System.lineSeparator()
				},
				{"#Main<<Number x>><<Void>>"
						+ "Number b = 3!\n"
						+ "Number a = 1!\n"
						+ "if<<b == 2>>\n"
						+ "then: Out<<b>>!\n"
						+ "else: Out<<a>>!\n"
						+ "fi!\n"
						+ "#!", "1" + System.lineSeparator()
				},
				{"#Main<<Number x>><<Void>>\n"
						+ "Number a = 1!"
						+ "do\n"
						+ "Out<<a>>!\n"
						+ "a = a + 1!\n"
						+ "while<<a<4>>\n"
						+ "od!\n"
						+ "#!", "1" + System.lineSeparator() + "2" + System.lineSeparator() + "3" + System.lineSeparator()
				},
				
				{"#Inc<<Number x>><<Number>>\n"
						+ "Number y = x+1!\n"
						+ "return y!\n"
						+ "#!\n"
						+ "#Main<<Number x>><<Void>>\n"
						+ "Number a = 1!\n"
						+ "do\n"
						+ "Out<<a>>!\n"
						+ "a = Inc<<a>>!\n"
						+ "while<<a<4>>\n"
						+ "od!\n"
						+ "#!\n", "1" + System.lineSeparator() + "2" + System.lineSeparator() + "3" + System.lineSeparator()
				},
				{"#Inc<<Number x, Number y>><<Number>>\n"
						+ "x = x+y!\n"
						+ "return x!\n"
						+ "#!\n"
						+ "#Main<<Number x>><<Void>>\n"
						+ "Number a = 1!\n"
						+ "Number b = 1!\n"
						+ "do\n"
						+ "Out<<a>>!\n"
						+ "a = Inc<<a,b>>!\n"
						+ "while<<a<4>>\n"
						+ "od!\n"
						+ "#!\n", "1" + System.lineSeparator() + "2" + System.lineSeparator() + "3" + System.lineSeparator()
				}
				
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
