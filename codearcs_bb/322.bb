; ID: 322
; Author: carcrash
; Date: 2002-05-16 17:38:47
; Title: mesh reassembler
; Description: Put multiple meshes back together easily

Graphics3D 1024,768,16,0
;ever have a hard time reassembling multiple meshes in blitz? this makes it easy
;write down your part offsets then position entity them as child meshes of the main object
;useful to reassemble .3ds or .x models made of multiple meshes rotate them 1st in 3dsmax
;finds the offsets you need to move each part
;written by chrisian anderson aka carcrash freeman@dimensional.com www.gothlust.com
SetBuffer BackBuffer()
move# = 0.005 ;change to whatever move amount you want
Global ScaleMeshfactor# = 0.06 ;adjust as needed
Global zoomfactor# = 1.01 ;adjust as needed zoom is lost when you change views
;put parts in order that you want to add them so if c gets atttached to b and b to a load a b c
;load your parts here hit f1 after moving 2nd part to 1st to start moving 3rd part etc
;supports an infinite number of parts
;all parts beyond the 1st 2 are hidden until you are ready to start placing them
;zoom and pans are lost when you change camera views

; LOAD MESHES HERE all info on how to actually run the program is in the display
createpart("tank_1.3ds") ;1st part base object
createpart("turret_1.3ds")
createpart("turret_1.3ds")

;dont change anything below here
Type partstype
Field s
Field name$
Field x#
Field y#
Field z#
Field size#
Field rotation
Field partnumber
End Type

Global cx# ;hold camera loc
Global cy#
Global cz#
Global rx# ;hold camera rotation
Global ry#
Global rz#

camera=CreateCamera ()
light=CreateLight()
Global cameracycle=0
Global cameraswitchtimer
Global zoomtimer = MilliSecs()
PositionEntity camera,10.0,0.0,0.0
RotateEntity camera,0.0,90.0,0.0,1
cx# = 10.0
cy# = 0.0
cz# = 0.0
rx# = 0.0
ry# = 90.0
rz# = 0.0

Global partcount = 0
Function createpart (part_name$)
part.partstype = New partstype
part\s = LoadMesh(part_name$)
If partcount > 0 Then HideEntity part\s
ScaleMesh part\s,scalemeshfactor#,scalemeshfactor#,scalemeshfactor#
part\name$ = part_name$
part\x# = 0.0
part\y# = 0.0
part\z# = 0.0
part\size# = scalemeshfactor#
part\partnumber = partcount
partcount = partcount + 1
PositionEntity part\s,part\x#,part\y#,part\z#
End Function



For part.partstype = Each partstype
If part\partnumber > 0
ShowEntity part\s
While Not KeyDown(1) 
If KeyDown(59) 
part = After part
ShowEntity part\s
;wait one sec before loading next part
cameraswitchtimer = MilliSecs() + 1000
Repeat
cameraswitchtimer = cameraswitchtimer
Until cameraswitchtimer < MilliSecs() 
EndIf
If KeyDown(60) And zoomtimer <MilliSecs()
cx# = cx# / zoomfactor#
cy# = cy# / zoomfactor#
cz# = cz# / zoomfactor#
zoomtimer = MilliSecs() + 50
EndIf
If KeyDown(61) And zoomtimer <MilliSecs()
cx# = cx# * zoomfactor#
cy# = cy# * zoomfactor#
cz# = cz# * zoomfactor#
zoomtimer = MilliSecs() + 50
EndIf
If KeyDown(62) And zoomtimer <MilliSecs()
cx# = cx# + zoomfactor - 1.0 
zoomtimer = MilliSecs() + 50
EndIf
If KeyDown(63) And zoomtimer <MilliSecs()
cx# = cx# - zoomfactor + 1.0
zoomtimer = MilliSecs() + 50
EndIf
If KeyDown(64) And zoomtimer <MilliSecs()
cy# = cy# + zoomfactor - 1.0 
zoomtimer = MilliSecs() + 50
EndIf
If KeyDown(65) And zoomtimer <MilliSecs()
cy# = cy# - zoomfactor + 1.0
zoomtimer = MilliSecs() + 50
EndIf

If KeyDown(200) Then part\z# = part\z# + move#
If KeyDown(208) Then part\z# = part\z# - move#
If KeyDown(203) Then part\x# = part\x# + move#
If KeyDown(205) Then part\x# = part\x# - move#
If KeyDown(199) Then part\y# = part\y# + move#
If KeyDown(207) Then part\y# = part\y# - move
If KeyDown(45) And cameraswitchtimer < MilliSecs()
cameraswitchtimer = MilliSecs() + 250
cameracycle = cameracycle + 1
cx# = 0.0
cy# = 0.0
cz# = 0.0
rx# = 0.0    
ry# = 0.0    
rz# = 0.0 
Select cameracycle
Case 1 ;top view
cy# = 10.0
rx# = 90.0
Case 2 ;rear view
cz# = 10.0
ry# = 180.0
Case 3 ;underneath
cy# = -10.0
rx# = 270.0
Case 4 ;front view
cz# = -10.0
ry# = 0.0
Case 5 ;rightview
cx# = -10.0
ry# = 270.0
Default ;left view
cx# = 10.0
ry# = 90.0
cameracycle = 0
End Select
EndIf
PositionEntity camera,cx#,cy#,cz#
RotateEntity camera,rx#,ry#,rz#
PositionEntity part\s,part\x#,part\y#,part\z#
RenderWorld ;******* RENDER WORLD **********
Text 0,0,"MOVEPARTS X leftarrow rightarrow Y Home End Z forward back end F1 MOVE NEXT PART"
Text 0,20,"CAMERA VIEW X toggel camera view F2 F3 Zoom, F4 F5 pan left right, F6 F7 pan up down"
Select cameracycle
Case 0
Text 0,40,"left view "+"currentpart "+part\name$+" x "+ part\x# +" y "+part\y#+" z "+part\z#
Case 1
Text 0,40,"top view "+"currentpart "+part\name$+" x "+ part\x# +" y "+part\y#+" z "+part\z#
Case 2
Text 0,40,"rear view "+"currentpart "+part\name$+" x "+ part\x# +" y "+part\y#+" z "+part\z#
Case 3
Text 0,40,"underneath view "+"currentpart "+part\name$+" x "+ part\x# +" y "+part\y#+" z "+part\z#
Case 4
Text 0,40,"front view "+"currentpart "+part\name$+" x "+ part\x# +" y "+part\y#+" z "+part\z#
Case 5
Text 0,40,"right view "+"currentpart "+part\name$+" x "+ part\x# +" y "+part\y#+" z "+part\z#
End Select

Flip
FlushKeys
Wend
EndIf
Next
