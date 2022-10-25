; ID: 2775
; Author: Leon Drake
; Date: 2010-10-09 15:11:58
; Title: Import Unreal T3d into Blitz3d
; Description: Code to import Unreal Level into Blitz3d

Type T3dSurface

	Field brush,texture,tw,th
	Field texfile$,flags
	Field surface
	Field u.T3dVector,v.T3dVector
	Field n.T3dVector,o.T3dVector
	;I believe i remember reading unrealed polygons never have more than 16 vertices.
	Field vert.T3dVector[16]
	Field vcount
	Field panu#,panv#
End Type


Type T3dVector

	Field x#,y#,z#
	Field id

End Type


Type T3d2dCoord

	Field x#,y#

End Type

Function LoadT3dMesh(file$,ext$=".bmp")

	Local newmesh,tmpim
	Local fstream
	Local fline$
	
	Local mysurf.T3dSurface
	
	If file$ = "" Or ext$ = "" Then Return -1
	
	fstream = ReadFile(file$)
		
	newmesh = CreateMesh()	
		
	While Not Eof(fstream)
	
		
		fline$ = ReadLine(fstream)	
		If Instr(fline$,"Begin Polygon") <> 0 Then
			DebugLog "Found Polygon"
			mysurf = New T3dSurface
			mysurf\surface = CreateSurface(newmesh)	
		
			If Instr(fline$, "Texture") <> 0 Then
			
				mysurf\texfile$ = gett3dval$(fline$,"Texture")
				If mysurf\texfile$ <> "" Then 
					DebugLog extractdir$(file$)+mysurf\texfile$+ext$
					mysurf\texture = LoadTexture(extractdir$(file$)+mysurf\texfile$+ext$)
					tmpim = LoadImage(extractdir$(file$)+mysurf\texfile$+ext$)
					If tmpim <> 0 Then
						mysurf\tw = ImageWidth(tmpim)
						mysurf\th = ImageHeight(tmpim)
						FreeImage(tmpim)
						mysurf\brush = CreateBrush()
						BrushTexture(mysurf\brush,mysurf\texture)
					EndIf
				EndIf
			
			EndIf
			If Instr(fline$, "Flags") <> 0 Then
			
				mysurf\flags = gett3dval$(fline$,"Flags")
			
			EndIf
			
			buildt3dsurface(mysurf,newmesh,fstream)
					
		
		EndIf
	
	Wend
	
	;mesh is really big and rotated differently
	RotateEntity(newmesh,90,0,0)
	ScaleEntity(newmesh,0.5,0.5,0.5)

	CloseFile(fstream)
	
	Delete Each T3dVector
	Delete Each T3dSurface
	


End Function


Function buildt3dsurface(s.T3dSurface,mesh,fstream)

	
	Local fline$
	If s = Null Or mesh = 0 Or fstream = 0 Then Return -1
	
	While Instr(fline$,"End Polygon") = 0 
		
		fline$ = ReadLine(fstream)
		
		If Instr(fline$, "Origin") <> 0 Then
		
			s\o = gett3dxyz(fline$,"Origin")
		
		
		EndIf
		If Instr(fline$, "Normal") <> 0 Then
		
			s\n = gett3dxyz(fline$,"Normal")
		
		EndIf	
		
		If Instr(fline$, "TextureU") <> 0 Then
		
			s\u = gett3dxyz(fline$,"TextureU")
		
		EndIf	
		
		If Instr(fline$, "TextureV") <> 0 Then
		
			s\v = gett3dxyz(fline$,"TextureV")
		
		EndIf
			
		If Instr(fline$, "Pan") <> 0 Then
		
			s\panu# = Float(gett3dval$(fline$,"U"))
			s\panv# = Float(gett3dval$(fline$,"V")) 
		
			DebugLog "Pan vals U: "+s\panu#+" V: "+s\panv#
		
		EndIf	
					
		If Instr(fline$, "Vertex") <> 0 Then
		
			s\vert[s\vcount] = gett3dxyz(fline$,"Vertex")
			s\vcount = s\vcount + 1
			
		EndIf		


		
	
	Wend
	buildt3dpolygon(s,mesh)
	If s\brush <> 0 Then
		PaintSurface(s\surface,s\brush)
	EndIf
	



End Function


Function buildt3dpolygon(s.T3dSurface,mesh)

	If s = Null Or mesh = 0 Then Return -1

	Local uv.T3d2dCoord

	For i = 0 To s\vcount-1
	
		uv = GenUV(s,i)
		s\vert[i]\id = AddVertex(s\surface,s\vert[i]\x#,s\vert[i]\y#,s\vert[i]\z#,uv\x#,uv\y#,1)
		Delete uv	
	
	Next
	For i = 0 To s\vcount-3
	
	AddTriangle(s\surface,s\vert[0]\id,s\vert[i+1]\id,s\vert[i+2]\id)

	
	Next

End Function


Function DotProduct#(ax#,ay#,az#,bx#,by#,bz#)
	
	Return ((ax# * bx#) + (ay# * by#) + (az# * bz#))	
	
End Function

Function GenUV.T3d2dCoord(s.T3dSurface,vindex)
	


	If s = Null Then Return Null
	
	If s\vert[vindex] = Null Then Return Null
	
	Local uv.T3d2dCoord = New T3d2dCoord

	uu# = DotProduct#(s\vert[vindex]\x#-s\o\x#,s\vert[vindex]\y#-s\o\y#,s\vert[vindex]\z#-s\o\z#,s\u\x#,s\u\y#,s\u\z#)
	vv# = DotProduct#(s\vert[vindex]\x#-s\o\x#,s\vert[vindex]\y#-s\o\y#,s\vert[vindex]\z#-s\o\z#,s\v\x#,s\v\y#,s\v\z#)
	uv\x# = (uu#+s\panu#)*(1.0/s\tw)
	uv\y# = (vv#+s\panv#)*(1.0/s\th)
	
	Return uv
	

End Function

Function gett3dxyz.T3dVector(fline$,item$)

	Local xyz.T3dVector
	Local tcoords$[3]
	Local nextone = 0
	
	fline$ = Replace(fline$,item$,"")
	fline$ = Replace(fline$," ","")
	For getpos = 1 To Len(fline$)
										
		tcoords[nextone] = tcoords[nextone] + Mid(fline$,getpos,1)
		If Mid(fline$,getpos,1) = "," Then 
			tcoords[nextone] = Replace(tcoords[nextone],",","")
			nextone = nextone + 1
		EndIf	
	Next	
	
	xyz.T3dVector = New T3dVector
	
	xyz\x# = Float(tcoords[0])
	xyz\y# = Float(tcoords[1])
	xyz\z# = Float(tcoords[2])
	
	Return xyz

End Function

Function gett3dval$(fline$,item$)

	Local newval$,i
	Local offset = Instr(fline$, item$)+Len(item$)
	
	For i = offset To Len(fline$)
	
		newval$ = newval$ + Mid(fline$,i,1)
		If Mid(fline$,i,1) = " " Then 
			newval$ = Replace(newval$,"=","")
			newval$ = Replace(newval$," ","")
			
			Return newval$
		EndIf
	Next
	Return newval$


End Function


Function extractdir$(filename$)

	If Len(filename$) = 0 Then Return filename$

	For i = Len(filename$) To 1 Step -1
	
		
	
		If Mid(filename$,i,1) = "\" Or Mid(filename$,i,1) = "/" Then
		
			
			Return Left(filename$,i)
		
		EndIf
	
	Next
	Return ""


End Function










;*******************************************************************************************************************
;DEMO
;*******************************************************************************************************************




Graphics3D 640,480,16,2 

Global player=CreatePivot()
Global floorpivot=CreatePivot()
Global camera=CreateCamera(player)
Global campitch;,mvx,mvy,mvz

Text 0,0,"Loading map... please wait."
Flip	
LoadT3dMesh("deck16.t3d",".BMP")
AmbientLight(200,200,200)

Repeat
Cls
freelook()
UpdateWorld()
RenderWorld()

Flip
Until KeyHit(1)
End


Function freelook()

	mxspd#=MouseXSpeed()*0.25
	myspd#=MouseYSpeed()*0.25
	
	
		
	MoveMouse GraphicsWidth()/2,GraphicsHeight()/2

	campitch=campitch+myspd
	If campitch<-85 Then campitch=-85
	If campitch>85 Then campitch=85
	RotateEntity player,campitch,EntityYaw(player)-mxspd,0
	
	If KeyDown(203) Then mvx=-2
	If KeyDown(205) Then mvx=+2
	If KeyDown(200) Then mvz=+2
	If KeyDown(208) Then mvz=-2
	
	If KeyDown(30) Then mvx=-2
	If KeyDown(32) Then mvx=+2
	If KeyDown(17) Then mvz=+2
	If KeyDown(31) Then mvz=-2
	
	

	
	MoveEntity player,mvx,0,mvz
	TranslateEntity player,0,mvy,0
	
End Function

;*******************************************************************************************************************
;*******************************************************************************************************************
