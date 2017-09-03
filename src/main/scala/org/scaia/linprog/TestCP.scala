// Copyright (C) Maxime MORGE 2017
package org.scaia.linprog

/*
import oscar.cp._


object TestCP  extends CPModel with App {
  val x1 = CPIntVar(0 to 1)
  val x2 = CPIntVar(0 to 1)
  val x3 = CPIntVar(0 to 1)
  val x4 = CPIntVar(0 to 1)
  val x5 = CPIntVar(0 to 1)
  val x6 = CPIntVar(0 to 1)
  val x7 = CPIntVar(0 to 1)
  val x8 = CPIntVar(0 to 1)

  add(x1 + x5 <= 1)
  add(x2 + x6 <= 1)
  add(x3 + x7 <= 1)
  add(x4 + x8 <= 1)
  add(x1 + x2 + x3 + x4 <= 2)
  add(x5 + x6 + x7 + x8 <= 2)


  maximize(
    - x1 + x2 + x2
      //+ .5 * x2
      //+ .5 * x3 + x4 - x5 + .5 * x6 + .5 * x7 - x8
      //- .17 * x1 * x2 + .66  * x1 * x3  + .66  * x1 * x4
      //+ .56 * x2 * x3 + .49 * x2 * x4
      //- .17 * x5 * x6 + .82 * x5 * x7 + .66 * x5 * x8
      //+ .16  * x6 * x7 + .49 * x6 * x8

  )

  search {
    binaryFirstFail(Seq(x1,x2))
  } onSolution {
    println("Solution found, value of x1 in this solution:" + x1.value)
  }

  start(nSols = 1)
}
*/