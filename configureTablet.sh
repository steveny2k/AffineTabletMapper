#!/bin/bash

#xinput set-prop 'HUION PenTablet Pen' 'Coordinate Transformation Matrix' 0.390243902439024 0.0 0.030487804878049 0.0 0.666666666666667 0.134259259259259 0 0 1

#xinput set-prop 'HUION PenTablet Pen' 'Coordinate Transformation Matrix' 0.65 0 0 0 1 0 0 0 1

#xinput set-prop 'HUION PenTablet Pen' 'Coordinate Transformation Matrix' 0.390243902439024 0.0 0.030487804878049 0.0 0.666666666666667 0.134259259259259 0 0 1

#xinput set-prop 'HUION PenTablet Pen' 'Coordinate Transformation Matrix' 0.329268 0.000000 0.000000 0.666667 0.414634 0.000000

xinput set-prop\
 'HUION PenTablet Pen'\
 'Coordinate Transformation Matrix'\
 $(java -jar /home/simon/bin/java/Affine.jar w 2> /dev/null)

