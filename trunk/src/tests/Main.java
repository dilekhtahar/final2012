package test;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

public class Main {
	private static Connection con = null;
	private static final String DB_CONNECTIONSTRING = "jdbc:mysql://localhost:3306/world";
	private static final String DB_USERNAME = "root";
	private static final String DB_PASSWORD = "alex";
	public static final int maxNR = 100;

	public static void main(String[] args) {

		ArrayList<String> tables = new ArrayList<String>();

		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(DB_CONNECTIONSTRING, DB_USERNAME,
					DB_PASSWORD);

			Writer out = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream("database.rdf"), "UTF-8"));

			DatabaseMetaData mdd = con.getMetaData();
			ResultSet rss = mdd.getTables(null, null, "%", null);
			while (rss.next()) {
				// System.out.println(rss.getString(3));
				tables.add(rss.getString(3));
			}

			// Create an empty Model
			Model model = ModelFactory.createDefaultModel();

			for (String table : tables) {

				System.out.println(table);
				

				String query = "Select * FROM " + table;
				String value = "";

				Statement stmt = con.createStatement();

				ResultSet rs = stmt.executeQuery(query);
				ResultSetMetaData md = rs.getMetaData();

				int col = md.getColumnCount();
				int maxNr = 0;
				
				while (rs.next() && maxNr <= maxNR) {
					
					// System.out.println("<city:record>");
					// out.write("<"+table+":record>\n");
					for (int i = 1; i <= col; i++) {

						String col_name = md.getColumnName(i);
						value = rs.getString(i);
						// System.out.println(value);
						// System.out.println("\t<city:"+col_name+">"+
						// value+"</city:"+col_name+">");
						// out.write("\t<"+table+":" + col_name + ">" + value
						// + "</"+table+":" + col_name + ">\n");

						
						
						if (value != null) {
							String nsA = "http://nsa.org/" + table + "#";
						//	model.setNsPrefix(table, nsA);
							// Create properties for the different types of
							// relationship to represent
						//	Resource res = model.createResource(nsA + table);
						//	Property column = model.createProperty(nsA
					//				+ col_name);
						//	com.hp.hpl.jena.rdf.model.Statement statement1 = model
						//			.createStatement(res, column, value);
						//	model.add(statement1);
						//	System.out.println("Stm added");
						}
					}

					// System.out.println("</city:record>");
					// out.write("</"+table+":record>\n");
					maxNr++;
				}
			}
			String nsA = "http://nsa.org/table#";
	

			String table = "t1";
			String table1 = "t2";

			model.setNsPrefix(table, nsA);
			Property p = model.createProperty(nsA + "column");
			Property p1 = model.createProperty(nsA + "column1");
			Resource res = model.createResource(nsA + table).addProperty(p, "o");
			Resource res1 = model.createResource(nsA + table).addProperty(p, "o1");
			
			model.setNsPrefix(table1, nsA);
			Property pp = model.createProperty(nsA + "column2");
			Property pp1 = model.createProperty(nsA + "column3");
			Resource rres = model.createResource(nsA + table1).addProperty(p, "o3");
			Resource rres1 = model.createResource(nsA + table1).addProperty(p, "o4");

			// model.write(System.out);
			model.write(out);

			out.close();

		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
