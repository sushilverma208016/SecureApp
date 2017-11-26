package commonsdk.server.mvc;

import commonsdk.server.dto.LoginDTO;
import commonsdk.server.dto.MessageDTO;
import commonsdk.server.filter.CSRFFilter;
import commonsdk.server.model.Message;
import commonsdk.server.dto.TransferRequestDTO;
import commonsdk.server.service.MessageService;
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
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
@RequestMapping(value = "/api/message")
public class MessageController {

    final static Logger LOG = LoggerFactory.getLogger(MessageController.class);
    public Map<String, String> sessionInfo = new HashMap<>();
    public Map<String, Message> sessionMessage = new HashMap<>();

    @Inject
    MessageService messageService;


    CSRFFilter csrfFilter;

    @RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<MessageDTO>> findAllMessage(Pageable pageable, HttpServletRequest req) {
        Page<MessageDTO> page = messageService.findMessages(pageable);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    @RequestMapping(value = "/transfer", method = RequestMethod.POST)
    public Message transferMoney(@RequestBody TransferRequestDTO transferRequestDTO, HttpServletRequest req) {
        Map.Entry<String,Message> entry = sessionMessage.entrySet().iterator().next();
        String key = entry.getKey();
        Message value = entry.getValue();
        if(value.getAccountnumber().equals(transferRequestDTO.getFromAccount())){
//        if(value.getCsrftoken().equals(transferRequestDTO.getCsrftoken()) && value.getAccountnumber().equals(transferRequestDTO.getFromAccount())) {
            return messageService.transferMoney(transferRequestDTO);
        }
        return null;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MessageDTO> getMessage(@PathVariable Long id, HttpServletRequest req) {
        MessageDTO messageDTO = new MessageDTO();
        try {
           messageDTO = messageService.getMessage(id);
        }
        catch(Exception e) {
            return null;
        };
        if(sessionInfo.containsKey(messageDTO.getUsername()))
            return new ResponseEntity<>(messageDTO, HttpStatus.OK);
        else return null;
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
    public Message loginProcess(@RequestBody LoginDTO login, HttpServletRequest request,HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        //session.setAttribute("username", login.getUsername());

        Message user;
        if(sessionInfo.get(login.getUsername()) == null) {
            user = messageService.validateUser(login);
            if(user != null) {
                session.setAttribute("user", login.getUsername());
                session.setMaxInactiveInterval(30);
                sessionInfo.put(login.getUsername(), session.getId());
                sessionMessage.put(login.getUsername(), user);
//                response.sendRedirect("index.html/?id=6");
            }
        }
        else {
            String id = session.getId();

            if(!id.equals(sessionInfo.get(login.getUsername()))){
                return null;
            }
            else {
                user = sessionMessage.get(login.getUsername());
            }
        }
        System.out.println("User " + user.toString());
        return user;
    }

    @RequestMapping(value = "/downloadCSV/{id}", method = RequestMethod.GET)
    public void downloadCSV(HttpServletResponse response, @PathVariable String id) throws IOException {
        Message user = messageService.getUserDetails(id);
        response.setContentType("text/csv");
        String reportName = "UserAccount.csv";
        response.setHeader("Content-disposition", "attachment;filename=" + reportName);

        ArrayList<String> rows = new ArrayList<String>();
        rows.add("Account Number, Username, Password, Total Balance, Last Account");
        rows.add("\n");
        rows.add(user.getAccountnumber() + "," + user.getUsername() + "," + user.getPassword() + "," + user.getTotalbalance() + "," + user.getLastaccount());
        rows.add("\n");

        Iterator<String> iter = rows.iterator();
        while (iter.hasNext()) {
            String outputString = (String) iter.next();
            response.getOutputStream().print(outputString);
        }
        response.getOutputStream().flush();
    }


}


