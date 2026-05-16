package mytodo.application.usecase;
import mytodo.application.command.UpdateCommand;
/**
 * タスクの詳細情報を更新する
 */
public interface UpdateTaskUseCase {
	void execute(UpdateCommand command);
}