; ID: 1323
; Author: jfk EO-11110
; Date: 2005-03-11 13:24:57
; Title: Import OBJ
; Description: Load an .OBJ Mesh in Blitz3D

; By Terabit

Function LoadObj(file$)
	Local infile, intex$, cmd$, themesh, blanksurf, ek, its
	Local P1$, P2$, P3$, P4$, vtx1#, vtx2#, vtx3#, vtx4#
	Local tx1#, tx2#, tx3#, tx4#, retmesh, v, uvo	
	If FileType(file$)=0 Then Return -1
	infile = ReadFile(file$)
	
	themesh = CreateMesh()
	tempvert = CreateMesh()

	retmesh = CreateMesh()
	
	TempVertSurf = CreateSurface(tempvert)
	blanksurf = CreateSurface(themesh)
	
	While Not Eof(infile)
	intex$ = ReadLine$(infile)
	cmd$ = ParseEntry(intex$,0)
	its = CountIt(intex$," ")
	
	If its>1 Then P1$ = ParseEntry(Intex$,1) Else p1$=""

	If its>2 Then P2$ = ParseEntry(Intex$,2) Else p2$=""

	If its>3 Then P3$ = ParseEntry(Intex$,3) Else p3$=""

	If its>4 Then P4$ = ParseEntry(Intex$,4) Else p4$=""
	
	;DebugLog "Command: "+cmd$+" P1: "+p1$+" P2: "+p2$+" P3: "+p3$+" P4: "+p4$
	
	Select cmd$
	Case "V"
	AddVertex blanksurf,Float(P1$),Float(P2$),Float(P3$)*-1
	Case "VT"
	AddVertex TempVertSurf,Rand(10),Rand(10),Rand(10)
	VertexTexCoords TempVertSurf,vtx,Float(P1$),1-Float(P2$)
	;DebugLog "P1$="+p1$+" P2$= "+p2$

	;DebugLog "U = "+P1$+" V = "+P2$
	vtx = vtx + 1
	Case "USEMTL"
	DebugLog "Hoot "+P1$
	currentsurf = CreateSurface(retmesh) 
	If Trim$(P1$)<>"" Then
		If Instr(p1$,".")<>0 And FileType(modelpath$+p1$)<>0 Then sometex = LoadTexture(modelpath$+p1$,1+VRAM) : Goto passway
		If FileType(modelpath$+p1$+".PNG")<>0 Then sometex = LoadTexture(modelpath$+p1$+".PNG",1+VRAM) : Goto passway
		If FileType(modelpath$+p1$+".BMP")<>0 Then sometex = LoadTexture(modelpath$+p1$+".BMP",1+VRAM) : Goto passway
		If FileType(modelpath$+p1$+".TGA")<>0 Then sometex = LoadTexture(modelpath$+p1$+".TGA",1+VRAM) : Goto passway
		If FileType(modelpath$+p1$+".JPG")<>0 Then sometex = LoadTexture(modelpath$+p1$+".JPG",1+VRAM) : Goto passway

		sometex = CreateTexture(512,512,1+VRAM)
		SetBuffer TextureBuffer(sometex)
		ClsColor 128,128,128
		Cls
		SetBuffer BackBuffer()
		savetex(p1$+".PNG",sometex)
		FreeTexture sometex
		sometex = LoadTexture(modelpath$+p1$+".PNG",1+VRAM)
.passway
			If sometex<>0 Then
				somebrush = CreateBrush(255,255,255)
				BrushTexture somebrush,sometex
				PaintSurface currentsurf,somebrush
			EndIf
		EndIf
	Case "F"
	If currentsurf = 0 Then 
		currentsurf = CreateSurface(retmesh)
		sometex = CreateTexture(512,512,1+VRAM)
		SetBuffer TextureBuffer(sometex)
		ClsColor 128,128,128
		Cls
		SetBuffer BackBuffer()
		somebrush = CreateBrush(255,255,255)
		BrushTexture somebrush,sometex
		PaintSurface currentsurf,somebrush
	EndIf
	
		Vtx1# = -65535 : TX1# = -65535 
		Vtx2# = -65535 : TX2# = -65535 
		Vtx3# = -65535 : TX3# = -65535 
		Vtx4# = -65535 : TX4# = -65535 
			
		If its>1 Then
			ug = CountIT(P1$,"/")
			If ug>0 Then
			Vtx1#=Float(ParseEntry(p1$,0,"/"))-1
			EndIf
	
			If ug>1 Then
			tx1# = Float(ParseEntry(p1$,1,"/"))-1
			EndIf
		EndIf

		If its>2 Then
			ug = CountIT(P2$,"/")
			If ug>0 Then
			Vtx2#=Float(ParseEntry(p2$,0,"/"))-1
			EndIf
	
			If ug>1 Then
			tx2# = Float(ParseEntry(p2$,1,"/"))-1
			EndIf
		EndIf

		If its>3 Then
			ug = CountIT(P3$,"/")
			If ug>0 Then
			Vtx3#=Float(ParseEntry(p3$,0,"/"))-1
			EndIf
	
			If ug>1 Then
			tx3# = Float(ParseEntry(p3$,1,"/"))-1
			EndIf
		EndIf
		
		
		If its>4 Then
			ug = CountIT(P4$,"/")
			If ug>0 Then
			Vtx4#=Float(ParseEntry(p4$,0,"/"))-1
			EndIf
	
			If ug>1 Then
			tx4# = Float(ParseEntry(p4$,1,"/"))-1
			EndIf
		EndIf

		If vtx1<0 Then vtx1 = 0
		If vtx2<0 Then vtx2 = 0
		If vtx3<0 Then vtx3 = 0
		If vtx4<0 Then vtx4 = 0
		
		If vtx1>CountVertices(blanksurf)-1 Then vtx1 = CountVertices(blanksurf)-1
		If vtx2>CountVertices(blanksurf)-1 Then vtx2 = CountVertices(blanksurf)-1
		If vtx3>CountVertices(blanksurf)-1 Then vtx3 = CountVertices(blanksurf)-1
		If vtx4>CountVertices(blanksurf)-1 Then vtx4 = CountVertices(blanksurf)-1
		
		If tx1>CountVertices(tempvertsurf)-1 Then tx1 = CountVertices(tempvertsurf)-1
		If tx2>CountVertices(tempvertsurf)-1 Then tx2 = CountVertices(tempvertsurf)-1
		If tx3>CountVertices(tempvertsurf)-1 Then tx3 = CountVertices(tempvertsurf)-1
		If tx4>CountVertices(tempvertsurf)-1 Then tx4 = CountVertices(tempvertsurf)-1
		
		v = CountVertices(currentsurf)
		
		If its>1 Then
			
			If tx1<0 Then tx1 = tx1 + vtx+1 
			
			UVO = TX1
			If CountVertices(TempVertSurf)=0 Then
				AddVertex currentsurf,VertexX(blanksurf,vtx1),VertexY(blanksurf,vtx1),VertexZ(blanksurf,vtx1)
			Else
				AddVertex currentsurf,VertexX(blanksurf,vtx1),VertexY(blanksurf,vtx1),VertexZ(blanksurf,vtx1),VertexU(TempVertSurf,UVO),VertexV(TempVertSurf,UVO)
			EndIf
		EndIf
		If its>2 Then
			If tx2<0 Then tx2 = tx2 + vtx+1   		
			UVO = TX2
			;DebugLog "TX2= "+tx2
			If CountVertices(TempVertSurf)=0 Then
				AddVertex currentsurf,VertexX(blanksurf,vtx2),VertexY(blanksurf,vtx2),VertexZ(blanksurf,vtx2)
			Else
				AddVertex currentsurf,VertexX(blanksurf,vtx2),VertexY(blanksurf,vtx2),VertexZ(blanksurf,vtx2),VertexU(TempVertSurf,UVO),VertexV(TempVertSurf,UVO)
			EndIf
		EndIf
		If its>3 Then
			If tx3<0 Then tx3 = tx3 + vtx+1
			UVO = TX3
			;DebugLog "TX3= "+tx3
			If CountVertices(TempVertSurf)=0 Then
			AddVertex currentsurf,VertexX(blanksurf,vtx3),VertexY(blanksurf,vtx3),VertexZ(blanksurf,vtx3)
			Else
			AddVertex currentsurf,VertexX(blanksurf,vtx3),VertexY(blanksurf,vtx3),VertexZ(blanksurf,vtx3),VertexU(TempVertSurf,UVO),VertexV(TempVertSurf,UVO)
			EndIf
		EndIf
		If its>4 Then
			If tx4<0 Then tx4 = tx4 + vtx+1  		
			UVO = TX4
			If CountVertices(TempVertSurf)=0 Then
			AddVertex currentsurf,VertexX(blanksurf,vtx4),VertexY(blanksurf,vtx4),VertexZ(blanksurf,vtx4)
			Else
			AddVertex currentsurf,VertexX(blanksurf,vtx4),VertexY(blanksurf,vtx4),VertexZ(blanksurf,vtx4),VertexU(TempVertSurf,UVO),VertexV(TempVertSurf,UVO)
			EndIf
		EndIf
		
		AddTriangle currentsurf,v,v+1,v+2
		
		If its>4 Then
			AddTriangle currentsurf,v+2,v+3,v
		EndIf
				
	End Select
	
	Wend
	CloseFile infile
	FitMesh retmesh,0,0,0,1,1,1,True
	FlipMesh retmesh
	centermesh (retmesh)
	UpdateNormals retmesh
	FreeEntity tempvert
	FreeEntity themesh
	
	Return retmesh
End Function

Function ParseEntry$( Message$, Item, Sep$ = " ")
	Local fas, count, spos, epos, num#, epon#,pos
	
	Repeat
		fas = Instr(message$,sep$,fas+1)
		count = count + 1 
	Until fas = 0 Or count = item
	
	spos = fas+1
	epos = Instr(message$+sep$,sep$,fas+1)
	grub$ = Upper$(Trim$(Mid$(message$,spos,(epos-spos))))
	If Instr(grub$,"E-") Then 
		pos = Instr(grub$,"E-")
		;1.41e-02 = 1.41 * (10 ^ -2)
		num# = Left$(grub$,pos-1)
		epon# = Mid$(grub$,pos+1,Len(grub$)-(pos))
		;'DebugLog "Total: "+grub$
		;'DebugLog "Num: "+num#
		;'DebugLog "etex: "+Mid$(grub$,pos+1,Len(grub$)-(pos))
		;'DebugLog "Exp: "+epon#
		num# = num# * (10 ^ epon#)
		Return num#
		Return "0.0"
	Else 
		Return grub$
	EndIf
End Function

Function CountIt( Message$, Sep$=" ")
	message$=Trim$(message$)
	Local fas, count
	Repeat
	fas = Instr(message$,sep$,fas+1)
		If fas<>0 Then count = count + 1 
	Until fas = 0
	
	Return count+1
End Function
Function CenterMesh (entity)
    FitMesh entity, -(MeshWidth (entity) / 2), -(MeshHeight (entity) / 2), -(MeshDepth (entity) / 2), MeshWidth (entity), MeshHeight (entity), MeshDepth (entity)
End Function
