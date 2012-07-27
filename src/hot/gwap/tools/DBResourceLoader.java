package gwap.tools;

import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.ResourceBundle;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.Locale;
import org.jboss.seam.core.ResourceLoader;

@Scope(ScopeType.STATELESS)
// Enable this to enable the database ResourceLoader
// @Name("org.jboss.seam.core.resourceLoader")
public class DBResourceLoader extends ResourceLoader {

	@Override
	public ResourceBundle loadBundle(String bundleName) {
		return new ResourceBundle() {
			@Override
			public Enumeration<String> getKeys() {
				EntityManager entityManager = (EntityManager) Component
						.getInstance("entityManager");
				Query query = entityManager
						.createNamedQuery("message.keysByLocale");
				query.setParameter("locale", Locale.instance().getLanguage());
				@SuppressWarnings("unchecked")
				List<String> resources = query.getResultList();

				return Collections.enumeration(resources);
			}

			@Override
			protected Object handleGetObject(String key) {
				try {
					EntityManager entityManager = (EntityManager) Component
							.getInstance("entityManager");
					Query query = entityManager
							.createNamedQuery("message.valueByLocaleAndKey");
					query.setParameter("locale", Locale.instance()
							.getLanguage());
					query.setParameter("key", key);
					return query.getSingleResult();
				} catch (NoResultException e) {
					return null;
				}
			}
		};

	}
}
