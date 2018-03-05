// Copyright (C) Maxime MORGE 2017
package org.scaia.solver.asia

import org.scaia.asia.{IAProblem, Matching}

class SocialRule{
  /**
    * Returns a string representation of the social rule
    */
  override def toString: String = this match{
    case Utilitarian => "Utilitarian"
    case Egalitarian => "Egalitarian"
  }
}
case object Utilitarian extends SocialRule
case object Egalitarian extends SocialRule

/**
  * Abstract class for solving an ASIA problem
  */
abstract class ASIASolver(pb: IAProblem) {
  var debug = false

  /**
    * Returns a matching
    */
  def solve() : Matching


}
