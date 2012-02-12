package sql2sparql;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class SQL2SPARQLConsts {
	public final static Map<String, String> NAMESPACES;

	public static final String DEFAULT_NS = "_default";
	static {
		Map<String, String> aMap = new HashMap<String, String>();
		aMap.put("foaf", "http://xmlns.com/foaf/0.1/");
		aMap.put("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
		aMap.put("dc", "http://purl.org/dc/elements/1.1/");
		aMap.put("dbpedia", "http://dbpedia.org/resource/");
		aMap.put("dbpprop", "http://dbpedia.org/property/");
		aMap.put("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
		aMap.put(DEFAULT_NS, "http://www.example.com");
		NAMESPACES = Collections.unmodifiableMap(aMap);
	}
}