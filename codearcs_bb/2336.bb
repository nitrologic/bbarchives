; ID: 2336
; Author: John Blackledge
; Date: 2008-10-17 06:25:23
; Title: SkyPanorama loading code
; Description: Loads a skybox created by SkyPanorama

;------------------
Function LoadSkyStrip(file$)
;------------------
	tex = LoadAnimTexture(file$,1+8+16+32,512,512,0,6)
	hEntSkyBox=CreateMesh()
	b = CreateBrush()
	BrushTexture b,tex,0

	;back face
	b = CreateBrush()
	BrushTexture b,tex,0
	s=CreateSurface(hEntSkyBox,b )
	AddVertex s,+1,+1,+1,0,0:AddVertex s,-1,+1,+1,1,0
	AddVertex s,-1,-1,+1,1,1:AddVertex s,+1,-1,+1,0,1
	AddTriangle s,0,1,2:AddTriangle s,0,2,3
	FreeBrush b
	;left face
	b = CreateBrush()
	BrushTexture b,tex,1
	s=CreateSurface(hEntSkyBox,b )
	AddVertex s,-1,+1,+1,0,0:AddVertex s,-1,+1,-1,1,0
	AddVertex s,-1,-1,-1,1,1:AddVertex s,-1,-1,+1,0,1
	AddTriangle s,0,1,2:AddTriangle s,0,2,3
	FreeBrush b
	;front face
	b = CreateBrush()
	BrushTexture b,tex,2
	s=CreateSurface(hEntSkyBox,b )
	AddVertex s,-1,+1,-1,0,0:AddVertex s,+1,+1,-1,1,0
	AddVertex s,+1,-1,-1,1,1:AddVertex s,-1,-1,-1,0,1
	AddTriangle s,0,1,2:AddTriangle s,0,2,3
	FreeBrush b
	;right face
	b = CreateBrush()
	BrushTexture b,tex,3
	s=CreateSurface(hEntSkyBox,b )
	AddVertex s,+1,+1,-1,0,0:AddVertex s,+1,+1,+1,1,0
	AddVertex s,+1,-1,+1,1,1:AddVertex s,+1,-1,-1,0,1
	AddTriangle s,0,1,2:AddTriangle s,0,2,3
	FreeBrush b

	;top face
	b = CreateBrush()
	BrushTexture b,tex,4
	s=CreateSurface(hEntSkyBox,b )
	AddVertex s,-1,+1,+1,0,1:AddVertex s,+1,+1,+1,0,0
	AddVertex s,+1,+1,-1,1,0:AddVertex s,-1,+1,-1,1,1
	AddTriangle s,0,1,2:AddTriangle s,0,2,3
	FreeBrush b
	;bottom face	
	b = CreateBrush()
	BrushTexture b,tex,5
	s=CreateSurface(hEntSkyBox,b )
	AddVertex s,-1,-1,-1,1,0:AddVertex s,+1,-1,-1,1,1
	AddVertex s,+1,-1,+1,0,1:AddVertex s,-1,-1,+1,0,0
	AddTriangle s,0,1,2:AddTriangle s,0,2,3
	FreeBrush b
	
	ScaleMesh hEntSkyBox,3000,3000,3000
	FlipMesh hEntSkyBox
	EntityFX hEntSkyBox,1+8
	
	Return hEntSkyBox
End Function
