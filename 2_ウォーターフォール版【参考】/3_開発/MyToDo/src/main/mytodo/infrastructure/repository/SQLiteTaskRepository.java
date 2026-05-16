package mytodo.infrastructure.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import mytodo.domain.model.Priority;
import mytodo.domain.model.SearchCriteria;
import mytodo.domain.model.Status;
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
					? task.getDueDate().toString() : null);
			stmt.setString(4, task.getPriority().name());
			stmt.setString(5, task.getStatus().name());
			stmt.setString(6, task.getCreatedAt().toString());
			stmt.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException("保存失敗", e);
		}
	}

	private void update(Task task) {
		String sql = """
				UPDATE tasks SET
				  title       = ?,
				  content     = ?,
				  due_date    = ?,
				  priority_id = (SELECT id FROM priorities WHERE name=?),
				  status_id   = (SELECT id FROM statuses   WHERE name=?)
				WHERE id = ?""";
		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.setString(1, task.getTitle());
			stmt.setString(2, task.getContent());
			stmt.setString(3, task.getDueDate() != null
					? task.getDueDate().toString()
					: null);
			stmt.setString(4, task.getPriority().name());
			stmt.setString(5, task.getStatus().name());
			stmt.setLong(6, task.getId());
			stmt.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException("更新失敗", e);
		}
	}

	@Override
	public void delete(Long id) {
		String sql = "DELETE FROM tasks WHERE id = ?";
		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.setLong(1, id);
			stmt.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException("削除失敗", e);
		}
	}

	@Override
	public void save(Task task) {
		if (task.getId() == null) { // タスクがないなら
			insert(task); // 新規作成
		} else { // タスクがあるなら
			update(task); // 既存タスクを更新
		}
	}

	@Override
	public List<Task> findAll(SearchCriteria criteria) {
		// WHERE 1=1 は常にTRUEなので実質全件検索。
		StringBuilder sql = new StringBuilder("""
			    SELECT t.id, t.title, t.content, t.due_date,
		           p.name AS priority, s.name AS status, t.created_at
		    FROM tasks t
		    JOIN priorities p ON t.priority_id = p.id
		    JOIN statuses   s ON t.status_id   = s.id
		    WHERE 1=1
		""");
		// パラメータ保管用List
		List<Object> params = new ArrayList<>();

		// 検索条件にステータスが指定されたか？
		if (criteria.status() != null) {
			sql.append(" AND status = ?");
			params.add(criteria.status().name());
		}
		// 検索条件に優先度が指定されたか？
		if (criteria.priority() != null) {
			sql.append(" AND priority = ?");
			params.add(criteria.priority().name());
		}
		// 検索条件にタイトルの文字列検索が指定されたか？
		if (criteria.keyword() != null && !criteria.keyword().isBlank()) {
			sql.append(" AND title LIKE ?");
			params.add("%" + criteria.keyword() + "%");
		}

		// 並び替えのキーが指定されたか？（デフォルトはタスクの作成順）
		String order = switch (criteria.sortKey()) {
		case "due_date" -> "due_date ASC";
		case "priority" -> "priority ASC";
		case "created_at" -> "created_at DESC";
		default -> "created_at DESC";
		};
		sql.append(" ORDER BY ").append(order);

		try (PreparedStatement stmt = connection.prepareStatement(sql.toString())) {
			for (int i = 0; i < params.size(); i++) {
				// SQL文の i+1 番目のプレスホルダーにListで保管したパラメータをセット
				stmt.setObject(i + 1, params.get(i));
			}
			// ResultSet型の検索結果をList<Task>に直してリターン
			return toList(stmt.executeQuery());
		} catch (SQLException e) {
			throw new RuntimeException("一覧取得失敗", e);
		}
	}

	@Override
	public Task findById(Long id) {
		String sql = """
				SELECT t.id, t.title, t.content, t.due_date,
				       p.name AS priority, s.name AS status, t.created_at
				FROM tasks t
				JOIN priorities p ON t.priority_id = p.id
				JOIN statuses   s ON t.status_id   = s.id
				WHERE t.id = ?""";
		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.setLong(1, id);
			ResultSet rs = stmt.executeQuery();
			return toTask(rs);
		} catch (SQLException e) {
			throw new RuntimeException("取得失敗", e);
		}
	}

	// ResultSet → Task に変換
	private Task toTask(ResultSet rs) throws SQLException {
		return new Task(
				rs.getLong("id"),
				rs.getString("title"),
				rs.getString("content"),
				rs.getString("due_date") != null
						? LocalDateTime.parse(rs.getString("due_date"))
						: null,
				Priority.valueOf(rs.getString("priority")),
				Status.valueOf(rs.getString("status")),
				LocalDateTime.parse(rs.getString("created_at")));
	}

	// ResultSet → ArrayList<Task> に変換
	private List<Task> toList(ResultSet rs) throws SQLException {
		List<Task> tasks = new ArrayList<>();
		while (rs.next())
			tasks.add(toTask(rs));
		return tasks;
	}

	@Override
	public void deleteCompleted() {
		String sql = """
				DELETE FROM tasks
				WHERE status_id = (
				    SELECT id FROM statuses WHERE name = 'DONE'
				)
				""";
		try (Statement stmt = connection.createStatement()) {
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			throw new RuntimeException("一括削除失敗", e);
		}
	}
}
