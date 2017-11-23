package commonsdk.server.mvc;

import commonsdk.server.dto.LoginDTO;
import commonsdk.server.dto.MessageDTO;
import commonsdk.server.model.Message;
import commonsdk.server.dto.TransferRequestDTO;
import commonsdk.server.service.MessageService;
import commonsdk.server.service.MessageServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.inject.Inject;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;


@RestController
@CrossOrigin
@RequestMapping(value = "/api/message")
public class MessageController {

    final static Logger LOG = LoggerFactory.getLogger(MessageController.class);

    @Inject
    MessageService messageService;

    @RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<MessageDTO>> findAllMessage(Pageable pageable, HttpServletRequest req) {
        Page<MessageDTO> page = messageService.findMessages(pageable);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    @RequestMapping(value = "/transfer", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public Message transferMoney(@RequestBody TransferRequestDTO transferRequestDTO) {

        return messageService.transferMoney(transferRequestDTO);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MessageDTO> getMessage(@PathVariable Long id, HttpServletRequest req) {
        MessageDTO message = messageService.getMessage(id);
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    @RequestMapping(value = "", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public void createMessage(@RequestBody MessageDTO messageDTO) {
        messageService.saveMessage(messageDTO);
    }

    @RequestMapping(value = "", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public void updateMessage(@RequestBody MessageDTO messageDTO) {
        messageService.updateMessage(messageDTO);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public void deleteMessage(@PathVariable Long id) {
        messageService.deleteMessage(id);
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public Message loginProcess(@RequestBody LoginDTO login) {
        Message user = messageService.validateUser(login);

        return user;
    }
}


