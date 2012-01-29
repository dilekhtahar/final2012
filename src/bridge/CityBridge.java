package bridge;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import base.City;

public class CityBridge {

	public List<City> getCitiesList() throws SQLException {
		ConnectionFactory conFac = new ConnectionFactory();

		// get database connection
		Connection con = conFac.getConnection();
		if (con == null) {
			throw new SQLException("Can't get database connection");
		}

		PreparedStatement ps = con
				.prepareStatement("select id, name, countrycode, district, population from city");

		// get customer data from database
		ResultSet result = ps.executeQuery();

		List<City> list = new ArrayList<City>();

		while (result.next()) {
			City city = new City();
			city.setId(result.getLong("id"));
			city.setName(result.getString("name"));
			city.setCountryCode(result.getString("countrycode"));
			city.setDistrict(result.getString("district"));
			city.setPopulation(result.getInt("population"));
			// store all data into a List
			list.add(city);
		}
		return list;
	}
}
