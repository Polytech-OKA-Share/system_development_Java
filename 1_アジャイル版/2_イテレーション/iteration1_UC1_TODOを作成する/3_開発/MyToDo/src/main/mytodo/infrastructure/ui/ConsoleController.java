package mytodo.infrastructure.ui;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

import mytodo.application.command.CreateCommand;
import mytodo.application.command.DeleteCommand;
import mytodo.application.command.UpdateCommand;
import mytodo.application.dto.TaskDTO;
import mytodo.application.usecase.CreateTaskUseCase;
import mytodo.application.usecase.DeleteCompletedUseCase;
import mytodo.application.usecase.DeleteTaskUseCase;
import mytodo.application.usecase.GetTasksUseCase;
import mytodo.application.usecase.UpdateTaskUseCase;
import mytodo.domain.model.Priority;
import mytodo.domain.model.SearchCriteria;
import mytodo.domain.model.Status;

public class ConsoleController {
	// このコントローラが利用するユースケース（まだ曖昧）
	private final CreateTaskUseCase createTaskUseCase;
	private final GetTasksUseCase getTasksUseCase;
	private final UpdateTaskUseCase editTaskUseCase;
	private final DeleteTaskUseCase deleteTaskUseCase;
	private final DeleteCompletedUseCase deleteCompletedUseCase;
	// CUIアプリなので入力のために必要
	private final Scanner scanner;

	// このコンストラクタによって、ConsoleControllerをnewする瞬間に
	// 具体的なユースケースを実装したクラスが渡される
	public ConsoleController(
			CreateTaskUseCase createTaskUseCase,
			GetTasksUseCase getTasksUseCase,
			UpdateTaskUseCase editTaskUseCase,
			DeleteTaskUseCase deleteTaskUseCase,
			DeleteCompletedUseCase deleteCompletedUseCase,
			Scanner scanner) {
		this.createTaskUseCase = createTaskUseCase;
		this.getTasksUseCase = getTasksUseCase;
		this.editTaskUseCase = editTaskUseCase;
		this.deleteTaskUseCase = deleteTaskUseCase;
		this.deleteCompletedUseCase = deleteCompletedUseCase;
		this.scanner = scanner;
	}

	// UC1: TODOを作成する のUI
	public void createTask() {
		System.out.println("タイトルを入力してください：");
		String title = scanner.nextLine();

		System.out.println("内容を入力してください：");
		String content = scanner.nextLine();

		System.out.println("期限を入力してください（yyyy-MM-dd）：");
		LocalDateTime dueDate = LocalDate.parse(scanner.nextLine()).atStartOfDay();

		System.out.println("優先度を入力してください（HIGH/MEDIUM/LOW）：");
		Priority priority = Priority.valueOf(scanner.nextLine());

		try {
			// タスクを生成するための入力コマンド作成
			CreateCommand cmd = new CreateCommand(title, content, dueDate, priority);
			// ユースケースにコマンド入力（具体的にどうなるかはわかってない）
			createTaskUseCase.execute(cmd);
			System.out.println("タスクを作成しました！");
		} catch (IllegalArgumentException e) {
			System.out.println("エラー：" + e.getMessage());
		}
	}
	
	// UC2：TODOを一覧表示する
	public void displayTasks() {
	// 検索条件なし
	    SearchCriteria criteria = null;
	    // 検索条件なしでタスクを検索できるユースケースを実行
	    List<TaskDTO> tasks = getTasksUseCase.execute(criteria);
	    if (tasks.isEmpty()) {
		System.out.println("タスクがありません。");
		return;
	    }
	    tasks.forEach(t -> System.out.printf("[%s] %s%n", t.status(), t.title()));
	}

	// UC3：TODOを編集する
	public void updateTask() {
	    System.out.println("編集対象のタスクIDを入力してください：");
	    Long id = Long.parseLong(scanner.nextLine());

	    System.out.println("新しいタイトルを入力してください：");
	    String title = scanner.nextLine();

	    System.out.println("新しい内容を入力してください：");
	    String content = scanner.nextLine();

	    System.out.println("新しい期限を入力してください（yyyy-MM-dd）：");
	    LocalDateTime dueDate = LocalDate.parse(scanner.nextLine()).atStartOfDay();

	    System.out.println("新しい優先度を入力してください（HIGH/MEDIUM/LOW）：");
	    Priority priority = Priority.valueOf(scanner.nextLine());

	    try {
	    	editTaskUseCase.execute(
			new UpdateCommand(id, title, content, dueDate, priority));
		System.out.println("タスクを更新しました！");
	    } catch (IllegalArgumentException e) {
		System.out.println("エラー：" + e.getMessage());
	    }
	}

	// UC4：TODOを削除する
	public void deleteTask(Long id) {
	    System.out.println("本当に削除しますか？（yes/no）");
	    boolean confirmed = "yes".equalsIgnoreCase(scanner.nextLine().trim());
	    if (confirmed) {
	   	deleteTaskUseCase.execute(new DeleteCommand(id));
	    	System.out.println("削除しました。");
	    } else {
		System.out.println("キャンセルしました。");
	    }
	}

	// UC5：条件で検索・整理する
	public void searchTasks() {
	    System.out.println("検索キーワードを入力してください（スキップはEnter）：");
	    String keyword = scanner.nextLine();

	    System.out.println("ステータスで絞り込みますか？（DONE/TODO/スキップはEnter）：");
	    String statusInput = scanner.nextLine();
	    Status status = statusInput.isBlank() ? null : Status.valueOf(statusInput);

	    System.out.println("優先度で絞り込みますか？（HIGH/MEDIUM/LOW/スキップはEnter）：");
	    String priorityInput = scanner.nextLine();
	    Priority priority = priorityInput.isBlank() ? null : Priority.valueOf(priorityInput);

	    System.out.println("ソート項目を入力してください（DUE_DATE/PRIORITY/CREATED_AT）：");
	    String sortKey = scanner.nextLine();
	    SearchCriteria criteria = new SearchCriteria(
				keyword.isBlank() ? null : keyword,
				status, priority, sortKey);
	    // 指定された条件で一覧表示
	    getTasksUseCase.execute(criteria)
		.forEach(t -> System.out.printf("[%s] %s%n", t.status(), t.title()));
	}

	// UC6：完了済みTODOを一括削除する
	public void deleteCompletedTasks() {
	    // 完了済みタスクを検索する検索条件の作成
	    SearchCriteria doneTask = new SearchCriteria(null, Status.DONE, null, null);
	    // 完了済みタスクの個数
	    int cnt = getTasksUseCase.execute(doneTask).size();
	    System.out.printf("完了済みタスクが%d件あります。"
				+ "一括削除しますか？（yes/no）%n", cnt);
	    boolean confirmed = "yes".equalsIgnoreCase(scanner.nextLine().trim());
	    if (confirmed) {
		deleteCompletedUseCase.execute();
		System.out.println("一括削除しました。");
	    } else {
		System.out.println("キャンセルしました。");
	    }
	}
}
