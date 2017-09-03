// Copyright (C) Maxime MORGE 2017
package hedonic

import org.scaia.hedonic.{Game, Player, Rlc}

/**
  * Example from "NP-completeness in hedonic games" Coralio Ballester. GEB 49:1 pp 1-30, 2004.
  *
  */
object NPCompletenessExample {

  val player1 = new Player("1")
  val player2 = new Player("2")
  val player3 = new Player("3")

  player1.rlc=new Rlc(Array(Set(Set("1","3")), Set(Set("1","2","3")), Set(Set("1"))))
  player2.rlc=new Rlc(Array(Set(Set("1","2")), Set(Set("1","2","3")), Set(Set("2"))))
  player3.rlc=new Rlc(Array(Set(Set("1","3")), Set(Set("1","2","3")), Set(Set("3"))))

  val g=new Game(Set(player1, player2, player3))

}