package sql2sparql;

import java.util.ArrayList;

public class MainTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		ArrayList<String> sqlList = new ArrayList<String>();
		sqlList.add("Select nume, prenume from tabel GROUP BY nume ORDER by prenume DESC, nume LIMIT 30");
		sqlList.add("Select nume, prenume " + "FROM tabel "
				+ "WHERE nume='NUME' " + "GROUP BY nume "
				+ "ORDER by prenume DESC, nume " + "LIMIT 30");
		sqlList.add("Select nume, prenume from tabel where nume='Daniel' or nume LIKE 'Daniel' and prenume=Alexandru ");
		sqlList.add("SELECT LastName,FirstName FROM Persons WHERE LastName='Svendson' AND FirstName='Tove' OR FirstName='Ola'");
		sqlList.add("SELECT LastName,FirstName FROM Persons WHERE LastName='Svendson' AND (FirstName='Tove' OR FirstName='Ola')");
		sqlList.add("SELECT LastName,FirstName FROM Persons WHERE (FirstName='Tove' OR FirstName='Ola') OR (FirstName LIKE Sve)");
		sqlList.add("SELECT LastName,FirstName FROM Persons WHERE LastName='Svendson' AND ((FirstName='Tove' OR FirstName='Ola') AND (SecondName LIKE 'Sve'))");
		sqlList.add("SELECT nume,prenume FROM tabela WHERE nume>prenume");
		sqlList.add("SELECT nume FROM tabela WHERE varsta>30");
		//sqlList.add("SELECT persoana FROM tabel WHERE varsta >= SELECT varsta from tabel");
		for (String sql : sqlList) {
			System.out.println(sql+"\n");
			System.out.println(SELECTStatementProcessor.processQuery(sql)
					+ "\n\n");

		}
	}
}
