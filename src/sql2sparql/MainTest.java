package sql2sparql;

public class MainTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out
				.println(SELECTStatementProcessor
						.processQuery("Select nume, prenume from tabel GROUP BY nume ORDER by prenume DESC, nume LIMIT 30"));
	}

}
