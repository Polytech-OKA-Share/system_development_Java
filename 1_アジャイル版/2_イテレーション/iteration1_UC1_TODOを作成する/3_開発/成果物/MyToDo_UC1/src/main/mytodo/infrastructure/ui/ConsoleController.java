package mytodo.infrastructure.ui;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Scanner;

import mytodo.application.command.CreateCommand;
import mytodo.application.usecase.CreateTaskUseCase;
import mytodo.domain.model.Priority;

public class ConsoleController {
	// このコントローラが利用するユースケース（まだ曖昧）
	private final CreateTaskUseCase createTaskUseCase;
	// CUIアプリなので入力のために必要
	private final Scanner scanner;

	// このコンストラクタによって、ConsoleControllerをnewする瞬間に
	// 具体的なユースケースを実装したクラスが渡される
	public ConsoleController(
			CreateTaskUseCase createTaskUseCase,
			Scanner scanner) {
		this.createTaskUseCase = createTaskUseCase;
		this.scanner = scanner;
	}
	
	// アプリケーション起動時のUC
	public void start() {
		while (true) {
			printMenu();
			String input = scanner.nextLine().trim();

			switch (input) {
			case "1" -> this.createTask();
			case "2", "3", "4", "5", "6" -> {
				System.out.println("まだ実装していないUCです。");
			}
			case "9" -> {
				System.out.println("終了します。");
				return;
			}
			default -> System.out.println("1〜6または9を入力してください。");
			}
			System.out.println();
		}
	}
	
	private void printMenu() {
		System.out.println("─────────────────────────");
		System.out.println(" TODOアプリ");
		System.out.println("─────────────────────────");
		System.out.println(" 1. タスクを作成する");
		System.out.println(" 2～6. 未実装UC");
		System.out.println(" 9. 終了");
		System.out.println("─────────────────────────");
		System.out.print("操作を選択してください：");
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
	
}
