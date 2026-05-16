package mytodo.domain;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import mytodo.domain.model.Priority;
import mytodo.domain.model.Status;
import mytodo.domain.model.Task;

class TaskTest {
	@Test
	@DisplayName("各属性が正しくセットされたTaskオブジェクトが生成される")
	void instantiateTask() {
		// 将来の日付を準備（LocalDateTime型）
		LocalDateTime dueDate = LocalDateTime.of(2026, 12, 31, 0, 0);
		// タスクを生成する直前の日時情報
		LocalDateTime before = LocalDateTime.now();
		// UT-01-01に合わせ、titleがTask1、期限を将来の日付とする
		Task t = new Task("Task1", "contents...", dueDate, Priority.HIGH);
		// タスクを生成した直後の日時情報
		LocalDateTime after = LocalDateTime.now();
		
		assertAll(
			// インスタンスが正しく生成されているか検証
			() -> assertNotNull(t),
			// インスタンスの各属性が設定したものと同じか検証
			() -> assertEquals("Task1", t.getTitle()),
			() -> assertEquals("contents...", t.getContent()),
			() -> assertEquals(dueDate, t.getDueDate()),
			() -> assertEquals(Priority.HIGH, t.getPriority()),
			() -> assertEquals(null, t.getId()),
			() -> assertEquals(Status.TODO, t.getStatus()),
			() -> assertFalse(t.getCreatedAt().isBefore(before),
	                "createdAt が生成前の時刻になっている"),
			() -> assertFalse(t.getCreatedAt().isAfter(after),
	                "createdAt が生成後の時刻になっている")
		);
	}
	
	@Test
	@DisplayName("タイトルが空のタスクを生成すると例外が送出される")
	void create_throwsWhenTitleIsBlank() {
		assertThrows(Exception.class, () -> {
			LocalDateTime dueDate = LocalDateTime.of(2026, 12, 31, 0, 0);
			new Task("", "contents...", dueDate, Priority.MEDIUM);
		}, "タイトルが空のタスクが生成されてしまっている");
	}
	
	@Test
	@DisplayName("タイトルがNullのタスクを生成すると例外が送出される")
	void create_throwsWhenTitleIsNull() {
		assertThrows(Exception.class, () -> {
			LocalDateTime dueDate = LocalDateTime.of(2026, 12, 31, 0, 0);
			new Task(null, "contents...", dueDate, Priority.MEDIUM);
		}, "タイトルがNullのタスクが生成されてしまっている");
	}
	
	@Test
	@DisplayName("過去の日付のタスクを生成すると例外が送出される")
	void create_throwsWhenDueDateIsBackword() {
		// 過去の日付を準備
		LocalDateTime past = LocalDateTime.of(1976, 12, 12, 0, 0);
		assertThrows(Exception.class, () -> {
			new Task("Task1", "contents...", past, Priority.MEDIUM);
		}, "期限が過去のタスクが生成されてしまっている");
	}
	
}
