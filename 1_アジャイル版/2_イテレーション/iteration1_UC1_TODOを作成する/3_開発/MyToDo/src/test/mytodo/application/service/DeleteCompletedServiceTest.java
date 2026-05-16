package mytodo.application.service;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import mytodo.domain.repository.TaskRepository;

@ExtendWith(MockitoExtension.class)
class DeleteCompletedServiceTest {
    @Mock TaskRepository repository;
    @InjectMocks DeleteCompletedService service;
    @Test
    @DisplayName("execute()するとrepository.deleteCompleted()が1回呼ばれる")
    void execute_callsDeleteCompleted() {
    	// Act
        service.execute();
        // Assert
        verify(repository, times(1)).deleteCompleted();
    }
    @Test
    @DisplayName("execute()はdelete(id)やsave()を呼ばない")
    void execute_doesNotCallOtherDeleteMethods() {
    	// Act
        service.execute();
        // Assert
        verify(repository, never()).delete(any());
        verify(repository, never()).save(any());
    }
}
