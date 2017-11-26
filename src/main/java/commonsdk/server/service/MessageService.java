package commonsdk.server.service;

import commonsdk.server.dto.LoginDTO;
import commonsdk.server.dto.MessageDTO;
import commonsdk.server.model.Message;

import commonsdk.server.dto.TransferRequestDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MessageService {

    Page<MessageDTO> findMessages(Pageable pageable);

    MessageDTO getMessage(Long id);

    void updateMessage(MessageDTO messageDTO);

    void saveMessage(MessageDTO messageDTO);

    void deleteMessage(Long id);

    Message transferMoney(TransferRequestDTO transferRequestDTO);

    Message validateUser(LoginDTO login);

    Message getUserDetails(String id);
}
