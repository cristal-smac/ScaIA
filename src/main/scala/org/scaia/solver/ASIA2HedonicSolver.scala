// Copyright (C) Maxime MORGE 2017
package org.scaia.solver

import org.scaia.asia._
import org.scaia.hedonic.Game
import org.scaia.solver.asia.{ASIADualSolver, ASIASolver}
import org.scaia.solver.hedonic.CISSolver

/**
  *  Translate the ASIA problem into an hedonic one , use CISSolver and retranslate
  *  @param pb to solved
  */
class ASIA2HedonicSolver(pb : IAProblem) extends ASIADualSolver(pb){

  override def solve() : Matching= {
    var startingTime = System.nanoTime()
    var hedonic=Game(pb)//call factory
    preSolvingTime = System.nanoTime() - startingTime
    if (debug) println("Hedonic game: "+hedonic)
    val solver= new CISSolver(hedonic)
    val matching= solver.solve()
    if (debug) println("Hedonic mathing: "+matching)
    startingTime = System.nanoTime()
    val result= Matching(matching,pb)
    postSolvingTime = System.nanoTime() - startingTime
    return result
  }
}
