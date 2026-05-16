package mytodo.domain.model;
/**
 * タスク検索・整理のための条件を保持する。
 */
public record SearchCriteria(
		String keyword,
	    Status status,
	    Priority priority,
	    String sortKey
) { }
