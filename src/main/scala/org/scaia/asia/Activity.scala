// Copyright (C) Maxime MORGE 2017
package org.scaia.asia

/**
  * Activity to be selected
  * @constructor create a new activity
  * @param name name of the activity
  * @param c capacity of the activity
  */
class Activity(val name: String, val c: Int){
  override def toString: String = name + " ("+c+")"
}

/**
  * Factory for [[Activity]] instances
  */
object Activity{
  val VOID= new Activity("phantom", Int.MaxValue)
}