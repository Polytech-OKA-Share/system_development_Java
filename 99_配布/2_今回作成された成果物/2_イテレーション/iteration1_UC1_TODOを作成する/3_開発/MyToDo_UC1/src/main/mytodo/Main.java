package mytodo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Scanner;

import mytodo.application.service.CreateTaskService;
import mytodo.domain.repository.TaskRepository;
import mytodo.infrastructure.repository.SQLiteTaskRepository;
import mytodo.infrastructure.ui.ConsoleController;

public class Main {

	public static void main(String[] args) throws Exception {

		Connection connection = DriverManager.getConnection("jdbc:sqlite:todo.db");
		TaskRepository repository = new SQLiteTaskRepository(connection);
		CreateTaskService createTaskService = new CreateTaskService(repository);
		
		ConsoleController controller = new ConsoleController(
				createTaskService,
				new Scanner(System.in));

		controller.start();

		connection.close();
	}
}
