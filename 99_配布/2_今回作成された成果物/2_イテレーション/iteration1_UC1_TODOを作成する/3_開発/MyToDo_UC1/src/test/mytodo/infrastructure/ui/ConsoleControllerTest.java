package mytodo.infrastructure.ui;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.util.Scanner;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import mytodo.application.command.CreateCommand;
import mytodo.application.usecase.CreateTaskUseCase;

@ExtendWith(MockitoExtension.class)
class ConsoleControllerTest {
	// @Mock でUseCaseのモックを生成
	@Mock
	CreateTaskUseCase createTaskUseCase;

	// テスト用の入力を Scanner に流し込むヘルパー
	private ConsoleController controllerWith(String input) {
		Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
		return new ConsoleController(createTaskUseCase, scanner);
	}

	@Test
	@DisplayName("正常な入力でcreateTask()を呼ぶとexecute()が1回呼ばれる")
	void createTask_callsExecuteOnce() {
		// Arrange：入力を改行区切りで並べる（タイトル・内容・期限・優先度）
		ConsoleController controller = controllerWith("買い物\n牛乳を買う\n2026-12-31\nHIGH\n");
		// Act
		controller.createTask();
		// Assert：execute() がちょうど1回呼ばれたか検証
		verify(createTaskUseCase, times(1)).execute(any(CreateCommand.class));
	}

	@Test
	@DisplayName("空タイトルでcreateTask()を呼ぶとexecute()が一度呼ばれる")
	void createTask_doesNotCallExecuteWhenTitleBlank() {
		// Task.create()がIllegalArgumentExceptionを投げるようにスタブ化
		doThrow(new IllegalArgumentException("タイトルは必須です"))
				.when(createTaskUseCase).execute(any(CreateCommand.class));

		ConsoleController controller = controllerWith("\n内容\n2026-12-31\nLOW\n");
		controller.createTask();

		// execute()は呼ばれているがexceptionをキャッチして終了
		// → ここではexecuteが呼ばれた後にエラー処理されることを確認
		verify(createTaskUseCase, times(1)).execute(any(CreateCommand.class));
	}
	
}
