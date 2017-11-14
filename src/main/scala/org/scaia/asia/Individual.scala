// Copyright (C) Maxime MORGE 2017
package org.scaia.asia

/**
  * Individual with additive preferences
  * @constructor creates a new individual
  * @param name the name of the individual
  * @param m the number of individuals of the problem
  */
class Individual(val name: String, val m : Int){

  val debug = false

  /**
    *  The valuation of the activities
    */
  var vMap= Map[String,Double]()

  /**
    * The weight of the individuals
    */
  var wMap= Map[String,Double]()

  /**
    * Returns a string describing the individual
    */
  override def toString: String = name

  /**
    * Returns true if this individual is equals to obj
    */
  override def equals(obj: scala.Any): Boolean = obj.isInstanceOf[Individual] & name.equals(obj.asInstanceOf[Individual].name)

  /**
    * Returns the valuation of an activity, in particular 0 for the void activity
    * @param a the activity's name
    */
  def v(a: String): Double = {
    if (a.equals(Activity.VOID.name)) return 0.0 // It is worth noticing that a inactive individual only cares about its group
    if (! vMap.contains(a)) throw new RuntimeException(name+" cannot valuate the activity "+a)
    vMap(a)
  }

  /**
    * Returns the valuation of an individual
    * @param i the individual's name
    */
  def w(i: String): Double = {
    if (! wMap.contains(i)) throw new RuntimeException(name+" cannot valuate the individual "+i)
    wMap(i)
  }

  /**
    * Returns the mean valuation of some individuals
    * @param g the individuals' name
    */

  def wMean(g: Set[String]) : Double = g.foldLeft(0.0)((acc, i) => acc+wMap(i))/(m-1)

  /**
    * Returns the valuation of its groups, in particular 0 when it is alone
    * @param g the individual's name
    */
  @throws(classOf[RuntimeException])
  def w(g: Set[String]) : Double = {
    if (! g.contains(name)) throw new RuntimeException(name+" cannot valuate a group in which it does not belong")
    if ((g-name).isEmpty) return 0.0 // It is worth noticing that a single individual only care about the activity
    val others = g.filterNot(_.equals(name))
    wMean(others)
  }

  /**
    * Returns the utility
    * @param g the name of the individuals in the group it belongs (including itself)
    * @param a the name of the activity it is assigned to
    */
  def u(g: Set[String], a: String): Double = (w(g) + v(a) ) /2


  /**
    *  Returns the list of attractive activities (with a positive valuation) by decreasing order of valuation
    *  @param activities the activities
    */
  def concessions(activities: Set[Activity]) : List[Activity] = activities.toList.filter(a => v(a.name)>=0).sortWith((a1, a2) => v(a1.name) > v(a2.name) )

  /**
    *   Returns true if a is preferred to b
    */
  def prefA(a: String, b: String): Boolean = v(a)>=v(b)

  /**
    * Returns true if the activity called a is strictly preferred to the activity called b
    */
  def sprefA(a: String, b: String): Boolean = v(a)>v(b)

  /**
    *   Returns true if the group with g1 is preferred to the group with name g2
    */
  def prefG(g1: Set[String], g2: Set[String]): Boolean = w(g1)>=w(g2)

  /***
    * Returns true if the group with g1 is strictly preferred to the group with name g2
    */
  def sprefG(g1: Set[String], g2: Set[String]): Boolean = w(g1)>w(g2)

  /**
    * Returns true if c1 is preferred to c2
    */
  def prefC(c1: Coalition, c2: Coalition) : Boolean= u(c1.group.names,c1.activity.name) >= u(c2.group.names,c2.activity.name)
  //w(c1.group.names)>=w(c2.group.names) &&  v(c1.activity.name)>=v(c2.activity.name)

  /**
    *  Returns true if c1 is strictly preferred to c2
    */
  def sprefC(c1: Coalition, c2: Coalition) : Boolean= u(c1.group.names,c1.activity.name) > u(c2.group.names,c2.activity.name)
    //prefC(c1,c2) && (w(c1.group.names) > w(c2.group.names) ||  v(c1.activity.name)>v(c2.activity.name))

  /**
    * Returns true if m1 is preferred to m2
    */
  @throws(classOf[RuntimeException])
  def prefM(m1: Matching, m2: Matching) : Boolean = {
    if (! m1.isSound())  throw new RuntimeException(m1+" is not sound")
    if (! m2.isSound())  throw new RuntimeException(m2+" is not sound")
    prefC(m1.coalitionFor(this), m1.coalitionFor(this))
  }

  /**
    *Returns true if m1 is strictly preferred to m2
    */
  @throws(classOf[RuntimeException])
  def sprefM(m1: Matching, m2: Matching) : Boolean = prefM(m1, m2) && ! prefM(m2, m1)

}

