package coreservlets;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import base.City;
import bridge.CityBridge;

@ManagedBean(name = "cities")
@RequestScoped
public class CitiesMB {
	private List<City> cities;

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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return cities;
	}
}
