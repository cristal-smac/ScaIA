set terminal pdf
set datafile separator ","
set style fill solid border rgb "black"
set auto x
set auto y
set grid
set hidden3d
set dgrid3d 50,50 qnorm 2
set ticslevel 0
set style data lines
set xlabel "Number of activities"
set ylabel "Number of individuals"
set zlabel "Welfare"
set zrange [0:1]
set output 'inclusiveWelfareEgalitarian.pdf'
splot  "inclusiveWelfareEgalitarian.csv" using 1:2:3 with lines lc "blue" title 'Our algorithm',\
       "inclusiveWelfareEgalitarian.csv" using 1:2:4 with lines lc "red" title 'Hill-climbing'
set auto z
set zlabel "Time (ms)"
set output 'inclusiveTimeEgalitarian.pdf'
splot  "inclusiveTimeEgalitarian.csv" using 1:2:6 with lines lc "green" title 'Distributed algorithm',\
       "inclusiveTimeEgalitarian.csv" using 1:2:5 with lines lc "blue" title 'Centralized algorithm'
