; ID: 471
; Author: sswift
; Date: 2002-10-26 17:28:49
; Title: Lines_Intersect()
; Description: This function determines if two line segments intersect in 2D, and if so, where they intersect.

Global Intersection_X#									; Values returned by the Lines_Intersect() function.
Global Intersection_Y#
Global Intersection_AB#
Global Intersection_CD#


; -------------------------------------------------------------------------------------------------------------------
; This function determines if two lines in intersect in 2D.
; 
; A & B are the endpoints of the first line segment.  C & D are the endpoints of the second.
;
;
; If the lines DO NOT instersect, the function returns FALSE.
;
; If the lines DO intersect, the point of intersection is returned in the global variables: 
; Intersection_X#, Intersection_Y#, Intersection_AB#, and Intersection_CD#
;
;
; Those last two variables indicate the location along each line segment where the point of intersection lies.
;
; For example:
;
; If Intersection_AB# is 0, then the point of intersection is at point A.  If it is 1, then it is at point B.
; If it is 0.5, then it is halfway between the two.  And if it is less than 0 or greater than 1, then the point lies
; on the line but outside of the specified line segment.
;
;
; Because you can determine if the intersection point lies within both line segments, you can also use this function
; to check to see if the line segments themselves intersect.
;
; Also, if these line segments indicate vectors of motion, then if either of the location values returned is negative
; then you know that the objects paths intersected in the past, and will not intersect in the future.
;
; And finally, please note that segments which are coincident (lie on the same line) are considered to be
; non-intersecting, as there is no single point of intersection.  You can easily detect this condition by changing
; the code below slightly as indicated.
; -------------------------------------------------------------------------------------------------------------------
Function Lines_Intersect(Ax#, Ay#, Bx#, By#, Cx#, Cy#, Dx#, Dy#)
  

	Rn# = (Ay#-Cy#)*(Dx#-Cx#) - (Ax#-Cx#)*(Dy#-Cy#)
        Rd# = (Bx#-Ax#)*(Dy#-Cy#) - (By#-Ay#)*(Dx#-Cx#)

	If Rd# = 0 
		
		; Lines are parralel.

		; If Rn# is also 0 then lines are coincident.  All points intersect. 
		; Otherwise, there is no intersection point.
	
		Return False
	
	Else
	
		; The lines intersect at some point.  Calculate the intersection point.
	
                Sn# = (Ay#-Cy#)*(Bx#-Ax#) - (Ax#-Cx#)*(By#-Ay#)

		Intersection_AB# = Rn# / Rd#
		Intersection_CD# = Sn# / Rd#

		Intersection_X# = Ax# + Intersection_AB#*(Bx#-Ax#)
         	Intersection_Y# = Ay# + Intersection_AB#*(By#-Ay#)
			
		Return True
		
	EndIf


End Function
