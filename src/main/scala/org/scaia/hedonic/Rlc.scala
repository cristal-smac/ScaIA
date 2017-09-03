// Copyright (C) Maxime MORGE 2017
package org.scaia.hedonic

import scala.collection.mutable

/**
  * Rational list for coalition representing the preferences of a player.
  * See Coralio Ballester. NP-completeness in hedonic games. Games and Economic Behavior. 49(1), pp 1-30, 2004.
  * @constructor  create a new Rlc with an array of rationals coalitions
  * @param level each cell of the array contains a set of rational coalitions where the most preferred coalitions come first
  */
class Rlc(val level : Array[Set[Set[String]]]){

  /**
    * Returns a string describing the preferences
    */
  override def toString(): String = {
    var s=""
    for (l <- level) {
      s+="["
      l.foreach{ c =>
        s += c.mkString("{", ", ", "}")+", "
      }
      s+="] "
    }
    return s
  }

  /**
    * Returns the number of levels
    */
  def nbLevel()= level.size


  /**
    *  Returns true if a is at least as preferred to b
    *  @param a the set of names of the players in the coalition a
    *  @param b the set of names of the players in the coalition b
    *
    */
  def pref(a: Set[String], b: Set[String]): Boolean ={
    for (i <- 0 to level.size-1) {
      if (level(i).contains(a)) return true
      if (level(i).contains(b)) return false
    }
    return false
  }

  /**
    * Return true if a is strictly preferred to b
    *  @param a the set of names of the players in the coalition a
    *  @param b the set of names of the players in the coalition b
    */
  def spref(a: Set[String], b: Set[String]): Boolean ={
    for (i <- 0 to level.size-1) {
      if (level(i).contains(a) && ! level(i).contains(b)) return true
      if (level(i).contains(b)) return false
    }
    return false
  }
}