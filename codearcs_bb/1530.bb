; ID: 1530
; Author: Berbank
; Date: 2005-11-11 11:12:17
; Title: See if an Entity is in View
; Description: This uses Vector Maths to find out if Entity A can See Entity B with angle and range parameters

Function InView(a,b,Angle#=90,range#=10000)

	; a is the entity looking
	; b is the entity being looked for
	; Angle# is the view angle in degrees.
	; range is the distance the entity can see

	If EntityDistance(a,b) > range# Then Return False

	dx# = EntityX(a,1) - EntityX(b,1)
	dz# = EntityZ(a,1) - EntityZ(b,1)

	TFormNormal dx#,0,dz#,0,0
	nx# = TFormedX()
	nz# = TFormedZ()
	
	TFormNormal 0,0,1,a,0
	hx# = TFormedX()
	hz# = TFormedZ()
	
	dot# = (nx# * hx#) + (nz# * hz#)
	
	If ACos(dot#) < (180 - (Angle#/2))
		Return False
	Else
		Return True
	EndIf
		
End Function
