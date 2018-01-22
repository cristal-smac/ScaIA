// Copyright (C) Maxime MORGE 2017
package org.scaia.util.asia.example

import org.scaia.asia._

/**
  * Example where the SelectiveSolver with the utilitarian rule and the InclusiveSolver with the egalitarian rule
  * reach different solution
  * */
object DilemmaPref {

  val club = new Activity("a", 2)
  val ball = new Activity("b", 2)

  val blue: Individual = new Individual("1", 4) {
    vMap += ("a" -> .5, "b" -> .25)
    wMap += ("2" -> 1.0, "3" -> -0.5, "4" -> -1.0)
  }

  val cyan: Individual = new Individual("2", 4) {
    vMap += ("a" -> 0.5, "b" -> 0.25)
    wMap += ("1" -> 1.0, "3" -> 0.5, "4" -> -1.0)
  }

  val magenta: Individual = new Individual("3", 4) {
    vMap += ("a" -> 0.5, "b" -> 0.25)
    wMap += ("1" -> 1.0, "2" -> 0.5, "4" -> -1.0)
  }

  val red: Individual = new Individual("4", 4) {
    vMap += ("a" -> 0.5, "b" -> 0.25)
    wMap += ("1" -> 1.0, "2" -> 1.0, "3" -> -1.0)
  }

  /*
  val club = new Activity("club",2)
  val ball = new Activity("ball",2)

  val blue: Individual = new Individual("blue",4) {
    vMap+=("club" -> .5, "ball" -> .25)
    wMap+=("cyan"-> 1.0, "magenta" -> -0.5, "red" -> -1.0)
  }

  val cyan: Individual = new Individual("cyan",4) {
    vMap += ("club" -> 0.5, "ball" -> 0.25)
    wMap += ("blue" -> 1.0, "magenta" -> 0.5, "red"-> -1.0)
  }

  val magenta: Individual = new Individual("magenta",4) {
    vMap+=("club" -> 0.5, "ball" -> 0.25)
    wMap+=("blue" -> 1.0, "cyan" -> 0.5, "red" -> -1.0)
  }

  val red: Individual = new Individual("red",4) {
    vMap+=("club" -> 0.5, "ball" -> 0.25)
    wMap+=("blue" -> 1.0, "cyan" -> 1.0, "magenta" -> -1.0)
  }
  */

  val pb = new IAProblem(Group(blue, cyan, magenta, red), Set(club, ball))

}