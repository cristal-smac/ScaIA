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
set zlabel "E(M)"
set zrange [-1:1]
set output 'welfareEgalitarian.pdf'
splot  "data/welfareEgalitarian.csv" using 1:2:3 with lines lc "blue" title 'Incl. algo.',\
       "data/welfareEgalitarian.csv" using 1:2:4 with lines lc "red" title 'Hill-Climbing.'
set auto z
set zlabel "Time (ms)"
set output 'timeEgalitarian.pdf'
splot  "data/timeEgalitarian.csv" using 1:2:6 with lines lc "green" title 'Dis. incl. algo. ',\
       "data/timeEgalitarian.csv" using 1:2:5 with lines lc "blue" title 'Centra. incl. algo.'
