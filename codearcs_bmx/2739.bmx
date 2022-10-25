; ID: 2739
; Author: Matthew Smith
; Date: 2010-06-30 20:52:12
; Title: Distance between 2 squares (2d)
; Description: Calculates the distance between 2 squares in a 2d grid

function dist(x1#,y1#,x2#,y2#)
distance# = sqr((x2-x1)*(x2-x1)+(y2-y1)*(y2-y1))
return distance
end function 

OR

d = sqr((x2-x1)^2+(y2-y1)^2)
