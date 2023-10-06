package de.gvk.np1;

import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

import de.gvk.np1.models.Destroyer;
import de.gvk.np1.models.TieFighter;

public class DestroyerActions
{

  public static Consumer<Set<Destroyer>> printAllFromDestroyers =
          destroyers -> destroyers.forEach(destroyer -> destroyer.getTieFighters().forEach(System.out::println));
  public static Supplier<Destroyer> withTeamNummers(int x){
    return () -> {
      Destroyer d11 = new de.gvk.np1.models.Destroyer("D1");
      for (int i = 0; i < x; i++)
      {
        d11.getTieFighters().add(new TieFighter("T" + i, d11));
      }
      return d11;
    };
  }
}
