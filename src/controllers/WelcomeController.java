package controllers;

import sql2sparql.SELECTStatementProcessor;

public class WelcomeController {

	public String sparqlize(String sqlQuery) {
		return SELECTStatementProcessor.processQuery(sqlQuery);
	}

	public void queryEndpoint() {
		// TODO Auto-generated method stub

	}
	// public void buildTriples() {
	// // File file = new File(CommonConstants.rdfOutputFilename);
	// FileWriter fstream;
	// try {
	//
	// String pathToTriples = FacesContext.getCurrentInstance()
	// .getExternalContext().getRealPath("/");
	//
	// fstream = new FileWriter(
	// (pathToTriples + CommonConstants.rdfOutputFilename));
	//
	// BufferedWriter out = new BufferedWriter(fstream);
	// out.write("TestXXXXXXXXXXXXXXXXXXXX");
	// out.close();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// System.out.println("File created successfully.");
	// }

}