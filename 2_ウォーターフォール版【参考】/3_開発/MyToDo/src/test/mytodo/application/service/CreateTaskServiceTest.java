package mytodo.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import mytodo.application.command.CreateCommand;
import mytodo.domain.model.Priority;
import mytodo.domain.model.Task;
import mytodo.domain.repository.TaskRepository;

@ExtendWith(MockitoExtension.class)
class CreateTaskServiceTest {
	// リポジトリのモック作成
	@Mock
	TaskRepository repository;
	// CreateTaskServiceにrepositoryをDI
	@InjectMocks
	CreateTaskService service;

	// 未来の日付を準備しておく
	private final LocalDateTime dueDate = LocalDateTime.of(2026, 12, 31, 0, 0);

	@Test
	@DisplayName("正常なコマンドでexecute()するとrepository.save()が1回呼ばれる")
	void execute_callsSaveOnce() {
		// Arrange:テストで利用する正常なコマンドを準備
	    CreateCommand command = 
	    		new CreateCommand("タスク", "内容", dueDate, Priority.HIGH);
	    // Act:正常なコマンドを実行
	    service.execute(command);
	    // Assert:serviceが依存するrepositoryのsaveメソッドが1回コールされたか確認
	    verify(repository, times(1)).save(any(Task.class));
	}


	@Test
	@DisplayName("タイトルが空のとき例外が投げられsave()は呼ばれない")
	void execute_throwsWhenTitleBlank_andDoesNotSave() {
		// Arrange:テストで利用する不正なコマンドを準備
		CreateCommand command = new CreateCommand("", "内容", dueDate, Priority.LOW);
		// Act:不正コマンドで実行。例外を投げるためその処理全体を変数に格納
		Executable act = () -> service.execute(command);
		// Assert:actの処理の流れで例外が投げられることを確認
		assertThrows(IllegalArgumentException.class, act);
		// Assert:repository.saveが呼ばれない事を確認
		verify(repository, never()).save(any());
	}

}