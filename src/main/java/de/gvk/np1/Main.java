package de.gvk.np1;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

import de.gvk.np1.models.Destroyer;
import de.gvk.np1.models.TieFighter;
@Slf4j
public class Main
{
  public static void main(String[] args)
  {
    HibernateSessionManager hibernateSessionManager = new HibernateSessionManager();
    try
    {
      hibernateSessionManager.withinTransaction(session -> {
        Destroyer d1 = DestroyerActions.withTeamNummers(10).get();
        session.persist(d1);
        List<Destroyer> destroyers = session.createQuery("from Destroyer ", Destroyer.class).getResultList();
        //destroyers.forEach(destroyer -> destroyer.getTieFighters().forEach(System.out::println));
      });

      getDestroyer(hibernateSessionManager);
    }catch (Exception e){
      log.error(e.getMessage());
    }
  }

  private static void getDestroyer(HibernateSessionManager hibernateSessionManager)
  {
    hibernateSessionManager.withinTransaction(session -> {
      log.info("######################### ");
      Set<Destroyer> destroyers = session.createQuery("from Destroyer d join fetch d.tieFighters", Destroyer.class)
                                         .stream().collect(Collectors.toSet());
      DestroyerActions.printAllFromDestroyers.accept(destroyers);
    });
  }
}

