package commonsdk.server.service;

import commonsdk.server.dto.LoginDTO;
import commonsdk.server.dto.MessageDTO;
import commonsdk.server.dto.TransferRequestDTO;
import commonsdk.server.mapper.MessageMapper;
import commonsdk.server.model.Message;
import commonsdk.server.repository.MessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Service
@Transactional
public class MessageServiceImpl implements MessageService {

    final static Logger LOG = LoggerFactory.getLogger(MessageServiceImpl.class);

    @Autowired
    MessageRepository messageRepository;

    @Autowired
    MessageMapper messageMapper;

    @Override
    public Page<MessageDTO> findMessages(Pageable pageable) {
        return messageRepository.findAll(pageable).map(message -> messageMapper.toDTO(message));
    }

    @Override
    public MessageDTO getMessage(Long id) {
        Message message = messageRepository.getOne(id);
        if (message == null) {
            return null;
        } else {
            return messageMapper.toDTO(message);
        }
    }

    @Override
    public void updateMessage(MessageDTO messageDTO) {
        Message message = messageRepository.findOne(messageDTO.getId());
        messageMapper.mapToEntity(messageDTO, message);
    }

    @Override
    public void saveMessage(MessageDTO messageDTO) {
        Message message = messageMapper.toEntity(messageDTO);
        messageRepository.save(message);
    }

    @Override
    public void deleteMessage(Long id) {
        messageRepository.delete(id);
    }

    @Override
    public Message transferMoney(TransferRequestDTO transferRequestDTO) {
        List<Message> fromAccountList = messageRepository.findAll().stream().filter(msg -> msg.getAccountnumber().equals(transferRequestDTO.getFromAccount())).collect(Collectors.toList());

        Message fromAccount = fromAccountList.get(0);
        final long[] id = {0};
        List<Message> toAccountList = messageRepository.findAll().stream().filter(msg -> msg.getAccountnumber().equals(transferRequestDTO.getToAccount())).collect(Collectors.toList());
        messageRepository.findAll().forEach(msg -> {
            if(msg.getId() > id[0]) {
                id[0] = msg.getId();
            }
        });
        Message toAccount;
        if(toAccountList.size()==1) {
            toAccount = toAccountList.get(0);
        }
        else {
            Message newAccount = new Message();
            newAccount.setAccountnumber(transferRequestDTO.getToAccount());
            newAccount.setTotalbalance(0);
            newAccount.setLastaccount("");
            newAccount.setUsername(newAccount.getAccountnumber());
            newAccount.setPassword(newAccount.getAccountnumber());
            newAccount.setId(id[0] + 1);
            toAccount = newAccount;
        }
        fromAccount.setTotalbalance(fromAccount.getTotalbalance() - transferRequestDTO.getAmount());
        toAccount.setTotalbalance(toAccount.getTotalbalance() + transferRequestDTO.getAmount());
        fromAccount.setLastaccount(transferRequestDTO.getToAccount());
        messageRepository.save(fromAccount);
        messageRepository.save(toAccount);
        return fromAccount;
    }


    @Override
    public Message validateUser(LoginDTO login) {
        List<Message> userNameList = messageRepository.findAll().stream().filter(msg -> Objects.equals(msg.getUsername(), login.getUsername())).collect(Collectors.toList());
        if(userNameList.size() != 1) {
            return null;
        }

        Message message = messageRepository.getOne(userNameList.get(0).getId());
        if(!Objects.equals(message.getPassword(), login.getPassword())){
            return null;
        }

        return message;
    }

}
