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
		sqlList.add("SELECT p.fname , a.city FROM people p , addresses a	WHERE p.ID_Department = a.ID AND ( p.age > 50 OR p.sex = 'M' ) ORDER BY p.fname DESC , a.city ASC");
		
		sqlList.add("SELECT f.name , d.occupation FROM foaf f , dbp d WHERE f.name LIKE 'escu$' ORDER BY f.name");
		sqlList.add("SELECT * FROM foaf");
		for (String sql : sqlList) {
			System.out.println(sql+"\n");
			System.out.println(SELECTStatementProcessor.processQuery(sql)
					+ "\n\n");

		}
	}
}
