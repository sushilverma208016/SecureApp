package commonsdk.server.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

@Entity
public class Message extends AbstractEntity {

	private static final long serialVersionUID = -6321180910534044216L;

	String accountnumber;

	String lastaccount;

	String username;

	String password;



	Integer totalbalance;


	public String getAccountnumber() {
		return accountnumber;
	}

	public void setAccountnumber(String accountnumber) {
		this.accountnumber = accountnumber;
	}

	public String getLastaccount() {
		return lastaccount;
	}

	public void setLastaccount(String lastaccount) {
		this.lastaccount = lastaccount;
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

	@Override
	public String toString() {
		return "Message{" +
				"accountnumber='" + accountnumber + '\'' +
				", lastaccount='" + lastaccount + '\'' +
				", username='" + username + '\'' +
				", password='" + password + '\'' +
				", totalbalance=" + totalbalance +
				'}';
	}
}
