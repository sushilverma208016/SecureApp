package commonsdk.server.repository;

import commonsdk.server.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long>, MessageRepositoryCustom {

}
