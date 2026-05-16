package mytodo.infrastructure.ui;

import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import mytodo.application.dto.TaskDTO;
import mytodo.application.usecase.GetTasksUseCase;
import mytodo.domain.model.Priority;
import mytodo.domain.model.SearchCriteria;
import mytodo.domain.model.Status;

@ExtendWith(MockitoExtension.class)  // MockitoをJUnit5で使う宣言
class PracticeTest {
	@Mock GetTasksUseCase getTaskUseCase;
	@Test
	void test() {
		// c1は検索条件なし（全件取得）
		SearchCriteria c1 = null;
		// c2は"test"がつき完了済みで優先度が高いタスクを検索
		SearchCriteria c2 = new SearchCriteria(
				"test", Status.DONE, Priority.HIGH, null);
		// c3は検索条件がなしで優先度順に並び変えて取得
		SearchCriteria c3 = new SearchCriteria(
				null, null, null, "優先度");
		// テスト用のTaskDTOを準備
		TaskDTO t1 = new TaskDTO("test task1", Status.DONE);
		TaskDTO t2 = new TaskDTO("タスク２", Status.TODO);
		TaskDTO t3 = new TaskDTO("test task3", Status.DONE);
		TaskDTO t4 = new TaskDTO("サンプル４", Status.TODO);
		// スタブ化する
		when(getTaskUseCase.execute(c1)).thenReturn(List.of(t1, t2, t3, t4));
		when(getTaskUseCase.execute(c2)).thenReturn(List.of(t1, t3));
		when(getTaskUseCase.execute(c3)).thenReturn(List.of(t1, t2, t3, t4));
		// 実行する
		getTaskUseCase.execute(c1);
		getTaskUseCase.execute(c2);
		getTaskUseCase.execute(c3);
		// 正しく1回ずつ呼ばれたか検証
		verify(getTaskUseCase, times(1)).execute(c1);
		verify(getTaskUseCase, times(1)).execute(c2);
		verify(getTaskUseCase, times(1)).execute(c3);
	}
}

