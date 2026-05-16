package mytodo.infrastructure.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import mytodo.domain.model.Priority;
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
    @DisplayName("正常なタスクを保存できる")
    void save_and_findAll() throws SQLException {

        // Arrange（準備）
        Task task = new Task("テストタスク", "内容", dueDate, Priority.HIGH);

        // Act（実行）
        repository.save(task);

        // Assert（検証）
        try (PreparedStatement stmt = connection.prepareStatement(
                "SELECT title, content, priority_id, status_id FROM tasks")) {

            ResultSet rs = stmt.executeQuery();
            assertTrue(rs.next());

            assertEquals("テストタスク", rs.getString("title"));
            assertEquals("内容", rs.getString("content"));
            assertEquals(1, rs.getInt("priority_id"));
            assertEquals(1, rs.getInt("status_id"));
        }
    }
    
    

}
