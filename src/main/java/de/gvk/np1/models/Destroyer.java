package de.gvk.np1.models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@ToString
@NoArgsConstructor
@RequiredArgsConstructor
public class Destroyer
{
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private  Long id;

  @Column
  private @NonNull String name;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "destroyer", cascade = CascadeType.ALL)
  @ToString.Exclude
  private @NonNull List<TieFighter> tieFighters = new ArrayList<>();
}
