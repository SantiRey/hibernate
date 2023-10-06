package de.gvk.np1.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

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
public class TieFighter
{
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column
  private @NonNull String name;


  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "destroyer_id", nullable = false, foreignKey = @ForeignKey(name = "fk_tiefigther_destroyer"))
  private @NonNull Destroyer destroyer;
}
