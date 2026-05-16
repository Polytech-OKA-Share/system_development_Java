package mytodo.application.service;

import mytodo.application.usecase.DeleteCompletedUseCase;
import mytodo.domain.repository.TaskRepository;

public class DeleteCompletedService implements DeleteCompletedUseCase {
    private final TaskRepository repository;
    public DeleteCompletedService(TaskRepository repository) {
        this.repository = repository;
    }
    @Override
    public void execute() {
        repository.deleteCompleted();
    }
}
