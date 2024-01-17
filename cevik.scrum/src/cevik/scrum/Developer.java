package cevik.scrum;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

//Developer.java
public class Developer extends TeamMember {
	
	private static final String url = "jdbc:mysql://localhost:3306/yazm457hw2";
    private static final String username = "root";
    private static final String password = "";


 public Developer(int teamSize, String threadName, int sprintCount) {
     super(teamSize, threadName, sprintCount);
 }

 @Override
 public void run() {
     for (int i = 1; i <= this.sprintCount; i++) {
         System.out.println(threadName + " (sprint" + i + ")");
         try {
             operate();
             Thread.sleep(1000);
         } catch (InterruptedException e) {
             e.printStackTrace();
         }
     }
     System.out.println(threadName + " bitti...");
 }

 @Override
 public void operate() {
	 System.out.println("Connecting database ...");

     try (Connection connection = DriverManager.getConnection(url, username, password)) {
         System.out.println("Database connected!");

         // Veri tabanındaki sprint_backlog tablosundan bir görev alınarak tamamlanacak.
         // Tamamlanan görev veri tabanındaki board tablosuna yazılacak.

         String selectTaskQuery = "SELECT taskname, backlogId, sprintId, priority FROM sprint_backlog WHERE sprintId = ? LIMIT 1";

         try (PreparedStatement preparedStatement = connection.prepareStatement(selectTaskQuery)) {
             preparedStatement.setInt(1, teamSize); // teamSize'ı sprintId olarak kullanıyoruz
             ResultSet resultSet = preparedStatement.executeQuery();

             if (resultSet.next()) {
                 String taskName = resultSet.getString("taskname");
                 int backlogId = resultSet.getInt("backlogId");
                 int sprintId = resultSet.getInt("sprintId");
                 int priority = resultSet.getInt("priority");

                 // Tamamlanan görev veri tabanındaki board tablosuna yazılacak.
                 String insertBoardQuery = "INSERT INTO board (taskname, backlogId, sprintId, developerName, priority) " +
                         "VALUES (?, ?, ?, ?, ?)";

                 try (PreparedStatement boardStatement = connection.prepareStatement(insertBoardQuery)) {
                     boardStatement.setString(1, taskName);
                     boardStatement.setInt(2, backlogId);
                     boardStatement.setInt(3, sprintId);
                     boardStatement.setString(4, threadName);
                     boardStatement.setInt(5, priority);
                     boardStatement.executeUpdate();
                 }
             }
         }

         System.out.println("Connection closed!");
     } catch (SQLException e) {
         throw new IllegalStateException("Cannot connect the database!", e);
     }
 }
}
	
	
