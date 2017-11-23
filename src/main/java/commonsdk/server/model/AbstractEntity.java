package commonsdk.server.model;

import javax.persistence.*;
import java.io.Serializable;

@MappedSuperclass
public class AbstractEntity implements Serializable {

	@Id
	private Long id;

	@Version
	private Integer version;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

}
