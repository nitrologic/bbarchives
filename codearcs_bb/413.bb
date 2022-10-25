; ID: 413
; Author: EdzUp[GD]
; Date: 2002-08-29 12:18:22
; Title: InsideCheck
; Description: The function checks to see if a point is within another point

;Inside check
;this will check to see if a specified 3d position is within another
;entity.

;This code uses the box code from Rob Cummings so he deserves some of the
;credit too =).

;if you use the code please give me (EdzUp) and Rob Cummings credit for
;the code :)

Function InsideCheck( Ent, x#, y#, z# )
	Local tx#, ty#, tz#			;Top Left
	Local tx2#, ty2#, tz2#		;Top Right
	Local bx#, by#, bz#			;Bottom Left
	Local bx2#, by2#, bz2#		;Bottom Right
	Local Top#, Bottom#			;absolute highest and lowest
	
	Local VX#, VY#, VZ#			;Vertex coords
	
	Local Inside=0				;this will be 0 if its not inside
	
	;We really need to check to see if the player is within the entity
	If Ent=0 Return 0
	
	For i=0 To CountSurfaces( Ent )-1
		s = GetSurface( Ent, 1 )
		For v=0 To CountVertices( s )-1 
            TFormPoint VertexX(s,v),VertexY(s,v),VertexZ(s,v),ent,0
		
			VX# = TFormedX#()
			VY# = TFormedY#()
			VZ# = TFormedZ#()
						
			;now we need to check to see if its in a better
			;position that any of our other vertex coords.
			If vy#>Top# Then Top# = vy#
			If vy#<Bottom# Then Bottom# = vy#
			;vx
			If vx#<tx# Then tx#=vx#
			If vx#<bx# Then bx#=vx#
			If vx#>tx2# Then tx2#=vx#
			If vx#>bx2# Then bx2#=vx#
						
			;vy
			If vy#>ty# Then ty#=vy#
			If vy#>ty2# Then ty2#=vy#
			If vy#<by# Then by#=vy#
			If vy#<by2# Then by2#=vy#
				
			;vz
			If vz#<tz# Then tz#=vz#
			If vz#<bz# Then bz#=vz#
			If vz#>tz2# Then tz2#=vz#
			If vz#>bz2# Then bz2#=vz#						
		Next
	Next
	
	;now we need to see if the point is within the entity
	ok=0
	If bx#<=x# And tx#<=x# Then	ok=ok+1
	If bx2#>x# And tx2#>x# Then ok=ok+1
	If bz#<z#  And tz#<z#  Then	ok=ok+1
	If bz2#>=z# And tz2#>=z Then ok=ok+1
	If top#>=y# And bottom#<y# Then	ok=ok+1

	If ok=5 Then Return 1 Else Return 0	;0 if point not inside entity
End Function
