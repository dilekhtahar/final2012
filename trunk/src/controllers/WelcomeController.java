package controllers;

import jenamodule.QueryExecModule;
import sql2sparql.SELECTStatementProcessor;

public class WelcomeController {

	public String sparqlize(String sqlQuery) {
		return SELECTStatementProcessor.processQuery(sqlQuery);
	}

	public String queryEndpoint(String sparqlQuery) {
		return QueryExecModule.queryDbpedia(sparqlQuery);
	}
}
