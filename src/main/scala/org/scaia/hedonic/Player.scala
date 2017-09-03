// Copyright (C) Maxime MORGE 2017
package org.scaia.hedonic

import org.scaia.asia.{Activity, IAProblem, Individual}

/**
  * Player in a hedonic coalition  game
  * @constructor create a new player
  * @param name name of the player
  */
class Player(val name: String) {

  /**
    *  rational list for coalition
    */
  var rlc = new Rlc(Array[Set[Set[String]]]())

  /**
    * Returns a string describing the individual
    */
  override def toString: String = name

  /**
    * Returns true if this player is equals to obj
    */
  override def equals(obj: scala.Any): Boolean = obj.isInstanceOf[Player] & name.equals(obj.asInstanceOf[Player].name)

  /**
    * Returns true if the coalition individually rational
    * @param names the names of the players in the coalition
    */
  def isRational(names: Set[String]): Boolean = {
    for (l <- rlc.level){
      if (l.contains(names)) return true
    }
    false
  }

  /**
    * Returns the set of rational coalitions
    * @return the set of coalitions, i.e. the names of the players
    */
  def rationalCoalitions() : Set[Set[String]] = {
    rlc.level.foldLeft(Set[Set[String]]())( (r,c) => r++c)
  }

  //Return true if a is at least as preferred to b
  def pref(a: Set[String], b: Set[String]) : Boolean= rlc.pref(a,b)

  //Return true if a is strictly preferred to b
  def spref(a: Set[String], b: Set[String]): Boolean = rlc.spref(a,b)
}

/**
  * Factory for a player
  */
object Player{

  /**
    * The debugging of the generation of players from an individual/activity
    */
  val debug=false


  /**
    * Creates a player from an [[org.scaia.asia.Activity]]
 *
    * @note All the groups are rational and equally preferred  as soon as the activity is not oversubscribed
    * @param a the activity
    * @param pb the IA problem
    */
  def apply(a: Activity, pb: IAProblem): Player = {
    val p=new Player(a.name)
    var subgroups= Set[Set[String]]()
    for( size <- 1 to a.c){
      val sets = pb.individuals.names.subsets(size).map(s  => s+ a.name)
      subgroups=subgroups.union(sets.toSet)
    }
    p.rlc= new Rlc(Array(subgroups))
    p
  }

  /**
    * Creates a player from an individual
    * @param i the individual
    * @param pb the IA problem
    */
  def apply(i: Individual, pb: IAProblem): Player = {
    val p=new Player(i.name)
    var utilities= Map[Double,Set[String]]()
    pb.activities.foreach { a => // foreach activity
      var u: Double = i.u(Set(i.name), a.name)
      var g: Set[String] = Set(i.name, a.name)
      if (debug) println("u_"+i.name+"("+g+")= "+u)
      utilities += ( u -> g)
      for (size<- 1 until a.c) { // for each size of sound IA coalition
        (pb.individuals-i).names.subsets(size).foreach { is => // for each group
          u = i.u(is + i.name, a.name)
          g = is + i.name + a.name
          if (i.w(is+i.name)>=0){ // if the IA coalition is rational
            if (debug) println("u_" + i.name + "(" + g + ")= " + u)
            utilities += (u -> g) // compute its utility
          }
        }
      }
    }
    val orderedHedonicCoalition = utilities.toSeq.sortWith(_._1 > _._1)
    val tab=new Array[Set[Set[String]]](utilities.keys.size)
    p.rlc = new Rlc(tab)
    var l = 0
    orderedHedonicCoalition.foreach { case (u: Double, s: Set[String]) =>
      p.rlc.level(l) = Set[Set[String]]()
      if (debug) println("Player "+i.name+" level "+l+": "+s)
      p.rlc.level(l) += s
      l+=1
    }
    p
  }

}