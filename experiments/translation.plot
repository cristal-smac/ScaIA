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
set xlabel "Number of individuals"
set ylabel "Welfare"
set key left center
set output 'translationWelfare.pdf'
plot  "data/translation.csv" using 2:3 with line lc rgb '#006400' title 'Our welfare',\
      "data/translation.csv" using 2:4 with line lc rgb '#e56b5d' title 'CIS welfare',\
      "data/translation.csv" using 2:5 with line lc rgb '#3b518b' title 'MIQP welfare'
set ylabel "Times (s)"
set logscale y
set key left top
set output 'translationTimeCIS.pdf'
plot  "data/translation.csv" using 2:($7+$8+$9)/1e9 title 'CIS runtime' w filledcurves x1 linestyle 3,\
      "data/translation.csv" using 2:($7+$8)/1e9 title 'CIS pre/post-processing runtime' w filledcurves x1 linestyle 2,\
      "data/translation.csv" using 2:($7)/1e9 title 'CIS pre-runtime' w filledcurves x1 linestyle 1,\
      "data/translation.csv" using 2:($6)/1e9 with line lc rgb '#006400' title 'Our runtime'
set output 'translationTimeMIQP.pdf'
plot  "data/translation.csv" using 2:($10+$11+$12)/1e9 title 'MIQP runtime' w filledcurves x1 linestyle 6,\
      "data/translation.csv" using 2:($10+$11)/1e9 title 'MIQP pre/post-processing runtime' w filledcurves x1 linestyle 5,\
      "data/translation.csv" using 2:($10)/1e9 title 'MIQP pre-runtime' w filledcurves x1 linestyle 4,\
      "data/translation.csv" using 2:($6)/1e9 with line lc rgb '#006400' title 'Our runtime'
