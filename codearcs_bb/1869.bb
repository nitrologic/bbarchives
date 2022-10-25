; ID: 1869
; Author: mindstorms
; Date: 2006-11-24 13:35:15
; Title: resizeterrain
; Description: resizes a terrain to an absolute value

Function resizeTerrain(terrain,x,y,z)
	BaseSize = terrainsize(terrain)
	sx# = (EntityScale(terrain,0)/BaseSize)*x
	sy# = EntityScale(terrain,1)*y	;starts at 1, no point in dividing by 1
	sz# = (EntityScale(terrain,2)/BaseSize)*z
	ScaleEntity terrain,sx,sy,sz
End Function

Function EntityScale#(entity,axis) 
    x#=GetMatElement(entity,axis,0) 
    y#=GetMatElement(entity,axis,1) 
    z#=GetMatElement(entity,axis,2) 
    Return Sqr(x*x+y*y+z*z) 
End Function 

;example
terrain = loadterrain("myterrain.bmp")
resizeterrain(terrain,500,300,500)
