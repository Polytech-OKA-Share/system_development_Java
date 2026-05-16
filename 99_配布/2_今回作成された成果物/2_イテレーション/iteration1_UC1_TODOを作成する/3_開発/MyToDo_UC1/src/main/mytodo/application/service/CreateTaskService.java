package mytodo.application.service;

import mytodo.application.command.CreateCommand;
import mytodo.application.usecase.CreateTaskUseCase;
import mytodo.domain.model.Task;
import mytodo.domain.repository.TaskRepository;

public class CreateTaskService implements CreateTaskUseCase {
	private final TaskRepository repository;

	public CreateTaskService(TaskRepository repository) {
		this.repository = repository;
	}

	@Override
	public void execute(CreateCommand command) {
		Task task = new Task(
				command.title(), command.content(),
				command.dueDate(), command.priority());
		repository.save(task);
	}
}
