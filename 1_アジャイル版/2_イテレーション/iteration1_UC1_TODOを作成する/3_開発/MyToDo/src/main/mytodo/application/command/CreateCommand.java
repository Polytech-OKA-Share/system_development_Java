package mytodo.application.command;

import java.time.LocalDateTime;

import mytodo.domain.model.Priority;

public record CreateCommand(
	String title,
	String content,
	LocalDateTime dueDate,
	Priority priority
){}
