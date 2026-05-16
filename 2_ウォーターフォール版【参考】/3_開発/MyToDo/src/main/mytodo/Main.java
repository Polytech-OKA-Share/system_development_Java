package mytodo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Scanner;

import mytodo.application.service.CreateTaskService;
import mytodo.application.service.DeleteCompletedService;
import mytodo.application.service.DeleteTaskService;
import mytodo.application.service.GetTasksService;
import mytodo.application.service.UpdateTaskService;
import mytodo.domain.repository.TaskRepository;
import mytodo.infrastructure.repository.SQLiteTaskRepository;
import mytodo.infrastructure.ui.ConsoleController;

public class Main {

	public static void main(String[] args) throws Exception {

		Connection connection = DriverManager.getConnection("jdbc:sqlite:todo.db");
		TaskRepository repository = new SQLiteTaskRepository(connection);

		GetTasksService getTasksService = new GetTasksService(repository);
		CreateTaskService createTaskService = new CreateTaskService(repository);
		UpdateTaskService updateTaskService = new UpdateTaskService(repository);
		DeleteTaskService deleteTaskService = new DeleteTaskService(repository);
		DeleteCompletedService deleteCompletedService = new DeleteCompletedService(repository);

		ConsoleController controller = new ConsoleController(
				createTaskService,
				getTasksService,
				updateTaskService,
				deleteTaskService,
				deleteCompletedService,
				new Scanner(System.in));

		controller.start();

		connection.close();
	}
}
