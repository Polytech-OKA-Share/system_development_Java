package mytodo.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import mytodo.application.dto.TaskDTO;
import mytodo.domain.model.Priority;
import mytodo.domain.model.SearchCriteria;
import mytodo.domain.model.Status;
import mytodo.domain.model.Task;
import mytodo.domain.repository.TaskRepository;

@ExtendWith(MockitoExtension.class)
class GetTasksServiceTest {

    @Mock TaskRepository repository;
    @InjectMocks GetTasksService service;

    @Test
    @DisplayName("findAll()の結果がTaskDTOに変換されて返る")
    void execute_returnsTaskDTOs() {
    	// Arrange: Taskオブジェクト2つを用意
        Task t1 = new Task(1L, "タスクA", "", null,
            Priority.HIGH, Status.TODO, LocalDateTime.now());
        Task t2 = new Task(2L, "タスクB", "", null,
            Priority.LOW, Status.DONE, LocalDateTime.now());
        // デフォルトの検索条件を用意
        SearchCriteria criteria = new SearchCriteria(null, null, null, "");
        // repositoryのスタブ化
        when(repository.findAll(any())).thenReturn(List.of(t1, t2));

        // Act: GetTasksServiceのexecuteを実行し、結果をresultに格納
        List<TaskDTO> result = service.execute(criteria);

        // Asert: resultについて状態の検証
        assertEquals(2, result.size());
        assertEquals("タスクA", result.get(0).title());
        assertEquals(Status.TODO, result.get(0).status());
        assertEquals("タスクB", result.get(1).title());
        assertEquals(Status.DONE, result.get(1).status());
    }

    @Test
    @DisplayName("findAll()が空リストを返したとき空のDTOリストを返す")
    void execute_returnsEmptyList_whenNoTasks() {
    	// Arrange: repositoryのスタブ化。findAllで空のListを返却
        when(repository.findAll(any())).thenReturn(List.of());
        // デフォルトの検索条件を用意
        SearchCriteria criteria = new SearchCriteria(null, null, null, "");
        
        // Act: GetTasksServiceのexecuteを実行し、結果をresultに格納
        List<TaskDTO> result = service.execute(criteria);

        // Assert: resultが空かどうか検証
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("findAll()にcriteriaがそのまま渡される")
    void execute_passesCriteriaToRepository() {
    	// Arrange: repositoryのスタブ化。findAllで空のListを返却
        when(repository.findAll(any())).thenReturn(List.of());
        // デフォルトの検索条件を用意
        SearchCriteria criteria = new SearchCriteria(null, null, null, "");

        // Act: GetTasksServiceのexecuteを実行し、結果をresultに格納
        service.execute(criteria);

        // Assert: criteriaがrepository.saveに正しく渡されたか
        verify(repository, times(1)).findAll(criteria);
    }
}

