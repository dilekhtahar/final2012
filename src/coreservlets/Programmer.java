package coreservlets;

/** Bean to represent a software developer. 
 *  <p>
 *  From <a href="http://courses.coreservlets.com/Course-Materials/">the
 *  coreservlets.com tutorials on servlets, JSP, Struts, JSF 2.0, Ajax, GWT, 
 *  Spring, Hibernate/JPA, Web Services, and Java programming</a>.
 */

public class Programmer {
  private String firstName, lastName, level;
  private double salary;
  private String[] languages;
  private boolean isLevelEditable;

  public Programmer(String firstName,
                    String lastName,
                    String level,
                    double salary,
                    String... languages) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.level = level;
    this.salary = salary;
    this.languages = languages;
  }

  public String getFirstName() {
    return(firstName);
  }
  
  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return(lastName);
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getLevel() {
    return(level);
  }
  
  public void setLevel(String level) {
    isLevelEditable = false;
    this.level = level;
  }
  
  public double getSalary() {
    return(salary);
  }
  
  public void setSalary(double salary) {
    this.salary = salary;
  }
  
  /** Formats the salary like "$24,567.89". */
  
  public String getFormattedSalary() {
    return(String.format("$%,.2f", salary));
  }
  
  public String[] getLanguages() {
    return(languages);
  }
  
  /** Returns a String like "Java, C++, and Lisp".
   *  That is, it puts commas and the word "and" into list.
   */
  public String getLanguageList() {
    StringBuilder langList = new StringBuilder();
    for(int i=0; i<languages.length; i++) {
      if(i < (languages.length-1)) {
        langList.append(languages[i] + ", ");
      } else {
        langList.append("and " + languages[i]);
      }
    }
    return(langList.toString());
  }
  
  public boolean isLevelEditable() {
    return(isLevelEditable);
  }

  public void setLevelEditable(boolean isLevelEditable) {
    this.isLevelEditable = isLevelEditable;
  }
}
  
