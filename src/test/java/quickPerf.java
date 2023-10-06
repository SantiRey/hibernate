import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.quickperf.junit5.QuickPerfTest;
import org.quickperf.jvm.allocation.AllocationUnit;
import org.quickperf.jvm.annotations.ExpectNoHeapAllocation;
import org.quickperf.jvm.annotations.HeapSize;
import org.quickperf.jvm.annotations.JvmOptions;
import org.quickperf.jvm.annotations.MeasureHeapAllocation;
import org.quickperf.jvm.jfr.annotation.ExpectNoJvmIssue;
import org.quickperf.sql.annotation.AnalyzeSql;
import org.quickperf.sql.annotation.DisableSameSelectTypesWithDifferentParamValues;
import org.quickperf.sql.annotation.ExpectMaxSelect;

import de.gvk.np1.DestroyerActions;
import de.gvk.np1.HibernateSessionManager;
import de.gvk.np1.Main;
import de.gvk.np1.models.Destroyer;

@Slf4j
@QuickPerfTest
public class quickPerf
{
  HibernateSessionManager hibernateSessionManager = new HibernateSessionManager();

  @BeforeEach
  public void init(){
    hibernateSessionManager.withinTransaction(session -> {
      Destroyer d1 = DestroyerActions.withTeamNummers(10).get();
      session.persist(d1);
      List<Destroyer> destroyers = session.createQuery("from Destroyer ", Destroyer.class).getResultList();
      //destroyers.forEach(destroyer -> destroyer.getTieFighters().forEach(System.out::println));
    });
  }
  @Test
  @AnalyzeSql
  @ExpectMaxSelect(1)
  @ExpectNoJvmIssue
  @HeapSize(value = 50, unit = AllocationUnit.MEGA_BYTE)
  @JvmOptions("-XX:FlightRecorderOptions=stackdepth=128")
  public void testGetDestroyer(){
    hibernateSessionManager.withinTransaction(session -> {
      log.info("######################### ");
      Set<Destroyer> destroyers = session.createQuery("from Destroyer d", Destroyer.class)
                                         .stream().collect(Collectors.toSet());
      DestroyerActions.printAllFromDestroyers.accept(destroyers);
    });
  }
}
