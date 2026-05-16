package mytodo.domain.repository;

import mytodo.domain.model.Task;

/**
 * UC実行のための出力ポート
 */
public interface TaskRepository {
    // UC1：新規保存
    void save(Task task);
}
