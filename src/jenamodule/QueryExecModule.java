package jenamodule;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QueryParseException;
import com.hp.hpl.jena.query.ResultSet;

public abstract class QueryExecModule {
	/**
	 * Returns result as string
	 * 
	 * @param args
	 * @return
	 */
	public static String queryDbpedia(String queryString) {
		// now creating query object
		try {
			Query query = QueryFactory.create(queryString);

			// initializing queryExecution factory with remote service.
			// **this actually was the main problem I couldn't figure out.**
			QueryExecution qexec = QueryExecutionFactory.sparqlService(
					"http://dbpedia.org/sparql", query);

			String result = "";
			try {
				ResultSet results = qexec.execSelect();
				while (results.hasNext()) {
					// QuerySolution binding = results.next();
					// Iterator<String> it = binding.varNames();
					// while (it.hasNext()) {
					// result += it.next() + ":=" + binding.get(it.next()) +
					// ",";
					// }
					result += results.next().toString() + "\n";
				}
			} finally {
				qexec.close();
			}
			if (result.equals("")) {
				return "No entries found";
			}
			return result;
		} catch (QueryParseException qpe) {
			return "PARSING EXCEPTION\n" + qpe;
		}
	}
}
