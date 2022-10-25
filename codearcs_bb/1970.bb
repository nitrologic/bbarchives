; ID: 1970
; Author: D4NM4N
; Date: 2007-03-16 05:42:06
; Title: Doors 101 - Simple multiple door system
; Description: Very simple latching doors system with trigger

Graphics3D 640,480
SetBuffer BackBuffer()

light=CreateLight(2)
MoveEntity light,-5,5,-5


;make a camera 
cam=CreateCamera()
MoveEntity cam,4,2,-5


;create cutom type add what you like to the fields like "key numbers" etc
Type doormesh
  Field door,activate,doorstate,speed#
End Type

;Create 2 doors, ideally this needs to be 'functionized' as it would be less messy and 
could eliminate the need for d1, d2 etc.
;for example: function CreateDoor.doormesh(px,py,pz,rx,ry,rz,sz,sy,sz)

;create first door object
d1.doormesh= New doormesh
d1\activate=0
d1\doorstate=0
d1\door=CreateCube()
PositionMesh d1\door,1,0,0
ScaleEntity d1\door,1,2,.1
EntityColor d1\door,255,0,0

;create a second door object
d2.doormesh= New doormesh
d2\activate=0
d2\doorstate=0
d2\door=CopyEntity (d1\door)
MoveEntity d2\door,-2,0,0
EntityColor d1\door,0,255,0


;point camera at door just so we can see whats going on
PointEntity cam,d1\door



;main loop
Repeat

	If KeyHit(2) D1\ACTIVATE=1
	If KeyHit(3) D2\ACTIVATE=1

	
	;do world updates
	UpdateAllDoors()

        ;Render 3d to backbuffer
	RenderWorld
	
	;display instructions on bbuffer
	Text 0,0,"Press '1' or '2' To open/close doors"

        ;flip the visible screen
	Flip


Until KeyHit(1)




;------------------------End of main code--------------------------------------------




Function UpdateAllDoors()  ;gets called every loop
For d.doormesh=Each doormesh
   Select d\doorstate
      Case 0 ;if standing shut
        If d\activate         ;this is your initial trigger
           d\activate=0       ;reset trigger
           d\doorstate=2        ;door is shut so set open trigger to beginon Next loop
          ;playsound (CREAK)
       EndIf

     Case 1  ; if standing open
       If d\activate
          d\activate=0
          d\doorstate=3
          ;playsound (CREAK)
       EndIf

     Case 2  ;if opening door
      If EntityYaw(d\door)<90 d\speed=d\speed+0.02 Else d\doorstate=1
	  If EntityYaw(d\door)>90 d\speed=0.00: RotateEntity d\door,0,90,0
	

     Case 3 ;if closing door
      If EntityYaw(d\door)>0 d\speed=d\speed-0.02 Else d\doorstate=0
	  If EntityYaw(d\door)<0 
	     d\speed=0.00: 
	     RotateEntity d\door,0,0,0:
	     ;playsound (SLAM)
	  endif

   End Select

   TurnEntity d\door,0,d\speed,0
	
Next
End Function
