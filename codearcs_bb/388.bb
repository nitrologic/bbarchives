; ID: 388
; Author: Kev
; Date: 2002-08-11 12:29:10
; Title: Pacman maze
; Description: creates a pacman type maze

;
; Filename: 3Dmaps.bb
;           By kev poole 

; setup graphics mode.
Graphics3D 640,480,16,2
SetBuffer BackBuffer()

; create a camera and create a light.
Global camera=CreateCamera()
PositionEntity camera,15,9,-13
Global light=CreateLight()

; numer of the rows/coloms in the map
Global max_rows=18,max_cols=18

; the map tiles array.
Dim tiles(max_rows,max_cols)

; create a wall 
entity=CreateCube():HideEntity entity

; point at the level1 data.
Restore level1

; read the level data.
For row=0 To max_rows-1
	For col=0 To max_cols-1
		
		Read tile
		If title=999 Then Exit
		
		; fill the array with the data.
		tiles(col,row)=tile
		
	Next
Next

; draw the level data.
For row=0 To max_rows-1
	For col=0 To max_cols-1
		copy=CopyEntity(entity)

		; only if theres a wall draw one.
		If tiles(col,row)>0 Then	
			PositionEntity copy,col*2,0,row*2
			EntityColor copy,255,Rnd(col*32),Rnd(row*32)
		EndIf
		
	Next
Next

; wait for the esc key.
While Not KeyDown(1)

	; easy move around to look.
	If KeyDown(200) Then
		MoveEntity camera,0,0,.05
	EndIf

	If KeyDown(208) Then
		MoveEntity camera,0,0,-.05 
	EndIf

	If KeyDown(205) Then
		MoveEntity camera,.05,0,0 
	EndIf

	If KeyDown(203) Then
		MoveEntity camera,-.05,0,0 
	EndIf
	
	If KeyDown(30) Then
		MoveEntity camera,0,.05,0 
	EndIf

	If KeyDown(44) Then
		MoveEntity camera,0,-.05,0 
	EndIf

	UpdateWorld
	RenderWorld
	
	Flip

Wend
End

.level1
; level data 0=no wall 1=wall
Data 1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1
Data 1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1
Data 1,0,1,0,1,1,1,1,1,1,1,1,1,1,1,1,0,1
Data 1,0,1,0,1,0,1,1,0,0,0,0,0,0,0,0,0,1
Data 1,0,1,0,1,0,0,1,0,1,1,1,1,1,1,1,0,1
Data 1,0,1,0,1,1,0,1,0,1,0,0,0,1,0,0,0,1
Data 1,0,1,0,0,0,0,1,0,1,0,1,0,1,0,1,0,1
Data 1,0,1,0,1,0,1,1,0,1,0,1,0,0,0,1,0,1
Data 1,0,1,0,1,0,1,1,0,1,0,1,1,1,1,1,0,1
Data 1,0,1,0,1,0,0,0,0,1,0,1,0,0,0,1,0,1
Data 1,0,1,0,1,1,1,1,1,1,0,1,0,1,1,1,0,1
Data 1,0,1,0,0,0,0,0,0,0,0,1,0,1,0,1,0,1
Data 1,0,1,1,1,1,1,1,1,1,1,1,0,1,0,1,0,1
Data 1,0,1,0,0,0,0,0,0,0,0,1,0,1,0,1,0,1
Data 1,0,1,0,1,1,1,1,1,1,0,0,0,1,0,1,0,1
Data 1,0,1,1,1,0,1,0,1,0,1,1,1,1,0,1,0,1
Data 1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1
Data 1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1
Data 999
