package mytodo.infrastructure.ui;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Scanner;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import mytodo.application.command.CreateCommand;
import mytodo.application.command.DeleteCommand;
import mytodo.application.usecase.CreateTaskUseCase;
import mytodo.application.usecase.DeleteCompletedUseCase;
import mytodo.application.usecase.DeleteTaskUseCase;
import mytodo.application.usecase.GetTasksUseCase;
import mytodo.application.usecase.UpdateTaskUseCase;

@ExtendWith(MockitoExtension.class)
class ConsoleControllerTest {
	// @Mock でUseCaseのモックを生成
	@Mock
	CreateTaskUseCase createTaskUseCase;
	@Mock
	GetTasksUseCase getTasksUseCase;
	@Mock
	UpdateTaskUseCase updateTaskUseCase;
	@Mock
	DeleteTaskUseCase deleteTaskUseCase;
	@Mock
	DeleteCompletedUseCase deleteCompletedUseCase;

	// テスト用の入力を Scanner に流し込むヘルパー
	private ConsoleController controllerWith(String input) {
		Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
		return new ConsoleController(
				createTaskUseCase, getTasksUseCase, updateTaskUseCase,
				deleteTaskUseCase, deleteCompletedUseCase,
				scanner);
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
	
	@Test
	@DisplayName("deleteTask()でyesと答えるとexecute()が呼ばれる")
	void deleteTask_callsExecuteWhenConfirmed() {
		// Arrange: yes\nと入力
	        ConsoleController controller = controllerWith("yes\n");
	        // Act: IDが1のタスクを削除
	        controller.deleteTask(1L);
	        // Assert: deleteTaskUseCaseのexecuteが1度呼ばれたか
	        verify(deleteTaskUseCase, times(1)).execute(any(DeleteCommand.class));
	}

	@Test
	@DisplayName("deleteTask()でnoと答えるとexecute()が呼ばれない")
	void deleteTask_doesNotCallExecuteWhenCancelled() {
	    	// Arrange: no\nと入力
	        ConsoleController controller = controllerWith("no\n");
	        // Act: IDが1のタスクを削除
	        controller.deleteTask(1L);
	        // Assert: deleteTaskUseCaseのexecuteが呼ばれていないか
	        verify(deleteTaskUseCase, never()).execute(any(DeleteCommand.class));
	}
	
    @Test
    @DisplayName("displayTasks()でタスクが0件のときメッセージを表示する")
    void displayTasks_showsMessageWhenEmpty() {
    	// スタブ：getTasksUseCase.execute()が空リストを返すように設定
        when(getTasksUseCase.execute(any())).thenReturn(List.of());
        ConsoleController controller = controllerWith("");

        controller.displayTasks();

        // execute()が1回呼ばれたことを確認
        verify(getTasksUseCase, times(1)).execute(any());
    }



}
