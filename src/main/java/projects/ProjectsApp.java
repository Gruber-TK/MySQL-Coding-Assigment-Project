package projects;

import java.sql.DriverManager;

import projects.dao.DbConnection;

public class ProjectsApp {

	public static void main(String[] args) {
	
		DbConnection.getConnection();
		
	}

}
