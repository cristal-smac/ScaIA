// Copyright (C) Maxime MORGE 2017
package org.scaia.solver.asia

import org.scaia.asia.{IAProblem, Matching}

/**
  * Abstract class for solving an ASIA problem by translating the problem/outcome
  */
abstract class ASIADualSolver(pb: IAProblem) extends ASIASolver(pb) {

  var preSolvingTime : Long = 0

  var postSolvingTime : Long = 0

  /**
    * Returns a matching
    */
  def solve() : Matching
}
