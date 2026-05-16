package mytodo.application.service;

import mytodo.application.command.UpdateCommand;
import mytodo.application.usecase.UpdateTaskUseCase;
import mytodo.domain.model.Task;
import mytodo.domain.repository.TaskRepository;

public class UpdateTaskService implements UpdateTaskUseCase{
	private final TaskRepository repository;

    public UpdateTaskService(TaskRepository repository) {
        this.repository = repository;
    }

    @Override
    public void execute(UpdateCommand command) {
        Task task = repository.findById(command.id());
        if(task == null) {
        	throw new IllegalArgumentException(
                    "タスクが見つかりません：id=" + command.id());
        }
        task.updateDetails(command.title(), command.content(),
                command.dueDate(), command.priority());
        repository.save(task);
    }
}

