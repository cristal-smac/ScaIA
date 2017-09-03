set terminal pdf
set datafile separator ","
set style fill solid border rgb "black"
set output 'welfareUtilitarian.pdf'
set auto x
set auto y
set zrange [-0.25:1]
set grid
set hidden3d
set dgrid3d 50,50 qnorm 2
set ticslevel 0
set style data lines
set xlabel "Number of activities"
set ylabel "Number of individuals"
set zlabel "welfare"
splot  "welfareUtilitarian.csv" using 1:2:3 with lines lc "blue" title 'Our algorithm',\
       "welfareUtilitarian.csv" using 1:2:5 with lines lc "red" title 'Hill-climbing'
set output 'welfareEgalitarian.pdf'
splot  "welfareEgalitarian.csv" using 1:2:3 with lines lc "blue" title 'Our algorithm',\
       "welfareEgalitarian.csv" using 1:2:5 with lines lc "red" title 'Hill-climbing'
set output 'timeUtilitarian.pdf'
set auto x
set auto y
set auto z
set grid
set hidden3d
set dgrid3d 50,50 qnorm 2
set ticslevel 0
set style data lines
set xlabel "Number of activities"
set ylabel "Number of individuals"
set zlabel "Time (ms)"
splot "timeUtilitarian.csv" using 1:2:5 with lines lc "blue" title 'Centralized algorithm', \
     "timeUtilitarian.csv" using 1:2:6 with lines lc "green" title 'Decentralized algorithm'
