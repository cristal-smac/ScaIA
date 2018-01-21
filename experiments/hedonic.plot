set terminal pdf
set datafile separator ","
set key autotitle columnhead
set style fill solid border rgb "black"
set output 'hedonic.pdf'
set style data lines
set xlabel "Number of individuals"
set ylabel "Welfare"
set y2label "Times (ms)"
set xtics nomirror
set ytics nomirror
set y2tics
set tics out
set logscale y2
set key left center
plot  "data/hedonic.csv" using 2:3 lc "green" title 'Our welfare' axes x1y1,\
      "data/hedonic.csv" using 2:4 lc "red" title 'CIS welfare' axes x1y1,\
      "data/hedonic.csv" using 2:5 lc "blue" title 'MIQP welfare' axes x1y1,\
      "data/hedonic.csv" using 2:6  with line dt '-' lc "green" title 'Our runtime' axes x1y2,\
      "data/hedonic.csv" using 2:7  with line dt '-' lc "red" title 'CIS runtime' axes x1y2,\
      "data/hedonic.csv" using 2:8  with line dt '-' lc "blue" title 'MIQP runtime' axes x1y2
