package cevik.scrum;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ScrumMaster extends TeamMember{

	private String url = "jdbc:mysql://localhost:3306/yazm457hw2";
	private String username = "root";
	private String password = "";
	

	public ScrumMaster(int teamSize, int sprintCount) {
		super(teamSize, "ScrumMaster", sprintCount);
	}

	@Override
	public void operate() {
		 System.out.println("Connecting database ...");

	        try {
	            Connection connection = DriverManager.getConnection(url, username, password);
	            System.out.println("Database connected!");

	            // Veri tabanında yer alan sprint_backlog tablosuna (teamSize-2) kadar yeni task ekler
	            // Bunları product_backlog içerisinden alıp, sprint_backlog'a getirir
	            // Buradaki görevler de geliştiriciler tarafından rastgele seçilir.

	            String moveTasksQuery = "INSERT INTO sprint_backlog (taskname, backlogId, sprintId, priority) " +
	                    "SELECT taskname, backlogId, ?, priority FROM product_backlog ORDER BY RAND() LIMIT ?";

	            try (PreparedStatement preparedStatement = connection.prepareStatement(moveTasksQuery)) {
	                preparedStatement.setInt(1, sprintCount);
	                preparedStatement.setInt(2, teamSize - 2);
	                preparedStatement.executeUpdate();
	            }

	            connection.close();
	            System.out.println("Connection closed!");
	        } catch (SQLException e) {
	            throw new IllegalStateException("Cannot connect the database!", e);
	        }
	    }


	@Override
	public void run() {
		
		for (int i=1;i <= sprintCount; i++){
            System.out.println(threadName + " (sprint"+i+")");
            try {
            	operate();
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        }
		System.out.println(threadName + " bitti...");
	}

}
