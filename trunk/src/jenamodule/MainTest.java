package jenamodule;

public class MainTest {

	public static void main(String[] args) {

		String queryString = "PREFIX foaf: <http://xmlns.com/foaf/0.1/>"
				+ "\nPREFIX dbp: <http://dbpedia.org/property/>"
				+ "\nSELECT ?nume ?ocupatie" + " WHERE{"
				+ " ?persoana foaf:name ?nume ;"
				+ "          dbp:occupation ?ocupatie. " + "\n}"
				+ "\nORDER BY ?nume LIMIT 7";
		System.out.println("Test:\n"
				+ QueryExecModule.queryDbpedia(queryString));
	}
}
