package mytodo.domain.repository;

import java.util.List;

import mytodo.domain.model.SearchCriteria;
import mytodo.domain.model.Task;

/**
 * UC実行のための出力ポート
 */
public interface TaskRepository {
	
    // UC1・UC3：新規保存・更新後保存
    void save(Task task);
    
    // UC2・UC5：条件付き一覧取得
    List<Task> findAll(SearchCriteria criteria);

    // UC3：ID指定1件取得
    Task findById(Long id);

    // UC4：ID指定1件削除
    void delete(Long id);

    // UC6：完了済み一括削除
    void deleteCompleted();
}
