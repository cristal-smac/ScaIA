// Copyright (C) Maxime MORGE 2017
package org.scaia.asia

import scala.language.postfixOps

import scala.collection.SetLike
import scala.collection.generic.CanBuildFrom
import scala.collection.mutable.{Builder, SetBuilder}

/**
  * Group of individuals to be aggregated
  * @constructor creates a new group
  * @param individuals the individuals of the group
  */
class Group(individuals: Individual*) extends Set[Individual] with SetLike[Individual, Group] with Serializable{

  override def empty: Group = new Group()
  def + (elem: Individual) : Group = if (individuals contains elem) this
      else new Group(elem +: individuals: _*)
  def - (elem: Individual) : Group = if (!(individuals contains elem)) this
  else new Group(individuals filterNot (elem ==): _*)
  def contains (elem: Individual) : Boolean = individuals exists (elem ==)
  def iterator : Iterator[Individual] = individuals.iterator

  /**
    *   Returns true if the groups are the same
    */
  override def equals(obj: scala.Any): Boolean = obj.isInstanceOf[Group] & names.equals(obj.asInstanceOf[Group].names)

  /**
    *   Returns the string describing the individuals
    */
  override def toString: String = individuals.mkString("[", ", ", "]")

  /**
    * Returns the set of names of individuals in the group
    */
  def names(): Set[String] = this.toList.sortWith(_.name < _.name).foldLeft(Set[String]())((r: Set[String], i: Individual) => r + i.name)

  /**
    * Returns true if
    * @param name is given to one of the individuals
    */
  def isNameOfAIndividual(name: String) : Boolean = {
    individuals.find(i => i.name.equals(name)) match {
      case Some(s) => true
      case None => false
    }
  }

  /**
  * Returns an individual
  * @param name the name fo the individual
  */
  @throws(classOf[RuntimeException])
  def getIndividual(name : String) : Individual = {
    individuals.find(i => i.name.equals(name)) match {
      case Some(s) => s
      case None => throw new RuntimeException("No activity "+name+" has been found")
    }
  }



  /**
    * Returns the utility of the group to be together for practicing the activity
    * @param activity the shared activity
    */
  def u(activity: String): Double = this.foldLeft(0.0)((sum,i) => sum +i.u(this.names,activity))

  /**
    * Returns the minimal utility for an individual in the group  to be together for practicing the activity
    * @param activity the shared activity
    */
  def umin(activity: String): Double = this.foldLeft(Double.MaxValue){
    case (min,i) =>
      val u= i.u(this.names,activity)
      if (u<min) u
      else min
  }

  /**
    * Returns all the possible subgroup of the group (the empty group and the group itself are included)
    */
  def subgroups(): Set[Group] = this.subsets().toSet
}

/**
  * Factory for [[Group]] instances
  */
object Group {
  def empty: Group = new Group()
  def newBuilder: Builder[Individual, Group] = new SetBuilder[Individual, Group](empty)
  def apply(elems: Individual*): Group = (empty /: elems) (_ + _)
  def thingSetCanBuildFrom = new CanBuildFrom[Group, Individual, Group] {
    def apply(from: Group) = newBuilder
    def apply() = newBuilder
  }
}

/**
  * Partition for a group of individual to be aggregated
  * @param groups the groups of the partitions
  */
class Partition(groups: Group*) extends Set[Group] with SetLike[Group, Partition] with Serializable{
  override def empty: Partition = new Partition()
  def + (elem: Group) : Partition = if (groups contains elem) this
  else new Partition(elem +: groups: _*)
  def - (elem: Group) : Partition = if (!(groups contains elem)) this
  else new Partition(groups filterNot (elem ==): _*)
  def contains (elem: Group) : Boolean = groups exists (elem ==)
  def iterator : Iterator[Group] = groups.iterator

  //Returns the string representing the set of groups in the partition
  override def toString: String = groups.foldLeft(Set[Group]())( (r,g) => r + g).mkString("{", ", ", "}")
}

/**
  * Factory for [[Partition]] instances
  */
object Partition {
  def empty: Partition = new Partition()
  def newBuilder: Builder[Group, Partition] = new SetBuilder[Group, Partition](empty)
  def apply(elems: Group*): Partition = (empty /: elems) (_ + _)
  def thingSetCanBuildFrom = new CanBuildFrom[Partition, Group, Partition] {
    def apply(from: Partition) = newBuilder
    def apply() = newBuilder
  }
}
