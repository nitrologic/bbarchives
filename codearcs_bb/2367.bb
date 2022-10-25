; ID: 2367
; Author: H. T. U.
; Date: 2008-12-03 13:04:08
; Title: Crater Code
; Description: Makes a nice crater in a terrain

Function crater(entity,terrain,radius#=1,th#=100,hardness#=1)
			ex#=EntityX(entity)
			ez#=EntityZ(entity)

			For x=-radius To radius
			For z=-radius To radius
				If Sqr(x*x+z*z)<=radius And hardness<>0
					d#=Sqr(x*x+z*z)
					
					h#=TerrainHeight(terrain,ex+x,ez+z)
										
					scale#=th/hardness
					
					h=h+d/scale-radius/scale
					If h<0 Then h=0
					ModifyTerrain terrain,ex+x,ez+z,h,True
				EndIf
			Next
			Next
End Function
