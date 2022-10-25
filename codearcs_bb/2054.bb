; ID: 2054
; Author: _33
; Date: 2007-07-05 21:44:40
; Title: Funky 3D titlescreen
; Description: A 3D titlescreen I created for a project

AppTitle "MAZEGAME"

;----------------------------------------------------------------------------------------
; Define Global things
;----------------------------------------------------------------------------------------
Graphics3D 1024,768,32,1
Global fontsize# = 168
Global ptr_fnt = LoadFont("Arial",fontsize#/2,True,False,False)
Global scrollcycle = 0

Global doing_nothing_fps = 0
Global camera%, ptr_light%

Dim sin_tb#(1079),cos_tb#(1079)
For i=0 To 1079: sin_tb#(i)=Sin(i): cos_tb#(i)=Cos(i): Next
;Dim getcos(3),getsin(3): getcos(0)=1: getcos(2)=-1: getsin(1)=1: getsin(3)=-1

init_title_screen()
While GetKey() = 0
   IntitleLoop()
   RenderWorld
   Flip 1
   scrollcycle = scrollcycle + 1 : If scrollcycle > 359 Then scrollcycle = scrollcycle - 360
   If doing_nothing_fps > 650 Then Exit
Wend
close_title_screen()
End


;----------------------------------------------------------------------------------------
; title screen management
;----------------------------------------------------------------------------------------
Function init_title_screen()
   doing_nothing_fps = 0
   camera = CreateCamera()

   ptr_light = CreateLight()
   LightColor ptr_light,0,0,0
   RotateEntity ptr_light,90,0,0
   AmbientLight 0,0,0

   t_obj.object_info = New object_info
   t_obj\ptr = CreateTexture(fontsize# * 2,fontsize#,256)
   t_obj\object_type = 2
   SetBuffer TextureBuffer(t_obj\ptr)
   ClsColor 255,255,0
   Cls
   Color 255, 0, 0
   SetFont ptr_fnt
   Text fontsize# * 1.5,fontsize# * 0.75,"MAZEGAME",True,True
   t_tex = t_obj\ptr
   SetBuffer BackBuffer()

   For z = 1 To 6
      For x = -z To z
         For y = -z To z
            t_obj.object_info = New object_info
            t_obj\ptr = CreateCylinder(36 - z * 4)
            t_obj\object_type = 1
            RotateEntity t_obj\ptr,15 * x,0,-15 * y
            ScaleEntity t_obj\ptr,8,2,8
            PositionEntity t_obj\ptr,32 * x, 32 * y,32 * z
            EntityTexture t_obj\ptr,t_tex,0,1 
         Next
      Next
   Next

   CameraFogMode camera,1
   CameraFogRange camera,64,224

End Function

Function IntitleLoop()
   Local lc#
   If doing_nothing_fps <= 250 Then
      lc = doing_nothing_fps 
      AmbientLight lc,lc,lc
   EndIf
   If doing_nothing_fps >= 400 Then
      lc = (650 - doing_nothing_fps)
      AmbientLight lc,lc,lc
   EndIf

   lx = (cos_tb#(scrollcycle * 2) * 30)
   ly = (sin_tb#(scrollcycle * 2) * 30)
   LightColor ptr_light, lx * 8, ly * 8, ly * 8
   PositionEntity ptr_light,lx,ly,0

   If doing_nothing_fps < 500 Then
      PositionEntity camera,0,0,(cos_tb#(scrollcycle * 2 + 120) * 15)
      For t_obj.object_info = Each object_info
         If t_obj\object_type = 1 Then
            TurnEntity t_obj\ptr,0,sin_tb#(scrollcycle * 2 + 270) * 2, sin_tb#(scrollcycle * 2) * 2.5
         EndIf
      Next
   EndIf

   doing_nothing_fps = doing_nothing_fps + 1
End Function

Function close_title_screen()
   For t_obj.object_info = Each object_info
      If t_obj\object_type = 1 Then
         FreeEntity t_obj\ptr
      ElseIf t_obj\object_type = 2 Then
         FreeTexture t_obj\ptr
      EndIf
      Delete t_obj.object_info
   Next

End Function

Type object_info
   Field ptr
   Field xpos#
   Field ypos#
   Field zpos#
   Field object_type%
End Type
