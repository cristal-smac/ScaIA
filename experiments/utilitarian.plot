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
set zlabel "U(M)"
set zrange [-1:1]
set output 'welfareUtilitarian.pdf'
splot  "data/welfareUtilitarian.csv" using 1:2:3 with lines lc "blue" title 'Sel. algo.',\
       "data/welfareUtilitarian.csv" using 1:2:4 with lines lc "red" title 'Hill-Climbing.'
set auto z
set zlabel "Time (ms)"
set output 'timeUtilitarian.pdf'
splot  "data/timeUtilitarian.csv" using 1:2:6 with lines lc "green" title 'Dis. sel. algo. ',\
       "data/timeUtilitarian.csv" using 1:2:5 with lines lc "blue" title 'Centra. sel. algo.'
