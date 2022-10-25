; ID: 1184
; Author: indiepath
; Date: 2004-11-03 08:32:24
; Title: 3D Line Lib
; Description: Draw lines using 3D acceleration,

; #########################################################################
; #  3D Accelerated Line Library (c)2004 Tim Fisher                       #
; #  This code is public domain, do wih it what you like. 				  #
; #########################################################################

Global screenwidth# 
Global screenheight# 

Global LineSurf
Global LineTex
Global LineCam
Global LineMesh
Global Pivot0 = CreatePivot()
Global Pivot1 = CreatePivot()


Function ClsLines()
	ClearSurface(LineSurf)
End Function

Function InitLine3d()
	LineTex = CreateTexture(16,16,4+256)
	SetBuffer TextureBuffer(LineTex)
	Line 7,0,7,16
	Line 8,0,8,16
	SetBuffer BackBuffer()
	LineCam = CreateCamera()
	PositionEntity LineCam,0,0,-40
	CameraProjMode LineCam,2
	CameraZoom LineCam,0.1
	LineMesh = CreateMesh()
	LineSurf = CreateSurface(LineMesh)
	EntityBlend LineMesh,3
	EntityTexture LineMesh,LineTex
	EntityFX LineMesh,3
	ScreenWidth# = GraphicsWidth()
	ScreenHeight# = GraphicsHeight()
End Function

Function Line3d(x0#,y0#,x1#,y1# , r=255, g=255, b=255, w# = .12 )
	x0# = (x0# / 40.0) - 10.0
	y0# = 7.5 - (y0# / 40.0)
	x1# = (x1# / 40) - 10.0
	y1# = 7.5 - (y1# / 40.0)
	PositionEntity Pivot0, x0#,0,y0#
	PositionEntity Pivot1, x1#,0,y1#
	PointEntity Pivot0,Pivot1
	TFormNormal 0,0,1,Pivot0,0
	dx# = TFormedX#()*w#
	dy# = TFormedZ#()*w#
	v0# = AddVertex( LineSurf 	, x0# - dy#	, y0# + dx# , 0		,0,0 )
	v1# = AddVertex( LineSurf	, x1# - dy#	, y1# + dx#	, 0 	,0,1 )
	v2# = AddVertex( LineSurf	, x1# + dy#	, y1# - dx#	, 0 	,1,0 )
	v3# = AddVertex( LineSurf	, x0# + dy#	, y0# - dx#	, 0 	,1,1 )
	For v = v0 To v3
		VertexColor LineSurf, v, r,g, b
	Next
	AddTriangle LineSurf,v0,v1,v2 
	AddTriangle LineSurf,v2,v3,v0
	Return
End Function
