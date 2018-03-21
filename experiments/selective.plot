set terminal pdf
set datafile separator ","
set key autotitle columnhead
set style fill solid border rgb "black"
set style line 1 linetype 1 pointtype 0 linewidth 1 linecolor rgb '#e56b5d' # red
set style line 2 linetype 2 pointtype 0 linewidth 1 linecolor rgb '#f89441' # orange
set style line 3 linetype 3 pointtype 0 linewidth 1 linecolor rgb '#f0f921' # yellow
set style line 4 linetype 1 pointtype 0 linewidth 1 linecolor rgb '#3b518b' # blue
set style line 5 linetype 2 pointtype 0 linewidth 1 linecolor rgb '#472c7a' # purple
set style line 6 linetype 3 pointtype 0 linewidth 1 linecolor rgb '#440154' # dark purple
set style line 7 linetype 3 pointtype 0 linewidth 1 linecolor rgb '#006400' # green
set xlabel "Number of individuals"
set ylabel "Percentage of matchings"
set yrange [0:100]
set key left center
set output 'selective.pdf'
plot  "data/selective.csv" using 2:($5)*100 with line lc rgb '#3b518b' title 'PO',\
      "data/selective.csv" using 2:($6)*100 with line lc rgb '#006400' title 'IR'

