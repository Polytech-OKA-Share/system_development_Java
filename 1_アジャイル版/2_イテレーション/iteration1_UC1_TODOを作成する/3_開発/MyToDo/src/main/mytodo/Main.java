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

        // ① Infrastructure：DB接続
        Connection connection = DriverManager.getConnection("jdbc:sqlite:todo.db");

        // ② Domain：Repository実装（Driven Adapter）
        TaskRepository repository = new SQLiteTaskRepository(connection);

        // ④ Application：Service（UseCaseの実装）
        // GetTasksServiceは他のServiceから参照されるので先に作る
        GetTasksService getTasksService = new GetTasksService(repository);

        CreateTaskService     createTaskService     = new CreateTaskService(repository);
        UpdateTaskService     updateTaskService       = new UpdateTaskService(repository);
        DeleteTaskService     deleteTaskService     = new DeleteTaskService(repository);
        DeleteCompletedService deleteCompletedService = new DeleteCompletedService(repository);

        // ⑤ Infrastructure：ConsoleController（Driving Adapter）
        ConsoleController controller = new ConsoleController(
            createTaskService,
            getTasksService,
            updateTaskService,
            deleteTaskService,
            deleteCompletedService,
            new Scanner(System.in)
        );

        // ⑥ メインループ
        run(controller);

        connection.close();
    }

    private static void run(ConsoleController controller) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            printMenu();
            String input = scanner.nextLine().trim();

            switch (input) {
                case "1" -> controller.createTask();
                case "2" -> controller.displayTasks();
                case "3" -> controller.updateTask();
                case "4" -> {
                    System.out.println("削除対象のタスクIDを入力してください：");
                    long id = Long.parseLong(scanner.nextLine());
                    controller.deleteTask(id);
                }
                case "5" -> controller.searchTasks();
                case "6" -> controller.deleteCompletedTasks();
                case "9" -> {
                    System.out.println("終了します。");
                    return;
                }
                default  -> System.out.println("1〜6または9を入力してください。");
            }
            System.out.println();
        }
    }

    private static void printMenu() {
        System.out.println("─────────────────────────");
        System.out.println(" TODOアプリ");
        System.out.println("─────────────────────────");
        System.out.println(" 1. タスクを作成する");
        System.out.println(" 2. タスク一覧を表示する");
        System.out.println(" 3. タスクを編集する");
        System.out.println(" 4. タスクを削除する");
        System.out.println(" 5. 条件で検索・整理する");
        System.out.println(" 6. 完了済みを一括削除する");
        System.out.println(" 9. 終了");
        System.out.println("─────────────────────────");
        System.out.print("操作を選択してください：");
    }
}
