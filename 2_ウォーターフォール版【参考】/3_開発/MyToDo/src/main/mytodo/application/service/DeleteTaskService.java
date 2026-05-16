package mytodo.application.service;

import mytodo.application.command.DeleteCommand;
import mytodo.application.usecase.DeleteTaskUseCase;
import mytodo.domain.repository.TaskRepository;

public class DeleteTaskService implements DeleteTaskUseCase {

    private final TaskRepository repository;

    public DeleteTaskService(TaskRepository repository) {
        this.repository = repository;
    }

    @Override
    public void execute(DeleteCommand command) {
        repository.delete(command.id());
    }
}

