// Copyright (C) Maxime MORGE 2017
package asia

import org.scaia.asia._
import org.scaia.solver._

/**
  * Toy example where the MNSolver  does not compute the best utilitarian matching
  * */
object NotBestUtil {
  val a = new Activity("a",2)
  val b = new Activity("b",2)

  val i1: Individual = new Individual("i1",3) {
    vMap+=("a" -> 1, "b" -> 0)
    wMap+=("i2"-> 0, "i3"-> -1)
  }
  val i2: Individual = new Individual("i2",3) {
    vMap+=("a" -> 1, "b" -> 0.9)
    wMap+=("i1" -> 0, "i3" -> 1)
  }

  val i3: Individual = new Individual("i3",3) {
    vMap+=("b" -> 1, "a" -> 0)
    wMap+=("i1" -> -1, "i2"->1)
  }

  val pb= new IAProblem(Group(i1, i2, i3), Set(a,b))

}