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
set zlabel "U(M)"
splot  "data/welfareUtilitarian.csv" using 1:2:3 with lines lc "blue" title 'Selec. algo.',\
       "data/welfareUtilitarian.csv" using 1:2:5 with lines lc "red" title 'MIQP'
set output 'welfareEgalitarian.pdf'
set zlabel "E(M)"
splot  "data/welfareEgalitarian.csv" using 1:2:3 with lines lc "blue" title 'Incl. algo.',\
       "data/welfareEgalitarian.csv" using 1:2:5 with lines lc "red" title 'Hill-climbing'
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
splot "data/timeUtilitarian.csv" using 1:2:5 with lines lc "blue" title 'Cent. sel. algo.', \
     "timeUtilitarian.csv" using 1:2:6 with lines lc "green" title 'Dec. sel. algo.'
set output 'timeEgalitarian.pdf'
splot "data/timeEgalitarian.csv" using 1:2:5 with lines lc "blue" title 'Cent. incl. algo.', \
     "timeEgalitarian.csv" using 1:2:6 with lines lc "green" title 'Dec. incl. algo.'
