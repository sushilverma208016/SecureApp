package commonsdk.server.repository;

import com.mysema.query.jpa.JPQLQuery;
import com.mysema.query.jpa.impl.JPAQuery;
import commonsdk.server.model.QMessage;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class MessageRepositoryImpl implements MessageRepositoryCustom {

	@PersistenceContext
	private EntityManager em;

	@Override
	public int countCustom() {
		JPQLQuery query = new JPAQuery(em);
		return (int) query.from(QMessage.message).where(QMessage.message.id.isNotNull()).count();
	}

}
