package mytodo.domain.model;

import java.time.LocalDateTime;

/**
 * TODOリストのタスクを表すEntity。
 * 純粋なビジネスロジックを担当する。
 */
public class Task {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime dueDate;
    private Priority priority;
    private Status status;
    private LocalDateTime createdAt;
    
    /**
     * 新規タスク作成用のコンストラクタ。
     * @param title タイトル（必須）
     * @param content 内容
     * @param dueDate 期限
     * @param priority 優先度
     * @throws IllegalArgumentException タイトルが空の場合
     */
    public Task(String title, String content, LocalDateTime dueDate, Priority priority) {
    		validateTitle(title);
    		validateDueDate(dueDate);
    		this.title = title;
        this.content = content;
        this.dueDate = dueDate;
        this.priority = priority;
        this.status = Status.TODO;
        this.createdAt = LocalDateTime.now();
    }

    /**
     * タスクのタイトルがNullや空文字でないか調べる
     * @param title
     * @throws IllegalArgumentException タイトルが空の場合
     */
    private void validateTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("タイトルは必須入力です。");
        }
    }
    
    /**
     * タスクの期限が過去の日付になってないか調べる
     * @param dueDate
     */
    private void validateDueDate(LocalDateTime dueDate) {
        if (dueDate.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException(
            		"期限は現在以降の日付を指定してください。");
        }
    }

	public Long getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public String getContent() {
		return content;
	}

	public LocalDateTime getDueDate() {
		return dueDate;
	}

	public Priority getPriority() {
		return priority;
	}

	public Status getStatus() {
		return status;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
}
