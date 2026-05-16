package mytodo.domain.model;

/**
 * タスクの状態を表す。statusesテーブルに対応するEntity
 */
public enum Status {
	/** 未完了 */
	TODO,
	/** 完了 */
	DONE;
}
