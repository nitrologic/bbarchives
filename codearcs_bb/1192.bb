; ID: 1192
; Author: aab
; Date: 2004-11-12 15:52:45
; Title: EntitySphere elapsing box check
; Description: dont look at me like that! i know, uber simple but i put it up anyway... still usefull

Function entCubeLaps(entity1,rad#,ax#,ay#,az#,havwidth#,havlen#=0,havheight#=0);checks sphere for intersection with cube
	
	If Not havlen havlen=havwidth
	If Not havheight havheight=havwidth
	

	Local piv%=CreatePivot():PositionEntity piv,EntityX(entity1),EntityY(entity1),EntityZ(entity1)
	;point it towrads 'box', and move it towards it by the suggested Radius
	entity2=CreatePivot():PositionEntity entity2,ax,ay,az
	PointEntity piv,entity2:MoveEntity piv,0,0,rad#:FreeEntity entity2
	
	Local bx#=EntityX(piv),by#=EntityY(piv),bz#=EntityZ(piv)
	
	FreeEntity piv
	
	;final condition
	If (bx>=ax-havwidth# And bx=<ax+havwidth#) And (by>=ay-havheight# And by<=ay+havheight#) And (bz>=az-havlen# And bz<=az+havlen#) Return True
	
	
	Return False
End Function
;
;
;some other crappy things
Function SphereLaps(x1#,y1#,z1#,rad1,x2#,y2#,z2#,rad2)
	dis#=Sqr((x2-x1)*(x2-x1)+(y2-y1)*(y2-y1)+(z2-z1)*(z2-z1))
	If dis#<=rad1+rad2 Return True Else Return False
End Function
;
;
;
Function entitylaps(entity1,rad1,entity2,rad2)
	If Abs(EntityDistance(entity1,entity2))<=(rad1+rad2) Return True Else Return False 
End Function
