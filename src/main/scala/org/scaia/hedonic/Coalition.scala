// Copyright (C) Maxime MORGE 2017
package org.scaia.hedonic

import scala.language.postfixOps

import scala.collection.SetLike
import scala.collection.generic.CanBuildFrom
import scala.collection.mutable.{Builder, SetBuilder}


/**
  * Coalition of players
  */
class Coalition(players: Player*) extends Set[Player] with SetLike[Player, Coalition] with Serializable{

  override def empty: Coalition = new Coalition()
  def + (elem: Player) : Coalition = if (players contains elem) this
  else new Coalition(elem +: players: _*)
  def - (elem: Player) : Coalition = if (!(players contains elem)) this
  else new Coalition(players filterNot (elem ==): _*)
  def contains (elem: Player) : Boolean = players exists (elem ==)
  def iterator : Iterator[Player] = players.iterator

  override def equals(obj: scala.Any): Boolean = obj.isInstanceOf[Coalition] & names.equals(obj.asInstanceOf[Coalition].names)

  //Returns the string describing the Players
  override def toString: String = players.mkString("{", ", ", "}")

  //Returns the set of names of Players in the Coalition
  def names(): Set[String] = this.toList.sortWith(_.name < _.name).foldLeft(Set[String]())((r: Set[String], i: Player) => r + i.name)

  //Returns all the possible subCoalition of the Coalition (the empty Coalition and the Coalition itself are included)
  def subCoalitions(): Set[Coalition] = this.subsets().toSet
}


object Coalition {
  def empty: Coalition = new Coalition()
  def newBuilder: Builder[Player, Coalition] = new SetBuilder[Player, Coalition](empty)
  def apply(elems: Player*): Coalition = (empty /: elems) (_ + _)
  def thingSetCanBuildFrom = new CanBuildFrom[Coalition, Player, Coalition] {
    def apply(from: Coalition) = newBuilder
    def apply() = newBuilder
  }
}


/**
  * Coalition partition
  */
class Partition(coalitions: Coalition*) extends Set[Coalition] with SetLike[Coalition, Partition] with Serializable{
  override def empty: Partition = new Partition()
  def + (elem: Coalition) : Partition = if (coalitions contains elem) this
  else new Partition(elem +: coalitions: _*)
  def - (elem: Coalition) : Partition = if (!(coalitions contains elem)) this
  else new Partition(coalitions filterNot (elem ==): _*)
  def contains (elem: Coalition) : Boolean = coalitions exists (elem ==)
  def iterator : Iterator[Coalition] = coalitions.iterator

  //Returns the string representing the set of Coalitions in the partition
  override def toString: String = coalitions.foldLeft(Set[Coalition]())( (r,g) => r + g).mkString("{", ", ", "}")
}

object Partition {
  def empty: Partition = new Partition()
  def newBuilder: Builder[Coalition, Partition] = new SetBuilder[Coalition, Partition](empty)
  def apply(elems: Coalition*): Partition = (empty /: elems) (_ + _)
  def thingSetCanBuildFrom = new CanBuildFrom[Partition, Coalition, Partition] {
    def apply(from: Partition) = newBuilder
    def apply() = newBuilder
  }
}
