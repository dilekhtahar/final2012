package beans;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import controllers.WelcomeController;

@ManagedBean(name = "welcome")
@RequestScoped
public class WelcomeMB {
	public String buildTriples() {
		WelcomeController wc = new WelcomeController();
		wc.buildTriples();
		
		return "thisPage";

	}
}
