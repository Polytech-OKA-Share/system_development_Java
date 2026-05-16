package mytodo.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import mytodo.application.command.UpdateCommand;
import mytodo.domain.model.Priority;
import mytodo.domain.model.Status;
import mytodo.domain.model.Task;
import mytodo.domain.repository.TaskRepository;

@ExtendWith(MockitoExtension.class)
class UpdateTaskServiceTest {

	@Mock
	TaskRepository repository;
	@InjectMocks
	UpdateTaskService service;
	private final LocalDateTime dueDate = LocalDateTime.of(2026, 12, 31, 0, 0);

	@Test
	@DisplayName("正常系：findById→save の順で呼ばれる")
	void execute_callsFindByIdThenSave() {
		// Arrange
		Task existing = new Task(
				1L, "旧タイトル", "",
				dueDate, Priority.LOW, Status.TODO, LocalDateTime.now());
		when(repository.findById(1L)).thenReturn(existing);

		UpdateCommand command = new UpdateCommand(1L, "新タイトル", "新内容", dueDate, Priority.HIGH);

		// Act
		service.execute(command);

		// Assert
		var order = inOrder(repository);
		order.verify(repository).findById(1L);
		order.verify(repository).save(any(Task.class));
	}

	@Test
	@DisplayName("存在しないIDのとき例外が投げられsave()は呼ばれない")
	void execute_throwsWhenNotFound_andDoesNotSave() {
		// Arrange
		when(repository.findById(999L)).thenReturn(null);
		UpdateCommand command = new UpdateCommand(999L, "タイトル", "", dueDate, Priority.LOW);

		// Act & Assert（例外検証）
		assertThrows(IllegalArgumentException.class,
				() -> service.execute(command));

		// Assert（副作用の検証）
		verify(repository, never()).save(any());
	}

	@Test
	@DisplayName("更新後のTaskにはコマンドの内容が反映される")
	void execute_savesUpdatedTask() {
		// Arrange
		Task existing = new Task(
				1L, "旧", "",
				dueDate, Priority.LOW, Status.TODO, LocalDateTime.now());
		when(repository.findById(1L)).thenReturn(existing);
		UpdateCommand command = new UpdateCommand(1L, "新タイトル",
				"新内容", dueDate, Priority.HIGH);

		// Act
		service.execute(command);

		// Assert
		verify(repository).save(argThat(t -> "新タイトル".equals(t.getTitle()) &&
				"新内容".equals(t.getContent()) &&
				Priority.HIGH.equals(t.getPriority())));
	}
}
