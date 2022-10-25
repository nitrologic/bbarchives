; ID: 538
; Author: Filax
; Date: 2003-01-06 07:11:54
; Title: Interactivity with 3D object
; Description: Simple function for select 3D object

Graphics3D 640,480,0,2 
SetBuffer BackBuffer() 

camera=CreateCamera() 
PositionEntity camera,0,2,-10 

light=CreateLight(2) 
PositionEntity light,40,50,-100
LightRange light,50

AmbientLight 0,0,0

plane=CreatePlane() 
EntityPickMode plane,2

cube=CreateCube() 
EntityPickMode cube,2 
PositionEntity cube,0,1,0 

cube1=CreateCube() 
EntityPickMode cube1,2 
PositionEntity cube1,5,1,0 

cube2=CreateCube() 
EntityPickMode cube2,2 
PositionEntity cube2,-5,1,0 

While Not KeyDown( 1 ) 

If Procedure_Pick3DObject(camera,cube1) Then
	Procedure_Turn3DObject(cube,90,0,0,2)
EndIf

If Procedure_Pick3DObject(camera,cube2) Then
	Procedure_Turn3DObject(cube,-90,0,0,2)
EndIf

RenderWorld 


Flip 

Wend 

End 

Function Procedure_Pick3DObject(camentity,objentity) 
	If MouseDown(1)=True Then 
		CameraPick(camentity,MouseX(),MouseY()) 
		
		If PickedEntity()=objentity Then
			Return True
		Else
			Return False
		EndIf
	EndIf
End Function

Function Procedure_Turn3DObject(objentity,Rx#,Ry#,Rz#,Speed#)
	Pitch#=0
	Yaw#=0
	Roll#=0

	Repeat
		If Sgn(Rx#)=-1 Then
			Pitch#=Pitch#-Speed#
		Else
			Pitch#=Pitch#+Speed#
		EndIf
	
		If Sgn(Ry#)=-1 Then
			Yaw#=Yaw#-Speed#
		Else
			Yaw#=Yaw#+Speed#
		EndIf
		
		If Sgn(Rz#)=-1 Then
			Roll#=Roll#-Speed#
		Else
			Roll#=Roll#+Speed#
		EndIf


		If Sgn(Rx#)=1 And Pitch#>Rx# Then
			Return
		EndIf

		If Sgn(Rx#)=-1 And Pitch#<Rx# Then
			Return
		EndIf
	
	
		If Sgn(Ry#)=1 And Pitch#>Ry# Then
			Return
		EndIf

		If Sgn(Ry#)=-1 And Pitch#<Ry# Then
			Return
		EndIf
	
		If Sgn(Rz#)=1 And Pitch#>Rz# Then
			Return
		EndIf

		If Sgn(Rz#)=-1 And Pitch#<Rz# Then
			Return
		EndIf

		RotateEntity objentity,Pitch#,Yaw#,Roll#
		RenderWorld
		Flip
	Forever
Return
End Function
