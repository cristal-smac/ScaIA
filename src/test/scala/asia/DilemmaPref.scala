// Copyright (C) Maxime MORGE 2017
package asia

import org.scaia.asia.{Activity, Group, IAProblem, Individual}

/**
  * Example where the MNSolver with the utilitarian rule and the InclusiveSolver with the egalitarian rule
  * reach different solution
  * */
object DilemmaPref {
  val debug = false

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
  val pb= new IAProblem(Group(blue, cyan, magenta, red), Set(club, ball))

}