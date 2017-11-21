// Copyright (C) Maxime MORGE 2017
package org.scaia.asia

import org.scaia.hedonic.Player

/**
  * Coalition of individuals around an activity
  * @constructor create a new coalition
  * @param activity the activity practiced by the individuals
  * @param group the group of individuals formed
  */
class Coalition(val activity: Activity, val group: Group) {

  override def toString: String = activity + ": " + group
  
  /**
    * Return true if the coalition is individually rational for i
    */
  def isIndividuallyRational(i: Individual) = i.u(group.names(), activity.name) >= 0

  /**
    * Returns true if the coalition is empty
    */
  def isEmpty() = group.isEmpty

  /**
    * Returns true if the coalition strongly blocks the matching
    */
  @throws(classOf[RuntimeException])
  def stronglyBlock(matching: Matching): Boolean = {
    if (group.isEmpty) throw new RuntimeException(this+" is an empty coalition "+this)
    (group.size <= activity.c) &&
      group.forall(i => i.sprefC(this,new Coalition(matching.a(i),matching.g(i))))
  }

  /**
    * Returns true if the coalition weakly blocks the matching
    */
  @throws(classOf[RuntimeException])
  def weaklyBlock(matching: Matching): Boolean = {
    if (group.isEmpty) throw new RuntimeException(this+" is an empty coalition "+this)
    (group.size <= activity.c) &&
      group.forall(i => i.prefC(this,new Coalition(matching.a(i),matching.g(i))) &&
        group.exists(i => i.sprefC(this,new Coalition(matching.a(i),matching.g(i)))))
  }
}

/*
 * Factory for [[Coalition]] instances
 */
object Coalition{
  val debug = false

  /**
    * Returns the result of applying [[org.scaia.hedonic.Coalition]] to [[Coalition]]
    * @param hedonicCoalition
    * @param pb
    * @return
    */
  def apply(hedonicCoalition: org.scaia.hedonic.Coalition, pb: IAProblem): Coalition = {
    var a: Activity = Activity.VOID
    var g : Group = new Group()
    //Find the player for this activity
    hedonicCoalition.find(p=> pb.activities.exists(a => a.name.equals(p.name))) match {
      case Some(playerActivity) => { // If one player represents an activity
        a=pb.getActivity(playerActivity.name)
        val playersIndividuals =hedonicCoalition-playerActivity
        playersIndividuals.foreach( playerIndividual => g+=pb.getIndividual(playerIndividual.name))
        val c= new Coalition(a,g)
        if (debug) println("Coalition: "+c)
        return c
      }
      case None => // Otherwise the activity is void and the group a single individual
        g+=pb.getIndividual(hedonicCoalition.head.name)
        val c= new Coalition(a,g)
        if (debug) println("Coalition: "+c)
        return c
    }
  }
}
