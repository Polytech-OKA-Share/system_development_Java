package mytodo.infrastructure.ui;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

// Domain層のポート
interface UserRepository {
	// idからuserの名前を検索する（BL）
    String findUserNameById(int id);
}

// Application層のサービスクラス
class UserService {
    private final UserRepository repository;
    // サービス利用時に具象クラスをDI
    public UserService(UserRepository repository) {
        this.repository = repository;
    }
    // 入力コマンド(id)からユーザー名を取得する(UC)
    public String getUserName(int id) {
        return repository.findUserNameById(id);
    }
}

@ExtendWith(MockitoExtension.class)
public class PracticeTest {
	@Mock UserRepository mockRepository;

    @Test
    @DisplayName("ユーザ名取得サービスを実行すると、名前が取得できる")
    void getUserName_returnsNameFromRepository() {
        when(mockRepository.findUserNameById(1)).thenReturn("Alice");
        System.out.println(mockRepository.findUserNameById(0));
        System.out.println(mockRepository.findUserNameById(1));
        UserService service = new UserService(mockRepository);

        String result = service.getUserName(1);

        assertEquals("Alice", result);
        verify(mockRepository, times(1)).findUserNameById(1);
    }
}
