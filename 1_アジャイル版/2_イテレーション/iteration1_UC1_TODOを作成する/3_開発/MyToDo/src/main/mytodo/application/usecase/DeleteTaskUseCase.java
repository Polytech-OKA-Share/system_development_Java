package mytodo.application.usecase;
import mytodo.application.command.DeleteCommand;
/**
 * 特定のタスクを削除する
 */
public interface DeleteTaskUseCase {
	void execute(DeleteCommand command);
}