package commonsdk.server.dto;


import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Created by mouradzouabi on 04/12/15.
 */
public class MessageDTO extends AbstractDTO {

    String accountnumber;

    String username;

    String password;

    Integer totalbalance;

    String lastaccount;

    public String getLastaccount() {
        return lastaccount;
    }

    public void setLastaccount(String lastaccount) {
        this.lastaccount = lastaccount;
    }

    public String getAccountnumber() {
        return accountnumber;
    }

    public void setAccountnumber(String accountnumber) {
        this.accountnumber = accountnumber;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getTotalbalance() {
        return totalbalance;
    }

    public void setTotalbalance(Integer totalbalance) {
        this.totalbalance = totalbalance;
    }
}
