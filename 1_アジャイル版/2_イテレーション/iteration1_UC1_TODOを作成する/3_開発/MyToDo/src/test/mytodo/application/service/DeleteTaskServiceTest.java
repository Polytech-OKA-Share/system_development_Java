package mytodo.application.service;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import mytodo.application.command.DeleteCommand;
import mytodo.domain.repository.TaskRepository;

@ExtendWith(MockitoExtension.class)
class DeleteTaskServiceTest {

    @Mock TaskRepository repository;
    @InjectMocks DeleteTaskService service;

    @Test
    @DisplayName("execute()するとrepository.delete()がIDを引数に1回呼ばれる")
    void execute_callsDeleteWithCorrectId() {
    	// Arrange
    	DeleteCommand command = new DeleteCommand(42L);
    	// Act
        service.execute(command);
        // Assert
        verify(repository, times(1)).delete(42L);
    }

    @Test
    @DisplayName("delete()はsave()やfindById()を呼ばない")
    void execute_doesNotCallOtherMethods() {
    	// Arrange
    	DeleteCommand command = new DeleteCommand(1L);
    	// Act
        service.execute(command);
        // Assert
        verify(repository, never()).save(any());
        verify(repository, never()).findById(any());
    }
}
