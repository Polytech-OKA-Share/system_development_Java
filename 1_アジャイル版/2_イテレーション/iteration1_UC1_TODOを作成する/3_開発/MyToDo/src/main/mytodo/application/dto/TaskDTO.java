package mytodo.application.dto;
import mytodo.domain.model.Status;

public record TaskDTO(
	String title,
	Status status
){}
