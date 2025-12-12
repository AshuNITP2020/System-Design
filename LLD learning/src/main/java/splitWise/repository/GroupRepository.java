package splitWise.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import splitWise.entity.Group;

public interface GroupRepository extends JpaRepository<Group, Long> {
    Group findByGroupId(String groupId);
}

