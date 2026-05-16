package mytodo.application.usecase;
import java.util.List;

import mytodo.application.dto.TaskDTO;
import mytodo.domain.model.SearchCriteria;
/**
 * 条件に合うタスクを取得する
 */
public interface GetTasksUseCase {
	List<TaskDTO> execute(SearchCriteria criteria);
}