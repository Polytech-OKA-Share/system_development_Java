package mytodo.application.service;

import java.util.List;

import mytodo.application.dto.TaskDTO;
import mytodo.application.usecase.GetTasksUseCase;
import mytodo.domain.model.SearchCriteria;
import mytodo.domain.repository.TaskRepository;

public class GetTasksService implements GetTasksUseCase {

	private final TaskRepository repository;

	public GetTasksService(TaskRepository repository) {
		this.repository = repository;
	}

	@Override
	public List<TaskDTO> execute(SearchCriteria criteria) {
		return repository.findAll(criteria).stream()
				.map(t -> new TaskDTO(t.getTitle(), t.getStatus()))
				.toList();
	}
}
