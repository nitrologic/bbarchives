; ID: 1785
; Author: John Blackledge
; Date: 2006-08-14 05:09:59
; Title: Centre Mesh
; Description: Centres a mesh around its centre point

;--------------------
Function CentreAnimMesh(mesh,mode=0)
;--------------------
; mode=0 : centre on 3 axes
; mode=1 : centre on x,z; align to y bottom
Local origx#,origy#,origz#
Local allx#,ally#,allz#
Local newx#,newy#,newz#
Local cc,c,childent,mult

	; get orig position
	origx# = EntityX#(mesh)
	origy# = EntityY#(mesh)
	origz# = EntityZ#(mesh)
	
	; get 'all' vertices positions
	allx# = MeshAllX#(mesh)
	If mode=0 Then ally# = MeshAllY#(mesh)
	If mode=1 Then ally# = MeshAllYlowest#(mesh)
	allz# = MeshAllZ#(mesh)

	; find difference
	newx# = (origx# - allx#)
	newy# = (origy# - ally#)
	newz# = (origz# - allz#)
	
	; move one or all meshes
	cc = CountChildren(mesh)
	If cc = 0
		PositionMesh mesh, newx#,newy#,newz#
	Else
		For c = 1 To cc
			childent = GetChild(mesh,c)
			mult = 2
			PositionMesh childent, mult * newx#,mult * newy#,mult * newz#
		Next
	EndIf

End Function

;--------------------
Function MeshAllX#(parent)
;--------------------
Local LeftX#,RightX#
Local cc,c,childent,vx#,x#

	cc = CountChildren(parent)
	If cc = 0
		x# = MeshX#(parent)
	Else
		For c = 1 To cc
			childent = GetChild(parent,c)
			vx# = MeshX#(childent)
			If vx#<LeftX# Then LeftX#=vx#
			If vx#>RightX# Then RightX#=vx#
		Next
		x# = LeftX# + ( (RightX# - LeftX#) / Float(2) )
	EndIf

Return x#
End Function

;--------------------
Function MeshAllY#(parent)
;--------------------
Local BottomY#,TopY#
Local cc,c,childent,vy#,y#

	cc = CountChildren(parent)
	If cc = 0
		y# = MeshY#(parent)
	Else
		For c = 1 To cc
			childent = GetChild(parent,c)
			vy# = MeshY#(childent)
			If vy#<BottomY# Then BottomY#=vy#
	    If vy#>TopY# Then TopY#=vy#
		Next
		y# = BottomY# + ( (TopY# - BottomY#) / Float(2) )
	EndIf

Return y#
End Function

;--------------------
Function MeshAllYlowest#(parent)
;--------------------
Local BottomY#,TopY#
Local cc,c,childent,vy#,y#

	cc = CountChildren(parent)
	If cc = 0
		y# = BottomMost#(parent)
	Else
		For c = 1 To cc
			childent = GetChild(parent,c)
			vy# = BottomMost#(childent)
			If vy#<BottomY# Then BottomY#=vy#
	    If vy#>TopY# Then TopY#=vy#
		Next
		y# = BottomY# + ( (TopY# - BottomY#) / Float(2) )
	EndIf

Return y#
End Function

;--------------------
Function MeshAllZ#(parent)
;--------------------
Local BackZ#,FrontZ#
Local cc,c,childent,vz#,z#

	cc = CountChildren(parent)
	If cc = 0
		z# = MeshZ#(parent)
	Else
		For c = 1 To cc
			childent = GetChild(parent,c)
			vz# = MeshZ#(childent)
			If vz#<BackZ# Then BackZ#=vz#
			If vz#>FrontZ# Then FrontZ#=vz#
		Next
		z# = BackZ# + (FrontZ# - BackZ#)/ 2
	EndIf

Return z#
End Function

;------------------------------------------------------------------------------------
;--------------------
Function MeshX#(mesh)
;--------------------
Local LeftX#,RightX#
Local s,su,v,vx#,x#

	For s = 1 To CountSurfaces(mesh)
		su = GetSurface(mesh,s)
		For v = 0  To CountVertices(su)-1
			vx# = VertexX#(su,v)
			If vx#<LeftX# Then LeftX#=vx#
			If vx#>RightX# Then RightX#=vx#
		Next
	Next
  x# = LeftX# + ( (RightX# - LeftX#) / Float(2) )

Return x#
End Function

;--------------------
Function MeshY#(mesh)
;--------------------
Local BottomY#,TopY#
Local s,su,v,vy#,y#

	For s = 1 To CountSurfaces(mesh)
		su = GetSurface(mesh,s)
		For v = 0  To CountVertices(su)-1
			vy# = VertexY#(su,v)
			If vy#<BottomY# Then BottomY#=vy#
	    If vy#>TopY# Then TopY#=vy#
		Next
	Next
	y# = BottomY# + ( (TopY# - BottomY#) / Float(2) )

Return y#
End Function

;--------------------
Function MeshZ#(mesh)
;--------------------
Local BackZ#,FrontZ#
Local s,su,v,vz#,z#

	For s = 1 To CountSurfaces(mesh)
		su = GetSurface(mesh,s)
		For v = 0  To CountVertices(su)-1
			vz# = VertexZ#(su,v)
			If vz#<BackZ# Then BackZ#=vz#
			If vz#>FrontZ# Then FrontZ#=vz#
		Next
	Next
	z# = BackZ# + (FrontZ# - BackZ#) / 2

Return z#
End Function

;--------------------
Function BottomMost#(mesh)
;--------------------
Local BottomY#,TopY#
Local s,su,v,vy#

	For s = 1 To CountSurfaces(mesh)
		su = GetSurface(mesh,s)
		For v = 0  To CountVertices(su)-1
			vy# = VertexY#(su,v)
			If vy#<BottomY# Then BottomY#=vy#
			If vy#>TopY# Then TopY#=vy#
		Next
	Next

Return BottomY#
End Function
