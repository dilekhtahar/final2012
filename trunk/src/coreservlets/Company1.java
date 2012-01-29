package coreservlets;

import java.sql.SQLException;
import java.util.List;

import javax.faces.bean.*;

import base.City;
import bridge.CityBridge;

@ManagedBean
@RequestScoped
public class Company1 extends Company {
	
	public Company1() {
		super("My-Small-Company.com", new Programmer("Larry", "Ellison",
				"Junior", 34762.52, "SQL", "Prolog", "OCL", "Datalog"),
				new Programmer("Larry", "Page", "Junior", 43941.86, "Java",
						"C++", "Python", "Go"), new Programmer("Steve",
						"Balmer", "Intermediate", 83678.29, "Visual Basic",
						"VB.NET", "C#", "Visual C++", "Assembler"),
				new Programmer("Sam", "Palmisano", "Intermediate", 96550.03,
						"REXX", "CLIST", "Java", "PL/I", "COBOL"),
				new Programmer("Steve", "Jobs", "Intermediate", 103488.80,
						"Objective-C", "AppleScript", "Java", "Perl", "Tcl"));
	}
}
