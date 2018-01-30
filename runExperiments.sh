#!/usr/bin/env bash
# Copyright (C) Maxime MORGE 2017
#sbt "run Welfare Utilitarian" 2>&1 | tee -a experiments/data/welfareUtilitarian.csv
#sbt "run Time Utilitarian" 2>&1 | tee -a experiments/data/timeUtilitarian.csv
sbt "run Welfare Egalitarian" 2>&1 | tee -a experiments/data/welfareEgalitarian.csv
