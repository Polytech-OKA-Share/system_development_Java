package mytodo.application.usecase;
import mytodo.application.command.CreateCommand;
/**
 * タスクを新規作成する
 */
public interface CreateTaskUseCase {
	void execute(CreateCommand command);
}