package controllers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.faces.context.FacesContext;

public class WelcomeController {
	public void buildTriples() {
		// File file = new File(CommonConstants.rdfOutputFilename);
		FileWriter fstream;
		try {

			String pathToTriples = FacesContext.getCurrentInstance()
					.getExternalContext().getRealPath("/");

			fstream = new FileWriter(
					(pathToTriples + CommonConstants.rdfOutputFilename));

			BufferedWriter out = new BufferedWriter(fstream);
			out.write("TestXXXXXXXXXXXXXXXXXXXX");
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("File created successfully.");
	}
}
