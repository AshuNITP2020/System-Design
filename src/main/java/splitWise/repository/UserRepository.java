package splitWise.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import splitWise.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUserId(String userId);
}

