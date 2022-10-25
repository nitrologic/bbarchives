; ID: 850
; Author: Beaker
; Date: 2003-12-08 20:03:29
; Title: Alpha Zorder
; Description: 'Instant' z-sorter for alpha'd triangles

; code by Beaker 2002 - please credit prominently (or arrange otherwise)



Function setup_example(mesh)
	zo.zorder = Zorderize(mesh)
End Function

Function mainloop_example(cam)
	For zo.zorder = Each zorder
		SortAlpha(zo,cam)
	Next
End Function



Type zorder
	Field hiddenmesh
	Field hiddensurf
	Field screenmesh
	Field screensurf
	Field x,y,z		;banks: size = tricnt * 2bytes(short)
	Field tricnt	;counttriangles(surf)-1
	Field incy		;use the y axis
End Type


Function Zorderize.zorder(mesh, incy = False)
	zo.zorder = New zorder
	zo\hiddenmesh = mesh
	zo\incy = incy

	zo\hiddensurf = GetSurface(zo\hiddenmesh,1)

	zo\tricnt = CountTriangles(zo\hiddensurf)-1
	zo\x = CreateBank(zo\tricnt*2+8)
	DebugLog BankSize(zo\x)
	If incy
		zo\y = CreateBank(zo\tricnt*2+8)
	EndIf
	zo\z = CreateBank(zo\tricnt*2+8)

	For f = 0 To zo\tricnt
		PokeShort zo\x, f*2, f
		If incy
			PokeShort zo\y, f*2, f
		EndIf
		PokeShort zo\z, f*2, f
	Next

	zo\screenmesh = CopyMesh(zo\hiddenmesh)
;	zo\screenmesh = CopyMesh(zo\hiddenmesh,getparent(zo\hiddenmesh))
	HideEntity zo\hiddenmesh
;	EntityFX zo\screenmesh,16

	zo\screensurf = GetSurface(zo\screenmesh,1)

	surf = zo\hiddensurf	;GetSurface(fullmesh,1)
	If Not surf Then RuntimeError "No surface found"
	ordered = False
	While ordered = False
		ordered = True
		For f = 1 To zo\tricnt
	;		If f > 0
				v0 = TriangleVertex ( surf,PeekShort(zo\x,f*2),0 )
				v1 = TriangleVertex ( surf,PeekShort(zo\x,f*2),1 )
				v2 = TriangleVertex ( surf,PeekShort(zo\x,f*2),2 )
				medx# = (VertexX(surf,v0)+VertexX(surf,v1)+VertexX(surf,v2)) / 3.0

				v0b = TriangleVertex ( surf,PeekShort(zo\x,(f-1)*2),0 )
				v1b = TriangleVertex ( surf,PeekShort(zo\x,(f-1)*2),1 )
				v2b = TriangleVertex ( surf,PeekShort(zo\x,(f-1)*2),2 )
				medxb# = (VertexX(surf,v0b)+VertexX(surf,v1b)+VertexX(surf,v2b)) / 3.0

				If (medx) > (medxb)
					temp = PeekShort(zo\x,f*2)
					PokeShort zo\x,f*2, PeekShort(zo\x,(f-1)*2)
					PokeShort zo\x, (f-1)*2, temp
					ordered = False
				EndIf


				v0 = TriangleVertex ( surf,PeekShort(zo\z,f*2),0 )
				v1 = TriangleVertex ( surf,PeekShort(zo\z,f*2),1 )
				v2 = TriangleVertex ( surf,PeekShort(zo\z,f*2),2 )

				medz# = (VertexZ(surf,v0)+VertexZ(surf,v1)+VertexZ(surf,v2)) / 3.0

				v0b = TriangleVertex ( surf,PeekShort(zo\z,(f-1)*2),0 )
				v1b = TriangleVertex ( surf,PeekShort(zo\z,(f-1)*2),1 )
				v2b = TriangleVertex ( surf,PeekShort(zo\z,(f-1)*2),2 )
				medzb# = (VertexZ(surf,v0b)+VertexZ(surf,v1b)+VertexZ(surf,v2b)) / 3.0

				If (medz) > (medzb)
					temp = PeekShort(zo\z,f*2)
					PokeShort zo\z,f*2, PeekShort(zo\z,(f-1)*2)
					PokeShort zo\z,(f-1)*2, temp
					ordered = False
				EndIf
	;		EndIf
		Next

		If incy
			For f = 1 To zo\tricnt
				v0 = TriangleVertex ( surf,PeekShort(zo\y,f*2),0 )
				v1 = TriangleVertex ( surf,PeekShort(zo\y,f*2),1 )
				v2 = TriangleVertex ( surf,PeekShort(zo\y,f*2),2 )
				medy# = (VertexX(surf,v0)+VertexX(surf,v1)+VertexX(surf,v2)) / 3.0

				v0b = TriangleVertex ( surf,PeekShort(zo\y,(f-1)*2),0 )
				v1b = TriangleVertex ( surf,PeekShort(zo\y,(f-1)*2),1 )
				v2b = TriangleVertex ( surf,PeekShort(zo\y,(f-1)*2),2 )
				medyb# = (VertexX(surf,v0b)+VertexX(surf,v1b)+VertexX(surf,v2b)) / 3.0

				If (medy) > (medyb)
					temp = PeekShort(zo\y,f*2)
					PokeShort zo\y,f*2, PeekShort(zo\y,(f-1)*2)
					PokeShort zo\y, (f-1)*2, temp
					ordered = False
				EndIf
			Next
		EndIf

		If KeyDown(1) Then End
	Wend

	Return zo
End Function


Function SortAlpha(zo.zorder,cam)
	Local origsurf = zo\hiddensurf
	Local newsurf = zo\screensurf
	Local xf,yf,zf,f
	TFormVector 0,0,1, cam,0
	If (Abs(TFormedX()) > Abs(TFormedZ())) And (Abs(TFormedX()) > Abs(TFormedY()))
		If prev <> 1
			prev = 1
			ClearSurface newsurf,0,1
			If TFormedX() > 0
				For f = 0 To zo\tricnt
					xf = PeekShort(zo\x,f*2)
					AddTriangle ( newsurf,TriangleVertex ( origsurf,xf,0 ),TriangleVertex ( origsurf,xf,1 ),TriangleVertex ( origsurf,xf,2 ) )
				Next
			Else
				For f = zo\tricnt To 0 Step -1
					xf = PeekShort(zo\x,f*2)
					AddTriangle ( newsurf,TriangleVertex ( origsurf,xf,0 ),TriangleVertex ( origsurf,xf,1 ),TriangleVertex ( origsurf,xf,2 ) )
				Next
			EndIf
		EndIf
	Else
		If (Abs(TFormedY()) > Abs(TFormedZ()))
			If (prev <> 2) And zo\incy
				prev = 2
				ClearSurface newsurf,0,1
				If TFormedY() > 0
					For f = 0 To zo\tricnt
						yf = PeekShort(zo\y,f*2)
						AddTriangle ( newsurf,TriangleVertex ( origsurf,yf,0 ),TriangleVertex ( origsurf,yf,1 ),TriangleVertex ( origsurf,yf,2 ) )
					Next
				Else
					For f = zo\tricnt To 0 Step -1
						yf = PeekShort(zo\y,f*2)
						AddTriangle ( newsurf,TriangleVertex ( origsurf,yf,0 ),TriangleVertex ( origsurf,yf,1 ),TriangleVertex ( origsurf,yf,2 ) )
					Next
				EndIf
			EndIf
		Else
			If prev <> 3
				prev = 3
				ClearSurface newsurf,0,1
				If TFormedZ() > 0
					For f = 0 To zo\tricnt
						zf = PeekShort(zo\z,f*2)
						AddTriangle ( newsurf,TriangleVertex ( origsurf,zf,0 ),TriangleVertex ( origsurf,zf,1 ),TriangleVertex ( origsurf,zf,2 ) )
					Next
				Else
					For f = zo\tricnt To 0 Step - 1
						zf = PeekShort(zo\z,f*2)
						AddTriangle ( newsurf,TriangleVertex ( origsurf,zf,0 ),TriangleVertex ( origsurf,zf,1 ),TriangleVertex ( origsurf,zf,2 ) )
					Next
				EndIf
			EndIf
		EndIf
	EndIf
End Function

Function FreeZorder()
	For zo.zorder = Each zorder
		FreeEntity zo\screenmesh 
		FreeBank zo\x 
		FreeBank zo\y 
		FreeBank zo\z 
	Next
	Delete Each zorder
End Function
