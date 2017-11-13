// Copyright (C) Maxime MORGE 2017
package org.scaia.asia

import org.scaia.hedonic.Game

/**
  * Class representing a matching between individuals and activities
  * @constructor creates a new matching
  * @param pb the instance of IA problem to tackle
  */
class Matching(val pb: IAProblem){

  /**
    * The debugging of the properties checking
    */
  val debug = false

  /**
    * The data structure which contains the assignments
    * @note all the individuals are initially assigned to the void activity
    */
  var a: Map[Individual,Activity] = Map[Individual,Activity]()
  pb.individuals.foreach{ i: Individual => //
    a+= (i -> Activity.VOID)
  }

  /**
    * The data structure which contains the groups
    * @note all the individuals are initially alone
    */
  var g:Map[Individual,Group] = Map[Individual,Group]()
  pb.individuals.foreach{ i: Individual =>
    g+= (i -> Group(i))
  }

  /**
    * Return true if obj is a matching which is equals to this
    * @param obj Any object
    */
  override def equals(obj: scala.Any): Boolean = obj match {
      case that: Matching => {
        this.a.equals(that.a)
      }
      case _ => false
    }

  /**
    * Returns true if the individual is assigned to an activity
    * @param i the individual which is (or) not busy
    */
  def isActive(i: Individual): Boolean = ! a(i).equals(Activity.VOID)

  /**
    * Return the coalition for an individual in the matching
    */
  def coalitionFor(i: Individual) : Coalition = new Coalition(a(i), g(i))

  /**
    * Returns the number of active individuals
    */
  def nbActive(): Int = {
    var cpt = 0
    pb.individuals.foreach{ i =>
      if (isActive(i)) cpt+=1
    }
    cpt
  }

  /**
    * Returns a string describing the matching
    */
  override def toString: String = {
    var s = ""
    pb.activities.foreach{
      case a: Activity => s+=a + ": " +  p(a).toString + "\n"
    }
    s+Activity.VOID + ": " +  p(Activity.VOID).toString
  }

  /**
    * Returns the posts of an activity
    * @param activity the activity
    */
  @throws(classOf[RuntimeException])
  def p(activity: Activity) : Group = {
    var g= Group()
    if (! activity.equals(Activity.VOID) && ! pb.activities.contains(activity)) throw new RuntimeException(activity+" does not belongs to the IA problem")
    pb.individuals.foreach { i: Individual =>
      if (a(i).equals(activity)) g+=i
    }
    g
  }

  /**
    * Returns true if the activity is overloaded
    * @param activity the activity
    * */
  def isOverloaded(activity: Activity) : Boolean =  p(activity).size > activity.c

  /**
   * Returns true if the activity is full
   * @param activity the activity
    */
  def isFull(activity: Activity) :Boolean = p(activity).size == activity.c

  /**
    * Return true if the matching is sound
    */
  def isSound() : Boolean =  ! pb.activities.exists(a => isOverloaded(a))

  /**
    * Returns the utility  of an individual wrt the matching
    * @param i the individual
    */
  def u(i: Individual) : Double = i.u(g(i).names,a(i).name)

  /**
    * Returns the utilitarian welfare of the matching
    */
  def utilitarianWelfare(): Double = pb.individuals.foldLeft(0.0)( (r,i) => r+u(i)) / pb.m()

  /**
    * Returns the egalitarian welfare of the matching
    */
  def egalitarianWelfare(): Double = {
    var min = Double.MaxValue
    pb.individuals.foreach{ i =>
      val utility=u(i)
      if (utility<min) min=utility
    }
    min
  }

  /**
    *   Returns true if the matching is core stable
    */
  def isCoreStable() : Boolean = {
    val coalitions = pb.allSoundCoalitions().filterNot(c => c.group.isEmpty)
    coalitions.foreach{ coalition =>
      if (coalition.stronglyBlock(this)) {
        if (debug) println("Coalition "+ coalition + " strongly blocks this matching")
        return false
      }
    }
    true
  }

  /**
    *  Returns true if the matching is strict core stable
    */
  def isStrictCoreStable() : Boolean = {
    val coalitions = pb.allSoundCoalitions().filterNot(c => c.group.isEmpty)
    coalitions.foreach{ coalition =>
      if (coalition.weaklyBlock(this)) {
        if (debug) println("Coalition "+ coalition + " weakly blocks this matching")
        return false
      }
    }
    true
  }

  /**
    * Returns true if the matching is Nash stable
    */
  def isNashStable() : Boolean = {
    if (!this.isSound() || !this.isIndividuallyRational()) return false
    pb.individuals.foreach{ i =>
      val otherActivities=pb.activities - this.a(i)
      otherActivities.foreach{ a : Activity =>
        val c=new Coalition(a,p(a)+i)
        if (!isFull(a) && ! i.prefC(coalitionFor(i),c)){
          if (debug) println("This matching is not Nash-stable since "+i+" strictly prefers the coalition "+a)
          return false
        }
      }
    }
    true
  }

  /**
    * Return true if the matching is individually rational
    */
  def isIndividuallyRational() : Boolean ={
    // Ordinal rationality
    //pb.individuals.forall(i => i.prefA(this.a(i).name, Activity.VOID.name) && i.prefG(this.g(i).names,Group(i).names))
    // Ordinal rationality
    pb.individuals.forall(i => i.u(this.g(i).names, this.a(i).name)>= 0)
  }

  /**
  * Returns true if the matching is individually stable
   */
  def isIndividuallyStable() : Boolean = {
    if (!isSound()) {
      if (debug) println("This matching is not stable since it is not sound")
      return  false
    }
    if (!isIndividuallyRational()) {
      if (debug) println("This matching is not stable since it is not individually rational")
      return  false
    }
    pb.individuals.foreach { i => // foreach individual i
      val otherActivities=pb.activities - this.a(i)
      otherActivities.foreach{ a : Activity => // foreach other activity a
        if (!isFull(a) && i.sprefC(new Coalition(a, p(a) + i), coalitionFor(i))){
          // i can benefit by moving to another under-subscribed activity
          if (p(a).forall(j => j.prefC(new Coalition(a, p(a) + i), coalitionFor(j)))) return false
          // while not making the posts of that activity worse off
        }
      }
    }
    true
  }

  /**
    *   Return true if the matching is contractually individually stable
    */
  def isContractuallyIndividuallyStable() : Boolean = {
    pb.individuals.foreach { i => // foreach individual i
      val otherActivities=pb.activities - this.a(i)
      otherActivities.foreach{ a : Activity => // foreach other activity a
        if (!isFull(a) && i.sprefC(new Coalition(a, p(a) + i), coalitionFor(i))){
            // i can benefit by moving to another under-subscribed activity
          if (p(a).forall(j => j.prefC(new Coalition(a, p(a) + i), coalitionFor(j)))////while not making the posts of that activity worse off
            ||
            (this.g(i)-i).exists(j2 => j2.sprefC(coalitionFor(j2),new Coalition(this.a(i), this.g(i) - i)))  // or the posts of the previous activity worse off
          )
            return false
        }
      }
    }
    true
  }

  /**
    *   Return true if the matching is contractually strict core stable
    */
  def isContractuallyStrictCore(): Boolean = {
    pb.allNonEmptyCoalitions.foreach{ c =>
      if (c.weaklyBlock(this)){ //if any weakly blocking coalition makes at least
        pb.individuals.filterNot(c.group.contains(_)).foreach{ j =>
          // one individual  j ∈ I \ GC worse off when breaking off.
          if (j.sprefC(this.coalitionFor(j), c)) return false
        }
      }
    }
    true
  }

  /**
  *Returns true if this matching Pareto-dominates m
   */
  def paretoDominate(m: Matching): Boolean = {
    if (!m.isSound()) throw new RuntimeException(m+" is not sound")
    if (!this.isSound() ) throw new RuntimeException(this+" is not sound")
    //this matching  is strictly better than m for at least one individual and not worst for the others
    (pb.individuals.forall(i => i.prefC(this.coalitionFor(i), m.coalitionFor(i)))
      &&
      pb.individuals.exists(i => i.sprefC(this.coalitionFor(i), m.coalitionFor(i))))
  }

  /**
  * Returns true if the matching is Pareto-optimum, i.e. not Pareto-dominated
   */
  def isParetoOptimal(): Boolean = ! pb.allSoundMatchings().exists(m => m.paretoDominate(this))


  /**
    * Return a copy of the matching
     */
def copy(): Matching ={
  val m2= new Matching(pb)
  a.foreach( assignement => m2.a+=assignement)
  g.foreach( allocation => m2.g+=allocation)
  m2
}


  /**
    *  Returns the matching where i and j with different activities are swapped
    *
    */
  def swap(i: Individual, j: Individual): Matching = {
    val ai= a(i)
    val aj= a(j)
    val m = copy() // TODO Check the outcome of LST is the same as m= this
    m.a+=(i -> aj)
    m.a+=(j -> ai)
    m.g+= (i -> (p(aj)-j+i))
    m.g+= (j -> (p(ai)-i+j))
    p(ai).filterNot(_.equals(i)).foreach( k  => m.g+= (k -> (m.g(k)+j-i) ) )
    p(aj).filterNot(_.equals(j)).foreach( k  => m.g+= (k -> (m.g(k)-j+i) ) )
    m
  }


}

/**
  * Factory for a matching of ASIA problem
  */
object Matching{
  def apply(hM: org.scaia.hedonic.Matching, pb: IAProblem): Matching = {
    val matching= new Matching(pb)
    hM.partition.foreach{ c:  org.scaia.hedonic.Coalition =>
      val coalition= Coalition(c,pb)
      coalition.group.foreach{ i =>
        matching.a+=(i->coalition.activity)
        matching.g+=(i->coalition.group)
      }
    }
    matching
  }
}
