## What is ScaIA ?

ScaIA is a library of algorithms which aim at forming coalitions of individuals around some
activities.

We consider here a particular coalition game called, the
individuals/activities (IA) problem.  In such a problem, some
individuals must be assigned to the activities they enjoy with their
favorite partners.

We have implemented our prototype with the
[Scala](https://www.scala-lang.org/) programming language and the
[Akka](http://akka.io/) toolkit. The latter, which is based on the
actor model, allows us to fill the gap between the specification and
its implementation.

## Requirements

In order to run the demonstration you need: the Java virtual machine
[JVM 1.8.0_60](http://www.oracle.com/technetwork/java/javase/downloads/index.html).

In order to compile the code you need:

- the programming language [Scala 2.11.8](http://www.scala-lang.org/download/);

- the interactive build tool [SBT 0.13](http://www.scala-sbt.org/download.html).

## Test

    java -jar ScaIA-assembly-X.Y.jar org.scaia.util.asia.IAProblemSolver -a -t -h -i -v -e -d examples/asia/undesiredGuestPb.txt  examples/asia/undesiredGuestMatching.txt

Usage: 

    Usage: java -jar ScaIA-assembly-X.Y.jar [-athived] inputFilename outputFilename
    The following options are available:
    -a: approximation (false by default)
    -t: trace (false by default)
    -h: hillclimbing (false by default)
    -i: inclusive (false by default)
    -v: verbose (false by default)
    -d: distributed (false by default)
    -e: egalitarian (utilitarian by default)

## Installation

Add to ~/.sbt/0.13/global.sbt

    resolvers += "Artima Maven Repository" at "http://repo.artima.com/releases"

Compile

    sbt compile

then

    sbt "run org.scaia.util.asia.IAProblemSolver -a -t -h -i -v -e -d examples/asia/undesiredGuestPb.txt examples/asia/undesiredGuestMatching.txt"
 
and eventually

    sbt assembly


## Contributors

Copyright (C) Maxime MORGE 2017

## License

This program is free software: you can redistribute it and/or modify it under the terms of the 
GNU General Public License as published by the Free Software Foundation, either version 3 of the License, 
or (at your option) any later version.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with this program.  
If not, see <http://www.gnu.org/licenses/>.
