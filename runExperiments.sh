#!/usr/bin/env bash
# Copyright (C) Maxime MORGE 2017
sbt "runMain  org.scaia.experiments.TestWelfareSolver Utilitarian"
sbt "runMain  org.scaia.experiments.TestTimeSolver Utilitarian"
sbt "runMain  org.scaia.experiments.TestWelfareSolver Egalitarian"
sbt "runMain  org.scaia.experiments.TestTimeSolver Egalitarian"
#sbt "runMain  org.scaia.experiments.TestTranslationSolver"