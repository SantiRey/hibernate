package de.gvk.np1;

import java.util.Properties;
import java.util.function.Consumer;
import java.util.function.Function;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.schema.Action;

import de.gvk.np1.models.Destroyer;
import de.gvk.np1.models.TieFighter;

import static org.hibernate.cfg.AvailableSettings.C3P0_ACQUIRE_INCREMENT;
import static org.hibernate.cfg.AvailableSettings.C3P0_CONFIG_PREFIX;
import static org.hibernate.cfg.AvailableSettings.C3P0_MAX_SIZE;
import static org.hibernate.cfg.AvailableSettings.C3P0_MIN_SIZE;
import static org.hibernate.cfg.AvailableSettings.CURRENT_SESSION_CONTEXT_CLASS;
import static org.hibernate.cfg.AvailableSettings.DIALECT;
import static org.hibernate.cfg.AvailableSettings.DRIVER;
import static org.hibernate.cfg.AvailableSettings.FORMAT_SQL;
import static org.hibernate.cfg.AvailableSettings.HBM2DDL_AUTO;
import static org.hibernate.cfg.AvailableSettings.HIGHLIGHT_SQL;
import static org.hibernate.cfg.AvailableSettings.PASS;
import static org.hibernate.cfg.AvailableSettings.SHOW_SQL;
import static org.hibernate.cfg.AvailableSettings.URL;
import static org.hibernate.cfg.AvailableSettings.USER;
import static org.hibernate.cfg.AvailableSettings.USE_SQL_COMMENTS;
import static org.hibernate.cfg.AvailableSettings.USE_STREAMS_FOR_BINARY;

@Slf4j
public class HibernateSessionManager
{

  private final SessionFactory sessionFactory;

  public HibernateSessionManager()
  {
    Configuration hibernateConfig = configuration();
    hibernateConfig.setProperties(configParams());

    StandardServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(hibernateConfig.getProperties()).build();
    sessionFactory = hibernateConfig.buildSessionFactory(serviceRegistry);
  }

  public void close()
  {
    sessionFactory.close();
  }

  // ############################################################################################################
  // sessions / transactions
  // ############################################################################################################

  public void withinTransaction(@NonNull Consumer<? super Session> c)
  {
    withSession(session -> {
      Transaction transaction = session.beginTransaction();
      try
      {
        c.accept(session);
        transaction.commit();
      }
      catch (Exception e)
      {
        log.error("ups", e);
        transaction.rollback();
      }
      return null;
    });
  }

  public <R> R withinTransaction(@NonNull Function<? super Session, R> f)
  {
    return withSession(session -> {
      Transaction transaction = session.beginTransaction();
      try
      {
        R result = f.apply(session);
        transaction.commit();
        return result;
      }
      catch (Exception e)
      {
        log.error("ups", e);
        transaction.rollback();
        return null;
      }
    });
  }

  private <R> R withSession(@NonNull Function<? super Session, R> f)
  {
    try (Session session = session())
    {
      return f.apply(session);
    }
  }

  @NonNull
  private Session session()
  {
    return sessionFactory.openSession();
  }

  // ############################################################################################################
  // config for factory
  // ############################################################################################################

  @NonNull
  private static Properties configParams()
  {
    Properties properties = new Properties();
    properties.put(USE_STREAMS_FOR_BINARY, "true");
    properties.put(CURRENT_SESSION_CONTEXT_CLASS, "thread");

    // C3P0 connection pool
    properties.put(C3P0_MIN_SIZE, "10");
    properties.put(C3P0_MAX_SIZE, "150");
    properties.put(C3P0_ACQUIRE_INCREMENT, "5");

    // use with caution !!! see https://www.mchange.com/projects/c3p0/#hibernate-specific
    properties.put(C3P0_CONFIG_PREFIX + ".unreturnedConnectionTimeout", "120000");
    properties.put(C3P0_CONFIG_PREFIX + ".debugUnreturnedConnectionStackTraces", "true");

    // old built-in pool doesn't matter
//    properties.put(POOL_SIZE, "100");
    properties.put(HBM2DDL_AUTO, Action.CREATE_DROP);
    properties.put(DIALECT, "org.hibernate.dialect.H2Dialect");
    properties.put(DRIVER, "org.h2.Driver");
    properties.put(URL, "jdbc:h2:./db/hibernate-demo");
    properties.put(USER, "");
    properties.put(PASS, "");
    properties.put(SHOW_SQL, "false");
    properties.put(FORMAT_SQL, "false");
    properties.put(HIGHLIGHT_SQL, "true");
    properties.put(USE_SQL_COMMENTS, "false");
    return properties;
  }

  @NonNull
  private static Configuration configuration()
  {
    Configuration cfg = new Configuration();

    cfg.addAnnotatedClass(Destroyer.class);
    cfg.addAnnotatedClass(TieFighter.class);

    return cfg;
  }
}
