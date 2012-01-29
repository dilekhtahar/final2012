package coreservlets;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import base.City;
import bridge.CityBridge;

public class Company {
	private List<City> cities;

	private String companyName;
	private Programmer[] programmers;

	public Company(String companyName, Programmer... programmers) {
		this.companyName = companyName;
		this.programmers = programmers;
	}

	public String getCompanyName() {
		return (companyName);
	}

	public List<Programmer> getProgrammers1() {
		getCities();
		return new ArrayList<Programmer>(Arrays.asList(programmers));
	}

	public List<City> getCities() {
		CityBridge cb = new CityBridge();
		if (cities != null) {
			cities.clear();
		} else {
			cities = new ArrayList<City>();
		}
		try {
			cities.addAll(cb.getCitiesList());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return cities;
	}
}
