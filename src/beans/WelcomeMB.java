package beans;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import controllers.WelcomeController;

@ManagedBean(name = "welcome")
@RequestScoped
public class WelcomeMB {
	private String sqlQuery;
	private String sparqlQuery;

	/**
	 * 
	 * @return
	 */
	public void sparqlize() {
		WelcomeController wc = new WelcomeController();
		sparqlQuery = wc.sparqlize(sqlQuery);		
	}

	public void queryEndpoint() {
		WelcomeController wc = new WelcomeController();
		wc.queryEndpoint();

	}

	/**
	 * @return the sqlQuery
	 */
	public String getSqlQuery() {
		return sqlQuery;
	}

	/**
	 * @param sqlQuery
	 *            the sqlQuery to set
	 */
	public void setSqlQuery(String sqlQuery) {
		this.sqlQuery = sqlQuery;
	}

	/**
	 * @return the sparqlQuery
	 */
	public String getSparqlQuery() {
		return sparqlQuery;
	}

	/**
	 * @param sparqlQuery
	 *            the sparqlQuery to set
	 */
	public void setSparqlQuery(String sparqlQuery) {
		this.sparqlQuery = sparqlQuery;
	}
}
