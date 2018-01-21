// Copyright (C) Maxime MORGE 2017
package org.scaia.asia

import scala.util.Random
import org.scaia.util.MathUtils._

/**
  * Class representing the Individuals/Activities problem
  * @constructor creates a new IA problem
  * @param individuals the individuals
  * @param activities the activities
  */
class IAProblem(val individuals: Group, val activities: Set[Activity]) extends Serializable{
  /**
    * the debugging random generation of IAProblem
    */
  val debug = false

  /**
    *   Return the numbers of individuals
    */
  def m(): Int = individuals.size

  /**
    *Returns the number of activities
    */
  def n(): Int = activities.size

  /**
    * Returns a string describing the IAProblem
    */
  override def toString: String = {
    //"Activities: " + activities.mkString("{",",","}") + "\n" + "Individuals: " + individuals
    var s="m: "+m+"\n"
    s+="n: "+n+"\n"
    s+="activities: "+activities.mkString("", " ", "")+"\n"
    s+="individuals: "+individuals.toSeq.sortBy(_.name).mkString("", " ", "")+"\n"
    individuals.toSeq.sortBy(_.name).foreach{ i =>
      activities.toSeq.sortBy(_.name).foreach{ a =>
        s+=i+": "+a.name+" "+i.v(a.name)+"\n"
      }
      (individuals-i).toSeq.sortBy(_.name).foreach{ j =>
        s+=i+": "+j+" "+i.w(j.name)+"\n"
      }
    }
    s
  }

  /**
    * Returns a string describing the IAProblem as a MIP Mixed Integrated Problem
    */
  def toOPL: String = {
    var s="M = "+m+"; \n"
    s+="N = "+n+"; \n"
    s+="V = "+individuals.toSeq.sortBy(_.name).map( i =>
      activities.toSeq.sortBy(_.name).map(a =>
        i.v(a.name).toString
      ).mkString("[", ", ", "]")
    ).mkString("[", ", ", "] ;\n")
    s+="C = "+activities.toSeq.sortBy(_.name).map( a =>
      a.c.toString
    ).mkString("[", ", ", "]") + "; \n"
    s+="W = "+individuals.toSeq.sortBy(_.name).map( i =>
      individuals.toSeq.sortBy(_.name).map(j =>
        (if (j.equals(i)) 0 else i.w(j.name)).toString
      ).mkString("[", ", ", "]")
    ).mkString("[", ", ", "] ;\n")
    s
  }

  /**
    * Returns a string which fully describing the IAProblem
    */
  def describe: String ={
    var s= "Activities: " + activities.mkString("{",",","}") + "\n"
    individuals.foreach{ i=>
      s+=i
      activities.foreach{ a=>
        s+= "u_"+i+"("+a+")="+i.v(a.name)+"\n"
      }
    }
    s
  }

  /**
    * Returns the attractive activities for an individual
    * @param i the inddividual
    */
  def attractiveActivities(i: Individual)= activities.filter(a=> i.v(a.name)>0)

  /**
    * Returns an activity
    * @param name the name of the activity
    */
  @throws(classOf[RuntimeException])
  def getActivity(name : String) : Activity = {
    if (name == Activity.VOID.name) return Activity.VOID
    activities.find(_.name==name) match {
      case Some(s) => s
      case None => throw new RuntimeException("No activity "+name+" has been found")
    }
  }

  /**
  * Returns an individual
  * @param name the name fo the individual
   */
  @throws(classOf[RuntimeException])
  def getIndividual(name : String) : Individual = individuals.getIndividual(name)

  /**
    * Returns true if the preferences of the individuals wrt peers/activities are total (i.e. fully specified)
    */
  def isTotalPreferences() : Boolean = individuals.forall(i => i.vMap.keys.size== n) && individuals.forall(i => i.wMap.keys.size== (m-1))


  /**
    * Returns all the groups which contains  G(i) = {G ⊆ I | i ∈ G}
    * @param i the individual
    */
  def G(i: Individual) : Set[Group] = (individuals-i).subgroups().map(g => g+i)


  /**
    * Returns all the potential coalitions
    */
  def allNonEmptyCoalitions() : Set[Coalition] = {
    var allCoalitions = Set[Coalition]()
    (activities+Activity.VOID).foreach { a => // foreach activity
      individuals.subgroups.filterNot(_.isEmpty).foreach { g  => // for each group
        allCoalitions+=new Coalition(a, g)
      }
    }
    allCoalitions
  }

  /**
    * Returns all the possible sound matchings
    */
  def allSoundMatchings(): Set[Matching] = allSoundSubmatchings(individuals,activities)

  /*
  * Returns all the possible sound sub-matchings
  * @param members the members for the sub-matching
  * @param acts the activity for the sub-matching
   */
  def allSoundSubmatchings(members: Group, acts: Set[Activity]) : Set[Matching] = {
    // There is a single matching with one activity
    if (acts.isEmpty) {
      val matching= new Matching(this)
      return Set(matching) // returns the matching where all the individuals are alone
    }
    var allMatchings = Set[Matching]()
    val act = acts.head // Select one acitvity
    val subacts = acts - act
    members.subgroups().foreach{ group: Group => // for each subgroup of the members
      var complementary: Group = members -- group
      if (group.size <= act.c){ // if the current group can be allocated to the selected activity
        val matchings = allSoundSubmatchings(complementary, subacts) // computes the submatchings of the complementary
        //and assign the current group to the current activity
        matchings.foreach { m =>
          group.foreach{ i =>
            m.a+=(i -> act)
            m.g+=(i -> group)
          }
        }
        allMatchings ++= matchings
      }
    }
    return allMatchings
  }

  /**
    * Returns all the sound matchings which maximize the utilitarian welfare
    */
  def allMaxUtilitarian(): Set[Matching] = {
    var u = -Double.MaxValue
    var matchings = Set[Matching]()
    allSoundMatchings().foreach { m =>
      if (u ~= m.utilitarianWelfare()) {
        u = m.utilitarianWelfare()
        matchings += m
      } else if (m.utilitarianWelfare() > u) {
        u = m.utilitarianWelfare()
        matchings = Set[Matching](m)
      }
    }
    matchings
  }


  /**
    * Returns all the sound matchings which maximize the egalitarian welfare
    */
  def allMaxEgalitarian(): Set[Matching] = {
    var u = -Double.MaxValue
    var matchings = Set[Matching]()
    allSoundMatchings().foreach { m =>
      if (u ~= m.egalitarianWelfare()) {
        u = m.egalitarianWelfare()
        matchings += m
      } else if (m.egalitarianWelfare() > u) {
        u = m.egalitarianWelfare()
        matchings = Set[Matching](m)
      }
    }
    matchings
  }


  /**
    * Returns a random matching with a random number of inactive individual
    */
  def generateRandomMatching() : Matching = {
    val r = scala.util.Random
    var matching = new Matching(this)
    var individualList = r.shuffle(this.individuals.toList) // random list of individuals
    var acts = this.activities.toArray // array of activity
    var nbOfInactiveIndividuals = r.nextInt(this.m)
    // 1. Select a random number of single individuals
    for (i <- 0 to nbOfInactiveIndividuals) {
      matching.a += (individualList.head -> Activity.VOID)
      individualList = individualList.tail
    }
    //  2. Assign the other individuals
    var index = 0
    individualList.foreach { i => // foreach random individual
      if (acts.isEmpty) {// If all the activity are full
        matching.a += (i -> Activity.VOID)//The individual is inactive
        individualList = individualList.tail
      } else{// If some activities are not full
      val currentAct = acts(index)//The individual is assigned
        matching.a += (i -> currentAct)
        individualList = individualList.tail
        if (matching.isFull(currentAct)){// If the current act is full
          acts = acts.filter(!_.equals(currentAct)) //remove it from the array
        }else index += 1
        if (acts.size!=0) index %= acts.size
      }
    }
    //  3. Build the groups
    this.activities.foreach { a =>
      val g = matching.p(a)
      g.foreach { i =>
        matching.g+=(i -> g)
      }
    }
    matching
  }

  /**
    * Returns a random matching where the individuals are assigned as much as possible
    */
  def generateRandomInclusiveMatching() : Matching = {
    val r = scala.util.Random
    var matching = new Matching(this)
    var individualsList = r.shuffle(this.individuals.toList) // random list of individuals
    var acts = this.activities.toArray // array of activity
    //  1. Assign the  individuals
    var index = 0
    individualsList.foreach { i => // Foreach random individual
      if (acts.isEmpty) {// If all the activity are full
        matching.a += (i -> Activity.VOID)// the individual is inactive
        individualsList = individualsList.tail
      } else {// If some activities are not full
      val currentAct = acts(index)
        if (i.v(currentAct.name)>0) {//If the activity is attractive then the individual is assigned
          matching.a += (i -> currentAct)
          individualsList = individualsList.tail
          if (matching.isFull(currentAct)) { // If the current act is full
            acts = acts.filter(!_.equals(currentAct))//remove it from the array
          } else index += 1
          if (acts.size != 0) index %= acts.size
        }
      }
    }
    // 2. Build the groups
    this.activities.foreach { a =>
      val g = matching.p(a)
      g.foreach { i =>
        matching.g+=(i -> g)
      }
    }
    matching
  }


  /**
    *Returns a random matching with positive problem not overconstrainted
    */
  def generateRandomPositiveInclusiveMatching() : Matching = {
    val r = scala.util.Random
    var matching = new Matching(this)
    var individualsList = r.shuffle(this.individuals.toList) // random list of individuals
    if (debug) println("Random: individuals:"+individualsList)
    var acts = this.activities.toArray   // array of activities
    if (debug) println("Random: acts:"+acts.foreach(println(_)))
    // 1. Assign the  individuals
    var index = 0
    individualsList.foreach { i => // foreach random individual
      val currentAct = acts(index)
      matching.a += (i -> currentAct)
      individualsList = individualsList.tail
      if (matching.isFull(currentAct)){// If the current act is full
        acts = acts.filter(!_.equals(currentAct))//remove it from the array
      } else index += 1
      if (acts.size != 0) index %= acts.size
    }
    this.activities.foreach { a => // build the groups
      val g = matching.p(a)
      g.foreach { i =>
        matching.g+=(i -> g)
      }
    }
    matching
  }

  /**
    * Returns all the potential non-empty and sound coalitions
    */
  def allSoundNonEmptyCoalitions() : Set[Coalition] = {
    var results = Set[Coalition]()
    val allgroups = individuals.subgroups()
    allgroups.foreach { group =>
      (activities+Activity.VOID).foreach{ activity =>
        val c= new Coalition(activity, group)
        if (c.isSound() && ! c.isEmpty()) results+=c
      }
    }
    results
  }


}

/**
  * Factory for [[IAProblem]] instances
  */
object IAProblem {

  /**
    * The debugging of the random generation of preferences
    */
  val debug = false

  val THRESOLD=0.5

  val r = scala.util.Random

  /**
    *  Returns a pseudo-randomly generated Double in ]0;1]

    */
  def myRandom() : Double= {
    val d=1 -r.nextDouble()
    if (d > THRESOLD) return d
    return d+THRESOLD
  }

  /**
    *  Generates a pseudo-random problem instance with valuation in [-1;1]
    *  @param n number of activities
    *  @param m number of individuals
    */
  def generateRandom(n: Int, m: Int): IAProblem = {
    var activities = Set[Activity]()
    val q: Int = m/n
    for (k <- 0 to n - 1) {
      val a = new Activity(s"${Random.alphanumeric take 10 mkString("")}", q)
      activities += a
    }
    if (debug) println(activities)
    var individuals = Group()
    for (k <- 1 to m) { // Generate new individuals
      val i = new Individual(k.toString,m) {
        activities.foreach { a => // valuates the activities in [-1;1]
          vMap += (a.name -> (r.nextDouble()*2-1))
          if (debug) println("u_" + k.toString + "(" + a + ")=" + vMap(a.name))
        }
      }
      individuals += i
    }
    individuals.foreach { i =>
      individuals.filter(j => j != i).foreach { j => // valuates the individuals in  [-1;1]
        i.wMap += (j.name -> (r.nextDouble()*2 -1))
      }
    }
    return new IAProblem(individuals, activities)
  }

  /**
    *  Generates a pseudo-random problem instance with valuation in  ]0;1]
    *  @param n number of activities
    *  @param m number of individuals
    *  @param capacity of the activities
    */
  def generatePositiveRandom(n: Int, m: Int, capacity: Int): IAProblem = {
    var activities = Set[Activity]()
    for (k <- 0 to n - 1) {
      val a = new Activity(s"${Random.alphanumeric take 10 mkString("")}", capacity)
      activities += a
    }
    if (debug) println(activities)
    var individuals = Group()
    for (k <- 1 to m) {
      if (debug) println("Generate new individual " + k.toString)
      val i = new Individual(k.toString,m) {
        activities.foreach { a =>
          vMap += (a.name -> myRandom)
          if (debug) println("u_" + k.toString + "(" + a + ")=" + vMap(a.name))
        }
      }
      individuals += i
    }
    individuals.foreach { i =>
      individuals.filter(j => j != i).foreach { j =>
        i.wMap += (j.name -> myRandom)
      }
    }
    new IAProblem(individuals, activities)
  }
}