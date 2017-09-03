// Copyright (C) Maxime MORGE 2017
package hedonic

import org.scaia.hedonic.{Game, Player, Rlc}

/**
  * Example from "The Stability of Hedonic Coalition Structures" Anna Bogomolnaia and Matthew O. Jackson. GEB 38:2 pp 201-230, 2002.
  *
  */
object TwoIsACompagnyThreeIsIrational {

  val player1 = new Player("1")
  val player2 = new Player("2")
  val player3 = new Player("3")

  player1.rlc=new Rlc(Array(Set(Set("1","2")), Set(Set("1","3")), Set(Set("1"))))
  player2.rlc=new Rlc(Array(Set(Set("2","3")), Set(Set("2","1")), Set(Set("2"))))
  player3.rlc=new Rlc(Array(Set(Set("3","1")), Set(Set("3","2")), Set(Set("3"))))

  val g=new Game(Set(player1, player2, player3))

}