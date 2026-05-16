package mytodo.domain.model;

/**
 * タスクの優先度を表す。prioritiesテーブルに対応するEntity
 */
public enum Priority {
	/** 高い優先度 */
    HIGH,
    /** 中程度の優先度 */
    MEDIUM,
    /** 低い優先度 */
    LOW;
}
