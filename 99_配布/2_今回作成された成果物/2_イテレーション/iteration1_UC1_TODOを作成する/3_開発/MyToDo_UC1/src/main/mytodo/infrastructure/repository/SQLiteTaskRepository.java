package mytodo.infrastructure.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import mytodo.domain.model.Task;
import mytodo.domain.repository.TaskRepository;

public class SQLiteTaskRepository implements TaskRepository {

	private final Connection connection;

	public SQLiteTaskRepository(Connection connection) {
		this.connection = connection;
		initTable();
	}

	// テーブルが存在しない場合は作成
	private void initTable() {
		try (Statement stmt = connection.createStatement()) {

			// マスタテーブル
			stmt.execute("""
					CREATE TABLE IF NOT EXISTS priorities (
					    id   INTEGER PRIMARY KEY,
					    name TEXT    NOT NULL UNIQUE
					)""");
			stmt.execute("""
					CREATE TABLE IF NOT EXISTS statuses (
					    id   INTEGER PRIMARY KEY,
					    name TEXT    NOT NULL UNIQUE
					)""");

			// マスタデータを初期投入（既に存在する場合は無視）
			stmt.execute("""
					INSERT OR IGNORE INTO priorities (id, name)
					VALUES (1,'HIGH'),(2,'MEDIUM'),(3,'LOW')""");
			stmt.execute("""
					INSERT OR IGNORE INTO statuses (id, name)
					VALUES (1,'TODO'),(2,'DONE')""");

			// メインテーブル
			stmt.execute("""
					CREATE TABLE IF NOT EXISTS tasks (
					    id          INTEGER PRIMARY KEY AUTOINCREMENT,
					    title       TEXT    NOT NULL,
					    content     TEXT,
					    due_date    TEXT,
					    priority_id INTEGER NOT NULL REFERENCES priorities(id),
					    status_id   INTEGER NOT NULL REFERENCES statuses(id),
					    created_at  TEXT    NOT NULL
					)""");
		} catch (SQLException e) {
			throw new RuntimeException("テーブル作成失敗", e);
		}
	}

	private void insert(Task task) {
		String sql = """
				INSERT INTO tasks
				  (title, content, due_date, priority_id, status_id, created_at)
				VALUES (?,?,?,
				  (SELECT id FROM priorities WHERE name=?),
				  (SELECT id FROM statuses   WHERE name=?),
				  ?)""";
		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.setString(1, task.getTitle());
			stmt.setString(2, task.getContent());
			stmt.setString(3, task.getDueDate() != null
					? task.getDueDate().toString()
					: null);
			stmt.setString(4, task.getPriority().name());
			stmt.setString(5, task.getStatus().name());
			stmt.setString(6, task.getCreatedAt().toString());
			stmt.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException("保存失敗", e);
		}
	}

	@Override
	public void save(Task task) {
		if (task.getId() == null) { // タスクがないなら
			insert(task); // 新規作成
		} else { // タスクがあるなら
			// 既存タスクを更新。UC1では未実装
		}

	}
}
