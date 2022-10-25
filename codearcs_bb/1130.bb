; ID: 1130
; Author: David819
; Date: 2004-08-10 10:52:32
; Title: Day To Night Function
; Description: Changes day to night

;You will need to change this to the sky boxes you are using!
Const 	Sky_Day$ = "sky17/sky17.3ds"
Const 	Sky_Night$ = "sky08/sky08.3ds"

Type Sky
Field SKY_AT_DAY
Field SKY_AT_NIGHT
Field Size
Field DAV#
Field NAV#
Field TS#
End Type

Function DTN( SBD$, SBN$, SIZE#, DAV#, NAV# )

D.Sky = New sky
D\SKY_AT_DAY = SBD$
D\SKY_AT_NIGHT = SBN$
D\SIZE = SIZE#
D\DAV# = DAV#
D\NAV# = NAV#

;Loads The Sky Boxes
D\SKY_AT_DAY = LoadMesh(SBD$)
D\SKY_AT_NIGHT = LoadMesh(SBN$)

;Scale The Sky Boxes To Chosen Size
ScaleEntity D\SKY_AT_DAY, D\SIZE, D\SIZE, D\SIZE
ScaleEntity D\SKY_AT_NIGHT, D\SIZE, D\SIZE, D\SIZE

End Function

Function DTNU()
For D.Sky=Each Sky
;Turns The Sky Boxes Around
TurnEntity D\Sky_At_Day, 0, 0.001, 0
TurnEntity D\Sky_At_Night, 0, 0.001, 0

;Set The Alpha Values
EntityAlpha D\Sky_At_Day, D\DAV#
EntityAlpha D\Sky_At_Night, D\NAV#
Next
End Function

Function Basic_Con()
For D.Sky=Each Sky
If MilliSecs()- Time < 28000 Then ; 7000 means 7:00 hours
      Status$="Night"
   ElseIf MilliSecs() - Time < 76000 Then ; 19000 means 19:00 hours
      Status$="Day"
   Else ; the else is because it isn't day time, so it must be night time
      Status$="Night"
   End If

If status$="Day" Then
If D\DAV#<=0 Then D\DAV#=D\DAV#+0.0005
If D\DAV#<1 And  D\DAV#>0 Then D\DAV#=D\DAV#+0.0005
If D\DAV#=>1 Then D\DAV#=1
If D\NAV#=>1 Then D\NAV#=D\NAV#-0.0005
If D\NAV#>0 And D\NAV#<1 Then D\NAV#=D\NAV#-.0005
If D\NAV#<=0 Then D\NAV#=0
EndIf

If status$="Night" Then 
If D\DAV#=>1 Then D\DAV#=D\DAV#-0.0005
If D\DAV#>0 And D\DAV#<1 Then D\DAV#=D\DAV#-.0005
If D\DAV#<=0 Then D\DAV#=0
If D\NAV#<=0 Then D\NAV#=D\NAV#+0.0005
If D\NAV#<1 And  D\NAV#>0 Then D\NAV#=D\NAV#+0.0005
If D\NAV#=>1 Then D\NAV#=1
EndIf

   If MilliSecs() - Time > 96000 Then ; check to see if 24 hours has past, and if so, start a new day.
      Time = Time + 96000 ; wrap the day back round to midnight again
   End If


Next
End Function

;Example You may need to change the screen's width and height
Include "DTN FUNCTION Final test.bb"

;Example Of Use
Global Time = MilliSecs()
Global Status$

;Costants
Const 	Width		=		1280
Const 	Height		=		1024
Const 	Depth		=		32
Const 	SType		=		1
Const 	EndKey		=		1

;Set Graphics mode
Graphics3D 	Width, Height, Depth, SType
SetBuffer	BackBuffer()

;Create Camera
CHAR_Cam	=	CreateCamera()
CameraRange 	CHAR_Cam, 1, 2000
PositionEntity 	CHAR_Cam, 0, 1, 0

;Day And Night Values
D#=0
N#=1

;Main Part
DTN( Sky_Day$, Sky_Night$, 100, D#, N# )

;Main Loop
While Not KeyHit (EndKey)
Basic_Con()
DTNU()
UpdateWorld
RenderWorld
   Text 0,0, " Day or Night:"+status$
   Text 0,10," Hour:"+((MilliSecs()-Time)/1000/4)
Flip False
Wend
End
