; ID: 923
; Author: OpticEvIl
; Date: 2004-02-06 17:05:30
; Title: True 3D Visibility / Line of Sight code (pseudo LOS)
; Description: Fast LOS code, accounts for all polys

;True 3D Visibility V1.0
;Visibility in native B3D
;by OpticEvIl
;manntec@msn.com

;if you use this in your code, give me a little credit

AppTitle "True 3D Visibility v1.0- OpticEvIl"
;Example in 640x480
Graphics3D 640,480   ;change this only after an understanding of the code
					 ;screen w&h are hard coded in the version
SetBuffer BackBuffer() 

				 ;xstep & ystep can be increased to increase speed
				 ;at the cost of resolution
Const xstep = 5	 ;check every 5th pixel on the x-axis (2D)
Const ystep = 4	 ;check every 4th pixel on the y-axis (2D)
Const nos = 2    ;number of objects
Const now = 10   ;number of obstructions

Type Visstruct
	Field ent
	Field child
	Field rc,gc,bc
	Field vis_type1,vis_type2,vis_type3
	Field ret_type1,ret_type2,ret_type3
End Type

Dim s(nos)
Dim w(now)

Global tex = LoadTexture ("./tex.jpg") ;replace with your own tex!
Global cap,v1,v2 ;cap will contain screen step val

Global camera=CreateCamera()
PositionEntity camera,-0,0,-8

;creates obstructions, and assign them to blue channel for visibility check
;the green and blue channels are not use in the example
For q = 1 To now
	w(q) = CreateCube()
	EntityColor w(q),128,128,128
	ScaleEntity w(q),1,.2,.2
	PositionEntity w(q),q-(now/2)-1,0,-4
	RotateEntity w(q),0,0,90
	RotateEntity w(q),0,0,Rand(0,359)
	
	targetvis.VisStruct = New VisStruct
	targetvis\ent = w(q)	
	targetvis\rc  = 128
	targetvis\gc  = 128
	targetvis\bc  = 128
	targetvis\vis_type1 = 0
	targetvis\vis_type2	= 0
	targetvis\vis_type3 = 1
Next


s(1)=CreateSphere() 
EntityColor s(1),120,50,50
PositionEntity s(1),2,0,0
EntityTexture s(1),tex 


s(2)=CreateSphere() 
EntityColor s(2),120,50,50
PositionEntity s(2),2,-1.8,-6

For q = 1 To nos
	targetvis.VisStruct = New VisStruct
	targetvis\ent = s(q)	
	targetvis\rc  = 120
	targetvis\gc  = 50
	targetvis\bc  = 50
	targetvis\vis_type1 = q
	targetvis\vis_type2	= 0
	targetvis\vis_type3 = 0
Next


AmbientLight 128,128,128
light = CreateLight()

Hmills#=MilliSecs()
While Not KeyDown(1) 

	;controls
	TurnEntity camera,0,(KeyDown(203)-KeyDown(205)),0 		
	MoveEntity camera,0,0,(KeyDown(200)-KeyDown(208))*0.1
	
	cap = cap + 96
	If cap > 480-(96) Then 			; -(96) is for TS only!
		cap = 0
	End If
	
	UpdateVis()	
	
	Color 255,255,255
	If Not (frames Mod 5) Then
		ov1 = v1
		ov2 = v2
		v1 = 0
		v2 = 0
	End If
	
	fps = frames/(MilliSecs()-Hmills)*1000		
	Text 10,20,"Object 1 optical strength:"+ov1
	Text 10,40,"Object 2 optical strength:"+ov2
	Text 10,60,"FPS:"+fps
		
	Flip
	frames = frames + 1
Wend

End

Function UpdateVis()
	
	AmbientLight 0,0,0
	PrepVis()
	
	RenderWorld 

	LockBuffer BackBuffer()
	For x = 0 To 639 Step xstep 	;change step number to change resolution

		y = cap
		While y < 96+cap
			Select xRed(ReadPixelFast (x,y))
			
				Case 0
				Case 1
					v1 = v1 + 1
				Case 2
					v2 = v2 + 1
			End Select
			y = y + ystep			;change step number to change resolution
		Wend
	Next
	UnlockBuffer BackBuffer()

	AmbientLight 128,128,128
	Retvis()

	UpdateWorld	
	RenderWorld 

End Function

Function PrepVis()

	;prepare all entities for visibility checking
	For targetvis.VisStruct = Each VisStruct
		EntityFX targetvis\ent,5
		EntityColor targetvis\ent,targetvis\vis_type1,targetvis\vis_type2,targetvis\vis_type3
 	Next

End Function

Function RetVis()

	;reassign original values
	For targetvis.VisStruct = Each VisStruct
		EntityFX targetvis\ent,0        
		EntityColor targetvis\ent,targetvis\rc,targetvis\bc,targetvis\gc
 	Next

End Function

Function xRed(rgb) 	
	Return rgb Shr 16 And $ff 
End Function 

Function xGreen(rgb) 
	Return rgb Shr 8 And $ff 
End Function 

Function xBlue(rgb) 
	Return rgb And $ff 
End Function
