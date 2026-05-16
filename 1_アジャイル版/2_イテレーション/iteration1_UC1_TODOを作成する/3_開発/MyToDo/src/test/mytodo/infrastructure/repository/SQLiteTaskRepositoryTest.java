package mytodo.infrastructure.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import mytodo.domain.model.Priority;
import mytodo.domain.model.SearchCriteria;
import mytodo.domain.model.Status;
import mytodo.domain.model.Task;
import mytodo.domain.repository.TaskRepository;

class SQLiteTaskRepositoryTest {
	
	private Connection connection;
    private TaskRepository repository;
    // 未来の日付を準備。テスト用。
    private final LocalDateTime dueDate = LocalDateTime.of(2026, 12, 31, 0, 0);
    
    @BeforeEach
    void setUp() throws Exception {
        // インメモリSQLite：テストごとに新しいDBを作る
        connection = DriverManager.getConnection("jdbc:sqlite::memory:");
        repository = new SQLiteTaskRepository(connection);
    }
    
    @AfterEach
    void tearDown() throws Exception {
        connection.close();
    }

	@Test
	@DisplayName("save()したタスクをfindAll()で取得できる")
	void save_and_findAll() {
		Task task = new Task("テストタスク", "内容", dueDate, Priority.HIGH);
		SearchCriteria criteria = new SearchCriteria(null, null, null, "");
		repository.save(task);
		List<Task> result = repository.findAll(criteria);
		assertEquals(1, result.size());
		assertEquals("テストタスク", result.get(0).getTitle());
		assertEquals(Status.TODO, result.get(0).getStatus());
	}
	
	@Test
    @DisplayName("findById()で存在するタスクを取得できる")
    void findById_returnsTask_whenExists() {
		// -- Arrange --
        Task task = new Task("タスクA", "内容", dueDate, Priority.LOW);
        SearchCriteria criteria = new SearchCriteria(null, null, null, "");
        // -- Act --
        repository.save(task);
        List<Task> r = repository.findAll(criteria);
        long id = r.get(0).getId();
        Task result = repository.findById(id);
        // -- Assert --
        assertEquals("タスクA", result.getTitle());
    }
	
	@Test
    @DisplayName("delete()でタスクを削除するとfindAll()で見つからない")
    void delete_removesTask() {
		// -- Arrange --
		// テスト用のTaskを生成
		Task task = new Task("削除対象", "内容", dueDate, Priority.LOW);
		// デフォルト検索条件の設定
		SearchCriteria criteria = new SearchCriteria(null, null, null, "");
		
		// -- Act --
		// テスト用タスクを保存
        repository.save(task);
        // 全件検索を行い、最初のタスクのidを取得
        long id = repository.findAll(criteria).get(0).getId();
        // 取得したIDのタスクを削除
        repository.delete(id);

        // -- Assert --
        // １件追加してその１件を削除したので、全件検索結果は空のはず
        assertTrue(repository.findAll(criteria).isEmpty());
    }
	
	@Test
    @DisplayName("deleteCompleted()で完了済みのみ削除される")
    void deleteCompleted_removesOnlyCompletedTasks() {
		// 未完了のタスクを生成
		Task todo = new Task("未完了タスク", "", dueDate, Priority.LOW);
		// 完了済みタスクを生成（実際は生成時にこのコンストラクタは使わない）
		Task done = new Task(null, "完了タスク", "", dueDate,
                Priority.LOW, Status.DONE, LocalDateTime.now());
		// デフォルト検索条件の設定
		SearchCriteria criteria = new SearchCriteria(null, null, null, "");
		
		// タスクを保存
        repository.save(todo);
        repository.save(done);
        // 完了済みタスクを削除
        repository.deleteCompleted();
        // タスクを全件検索
        List<Task> tasks = repository.findAll(criteria);
        
        // 2件のタスクのうち1つ消えたので、残るタスクは1件のはず
        assertEquals(1, tasks.size());
        // 1件のタスクのタイトルは"未完了タスク"のはず
        assertEquals("未完了タスク", tasks.get(0).getTitle());
    }

}
