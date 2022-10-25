; ID: 721
; Author: Guybrushnivek
; Date: 2003-06-19 19:02:34
; Title: Full Alpha 3D Clock in 2D
; Description: Same as 3D Clock in 2D, now Clock is wholly Transparent

AppTitle "3D Clock"										;Kevin Cockburn (guybrushnivek@aol.com)

;Move mouse, moves light-source.
;Click mouse, end program.
;Numbers 1 - 3, rotate X , Y , Z angles
;Numbers 4 - 5, zoom in and out
;Number 6, reset angles and zoom to preset

;Change GWIDTH , GHEIGHT , GDEPTH , GMODE

Const GWIDTH = 640 , GHEIGHT = 480 , GDEPTH = 16 , GMODE = 1
Const MINX = 0 , MINY = 0 , MAXX = GWIDTH - 1 , MAXY = GHEIGHT - 1
Const GHALFX = GWIDTH / 2 , GHALFY = GHEIGHT / 2

Const CLSR = 70 , CLSG = 70 , CLSB = 70

Graphics GWIDTH , GHEIGHT , GDEPTH , GMODE

ClsColor(CLSR , CLSG , CLSB)

SetBuffer BackBuffer()

;CLOCK_POINT_AMOUNT : 3 - 1200
	
Const CLOCK_POINT_AMOUNT = 9		;Amount of Points in the circle we will allow to describe the face
Const CLOCK_SIZE = 235				;Radius of Clock Circle(DON'T CHANGE.)
Const CLOCK_RIM_SIZE = 30			;Thickness of Clock rim
Const CLOCK_WIDTH = 20				;Thickness of Clock side-on.
Const NUM_TRIANGLES_ON_CLOCK = CLOCK_POINT_AMOUNT Shl 3 + 144 + 14 + 14 + 14
Const CLOCK_FRAME_POINTS = CLOCK_POINT_AMOUNT Shl 2
Const ALL_CLOCK_VERTICES = CLOCK_FRAME_POINTS + 95 + 9 + 9 + 9
Const ONEOVER3# = Float 1 / 3
Const CURRENT1 = CLOCK_FRAME_POINTS + 96
Const CURRENT2 = CLOCK_POINT_AMOUNT Shl 3 + 144
Const CURRENT3 = CLOCK_FRAME_POINTS + 105
Const CURRENT4 = CLOCK_POINT_AMOUNT Shl 3 + 158
Const CURRENT5 = CLOCK_FRAME_POINTS + 114
Const CURRENT6 = CLOCK_POINT_AMOUNT Shl 3 + 172
Const CLS_COLOR_ARGB = (CLSR Shl 16) + (CLSG Shl 8) + CLSB

Const LIGHTSOURCE_RATIO_X# = Float 2000 / (GWIDTH - 1) , LIGHTSOURCE_RATIO_Y# = Float 2000 / (GHEIGHT - 1) 

Const MULTIPLANT = 768

Dim ksin#(2047) , kcos#(2047)

Dim Clock_Points#(ALL_CLOCK_VERTICES , 2)
Dim Clock_Points_PROJ#(ALL_CLOCK_VERTICES , 2)

Dim Clock_Triangles(NUM_TRIANGLES_ON_CLOCK - 1 , 2)
Dim Clock_Triangles_NORMAL#(NUM_TRIANGLES_ON_CLOCK - 1 , 2)
Dim Clock_Triangles_NORMAL_PROJ#(NUM_TRIANGLES_ON_CLOCK - 1 , 2)

Dim Seconds_Hand_Points#(8 , 2)
Dim Seconds_Hand_NORMALS#(13 , 2)

Dim Minutes_Hand_Points#(8 , 2)
Dim Minutes_Hand_NORMALS#(13 , 2)

Dim Hours_Hand_Points#(8 , 2)
Dim Hours_Hand_NORMALS#(13 , 2)

Dim Screen_Array(GWIDTH - 1 , GHEIGHT - 1)

Dim LightSource(2) , LightSourceRGB#(2)
Dim Triangles_Color(NUM_TRIANGLES_ON_CLOCK - 1 , 2)

Dim Triangles_2_Render(NUM_TRIANGLES_ON_CLOCK - 1 , 1) , Triangle_Brightness#(NUM_TRIANGLES_ON_CLOCK - 1) , Triangles_Mid_ZPoints#(NUM_TRIANGLES_ON_CLOCK - 1)

Dim RadixArray(15 , NUM_TRIANGLES_ON_CLOCK - 1) , NumArray(15)
Dim RadixArray2(15 , NUM_TRIANGLES_ON_CLOCK - 1)

Local cnt , R , G , B

Global Ang_X = 0 , Ang_Y = 1024 , Ang_Z = 0 , Alpha_Beyond
Global Cam_X# = 1000 , Cam_Y# = 1000 , Cam_Z# = 600 , LightSourceARGB , ClockColorARGB

LightSource(0) = MouseX()
LightSource(1) = MouseY()
LightSource(2) = 600

For cnt = 0 To CLOCK_POINT_AMOUNT Shl 3 - 1

	Triangles_Color(cnt , 0) = 64
	Triangles_Color(cnt , 1) = 255
	Triangles_Color(cnt , 2) = 0
	
Next

For cnt = CLOCK_POINT_AMOUNT Shl 3 To CLOCK_POINT_AMOUNT Shl 3 + 144 - 1

	Triangles_Color(cnt , 0) = 255
	Triangles_Color(cnt , 1) = 255
	Triangles_Color(cnt , 2) = 64
	
Next

For cnt = CLOCK_POINT_AMOUNT Shl 3 + 144 To CLOCK_POINT_AMOUNT Shl 3 + 144 + 13

	Triangles_Color(cnt , 0) = 64
	Triangles_Color(cnt , 1) = 64
	Triangles_Color(cnt , 2) = 255
	
Next

For cnt = CLOCK_POINT_AMOUNT Shl 3 + 144 + 14 To CLOCK_POINT_AMOUNT Shl 3 + 144 + 14 + 13

	Triangles_Color(cnt , 0) = 255
	Triangles_Color(cnt , 1) = 64
	Triangles_Color(cnt , 2) = 64
	
Next

For cnt = CLOCK_POINT_AMOUNT Shl 3 + 144 + 14 + 14 To CLOCK_POINT_AMOUNT Shl 3 + 144 + 14 + 14 + 13

	Triangles_Color(cnt , 0) = 255
	Triangles_Color(cnt , 1) = 64
	Triangles_Color(cnt , 2) = 255
	
Next


LightSourceRGB(0) = Float 255 / 255
LightSourceRGB(1) = Float 255 / 255
LightSourceRGB(2) = Float 255 / 255

LightSourceARGB = Float ((Float LightSourceRGB(0) * 255) * 65536) + ((Float LightSourceRGB(1) * 255) * 256) + (Float LightSourceRGB(2) * 255)

If CLOCK_POINT_AMOUNT < 3 Then End

GenerateSinCosTable()

BuildClockFace3D()

time$ = CurrentTime$()

hours_str$ = Mid$(time$ , 1 , 2)
minutes_str$ = Mid$(time$ , 4 , 2)
seconds_str$ = Mid$(time$ , 7 , 2)

hours = Val(hours_str$)
minutes = Val(minutes_str$)
seconds = Val(seconds_str$)
		
RotateClockHandsToArray(seconds , minutes , hours)

timedelay = 10

timer = CreateTimer(30)

.Start

Repeat

	For cnt = 0 To GWIDTH - 1
	
		For cnt2 = 0 To GHEIGHT - 1
		
			Screen_Array(cnt , cnt2) = CLS_COLOR_ARGB
			
		Next
		
	Next

	d = MilliSecs()

	If timedelay = 10
	
		timedelay = 0

		time$ = CurrentTime$()

		hours_str$ = Mid$(time$ , 1 , 2)
		minutes_str$ = Mid$(time$ , 4 , 2)
		seconds_str$ = Mid$(time$ , 7 , 2)

		hours = Val(hours_str$)
		minutes = Val(minutes_str$)
		seconds = Val(seconds_str$)
		
		RotateClockHandsToArray(seconds , minutes , hours)
	
	End If
	
	KSX# = ksin(Ang_X)
	KCX# = kcos(Ang_X)
	KSY# = ksin(Ang_Y)
	KCY# = kcos(Ang_Y)
	KSZ# = ksin(Ang_Z)
	KCZ# = kcos(Ang_Z)
	
	RotateVertices(KSX# , KCX# , KSY# , KCY# , KSZ# , KCZ#)
	
	Triangle_2_Draw_Amount = CullandLightSourceTriangles(KSX# , KCX# , KSY# , KCY# , KSZ# , KCZ#)
	
	ProjectVertices()
	
	RadixSort(Triangle_2_Draw_Amount)
	
	RemoveObsoleteTriangles(Triangle_2_Draw_Amount)
	
	LockBuffer()
		
	For cnt = 1 To Triangle_2_Draw_Amount
	
	;	If Triangles_2_Render(cnt , 1) = 1
			
			R = Int(Float (Triangles_Color(Triangles_2_Render(cnt , 0) , 0) * LightSourceRGB(0)) * Triangle_Brightness(Triangles_2_Render(cnt , 0)))
			G = Int(Float (Triangles_Color(Triangles_2_Render(cnt , 0) , 1) * LightSourceRGB(1)) * Triangle_Brightness(Triangles_2_Render(cnt , 0)))
			B = Int(Float (Triangles_Color(Triangles_2_Render(cnt , 0) , 2) * LightSourceRGB(2)) * Triangle_Brightness(Triangles_2_Render(cnt , 0)))
			
			;REMOVE THESE ';' for the clock to have only Alpha Innards, hands and hour-squares
			
		;	If Triangles_2_Render(cnt , 0) <= Alpha_Beyond

		;		DrawClockTriangleFlatShaded(Triangles_2_Render(cnt , 0) , (R Shl 16) + (G Shl 8) + B)
			
		;	Else
			
				DrawClockTriangleFlatShadedAlpha(Triangles_2_Render(cnt , 0) , (R Shl 16) + (G Shl 8) + B , 0.5)
			
		;	End If
			
	;	End If
				
	Next
	
	UnlockBuffer()
		
	If KeyDown(2) Then Ang_X = Ang_X + 2
	If Ang_X = 2048 Then Ang_X = 0 
	If KeyDown(3) Then Ang_Y = Ang_Y + 2
	If Ang_Y = 2048 Then Ang_Y = 0
	If KeyDown(4) Then Ang_Z = Ang_Z + 2
	If Ang_Z = 2048 Then Ang_Z = 0
	If KeyDown(5) Then Cam_Z = Cam_Z + 2
	If KeyDown(6) Then Cam_Z = Cam_Z - 2
	If KeyDown(7)
	
		Ang_X = 0
		Ang_Y = 1024
		Ang_Z = 0
		Cam_X# = 1000
		Cam_Y# = 1000
		Cam_Z# = 600
		
	End If
	
	Mx = MouseX()
	My = MouseY()

	Lightsource(0) = Float (Mx * LIGHTSOURCE_RATIO_X)
	Lightsource(1) = Float (My * LIGHTSOURCE_RATIO_Y)
	
	WritePixel Mx , My , LightSourceARGB
	
	Text 0 , 0 , MilliSecs() - d
	Text 0 , 16 , Triangles_Drawn
	
	;WaitTimer(timer)

	Flip

	Cls
	
	timedelay = timedelay + 1

Until MouseDown(1)

End




Function BuildClockFace3D()

  Local Dist_between_points# , X# , Y# , Nx# , Ny# , Cx1# , Cy1# , Cx2# , Cy2#
  Local Current_Point , Current_Value# , cnt , cnt2 , Scale# = Float CLOCK_SIZE / 235
  Local Num_Triangles = 0

	Dist_between_points# = Float 360 / CLOCK_POINT_AMOUNT
	
	X# = 0
	Y# = - CLOCK_SIZE
	
	Current_Point = 0
	Current_Value# = 0
	
	Repeat
	
		Nx# = Y# * Sin(Current_Value#)							;Rotation is going clock-wise
		Ny# = Float Y# * Cos(Current_Value#)
		
		Clock_Points#(Current_Point , 0) = Float Nx#
		Clock_Points#(Current_Point , 1) = Float Ny#
		Clock_Points#(Current_Point , 2) = Float CLOCK_WIDTH / 2
		
		Clock_Points#(Current_Point + CLOCK_POINT_AMOUNT * 2 , 0) = Float Nx#
		Clock_Points#(Current_Point + CLOCK_POINT_AMOUNT * 2 , 1) = Float Ny#
		Clock_Points#(Current_Point + CLOCK_POINT_AMOUNT * 2 , 2) = Float - (CLOCK_WIDTH / 2)

		Nx# = Float (Y# + CLOCK_RIM_SIZE) * Sin(Current_Value#)	;Rotation is going clock-wise
		Ny# = Float (Float Y# + CLOCK_RIM_SIZE) * Cos(Current_Value#)
		
		Clock_Points#(Current_Point + CLOCK_POINT_AMOUNT , 0) = Float Nx#
		Clock_Points#(Current_Point + CLOCK_POINT_AMOUNT , 1) = Float Ny#
		Clock_Points#(Current_Point + CLOCK_POINT_AMOUNT , 2) = Float CLOCK_WIDTH / 2
		
		Clock_Points#(Current_Point + CLOCK_POINT_AMOUNT * 3 , 0) = Float Nx#
		Clock_Points#(Current_Point + CLOCK_POINT_AMOUNT * 3 , 1) = Float Ny#
		Clock_Points#(Current_Point + CLOCK_POINT_AMOUNT * 3 , 2) = Float - (CLOCK_WIDTH / 2)

		Current_Point = Current_Point + 1
		Current_Value# = Current_Value# + Dist_between_points#
	
	Until Current_Point = CLOCK_POINT_AMOUNT
		
	For cnt = 0 To CLOCK_POINT_AMOUNT - 2									;Faces facing front
	
		Clock_Triangles(Num_Triangles , 0) = cnt							;1st Triangle
		Clock_Triangles(Num_Triangles , 1) = cnt + 1 + CLOCK_POINT_AMOUNT
		Clock_Triangles(Num_Triangles , 2) = cnt + 1
				
		Num_Triangles = Num_Triangles + 1
																			;2nd Triangle
		Clock_Triangles(Num_Triangles , 0) = cnt
		Clock_Triangles(Num_Triangles , 1) = cnt + CLOCK_POINT_AMOUNT
		Clock_Triangles(Num_Triangles , 2) = cnt + 1 + CLOCK_POINT_AMOUNT
		
		Num_Triangles = Num_Triangles + 1
		
	Next
	
	Clock_Triangles(Num_Triangles , 0) = cnt								;1st Triangle
	Clock_Triangles(Num_Triangles , 1) = cnt + 1
	Clock_Triangles(Num_Triangles , 2) = 0
				
	Num_Triangles = Num_Triangles + 1
													;2nd Triangle
	Clock_Triangles(Num_Triangles , 0) = cnt
	Clock_Triangles(Num_Triangles , 1) = cnt + CLOCK_POINT_AMOUNT
	Clock_Triangles(Num_Triangles , 2) = cnt + 1							;End Facing front
		
	Num_Triangles = Num_Triangles + 1
	
	For cnt = 0 To CLOCK_POINT_AMOUNT - 2									;Faces facing away
	
		Clock_Triangles(Num_Triangles , 0) = cnt + 1 + CLOCK_POINT_AMOUNT * 2	;1st Triangle
		Clock_Triangles(Num_Triangles , 1) = cnt + CLOCK_POINT_AMOUNT * 3
		Clock_Triangles(Num_Triangles , 2) = cnt + CLOCK_POINT_AMOUNT * 2
				
		Num_Triangles = Num_Triangles + 1
																			;2nd Triangle
		Clock_Triangles(Num_Triangles , 0) = cnt + 1 + CLOCK_POINT_AMOUNT * 2
		Clock_Triangles(Num_Triangles , 1) = cnt + 1 + CLOCK_POINT_AMOUNT * 3
		Clock_Triangles(Num_Triangles , 2) = cnt + CLOCK_POINT_AMOUNT * 3
		
		Num_Triangles = Num_Triangles + 1
		
	Next
	
	Clock_Triangles(Num_Triangles , 0) = CLOCK_POINT_AMOUNT * 2				;1st Triangle
	Clock_Triangles(Num_Triangles , 1) = cnt + CLOCK_POINT_AMOUNT * 3
	Clock_Triangles(Num_Triangles , 2) = cnt + CLOCK_POINT_AMOUNT * 2
				
	Num_Triangles = Num_Triangles + 1
																			;2nd Triangle
	Clock_Triangles(Num_Triangles , 0) = CLOCK_POINT_AMOUNT * 2
	Clock_Triangles(Num_Triangles , 1) = cnt + 1 + CLOCK_POINT_AMOUNT * 2
	Clock_Triangles(Num_Triangles , 2) = cnt + CLOCK_POINT_AMOUNT * 3
		
	Num_Triangles = Num_Triangles + 1										;End Faces facing away
	
	For cnt = 0 To CLOCK_POINT_AMOUNT - 2									;Faces on outside rim
	
		Clock_Triangles(Num_Triangles , 0) = cnt							;1st Triangle
		Clock_Triangles(Num_Triangles , 1) = cnt + 1
		Clock_Triangles(Num_Triangles , 2) = cnt + 1 + CLOCK_POINT_AMOUNT * 2
				
		Num_Triangles = Num_Triangles + 1
																			;2nd Triangle
		Clock_Triangles(Num_Triangles , 0) = cnt
		Clock_Triangles(Num_Triangles , 1) = cnt + 1 + CLOCK_POINT_AMOUNT * 2
		Clock_Triangles(Num_Triangles , 2) = cnt + CLOCK_POINT_AMOUNT * 2
		
		Num_Triangles = Num_Triangles + 1
		
	Next
	
	Clock_Triangles(Num_Triangles , 0) = cnt								;1st Triangle
	Clock_Triangles(Num_Triangles , 1) = 0
	Clock_Triangles(Num_Triangles , 2) = CLOCK_POINT_AMOUNT * 2
				
	Num_Triangles = Num_Triangles + 1
																			;2nd Triangle
	Clock_Triangles(Num_Triangles , 0) = cnt
	Clock_Triangles(Num_Triangles , 1) = CLOCK_POINT_AMOUNT * 2
	Clock_Triangles(Num_Triangles , 2) = cnt + CLOCK_POINT_AMOUNT * 2
		
	Num_Triangles = Num_Triangles + 1										;End Faces on outside rim

	For cnt = 0 To CLOCK_POINT_AMOUNT - 2									;Faces facing inside rim
	
		Clock_Triangles(Num_Triangles , 0) = cnt + CLOCK_POINT_AMOUNT		;1st Triangle
		Clock_Triangles(Num_Triangles , 1) = cnt + 1 + CLOCK_POINT_AMOUNT * 3
		Clock_Triangles(Num_Triangles , 2) = cnt + 1 + CLOCK_POINT_AMOUNT
				
		Num_Triangles = Num_Triangles + 1
																			;2nd Triangle
		Clock_Triangles(Num_Triangles , 0) = cnt + CLOCK_POINT_AMOUNT
		Clock_Triangles(Num_Triangles , 1) = cnt + CLOCK_POINT_AMOUNT * 3
		Clock_Triangles(Num_Triangles , 2) = cnt + 1 + CLOCK_POINT_AMOUNT * 3
		
		Num_Triangles = Num_Triangles + 1
		
	Next
	
	Clock_Triangles(Num_Triangles , 0) = CLOCK_POINT_AMOUNT					;1st Triangle
	Clock_Triangles(Num_Triangles , 1) = cnt + CLOCK_POINT_AMOUNT * 3
	Clock_Triangles(Num_Triangles , 2) = CLOCK_POINT_AMOUNT * 3
				
	Num_Triangles = Num_Triangles + 1
																			;2nd Triangle
	Clock_Triangles(Num_Triangles , 0) = CLOCK_POINT_AMOUNT
	Clock_Triangles(Num_Triangles , 1) = cnt + CLOCK_POINT_AMOUNT
	Clock_Triangles(Num_Triangles , 2) = cnt + CLOCK_POINT_AMOUNT * 3
																			;End Faces facing inside rim	
																			;BUILD HOURSQUARE VERTICES
	For cnt = 0 To 11
	
		Y# = -140
			
		Cx# = Float (X# * Cos(cnt * 30) + Y# * Sin(cnt * 30)) * Scale#
		Cy# = Float (Y# * Cos(cnt * 30) - X# * Sin(cnt * 30)) * Scale#

		Y# = -15
		
		For cnt2 = 0 To 3
		
			Clock_Points#(cnt * 4 + cnt2 + CLOCK_FRAME_POINTS , 0) = Float (X# * Cos(cnt2 * 90) + Y# * Sin(cnt2 * 90)) + Cx#
			Clock_Points#(cnt * 4 + cnt2 + CLOCK_FRAME_POINTS , 1) = Float (Y# * Cos(cnt2 * 90) - X# * Sin(cnt2 * 90)) + Cy#
			Clock_Points#(cnt * 4 + cnt2 + CLOCK_FRAME_POINTS , 2) = Float (CLOCK_WIDTH / 2) / 4
			
			Clock_Points#(cnt * 4 + cnt2 + CLOCK_FRAME_POINTS + 48 , 0) = Float (X# * Cos(cnt2 * 90) + Y# * Sin(cnt2 * 90)) + Cx#
			Clock_Points#(cnt * 4 + cnt2 + CLOCK_FRAME_POINTS + 48 , 1) = Float (Y# * Cos(cnt2 * 90) - X# * Sin(cnt2 * 90)) + Cy#
			Clock_Points#(cnt * 4 + cnt2 + CLOCK_FRAME_POINTS + 48 , 2) = Float -(CLOCK_WIDTH / 2) / 4

		Next
		
	Next
	
	Alpha_Beyond = Num_Triangles
	
	For cnt = 0 To 11
	
		Num_Triangles = Num_Triangles + 1
	
		Clock_Triangles(Num_Triangles , 0) = cnt * 4 + CLOCK_FRAME_POINTS + 0	;FRONT FACE
		Clock_Triangles(Num_Triangles , 1) = cnt * 4 + CLOCK_FRAME_POINTS + 2
		Clock_Triangles(Num_Triangles , 2) = cnt * 4 + CLOCK_FRAME_POINTS + 1
	
		Num_Triangles = Num_Triangles + 1
	
		Clock_Triangles(Num_Triangles , 0) = cnt * 4 + CLOCK_FRAME_POINTS + 0
		Clock_Triangles(Num_Triangles , 1) = cnt * 4 + CLOCK_FRAME_POINTS + 3
		Clock_Triangles(Num_Triangles , 2) = cnt * 4 + CLOCK_FRAME_POINTS + 2
		
		Num_Triangles = Num_Triangles + 1
	
		Clock_Triangles(Num_Triangles , 0) = cnt * 4 + CLOCK_FRAME_POINTS + 0 + 48	;BACK FACE
		Clock_Triangles(Num_Triangles , 1) = cnt * 4 + CLOCK_FRAME_POINTS + 1 + 48
		Clock_Triangles(Num_Triangles , 2) = cnt * 4 + CLOCK_FRAME_POINTS + 2 + 48
	
		Num_Triangles = Num_Triangles + 1
	
		Clock_Triangles(Num_Triangles , 0) = cnt * 4 + CLOCK_FRAME_POINTS + 0 + 48
		Clock_Triangles(Num_Triangles , 1) = cnt * 4 + CLOCK_FRAME_POINTS + 2 + 48
		Clock_Triangles(Num_Triangles , 2) = cnt * 4 + CLOCK_FRAME_POINTS + 3 + 48
		
		Num_Triangles = Num_Triangles + 1
	
		Clock_Triangles(Num_Triangles , 0) = cnt * 4 + CLOCK_FRAME_POINTS + 0		;SIDE FACE 1
		Clock_Triangles(Num_Triangles , 1) = cnt * 4 + CLOCK_FRAME_POINTS + 1
		Clock_Triangles(Num_Triangles , 2) = cnt * 4 + CLOCK_FRAME_POINTS + 1 + 48
	
		Num_Triangles = Num_Triangles + 1
	
		Clock_Triangles(Num_Triangles , 0) = cnt * 4 + CLOCK_FRAME_POINTS + 0
		Clock_Triangles(Num_Triangles , 1) = cnt * 4 + CLOCK_FRAME_POINTS + 1 + 48
		Clock_Triangles(Num_Triangles , 2) = cnt * 4 + CLOCK_FRAME_POINTS + 0 + 48
		
		Num_Triangles = Num_Triangles + 1
	
		Clock_Triangles(Num_Triangles , 0) = cnt * 4 + CLOCK_FRAME_POINTS + 1		;SIDE FACE 2
		Clock_Triangles(Num_Triangles , 1) = cnt * 4 + CLOCK_FRAME_POINTS + 2 + 48
		Clock_Triangles(Num_Triangles , 2) = cnt * 4 + CLOCK_FRAME_POINTS + 1 + 48
	
		Num_Triangles = Num_Triangles + 1
	
		Clock_Triangles(Num_Triangles , 0) = cnt * 4 + CLOCK_FRAME_POINTS + 1
		Clock_Triangles(Num_Triangles , 1) = cnt * 4 + CLOCK_FRAME_POINTS + 2
		Clock_Triangles(Num_Triangles , 2) = cnt * 4 + CLOCK_FRAME_POINTS + 2 + 48
		
		Num_Triangles = Num_Triangles + 1
	
		Clock_Triangles(Num_Triangles , 0) = cnt * 4 + CLOCK_FRAME_POINTS + 2		;SIDE FACE 3
		Clock_Triangles(Num_Triangles , 1) = cnt * 4 + CLOCK_FRAME_POINTS + 3
		Clock_Triangles(Num_Triangles , 2) = cnt * 4 + CLOCK_FRAME_POINTS + 3 + 48
	
		Num_Triangles = Num_Triangles + 1
	
		Clock_Triangles(Num_Triangles , 0) = cnt * 4 + CLOCK_FRAME_POINTS + 2
		Clock_Triangles(Num_Triangles , 1) = cnt * 4 + CLOCK_FRAME_POINTS + 3 + 48
		Clock_Triangles(Num_Triangles , 2) = cnt * 4 + CLOCK_FRAME_POINTS + 2 + 48
		
		Num_Triangles = Num_Triangles + 1
	
		Clock_Triangles(Num_Triangles , 0) = cnt * 4 + CLOCK_FRAME_POINTS + 3		;SIDE FACE 4
		Clock_Triangles(Num_Triangles , 1) = cnt * 4 + CLOCK_FRAME_POINTS + 0 + 48
		Clock_Triangles(Num_Triangles , 2) = cnt * 4 + CLOCK_FRAME_POINTS + 3 + 48
	
		Num_Triangles = Num_Triangles + 1
	
		Clock_Triangles(Num_Triangles , 0) = cnt * 4 + CLOCK_FRAME_POINTS + 3
		Clock_Triangles(Num_Triangles , 1) = cnt * 4 + CLOCK_FRAME_POINTS + 0
		Clock_Triangles(Num_Triangles , 2) = cnt * 4 + CLOCK_FRAME_POINTS + 0 + 48
		
	Next
	
	;	SECONDS HAND POINTS
	
	
	Seconds_Hand_Points#(0 , 0) = 0
	Seconds_Hand_Points#(0 , 1) = -125
	Seconds_Hand_Points#(0 , 2) = (CLOCK_WIDTH / 2) / 2 + 17
	
	Seconds_Hand_Points#(1 , 0) = -2.5
	Seconds_Hand_Points#(1 , 1) = -57.5
	Seconds_Hand_Points#(1 , 2) = (CLOCK_WIDTH / 2) / 2 + 17
	
	Seconds_Hand_Points#(2 , 0) = -5
	Seconds_Hand_Points#(2 , 1) = -10
	Seconds_Hand_Points#(2 , 2) = (CLOCK_WIDTH / 2) / 2 + 17
	
	Seconds_Hand_Points#(3 , 0) = -2.5
	Seconds_Hand_Points#(3 , 1) = -5
	Seconds_Hand_Points#(3 , 2) = (CLOCK_WIDTH / 2) / 2 + 17
	
	Seconds_Hand_Points#(4 , 0) = 0
	Seconds_Hand_Points#(4 , 1) = 0
	Seconds_Hand_Points#(4 , 2) = (CLOCK_WIDTH / 2) / 2 + 17
	
	Seconds_Hand_Points#(5 , 0) = 2.5
	Seconds_Hand_Points#(5 , 1) = -5
	Seconds_Hand_Points#(5 , 2) = (CLOCK_WIDTH / 2) / 2 + 17
	
	Seconds_Hand_Points#(6 , 0) = 5
	Seconds_Hand_Points#(6 , 1) = -10
	Seconds_Hand_Points#(6 , 2) = (CLOCK_WIDTH / 2) / 2 + 17

	Seconds_Hand_Points#(7 , 0) = 2.5
	Seconds_Hand_Points#(7 , 1) = -57.5
	Seconds_Hand_Points#(7 , 2) = (CLOCK_WIDTH / 2) / 2 + 17

	Seconds_Hand_Points#(8 , 0) = 0
	Seconds_Hand_Points#(8 , 1) = -10
	Seconds_Hand_Points#(8 , 2) = (CLOCK_WIDTH / 2) / 2 + 20
	
	;SECONDS HAND POINTS
	
	Clock_Points#(CLOCK_FRAME_POINTS + 96 , 0) = Seconds_Hand_Points#(0 , 0)
	Clock_Points#(CLOCK_FRAME_POINTS + 96 , 1) = Seconds_Hand_Points#(0 , 1)
	Clock_Points#(CLOCK_FRAME_POINTS + 96 , 2) = Seconds_Hand_Points#(0 , 2)
	
	Clock_Points#(CLOCK_FRAME_POINTS + 97 , 0) = Seconds_Hand_Points#(1 , 0)
	Clock_Points#(CLOCK_FRAME_POINTS + 97 , 1) = Seconds_Hand_Points#(1 , 1)
	Clock_Points#(CLOCK_FRAME_POINTS + 97 , 2) = Seconds_Hand_Points#(1 , 2)
	
	Clock_Points#(CLOCK_FRAME_POINTS + 98 , 0) = Seconds_Hand_Points#(2 , 0)
	Clock_Points#(CLOCK_FRAME_POINTS + 98 , 1) = Seconds_Hand_Points#(2 , 1)
	Clock_Points#(CLOCK_FRAME_POINTS + 98 , 2) = Seconds_Hand_Points#(2 , 2)
	
	Clock_Points#(CLOCK_FRAME_POINTS + 99 , 0) = Seconds_Hand_Points#(3 , 0)
	Clock_Points#(CLOCK_FRAME_POINTS + 99 , 1) = Seconds_Hand_Points#(3 , 1)
	Clock_Points#(CLOCK_FRAME_POINTS + 99 , 2) = Seconds_Hand_Points#(3 , 2)
	
	Clock_Points#(CLOCK_FRAME_POINTS + 100 , 0) = Seconds_Hand_Points#(4 , 0)
	Clock_Points#(CLOCK_FRAME_POINTS + 100 , 1) = Seconds_Hand_Points#(4 , 1)
	Clock_Points#(CLOCK_FRAME_POINTS + 100 , 2) = Seconds_Hand_Points#(4 , 2)
	
	Clock_Points#(CLOCK_FRAME_POINTS + 101 , 0) = Seconds_Hand_Points#(5 , 0)
	Clock_Points#(CLOCK_FRAME_POINTS + 101 , 1) = Seconds_Hand_Points#(5 , 1)
	Clock_Points#(CLOCK_FRAME_POINTS + 101 , 2) = Seconds_Hand_Points#(5 , 2)
	
	Clock_Points#(CLOCK_FRAME_POINTS + 102 , 0) = Seconds_Hand_Points#(6 , 0)
	Clock_Points#(CLOCK_FRAME_POINTS + 102 , 1) = Seconds_Hand_Points#(6 , 1)
	Clock_Points#(CLOCK_FRAME_POINTS + 102 , 2) = Seconds_Hand_Points#(6 , 2)

	Clock_Points#(CLOCK_FRAME_POINTS + 103 , 0) = Seconds_Hand_Points#(7 , 0)
	Clock_Points#(CLOCK_FRAME_POINTS + 103 , 1) = Seconds_Hand_Points#(7 , 1)
	Clock_Points#(CLOCK_FRAME_POINTS + 103 , 2) = Seconds_Hand_Points#(7 , 2)

	Clock_Points#(CLOCK_FRAME_POINTS + 104 , 0) = Seconds_Hand_Points#(8 , 0)
	Clock_Points#(CLOCK_FRAME_POINTS + 104 , 1) = Seconds_Hand_Points#(8 , 1)
	Clock_Points#(CLOCK_FRAME_POINTS + 104 , 2) = Seconds_Hand_Points#(8 , 2)
	
	
	;	SECOND HAND TRIANGLES			;FRONT
	
	;Alpha_Beyond = Num_Triangles

	Num_Triangles = Num_Triangles + 1
	
	Clock_Triangles(Num_Triangles , 0) = CLOCK_FRAME_POINTS + 97
	Clock_Triangles(Num_Triangles , 1) = CLOCK_FRAME_POINTS + 96
	Clock_Triangles(Num_Triangles , 2) = CLOCK_FRAME_POINTS + 104
	
	Num_Triangles = Num_Triangles + 1
	
	Clock_Triangles(Num_Triangles , 0) = CLOCK_FRAME_POINTS + 97
	Clock_Triangles(Num_Triangles , 1) = CLOCK_FRAME_POINTS + 104
	Clock_Triangles(Num_Triangles , 2) = CLOCK_FRAME_POINTS + 98 
	
	Num_Triangles = Num_Triangles + 1
	
	Clock_Triangles(Num_Triangles , 0) = CLOCK_FRAME_POINTS + 104
	Clock_Triangles(Num_Triangles , 1) = CLOCK_FRAME_POINTS + 99
	Clock_Triangles(Num_Triangles , 2) = CLOCK_FRAME_POINTS + 98 
	
	Num_Triangles = Num_Triangles + 1
	
	Clock_Triangles(Num_Triangles , 0) = CLOCK_FRAME_POINTS + 104
	Clock_Triangles(Num_Triangles , 1) = CLOCK_FRAME_POINTS + 100
	Clock_Triangles(Num_Triangles , 2) = CLOCK_FRAME_POINTS + 99
	
	Num_Triangles = Num_Triangles + 1
	
	Clock_Triangles(Num_Triangles , 0) = CLOCK_FRAME_POINTS + 104
	Clock_Triangles(Num_Triangles , 1) = CLOCK_FRAME_POINTS + 101
	Clock_Triangles(Num_Triangles , 2) = CLOCK_FRAME_POINTS + 100
	
	Num_Triangles = Num_Triangles + 1
	
	Clock_Triangles(Num_Triangles , 0) = CLOCK_FRAME_POINTS + 104
	Clock_Triangles(Num_Triangles , 1) = CLOCK_FRAME_POINTS + 102
	Clock_Triangles(Num_Triangles , 2) = CLOCK_FRAME_POINTS + 101
	
	Num_Triangles = Num_Triangles + 1
	
	Clock_Triangles(Num_Triangles , 0) = CLOCK_FRAME_POINTS + 103
	Clock_Triangles(Num_Triangles , 1) = CLOCK_FRAME_POINTS + 102
	Clock_Triangles(Num_Triangles , 2) = CLOCK_FRAME_POINTS + 104
	
	Num_Triangles = Num_Triangles + 1
	
	Clock_Triangles(Num_Triangles , 0) = CLOCK_FRAME_POINTS + 103
	Clock_Triangles(Num_Triangles , 1) = CLOCK_FRAME_POINTS + 104
	Clock_Triangles(Num_Triangles , 2) = CLOCK_FRAME_POINTS + 96

	;	SECONDS HAND TRIANGLES	;BACK
	
	Num_Triangles = Num_Triangles + 1
	
	Clock_Triangles(Num_Triangles , 0) = CLOCK_FRAME_POINTS + 96
	Clock_Triangles(Num_Triangles , 1) = CLOCK_FRAME_POINTS + 97
	Clock_Triangles(Num_Triangles , 2) = CLOCK_FRAME_POINTS + 103
	
	Num_Triangles = Num_Triangles + 1
	
	Clock_Triangles(Num_Triangles , 0) = CLOCK_FRAME_POINTS + 101
	Clock_Triangles(Num_Triangles , 1) = CLOCK_FRAME_POINTS + 99
	Clock_Triangles(Num_Triangles , 2) = CLOCK_FRAME_POINTS + 100
	
	Num_Triangles = Num_Triangles + 1
	
	Clock_Triangles(Num_Triangles , 0) = CLOCK_FRAME_POINTS + 103
	Clock_Triangles(Num_Triangles , 1) = CLOCK_FRAME_POINTS + 97
	Clock_Triangles(Num_Triangles , 2) = CLOCK_FRAME_POINTS + 98
	
	Num_Triangles = Num_Triangles + 1
	
	Clock_Triangles(Num_Triangles , 0) = CLOCK_FRAME_POINTS + 103
	Clock_Triangles(Num_Triangles , 1) = CLOCK_FRAME_POINTS + 98
	Clock_Triangles(Num_Triangles , 2) = CLOCK_FRAME_POINTS + 102
	
	Num_Triangles = Num_Triangles + 1
	
	Clock_Triangles(Num_Triangles , 0) = CLOCK_FRAME_POINTS + 102
	Clock_Triangles(Num_Triangles , 1) = CLOCK_FRAME_POINTS + 98
	Clock_Triangles(Num_Triangles , 2) = CLOCK_FRAME_POINTS + 99
	
	Num_Triangles = Num_Triangles + 1
	
	Clock_Triangles(Num_Triangles , 0) = CLOCK_FRAME_POINTS + 102
	Clock_Triangles(Num_Triangles , 1) = CLOCK_FRAME_POINTS + 99
	Clock_Triangles(Num_Triangles , 2) = CLOCK_FRAME_POINTS + 101


	;	MINUTES HAND POINTS
	
	Minutes_Hand_Points#(0 , 0) = 0
	Minutes_Hand_Points#(0 , 1) = -100
	Minutes_Hand_Points#(0 , 2) = (CLOCK_WIDTH / 2) / 2
	
	Minutes_Hand_Points#(1 , 0) = -5
	Minutes_Hand_Points#(1 , 1) = -45
	Minutes_Hand_Points#(1 , 2) = (CLOCK_WIDTH / 2) / 2 
	
	Minutes_Hand_Points#(2 , 0) = -10
	Minutes_Hand_Points#(2 , 1) = -10
	Minutes_Hand_Points#(2 , 2) = (CLOCK_WIDTH / 2) / 2
	
	Minutes_Hand_Points#(3 , 0) = -5
	Minutes_Hand_Points#(3 , 1) = -5
	Minutes_Hand_Points#(3 , 2) = (CLOCK_WIDTH / 2) / 2
	
	Minutes_Hand_Points#(4 , 0) = 0
	Minutes_Hand_Points#(4 , 1) = 0
	Minutes_Hand_Points#(4 , 2) = (CLOCK_WIDTH / 2) / 2
	
	Minutes_Hand_Points#(5 , 0) = 5
	Minutes_Hand_Points#(5 , 1) = -5
	Minutes_Hand_Points#(5 , 2) = (CLOCK_WIDTH / 2) / 2
	
	Minutes_Hand_Points#(6 , 0) = 10
	Minutes_Hand_Points#(6 , 1) = -10
	Minutes_Hand_Points#(6 , 2) = (CLOCK_WIDTH / 2) / 2 

	Minutes_Hand_Points#(7 , 0) = 5
	Minutes_Hand_Points#(7 , 1) = -45
	Minutes_Hand_Points#(7 , 2) = (CLOCK_WIDTH / 2) / 2

	Minutes_Hand_Points#(8 , 0) = 0
	Minutes_Hand_Points#(8 , 1) = -10
	Minutes_Hand_Points#(8 , 2) = (CLOCK_WIDTH / 2) / 2 + 3
	
	;Minutes HAND POINTS
	
	Clock_Points#(CLOCK_FRAME_POINTS + 105 , 0) = Minutes_Hand_Points#(0 , 0)
	Clock_Points#(CLOCK_FRAME_POINTS + 105 , 1) = Minutes_Hand_Points#(0 , 1)
	Clock_Points#(CLOCK_FRAME_POINTS + 105 , 2) = Minutes_Hand_Points#(0 , 2)
	
	Clock_Points#(CLOCK_FRAME_POINTS + 106 , 0) = Minutes_Hand_Points#(1 , 0)
	Clock_Points#(CLOCK_FRAME_POINTS + 106 , 1) = Minutes_Hand_Points#(1 , 1)
	Clock_Points#(CLOCK_FRAME_POINTS + 106 , 2) = Minutes_Hand_Points#(1 , 2)
	
	Clock_Points#(CLOCK_FRAME_POINTS + 107 , 0) = Minutes_Hand_Points#(2 , 0)
	Clock_Points#(CLOCK_FRAME_POINTS + 107 , 1) = Minutes_Hand_Points#(2 , 1)
	Clock_Points#(CLOCK_FRAME_POINTS + 107 , 2) = Minutes_Hand_Points#(2 , 2)
	
	Clock_Points#(CLOCK_FRAME_POINTS + 108 , 0) = Minutes_Hand_Points#(3 , 0)
	Clock_Points#(CLOCK_FRAME_POINTS + 108 , 1) = Minutes_Hand_Points#(3 , 1)
	Clock_Points#(CLOCK_FRAME_POINTS + 108 , 2) = Minutes_Hand_Points#(3 , 2)
	
	Clock_Points#(CLOCK_FRAME_POINTS + 109 , 0) = Minutes_Hand_Points#(4 , 0)
	Clock_Points#(CLOCK_FRAME_POINTS + 109 , 1) = Minutes_Hand_Points#(4 , 1)
	Clock_Points#(CLOCK_FRAME_POINTS + 109 , 2) = Minutes_Hand_Points#(4 , 2)
	
	Clock_Points#(CLOCK_FRAME_POINTS + 110 , 0) = Minutes_Hand_Points#(5 , 0)
	Clock_Points#(CLOCK_FRAME_POINTS + 110 , 1) = Minutes_Hand_Points#(5 , 1)
	Clock_Points#(CLOCK_FRAME_POINTS + 110 , 2) = Minutes_Hand_Points#(5 , 2)
	
	Clock_Points#(CLOCK_FRAME_POINTS + 111 , 0) = Minutes_Hand_Points#(6 , 0)
	Clock_Points#(CLOCK_FRAME_POINTS + 111 , 1) = Minutes_Hand_Points#(6 , 1)
	Clock_Points#(CLOCK_FRAME_POINTS + 111 , 2) = Minutes_Hand_Points#(6 , 2)

	Clock_Points#(CLOCK_FRAME_POINTS + 112 , 0) = Minutes_Hand_Points#(7 , 0)
	Clock_Points#(CLOCK_FRAME_POINTS + 112 , 1) = Minutes_Hand_Points#(7 , 1)
	Clock_Points#(CLOCK_FRAME_POINTS + 112 , 2) = Minutes_Hand_Points#(7 , 2)

	Clock_Points#(CLOCK_FRAME_POINTS + 113 , 0) = Minutes_Hand_Points#(8 , 0)
	Clock_Points#(CLOCK_FRAME_POINTS + 113 , 1) = Minutes_Hand_Points#(8 , 1)
	Clock_Points#(CLOCK_FRAME_POINTS + 113 , 2) = Minutes_Hand_Points#(8 , 2)
	
;	Minute HAND TRIANGLES			;FRONT

	Num_Triangles = Num_Triangles + 1
	
	Clock_Triangles(Num_Triangles , 0) = CLOCK_FRAME_POINTS + 106
	Clock_Triangles(Num_Triangles , 1) = CLOCK_FRAME_POINTS + 105
	Clock_Triangles(Num_Triangles , 2) = CLOCK_FRAME_POINTS + 113
	
	Num_Triangles = Num_Triangles + 1
	
	Clock_Triangles(Num_Triangles , 0) = CLOCK_FRAME_POINTS + 106
	Clock_Triangles(Num_Triangles , 1) = CLOCK_FRAME_POINTS + 113
	Clock_Triangles(Num_Triangles , 2) = CLOCK_FRAME_POINTS + 107 
	
	Num_Triangles = Num_Triangles + 1
	
	Clock_Triangles(Num_Triangles , 0) = CLOCK_FRAME_POINTS + 113
	Clock_Triangles(Num_Triangles , 1) = CLOCK_FRAME_POINTS + 108
	Clock_Triangles(Num_Triangles , 2) = CLOCK_FRAME_POINTS + 107 
	
	Num_Triangles = Num_Triangles + 1
	
	Clock_Triangles(Num_Triangles , 0) = CLOCK_FRAME_POINTS + 113
	Clock_Triangles(Num_Triangles , 1) = CLOCK_FRAME_POINTS + 109
	Clock_Triangles(Num_Triangles , 2) = CLOCK_FRAME_POINTS + 108
	
	Num_Triangles = Num_Triangles + 1
	
	Clock_Triangles(Num_Triangles , 0) = CLOCK_FRAME_POINTS + 113
	Clock_Triangles(Num_Triangles , 1) = CLOCK_FRAME_POINTS + 110
	Clock_Triangles(Num_Triangles , 2) = CLOCK_FRAME_POINTS + 109
	
	Num_Triangles = Num_Triangles + 1
	
	Clock_Triangles(Num_Triangles , 0) = CLOCK_FRAME_POINTS + 113
	Clock_Triangles(Num_Triangles , 1) = CLOCK_FRAME_POINTS + 111
	Clock_Triangles(Num_Triangles , 2) = CLOCK_FRAME_POINTS + 110
	
	Num_Triangles = Num_Triangles + 1
	
	Clock_Triangles(Num_Triangles , 0) = CLOCK_FRAME_POINTS + 112
	Clock_Triangles(Num_Triangles , 1) = CLOCK_FRAME_POINTS + 111
	Clock_Triangles(Num_Triangles , 2) = CLOCK_FRAME_POINTS + 113
	
	Num_Triangles = Num_Triangles + 1
	
	Clock_Triangles(Num_Triangles , 0) = CLOCK_FRAME_POINTS + 112
	Clock_Triangles(Num_Triangles , 1) = CLOCK_FRAME_POINTS + 113
	Clock_Triangles(Num_Triangles , 2) = CLOCK_FRAME_POINTS + 105

	;	MinuteS HAND TRIANGLES	;BACK
	
	Num_Triangles = Num_Triangles + 1
	
	Clock_Triangles(Num_Triangles , 0) = CLOCK_FRAME_POINTS + 105
	Clock_Triangles(Num_Triangles , 1) = CLOCK_FRAME_POINTS + 106
	Clock_Triangles(Num_Triangles , 2) = CLOCK_FRAME_POINTS + 112
	
	Num_Triangles = Num_Triangles + 1
	
	Clock_Triangles(Num_Triangles , 0) = CLOCK_FRAME_POINTS + 110
	Clock_Triangles(Num_Triangles , 1) = CLOCK_FRAME_POINTS + 108
	Clock_Triangles(Num_Triangles , 2) = CLOCK_FRAME_POINTS + 109
	
	Num_Triangles = Num_Triangles + 1
	
	Clock_Triangles(Num_Triangles , 0) = CLOCK_FRAME_POINTS + 112
	Clock_Triangles(Num_Triangles , 1) = CLOCK_FRAME_POINTS + 106
	Clock_Triangles(Num_Triangles , 2) = CLOCK_FRAME_POINTS + 107
	
	Num_Triangles = Num_Triangles + 1
	
	Clock_Triangles(Num_Triangles , 0) = CLOCK_FRAME_POINTS + 112
	Clock_Triangles(Num_Triangles , 1) = CLOCK_FRAME_POINTS + 107
	Clock_Triangles(Num_Triangles , 2) = CLOCK_FRAME_POINTS + 111
	
	Num_Triangles = Num_Triangles + 1
	
	Clock_Triangles(Num_Triangles , 0) = CLOCK_FRAME_POINTS + 111
	Clock_Triangles(Num_Triangles , 1) = CLOCK_FRAME_POINTS + 107
	Clock_Triangles(Num_Triangles , 2) = CLOCK_FRAME_POINTS + 108
	
	Num_Triangles = Num_Triangles + 1
	
	Clock_Triangles(Num_Triangles , 0) = CLOCK_FRAME_POINTS + 111
	Clock_Triangles(Num_Triangles , 1) = CLOCK_FRAME_POINTS + 108
	Clock_Triangles(Num_Triangles , 2) = CLOCK_FRAME_POINTS + 110


	
		;	HOURS HAND POINTS
	
	Hours_Hand_Points#(0 , 0) = 0
	Hours_Hand_Points#(0 , 1) = -80
	Hours_Hand_Points#(0 , 2) = (CLOCK_WIDTH / 2) / 2 + 10
	
	Hours_Hand_Points#(1 , 0) = -5
	Hours_Hand_Points#(1 , 1) = -35
	Hours_Hand_Points#(1 , 2) = (CLOCK_WIDTH / 2) / 2 + 10
	
	Hours_Hand_Points#(2 , 0) = -10
	Hours_Hand_Points#(2 , 1) = -10
	Hours_Hand_Points#(2 , 2) = (CLOCK_WIDTH / 2) / 2 + 10
	
	Hours_Hand_Points#(3 , 0) = -5
	Hours_Hand_Points#(3 , 1) = -5
	Hours_Hand_Points#(3 , 2) = (CLOCK_WIDTH / 2) / 2 + 10
	
	Hours_Hand_Points#(4 , 0) = 0
	Hours_Hand_Points#(4 , 1) = 0
	Hours_Hand_Points#(4 , 2) = (CLOCK_WIDTH / 2) / 2 + 10
	
	Hours_Hand_Points#(5 , 0) = 5
	Hours_Hand_Points#(5 , 1) = -5
	Hours_Hand_Points#(5 , 2) = (CLOCK_WIDTH / 2) / 2 + 10
	
	Hours_Hand_Points#(6 , 0) = 10
	Hours_Hand_Points#(6 , 1) = -10
	Hours_Hand_Points#(6 , 2) = (CLOCK_WIDTH / 2) / 2 + 10

	Hours_Hand_Points#(7 , 0) = 5
	Hours_Hand_Points#(7 , 1) = -35
	Hours_Hand_Points#(7 , 2) = (CLOCK_WIDTH / 2) / 2 + 10

	Hours_Hand_Points#(8 , 0) = 0
	Hours_Hand_Points#(8 , 1) = -10
	Hours_Hand_Points#(8 , 2) = (CLOCK_WIDTH / 2) / 2 + 13
	
	;Hours HAND POINTS
	
	Clock_Points#(CLOCK_FRAME_POINTS + 114 , 0) = Hours_Hand_Points#(0 , 0)
	Clock_Points#(CLOCK_FRAME_POINTS + 114 , 1) = Hours_Hand_Points#(0 , 1)
	Clock_Points#(CLOCK_FRAME_POINTS + 114 , 2) = Hours_Hand_Points#(0 , 2)
	
	Clock_Points#(CLOCK_FRAME_POINTS + 115 , 0) = Hours_Hand_Points#(1 , 0)
	Clock_Points#(CLOCK_FRAME_POINTS + 115 , 1) = Hours_Hand_Points#(1 , 1)
	Clock_Points#(CLOCK_FRAME_POINTS + 115 , 2) = Hours_Hand_Points#(1 , 2)
	
	Clock_Points#(CLOCK_FRAME_POINTS + 116 , 0) = Hours_Hand_Points#(2 , 0)
	Clock_Points#(CLOCK_FRAME_POINTS + 116 , 1) = Hours_Hand_Points#(2 , 1)
	Clock_Points#(CLOCK_FRAME_POINTS + 116 , 2) = Hours_Hand_Points#(2 , 2)
	
	Clock_Points#(CLOCK_FRAME_POINTS + 117 , 0) = Hours_Hand_Points#(3 , 0)
	Clock_Points#(CLOCK_FRAME_POINTS + 117 , 1) = Hours_Hand_Points#(3 , 1)
	Clock_Points#(CLOCK_FRAME_POINTS + 117 , 2) = Hours_Hand_Points#(3 , 2)
	
	Clock_Points#(CLOCK_FRAME_POINTS + 118 , 0) = Hours_Hand_Points#(4 , 0)
	Clock_Points#(CLOCK_FRAME_POINTS + 118 , 1) = Hours_Hand_Points#(4 , 1)
	Clock_Points#(CLOCK_FRAME_POINTS + 118 , 2) = Hours_Hand_Points#(4 , 2)
	
	Clock_Points#(CLOCK_FRAME_POINTS + 119 , 0) = Hours_Hand_Points#(5 , 0)
	Clock_Points#(CLOCK_FRAME_POINTS + 119 , 1) = Hours_Hand_Points#(5 , 1)
	Clock_Points#(CLOCK_FRAME_POINTS + 119 , 2) = Hours_Hand_Points#(5 , 2)
	
	Clock_Points#(CLOCK_FRAME_POINTS + 120 , 0) = Hours_Hand_Points#(6 , 0)
	Clock_Points#(CLOCK_FRAME_POINTS + 120 , 1) = Hours_Hand_Points#(6 , 1)
	Clock_Points#(CLOCK_FRAME_POINTS + 120 , 2) = Hours_Hand_Points#(6 , 2)

	Clock_Points#(CLOCK_FRAME_POINTS + 121 , 0) = Hours_Hand_Points#(7 , 0)
	Clock_Points#(CLOCK_FRAME_POINTS + 121 , 1) = Hours_Hand_Points#(7 , 1)
	Clock_Points#(CLOCK_FRAME_POINTS + 121 , 2) = Hours_Hand_Points#(7 , 2)

	Clock_Points#(CLOCK_FRAME_POINTS + 122 , 0) = Hours_Hand_Points#(8 , 0)
	Clock_Points#(CLOCK_FRAME_POINTS + 122 , 1) = Hours_Hand_Points#(8 , 1)
	Clock_Points#(CLOCK_FRAME_POINTS + 122 , 2) = Hours_Hand_Points#(8 , 2)
	
;	Minute HAND TRIANGLES			;FRONT

	Num_Triangles = Num_Triangles + 1
	
	Clock_Triangles(Num_Triangles , 0) = CLOCK_FRAME_POINTS + 115
	Clock_Triangles(Num_Triangles , 1) = CLOCK_FRAME_POINTS + 114
	Clock_Triangles(Num_Triangles , 2) = CLOCK_FRAME_POINTS + 122
	
	Num_Triangles = Num_Triangles + 1
	
	Clock_Triangles(Num_Triangles , 0) = CLOCK_FRAME_POINTS + 115
	Clock_Triangles(Num_Triangles , 1) = CLOCK_FRAME_POINTS + 122
	Clock_Triangles(Num_Triangles , 2) = CLOCK_FRAME_POINTS + 116 
	
	Num_Triangles = Num_Triangles + 1
	
	Clock_Triangles(Num_Triangles , 0) = CLOCK_FRAME_POINTS + 122
	Clock_Triangles(Num_Triangles , 1) = CLOCK_FRAME_POINTS + 117
	Clock_Triangles(Num_Triangles , 2) = CLOCK_FRAME_POINTS + 116 
	
	Num_Triangles = Num_Triangles + 1
	
	Clock_Triangles(Num_Triangles , 0) = CLOCK_FRAME_POINTS + 122
	Clock_Triangles(Num_Triangles , 1) = CLOCK_FRAME_POINTS + 118
	Clock_Triangles(Num_Triangles , 2) = CLOCK_FRAME_POINTS + 117
	
	Num_Triangles = Num_Triangles + 1
	
	Clock_Triangles(Num_Triangles , 0) = CLOCK_FRAME_POINTS + 122
	Clock_Triangles(Num_Triangles , 1) = CLOCK_FRAME_POINTS + 119
	Clock_Triangles(Num_Triangles , 2) = CLOCK_FRAME_POINTS + 118
	
	Num_Triangles = Num_Triangles + 1
	
	Clock_Triangles(Num_Triangles , 0) = CLOCK_FRAME_POINTS + 122
	Clock_Triangles(Num_Triangles , 1) = CLOCK_FRAME_POINTS + 120
	Clock_Triangles(Num_Triangles , 2) = CLOCK_FRAME_POINTS + 119
	
	Num_Triangles = Num_Triangles + 1
	
	Clock_Triangles(Num_Triangles , 0) = CLOCK_FRAME_POINTS + 121
	Clock_Triangles(Num_Triangles , 1) = CLOCK_FRAME_POINTS + 120
	Clock_Triangles(Num_Triangles , 2) = CLOCK_FRAME_POINTS + 122
	
	Num_Triangles = Num_Triangles + 1
	
	Clock_Triangles(Num_Triangles , 0) = CLOCK_FRAME_POINTS + 121
	Clock_Triangles(Num_Triangles , 1) = CLOCK_FRAME_POINTS + 122
	Clock_Triangles(Num_Triangles , 2) = CLOCK_FRAME_POINTS + 114

	;	Hours HAND TRIANGLES	;BACK
	
	Num_Triangles = Num_Triangles + 1
	
	Clock_Triangles(Num_Triangles , 0) = CLOCK_FRAME_POINTS + 114
	Clock_Triangles(Num_Triangles , 1) = CLOCK_FRAME_POINTS + 115
	Clock_Triangles(Num_Triangles , 2) = CLOCK_FRAME_POINTS + 121
	
	Num_Triangles = Num_Triangles + 1
	
	Clock_Triangles(Num_Triangles , 0) = CLOCK_FRAME_POINTS + 119
	Clock_Triangles(Num_Triangles , 1) = CLOCK_FRAME_POINTS + 117
	Clock_Triangles(Num_Triangles , 2) = CLOCK_FRAME_POINTS + 118
	
	Num_Triangles = Num_Triangles + 1
	
	Clock_Triangles(Num_Triangles , 0) = CLOCK_FRAME_POINTS + 121
	Clock_Triangles(Num_Triangles , 1) = CLOCK_FRAME_POINTS + 115
	Clock_Triangles(Num_Triangles , 2) = CLOCK_FRAME_POINTS + 116
	
	Num_Triangles = Num_Triangles + 1
	
	Clock_Triangles(Num_Triangles , 0) = CLOCK_FRAME_POINTS + 121
	Clock_Triangles(Num_Triangles , 1) = CLOCK_FRAME_POINTS + 116
	Clock_Triangles(Num_Triangles , 2) = CLOCK_FRAME_POINTS + 120
	
	Num_Triangles = Num_Triangles + 1
	
	Clock_Triangles(Num_Triangles , 0) = CLOCK_FRAME_POINTS + 120
	Clock_Triangles(Num_Triangles , 1) = CLOCK_FRAME_POINTS + 116
	Clock_Triangles(Num_Triangles , 2) = CLOCK_FRAME_POINTS + 117
	
	Num_Triangles = Num_Triangles + 1
	
	Clock_Triangles(Num_Triangles , 0) = CLOCK_FRAME_POINTS + 120
	Clock_Triangles(Num_Triangles , 1) = CLOCK_FRAME_POINTS + 117
	Clock_Triangles(Num_Triangles , 2) = CLOCK_FRAME_POINTS + 119

	CalculateClockNormals()
	
End Function	 
	
	
	
	
	




Function GenerateSinCosTable()

	;FUNCTION TO GENERATE A SIN/COS LUT FOR 3D GFX
	;REQUIRES GLOBAL FLOAT ARRAYS KSIN(rotation_amount) , KCOS(rotation_amount)

	Local cnt# , cnt2

	For cnt=0 To 359.82421875 Step 0.17578125			; 2048 rotations within 360 degrees (0 - 2047)
		ksin(cnt2)=Sin(cnt)
		kcos(cnt2)=Cos(cnt)
		cnt2 = cnt2 + 1
	Next
	
End Function



Function RotateVertices(KSX# , KCX# , KSY# , KCY# , KSZ# , KCZ#)

	For cnt = 0 To ALL_CLOCK_VERTICES
	
		A# = Clock_Points(cnt , 0)
		B# = Clock_Points(cnt , 1)
		C# = Clock_Points(cnt , 2)
			
		;XROT
		E# = C# * KCX# + B# * KSX#
		
		;YROT
		Clock_Points_PROJ(cnt , 2) = 1500 + (E# * KCY# + A# * KSY#)
		
		;ZROT
		D# = B# * KCX# - C# * KSX#
		F# = A# * KCY# - E# * KSY#
		Clock_Points_PROJ(cnt , 0) = 1000 + (D# * KSZ# - F# * KCZ#)
		Clock_Points_PROJ(cnt , 1) = 1000 + (D# * KCZ# + F# * KSZ#)
	
	Next

End Function




Function ProjectVertices()

	For cnt = 0 To ALL_CLOCK_VERTICES
	
		A# = Float 1 / (Clock_Points_PROJ(cnt , 2) - Cam_Z)
		Clock_Points_PROJ#(cnt , 0) = ((Cam_X - Clock_Points_PROJ#(cnt , 0)) * MULTIPLANT) * A# + GHALFX
		Clock_Points_PROJ#(cnt , 1) = ((Clock_Points_PROJ#(cnt , 1) - Cam_Y) * MULTIPLANT) * A# + GHALFY
	
	Next
	
End Function




Function CullandLightSourceTriangles(KSX# , KCX# , KSY# , KCY# , KSZ# , KCZ#)

  Local Triangles_to_draw_amount = 0

	For cnt = 0 To NUM_TRIANGLES_ON_CLOCK - 1 Step 2
	
		;XROT
		Clock_Triangles_NORMAL_PROJ(cnt , 1) = Clock_Triangles_NORMAL(cnt , 1) * KCX# - Clock_Triangles_NORMAL(cnt , 2) * KSX#
		Clock_Triangles_NORMAL_PROJ(cnt , 2) = Clock_Triangles_NORMAL(cnt , 2) * KCX# + Clock_Triangles_NORMAL(cnt , 1) * KSX#
		
		;YROT
		Clock_Triangles_NORMAL_PROJ(cnt , 0) = Clock_Triangles_NORMAL(cnt , 0) * KCY# - Clock_Triangles_NORMAL_PROJ(cnt , 2) * KSY#
		Clock_Triangles_NORMAL_PROJ(cnt , 2) = Clock_Triangles_NORMAL_PROJ(cnt , 2) * KCY# + Clock_Triangles_NORMAL(cnt , 0) * KSY#
		
		;ZROT
		Temp# = Clock_Triangles_NORMAL_PROJ(cnt , 0)
		Clock_Triangles_NORMAL_PROJ(cnt , 0) = Clock_Triangles_NORMAL_PROJ(cnt , 1) * KSZ# - Temp * KCZ#
		Clock_Triangles_NORMAL_PROJ(cnt , 1) = Clock_Triangles_NORMAL_PROJ(cnt , 1) * KCZ# + Temp * KSZ#
	
		VX# = Clock_Points_PROJ(Clock_Triangles(cnt , 0) , 0) - Cam_X
		VY# = Clock_Points_PROJ(Clock_Triangles(cnt , 0) , 1) - Cam_Y	;Get Vector from Camera to Polygon
		VZ# = Clock_Points_PROJ(Clock_Triangles(cnt , 0) , 2) - Cam_Z
			
		If (Clock_Triangles_NORMAL_PROJ(cnt , 0) * VX#) + (Clock_Triangles_NORMAL_PROJ(cnt , 1) * VY#) + (Clock_Triangles_NORMAL_PROJ(cnt , 2) * VZ#) < 0
		
			VX# = Float LightSource(0) - Clock_Points_PROJ(Clock_Triangles(cnt , 0) , 0)
			VY# = Float Clock_Points_PROJ(Clock_Triangles(cnt , 0) , 1) - LightSource(1)
			VZ# = Float Clock_Points_PROJ(Clock_Triangles(cnt , 0) , 2) - LightSource(2)
				
			Mag# = 1 / Sqr(VX# * VX# + VY# * VY# + VZ# * VZ#)
				
			VX# = Float VX# * Mag
			VY# = Float VY# * Mag
			VZ# = Float VZ# * Mag
				
			Result# = Float (Clock_Triangles_NORMAL_PROJ(cnt , 0) * VX#) + (Clock_Triangles_NORMAL_PROJ(cnt , 1) * VY#) + (Clock_Triangles_NORMAL_PROJ(cnt , 2) * VZ#)
			
			cnt2 = cnt + 1
			
			If Result# > 0
				
				Triangle_Brightness(cnt) = 0.15
				Triangle_Brightness(cnt2) = 0.15

			Else
			
				If Result# > - 0.15
				
					Result# = - 0.15
									
				End If
				
				Triangle_Brightness(cnt) = - Result
				Triangle_Brightness(cnt2) = - Result

			End If
			
			Triangles_to_draw_amount = Triangles_to_draw_amount + 1
			
			Triangles_2_Render(Triangles_to_draw_amount , 0) = cnt
			
			Triangles_Mid_Zpoints(Triangles_to_draw_amount) = Float 1000000 * ((Clock_Points_PROJ(Clock_Triangles(cnt , 0) , 2) + Clock_Points_PROJ(Clock_Triangles(cnt , 1) , 2) + Clock_Points_PROJ(Clock_Triangles(cnt , 2) , 2)) * OneOver3)
			
			Triangles_to_draw_amount = Triangles_to_draw_amount + 1
			
			Triangles_2_Render(Triangles_to_draw_amount , 0) = cnt2
			
			Triangles_Mid_Zpoints(Triangles_to_draw_amount) = Float 1000000 * ((Clock_Points_PROJ(Clock_Triangles(cnt , 0) , 2) + Clock_Points_PROJ(Clock_Triangles(cnt , 1) , 2) + Clock_Points_PROJ(Clock_Triangles(cnt , 2) , 2)) * OneOver3)
			
		End If
			
	Next
	
	Return Triangles_to_draw_amount
	
End Function




Function RadixSort(Triangle_Amount)

	Local Mask = 24
	
	For Cntr = 0 To 6				;Every 1 of these sorts a 4-bit radix from LSB(0) to MSB(n) (0 - 3 = 2 Bytes)
	
		For Index = 0 To 15
		
			NumArray(Index) = 0
			
		Next
	
		For Index = 1 To Triangle_Amount
		
			Var = (Triangles_Mid_ZPoints(Index) Shl Mask) Shr 28
			
			NumArray(Var) = NumArray(Var) + 1
			RadixArray(Var , NumArray(Var)) = Triangles_Mid_ZPoints(Index)
			RadixArray2(Var , NumArray(Var)) = Triangles_2_Render(Index , 0)
			
		Next
		
		Index = 0
		
		For Cntr2 = 15 To 0 Step -1
		
			For Cntr3 = 1 To NumArray(Cntr2)
			
				Index = Index + 1
				Triangles_Mid_ZPoints(Index) = RadixArray(Cntr2 , Cntr3)
				Triangles_2_Render(Index , 0) = RadixArray2(Cntr2 , Cntr3)
				
			Next
			
		Next
		
		Mask = Mask - 4
	
	Next
		
End Function











Function DrawClockTriangleFlatShaded(Triangle_No , col)

  	EdgeAmount = 3
  
	Top = 0

	If Clock_Points_PROJ(Clock_Triangles(Triangle_No , 1) , 1) < Clock_Points_PROJ(Clock_Triangles(Triangle_No , Top) , 1) Then Top = 1
	If Clock_Points_PROJ(Clock_Triangles(Triangle_No , 2) , 1) < Clock_Points_PROJ(Clock_Triangles(Triangle_No , Top) , 1) Then Top = 2

	RightPos = Top
	LeftPos = RightPos

	While EdgeAmount > 0
	
		If LEdgeHeight <= 0									;(RE)Initialse LeftSegment Data
		
			NewLeftPos = LeftPos + 1
			If NewLeftPos > 2 Then NewLeftPos = 0
						
			y1 = Floor(Clock_Points_PROJ(Clock_Triangles(Triangle_No , LeftPos) , 1))
			x2# = Clock_Points_PROJ(Clock_Triangles(Triangle_No , NewLeftPos) , 0)
			y2 = Floor(Clock_Points_PROJ(Clock_Triangles(Triangle_No , NewLeftPos) , 1))
	
			LEdgeHeight = y2 - y1
			
			If LEdgeHeight > 0
			
				x1# = Clock_Points_PROJ(Clock_Triangles(Triangle_No , LeftPos) , 0)
					
				LStepX# = (x2# - x1#) / LEdgeHeight
				
				If y1 < MINY				;CLIP to the MINY if it needs it
				
					y = MINY
					StepY = MINY - y1
					Lx# = x1# + StepY * LStepX#
					LEdgeHeight = LEdgeHeight - StepY
					
				Else
				
					y = y1
					Lx# = x1#
					
				End If
						
			End If

			LeftPos = NewLeftPos
			
			EdgeAmount = EdgeAmount - 1
				
		End If
		
		If REdgeHeight <= 0					;(RE)Initialise RightSegment Data
		
			NewRightPos = RightPos - 1
			If NewRightPos < 0 Then NewRightPos = 2
			
			y1 = Floor(Clock_Points_PROJ(Clock_Triangles(Triangle_No , RightPos) , 1))
			x2# = Clock_Points_PROJ(Clock_Triangles(Triangle_No , NewRightPos) , 0)
			y2 = Floor(Clock_Points_PROJ(Clock_Triangles(Triangle_No , NewRightPos) , 1))
			
			REdgeHeight = y2 - y1
			
			If REdgeHeight > 0
			
				x1# = Clock_Points_PROJ(Clock_Triangles(Triangle_No , RightPos) , 0)
				
				RStepX# = Float (x2# - x1#) / REdgeHeight

				If y1 < MINY				;Clip to the MINY if it needs it
					
					y = MINY
					StepY = MINY - y1
					Rx# = x1# + StepY * RStepX#
					REdgeHeight = REdgeHeight - StepY
										
				Else
				
					y = y1
					Rx# = x1#
					
				End If
					
			End If
			
			RightPos = NewRightPos
			EdgeAmount = EdgeAmount - 1
				
		End If
		
		If LEdgeHeight < REdgeHeight
		
			Height = LEdgeHeight - 1
			
			If y + Height > MAXY
			
				Height = MAXY - y
				EdgeAmount = -1
				
			End If
			
		Else
		
			Height = REdgeHeight - 1
			
			If y + Height > MAXY
			
				Height = MAXY - y
				EdgeAmount = -1
				
			End If
				
		End If
		
		For cntr = y To y + Height		;RASTER SCANLINE
		
			If Lx# < Rx#
			
				PixL = Floor(Lx#)
				PixR = Floor(Rx#) - 1
				
			Else
			
				PixL = Floor(Rx#)
				PixR = Floor(Lx#) - 1
				
			End If

			If PixL < MINX
				
				PixL = MINX
					
			Else
				
				If PixL > MAXX Then PixL = MAXX + 1
					
			End If
				
			If PixR < MINX
				
				PixR = MINX - 1
					
			Else
				
				If PixR > MAXX Then PixR = MAXX
					
			End If
			
			For pix = PixL To PixR
			
				Screen_Array(pix , cntr) = col
				
			Next
						
			For pix = PixL To PixR
			
				WritePixelFast pix , cntr , col
							
			Next
				
			Lx# = Lx# + LStepX#
			Rx# = Rx# + RStepX#
		
			LEdgeHeight = LEdgeHeight - 1
			REdgeHeight = REdgeHeight - 1
			
		Next									;END RASTER SCANLINE
		
	Wend

End Function





Function DrawClockTriangleFlatShadedAlpha(Triangle_No , col , Alpha#)

  	EdgeAmount = 3
  
	Top = 0

	If Clock_Points_PROJ(Clock_Triangles(Triangle_No , 1) , 1) < Clock_Points_PROJ(Clock_Triangles(Triangle_No , Top) , 1) Then Top = 1
	If Clock_Points_PROJ(Clock_Triangles(Triangle_No , 2) , 1) < Clock_Points_PROJ(Clock_Triangles(Triangle_No , Top) , 1) Then Top = 2

	RightPos = Top
	LeftPos = RightPos
	
	Col1R = (col Shl 8) Shr 24
	Col1G = (col Shl 16) Shr 24
	Col1B = (col Shl 24) Shr 24

	While EdgeAmount > 0
	
		If LEdgeHeight <= 0									;(RE)Initialse LeftSegment Data
		
			NewLeftPos = LeftPos + 1
			If NewLeftPos > 2 Then NewLeftPos = 0
						
			y1 = Floor(Clock_Points_PROJ(Clock_Triangles(Triangle_No , LeftPos) , 1))
			x2# = Clock_Points_PROJ(Clock_Triangles(Triangle_No , NewLeftPos) , 0)
			y2 = Floor(Clock_Points_PROJ(Clock_Triangles(Triangle_No , NewLeftPos) , 1))
	
			LEdgeHeight = y2 - y1
			
			If LEdgeHeight > 0
			
				x1# = Clock_Points_PROJ(Clock_Triangles(Triangle_No , LeftPos) , 0)
					
				LStepX# = (x2# - x1#) / LEdgeHeight
				
				If y1 < MINY				;CLIP to the MINY if it needs it
				
					y = MINY
					StepY = MINY - y1
					Lx# = x1# + StepY * LStepX#
					LEdgeHeight = LEdgeHeight - StepY
					
				Else
				
					y = y1
					Lx# = x1#
					
				End If
						
			End If

			LeftPos = NewLeftPos
			
			EdgeAmount = EdgeAmount - 1
				
		End If
		
		If REdgeHeight <= 0					;(RE)Initialise RightSegment Data
		
			NewRightPos = RightPos - 1
			If NewRightPos < 0 Then NewRightPos = 2
			
			y1 = Floor(Clock_Points_PROJ(Clock_Triangles(Triangle_No , RightPos) , 1))
			x2# = Clock_Points_PROJ(Clock_Triangles(Triangle_No , NewRightPos) , 0)
			y2 = Floor(Clock_Points_PROJ(Clock_Triangles(Triangle_No , NewRightPos) , 1))
			
			REdgeHeight = y2 - y1
			
			If REdgeHeight > 0
			
				x1# = Clock_Points_PROJ(Clock_Triangles(Triangle_No , RightPos) , 0)
				
				RStepX# = Float (x2# - x1#) / REdgeHeight

				If y1 < MINY				;Clip to the MINY if it needs it
					
					y = MINY
					StepY = MINY - y1
					Rx# = x1# + StepY * RStepX#
					REdgeHeight = REdgeHeight - StepY
										
				Else
				
					y = y1
					Rx# = x1#
					
				End If
					
			End If
			
			RightPos = NewRightPos
			EdgeAmount = EdgeAmount - 1
				
		End If
		
		If LEdgeHeight < REdgeHeight
		
			Height = LEdgeHeight - 1
			
			If y + Height > MAXY
			
				Height = MAXY - y
				EdgeAmount = -1
				
			End If
			
		Else
		
			Height = REdgeHeight - 1
			
			If y + Height > MAXY
			
				Height = MAXY - y
				EdgeAmount = -1
				
			End If
				
		End If
		
		For cntr = y To y + Height		;RASTER SCANLINE
		
			If Lx# < Rx#
			
				PixL = Floor(Lx#)
				PixR = Floor(Rx#) - 1
				
			Else
			
				PixL = Floor(Rx#)
				PixR = Floor(Lx#) - 1
				
			End If

			If PixL < MINX
				
				PixL = MINX
					
			Else
				
				If PixL > MAXX Then PixL = MAXX + 1
					
			End If
				
			If PixR < MINX
				
				PixR = MINX - 1
					
			Else
				
				If PixR > MAXX Then PixR = MAXX
					
			End If
				
			For pix = PixL To PixR
			
				Pixel_Read = Screen_Array(pix , cntr)
				
				Col2R = Float (((Pixel_Read Shl 8) Shr 24) - Col1R) * Alpha + Col1R
				Col2G = Float (((Pixel_Read Shl 16) Shr 24) - Col1G) * Alpha + Col1G
				Col2B = Float (((Pixel_Read Shl 24) Shr 24) - Col1B) * Alpha + Col1B
				
				NewCol = (Col2R Shl 16) + (Col2G Shl 8) + Col2B
				
				Screen_Array(pix , cntr) = NewCol
				
				WritePixelFast pix , cntr , NewCol
							
			Next
				
			Lx# = Lx# + LStepX#
			Rx# = Rx# + RStepX#
		
			LEdgeHeight = LEdgeHeight - 1
			REdgeHeight = REdgeHeight - 1
			
		Next									;END RASTER SCANLINE
		
	Wend

End Function








Function val(txt$) 

	; String to Integer function (Decimal). 
	
  Local T$ = Upper$(txt$) , L = Len(t$) , Power = 1
  Local Pos , V , Value

	For Pos = L To 1 Step -1 

		V = Asc(Mid$(T$ , Pos , 1)) - 48 
		
		If (V > -1) And (V < 10) ; Ensure a Zero for letters 

			Value = Value + (V * Power) 
			Power = Power * 10
		
		End If 

	Next 
	
	Return Value 
	
End Function





Function CalculateClockNormals()

  Local Vector1X# , Vector1Y# , Vector1Z# , Result1X# , Result2X# , Result1Y# , Result2Y# , Result1Z# , Result2Z#
  Local Vector2X# , Vector2Y# , Vector2Z#
  Local Mag1# , Mag2# 

	For cnt = 0 To CLOCK_POINT_AMOUNT Shl 3 + 144 - 1 Step 2
	
		Vector1X = Float Clock_Points(Clock_Triangles(cnt , 0) , 0) - Clock_Points(Clock_Triangles(cnt , 1) , 0); Vector from
		Vector1Y = Float Clock_Points(Clock_Triangles(cnt , 0) , 1) - Clock_Points(Clock_Triangles(cnt , 1) , 1); point 1 to 0
		Vector1Z = Float Clock_Points(Clock_Triangles(cnt , 0) , 2) - Clock_Points(Clock_Triangles(cnt , 1) , 2)
		
		Vector2X = Float Clock_Points(Clock_Triangles(cnt , 1) , 0) - Clock_Points(Clock_Triangles(cnt , 2) , 0); Vector from
		Vector2Y = Float Clock_Points(Clock_Triangles(cnt , 1) , 1) - Clock_Points(Clock_Triangles(cnt , 2) , 1); point 1 to 0
		Vector2Z = Float Clock_Points(Clock_Triangles(cnt , 1) , 2) - Clock_Points(Clock_Triangles(cnt , 2) , 2)

		Mag1# = Sqr#(Vector1X * Vector1X + Vector1Y * Vector1Y + Vector1Z * Vector1Z);Get
		Mag2# = Sqr#(Vector2X * Vector2X + Vector2Y * Vector2Y + Vector2Z * Vector2Z);Magnitutes
		 
		Vector1X = Float Vector1X / Mag1
		Vector1Y = Float Vector1Y / Mag1
		Vector1Z = Float Vector1Z / Mag1
		Vector2X = Float Vector2X / Mag2
		Vector2Y = Float Vector2Y / Mag2
		Vector2Z = Float Vector2Z / Mag2

		Result1X# = Float Vector1Y * Vector2Z - Vector1Z * Vector2Y
		Result1Y# = Float Vector1Z * Vector2X - Vector1X * Vector2Z
		Result1Z# = Float Vector1X * Vector2Y - Vector1Y * Vector2X
		
		Vector1X = Float Clock_Points(Clock_Triangles(cnt + 1 , 0) , 0) - Clock_Points(Clock_Triangles(cnt + 1 , 1) , 0); Vector from
		Vector1Y = Float Clock_Points(Clock_Triangles(cnt + 1 , 0) , 1) - Clock_Points(Clock_Triangles(cnt + 1 , 1) , 1); point 1 to 0
		Vector1Z = Float Clock_Points(Clock_Triangles(cnt + 1 , 0) , 2) - Clock_Points(Clock_Triangles(cnt + 1 , 1) , 2)
		
		Vector2X = Float Clock_Points(Clock_Triangles(cnt + 1 , 1) , 0) - Clock_Points(Clock_Triangles(cnt + 1 , 2) , 0); Vector from
		Vector2Y = Float Clock_Points(Clock_Triangles(cnt + 1 , 1) , 1) - Clock_Points(Clock_Triangles(cnt + 1 , 2) , 1); point 1 to 0
		Vector2Z = Float Clock_Points(Clock_Triangles(cnt + 1 , 1) , 2) - Clock_Points(Clock_Triangles(cnt + 1 , 2) , 2)

		Mag1# = Sqr#(Vector1X * Vector1X + Vector1Y * Vector1Y + Vector1Z * Vector1Z);Get
		Mag2# = Sqr#(Vector2X * Vector2X + Vector2Y * Vector2Y + Vector2Z * Vector2Z);Magnitutes
		 
		Vector1X = Float Vector1X / Mag1
		Vector1Y = Float Vector1Y / Mag1
		Vector1Z = Float Vector1Z / Mag1
		Vector2X = Float Vector2X / Mag2
		Vector2Y = Float Vector2Y / Mag2
		Vector2Z = Float Vector2Z / Mag2

		Result2X# = Float Vector1Y * Vector2Z - Vector1Z * Vector2Y
		Result2Y# = Float Vector1Z * Vector2X - Vector1X * Vector2Z
		Result2Z# = Float Vector1X * Vector2Y - Vector1Y * Vector2X
		
		If Float Abs(Result1X#) + Abs(Result1Y#) + Abs(Result1Z#) > Float Abs(Result2X#) + Abs(Result2Y#) + Abs(Result2Z#)
		
			Clock_Triangles_NORMAL(cnt , 0) = Result1X#
			Clock_Triangles_NORMAL(cnt , 1) = Result1Y#
			Clock_Triangles_NORMAL(cnt , 2) = Result1Z#
			
			Clock_Triangles_NORMAL(cnt + 1 , 0) = Result1X#
			Clock_Triangles_NORMAL(cnt + 1 , 1) = Result1Y#
			Clock_Triangles_NORMAL(cnt + 1 , 2) = Result1Z#
		
		Else
		
			Clock_Triangles_NORMAL(cnt , 0) = Result2X#
			Clock_Triangles_NORMAL(cnt , 1) = Result2Y#
			Clock_Triangles_NORMAL(cnt , 2) = Result2Z#
			
			Clock_Triangles_NORMAL(cnt + 1 , 0) = Result2X#
			Clock_Triangles_NORMAL(cnt + 1 , 1) = Result2Y#
			Clock_Triangles_NORMAL(cnt + 1 , 2) = Result2Z#
			
		End If
		
	Next
	
	;CALCULATE NORMAL FOR SECONDS HAND
	
	Current = CLOCK_POINT_AMOUNT * 8 + 144
	
	For cnt = 0 To 13 Step 2
	
		Vector1X = Float Clock_Points(Clock_Triangles(cnt + Current , 0) , 0) - Clock_Points(Clock_Triangles(cnt + Current , 1) , 0); Vector from
		Vector1Y = Float Clock_Points(Clock_Triangles(cnt + Current , 0) , 1) - Clock_Points(Clock_Triangles(cnt + Current , 1) , 1); point 1 to 0
		Vector1Z = Float Clock_Points(Clock_Triangles(cnt + Current , 0) , 2) - Clock_Points(Clock_Triangles(cnt + Current , 1) , 2)
		
		Vector2X = Float Clock_Points(Clock_Triangles(cnt + Current , 1) , 0) - Clock_Points(Clock_Triangles(cnt + Current , 2) , 0); Vector from
		Vector2Y = Float Clock_Points(Clock_Triangles(cnt + Current , 1) , 1) - Clock_Points(Clock_Triangles(cnt + Current , 2) , 1); point 1 to 0
		Vector2Z = Float Clock_Points(Clock_Triangles(cnt + Current , 1) , 2) - Clock_Points(Clock_Triangles(cnt + Current , 2) , 2)

		Mag1# = Sqr#(Vector1X * Vector1X + Vector1Y * Vector1Y + Vector1Z * Vector1Z);Get
		Mag2# = Sqr#(Vector2X * Vector2X + Vector2Y * Vector2Y + Vector2Z * Vector2Z);Magnitutes
		 
		Vector1X = Float Vector1X / Mag1
		Vector1Y = Float Vector1Y / Mag1
		Vector1Z = Float Vector1Z / Mag1
		Vector2X = Float Vector2X / Mag2
		Vector2Y = Float Vector2Y / Mag2
		Vector2Z = Float Vector2Z / Mag2

		Result1X# = Float Vector1Y * Vector2Z - Vector1Z * Vector2Y
		Result1Y# = Float Vector1Z * Vector2X - Vector1X * Vector2Z
		Result1Z# = Float Vector1X * Vector2Y - Vector1Y * Vector2X
		
		Vector1X = Float Clock_Points(Clock_Triangles(cnt + Current + 1 , 0) , 0) - Clock_Points(Clock_Triangles(cnt + Current + 1 , 1) , 0); Vector from
		Vector1Y = Float Clock_Points(Clock_Triangles(cnt + Current + 1 , 0) , 1) - Clock_Points(Clock_Triangles(cnt + Current + 1 , 1) , 1); point 1 to 0
		Vector1Z = Float Clock_Points(Clock_Triangles(cnt + Current + 1 , 0) , 2) - Clock_Points(Clock_Triangles(cnt + Current + 1 , 1) , 2)
		
		Vector2X = Float Clock_Points(Clock_Triangles(cnt + Current + 1 , 1) , 0) - Clock_Points(Clock_Triangles(cnt + Current + 1 , 2) , 0); Vector from
		Vector2Y = Float Clock_Points(Clock_Triangles(cnt + Current + 1 , 1) , 1) - Clock_Points(Clock_Triangles(cnt + Current + 1 , 2) , 1); point 1 to 0
		Vector2Z = Float Clock_Points(Clock_Triangles(cnt + Current + 1 , 1) , 2) - Clock_Points(Clock_Triangles(cnt + Current + 1 , 2) , 2)

		Mag1# = Sqr#(Vector1X * Vector1X + Vector1Y * Vector1Y + Vector1Z * Vector1Z);Get
		Mag2# = Sqr#(Vector2X * Vector2X + Vector2Y * Vector2Y + Vector2Z * Vector2Z);Magnitutes
		 
		Vector1X = Float Vector1X / Mag1
		Vector1Y = Float Vector1Y / Mag1
		Vector1Z = Float Vector1Z / Mag1
		Vector2X = Float Vector2X / Mag2
		Vector2Y = Float Vector2Y / Mag2
		Vector2Z = Float Vector2Z / Mag2

		Result2X# = Float Vector1Y * Vector2Z - Vector1Z * Vector2Y
		Result2Y# = Float Vector1Z * Vector2X - Vector1X * Vector2Z
		Result2Z# = Float Vector1X * Vector2Y - Vector1Y * Vector2X
		
		If Float Abs(Result1X#) + Abs(Result1Y#) + Abs(Result1Z#) > Float Abs(Result2X#) + Abs(Result2Y#) + Abs(Result2Z#)
		
			Seconds_Hand_NORMALS(cnt , 0) = Result1X#
			Seconds_Hand_NORMALS(cnt , 1) = Result1Y#
			Seconds_Hand_NORMALS(cnt , 2) = Result1Z#
			
			Seconds_Hand_NORMALS(cnt + 1 , 0) = Result1X#
			Seconds_Hand_NORMALS(cnt + 1 , 1) = Result1Y#
			Seconds_Hand_NORMALS(cnt + 1 , 2) = Result1Z#

			Clock_Triangles_NORMAL(cnt + Current , 0) = Result1X#
			Clock_Triangles_NORMAL(cnt + Current , 1) = Result1Y#
			Clock_Triangles_NORMAL(cnt + Current , 2) = Result1Z#
			
			Clock_Triangles_NORMAL(cnt + Current + 1 , 0) = Result1X#
			Clock_Triangles_NORMAL(cnt + Current + 1 , 1) = Result1Y#
			Clock_Triangles_NORMAL(cnt + Current + 1 , 2) = Result1Z#
			
		Else
		
			Seconds_Hand_NORMALS(cnt , 0) = Result2X#
			Seconds_Hand_NORMALS(cnt , 1) = Result2Y#
			Seconds_Hand_NORMALS(cnt , 2) = Result2Z#
			
			Seconds_Hand_NORMALS(cnt + 1 , 0) = Result2X#
			Seconds_Hand_NORMALS(cnt + 1 , 1) = Result2Y#
			Seconds_Hand_NORMALS(cnt + 1 , 2) = Result2Z#

			Clock_Triangles_NORMAL(cnt + Current , 0) = Result2X#
			Clock_Triangles_NORMAL(cnt + Current , 1) = Result2Y#
			Clock_Triangles_NORMAL(cnt + Current , 2) = Result2Z#
			
			Clock_Triangles_NORMAL(cnt + Current + 1 , 0) = Result2X#
			Clock_Triangles_NORMAL(cnt + Current + 1 , 1) = Result2Y#
			Clock_Triangles_NORMAL(cnt + Current + 1 , 2) = Result2Z#
			
		End If
		
	Next
	
	;CALCULATE NORMAL FOR MINUTES HAND
	
	Current = CLOCK_POINT_AMOUNT * 8 + 158
	
	For cnt = 0 To 13 Step 2
	
		Vector1X = Float Clock_Points(Clock_Triangles(cnt + Current , 0) , 0) - Clock_Points(Clock_Triangles(cnt + Current , 1) , 0); Vector from
		Vector1Y = Float Clock_Points(Clock_Triangles(cnt + Current , 0) , 1) - Clock_Points(Clock_Triangles(cnt + Current , 1) , 1); point 1 to 0
		Vector1Z = Float Clock_Points(Clock_Triangles(cnt + Current , 0) , 2) - Clock_Points(Clock_Triangles(cnt + Current , 1) , 2)
		
		Vector2X = Float Clock_Points(Clock_Triangles(cnt + Current , 1) , 0) - Clock_Points(Clock_Triangles(cnt + Current , 2) , 0); Vector from
		Vector2Y = Float Clock_Points(Clock_Triangles(cnt + Current , 1) , 1) - Clock_Points(Clock_Triangles(cnt + Current , 2) , 1); point 1 to 0
		Vector2Z = Float Clock_Points(Clock_Triangles(cnt + Current , 1) , 2) - Clock_Points(Clock_Triangles(cnt + Current , 2) , 2)

		Mag1# = Sqr#(Vector1X * Vector1X + Vector1Y * Vector1Y + Vector1Z * Vector1Z);Get
		Mag2# = Sqr#(Vector2X * Vector2X + Vector2Y * Vector2Y + Vector2Z * Vector2Z);Magnitutes
		 
		Vector1X = Float Vector1X / Mag1
		Vector1Y = Float Vector1Y / Mag1
		Vector1Z = Float Vector1Z / Mag1
		Vector2X = Float Vector2X / Mag2
		Vector2Y = Float Vector2Y / Mag2
		Vector2Z = Float Vector2Z / Mag2

		Result1X# = Float Vector1Y * Vector2Z - Vector1Z * Vector2Y
		Result1Y# = Float Vector1Z * Vector2X - Vector1X * Vector2Z
		Result1Z# = Float Vector1X * Vector2Y - Vector1Y * Vector2X
		
		Vector1X = Float Clock_Points(Clock_Triangles(cnt + Current + 1 , 0) , 0) - Clock_Points(Clock_Triangles(cnt + Current + 1 , 1) , 0); Vector from
		Vector1Y = Float Clock_Points(Clock_Triangles(cnt + Current + 1 , 0) , 1) - Clock_Points(Clock_Triangles(cnt + Current + 1 , 1) , 1); point 1 to 0
		Vector1Z = Float Clock_Points(Clock_Triangles(cnt + Current + 1 , 0) , 2) - Clock_Points(Clock_Triangles(cnt + Current + 1 , 1) , 2)
		
		Vector2X = Float Clock_Points(Clock_Triangles(cnt + Current + 1 , 1) , 0) - Clock_Points(Clock_Triangles(cnt + Current + 1 , 2) , 0); Vector from
		Vector2Y = Float Clock_Points(Clock_Triangles(cnt + Current + 1 , 1) , 1) - Clock_Points(Clock_Triangles(cnt + Current + 1 , 2) , 1); point 1 to 0
		Vector2Z = Float Clock_Points(Clock_Triangles(cnt + Current + 1 , 1) , 2) - Clock_Points(Clock_Triangles(cnt + Current + 1 , 2) , 2)

		Mag1# = Sqr#(Vector1X * Vector1X + Vector1Y * Vector1Y + Vector1Z * Vector1Z);Get
		Mag2# = Sqr#(Vector2X * Vector2X + Vector2Y * Vector2Y + Vector2Z * Vector2Z);Magnitutes
		 
		Vector1X = Float Vector1X / Mag1
		Vector1Y = Float Vector1Y / Mag1
		Vector1Z = Float Vector1Z / Mag1
		Vector2X = Float Vector2X / Mag2
		Vector2Y = Float Vector2Y / Mag2
		Vector2Z = Float Vector2Z / Mag2

		Result2X# = Float Vector1Y * Vector2Z - Vector1Z * Vector2Y
		Result2Y# = Float Vector1Z * Vector2X - Vector1X * Vector2Z
		Result2Z# = Float Vector1X * Vector2Y - Vector1Y * Vector2X
		
		If Float Abs(Result1X#) + Abs(Result1Y#) + Abs(Result1Z#) > Float Abs(Result2X#) + Abs(Result2Y#) + Abs(Result2Z#)
		
			Minutes_Hand_NORMALS(cnt , 0) = Result1X#
			Minutes_Hand_NORMALS(cnt , 1) = Result1Y#
			Minutes_Hand_NORMALS(cnt , 2) = Result1Z#
			
			Minutes_Hand_NORMALS(cnt + 1 , 0) = Result1X#
			Minutes_Hand_NORMALS(cnt + 1 , 1) = Result1Y#
			Minutes_Hand_NORMALS(cnt + 1 , 2) = Result1Z#

			Clock_Triangles_NORMAL(cnt + Current , 0) = Result1X#
			Clock_Triangles_NORMAL(cnt + Current , 1) = Result1Y#
			Clock_Triangles_NORMAL(cnt + Current , 2) = Result1Z#
			
			Clock_Triangles_NORMAL(cnt + Current + 1 , 0) = Result1X#
			Clock_Triangles_NORMAL(cnt + Current + 1 , 1) = Result1Y#
			Clock_Triangles_NORMAL(cnt + Current + 1 , 2) = Result1Z#

		Else
		
			Minutes_Hand_NORMALS(cnt , 0) = Result2X#
			Minutes_Hand_NORMALS(cnt , 1) = Result2Y#
			Minutes_Hand_NORMALS(cnt , 2) = Result2Z#
			
			Minutes_Hand_NORMALS(cnt + 1 , 0) = Result2X#
			Minutes_Hand_NORMALS(cnt + 1 , 1) = Result2Y#
			Minutes_Hand_NORMALS(cnt + 1 , 2) = Result2Z#

			Clock_Triangles_NORMAL(cnt + Current , 0) = Result2X#
			Clock_Triangles_NORMAL(cnt + Current , 1) = Result2Y#
			Clock_Triangles_NORMAL(cnt + Current , 2) = Result2Z#
			
			Clock_Triangles_NORMAL(cnt + Current + 1 , 0) = Result2X#
			Clock_Triangles_NORMAL(cnt + Current + 1 , 1) = Result2Y#
			Clock_Triangles_NORMAL(cnt + Current + 1 , 2) = Result2Z#

		End If
		
	Next
	
	;CALCULATE NORMAL FOR HOURS HAND
	
	Current = CLOCK_POINT_AMOUNT * 8 + 172
	
	For cnt = 0 To 13 Step 2
	
		Vector1X = Float Clock_Points(Clock_Triangles(cnt + Current , 0) , 0) - Clock_Points(Clock_Triangles(cnt + Current , 1) , 0); Vector from
		Vector1Y = Float Clock_Points(Clock_Triangles(cnt + Current , 0) , 1) - Clock_Points(Clock_Triangles(cnt + Current , 1) , 1); point 1 to 0
		Vector1Z = Float Clock_Points(Clock_Triangles(cnt + Current , 0) , 2) - Clock_Points(Clock_Triangles(cnt + Current , 1) , 2)
		
		Vector2X = Float Clock_Points(Clock_Triangles(cnt + Current , 1) , 0) - Clock_Points(Clock_Triangles(cnt + Current , 2) , 0); Vector from
		Vector2Y = Float Clock_Points(Clock_Triangles(cnt + Current , 1) , 1) - Clock_Points(Clock_Triangles(cnt + Current , 2) , 1); point 1 to 0
		Vector2Z = Float Clock_Points(Clock_Triangles(cnt + Current , 1) , 2) - Clock_Points(Clock_Triangles(cnt + Current , 2) , 2)

		Mag1# = Sqr#(Vector1X * Vector1X + Vector1Y * Vector1Y + Vector1Z * Vector1Z);Get
		Mag2# = Sqr#(Vector2X * Vector2X + Vector2Y * Vector2Y + Vector2Z * Vector2Z);Magnitutes
		 
		Vector1X = Float Vector1X / Mag1
		Vector1Y = Float Vector1Y / Mag1
		Vector1Z = Float Vector1Z / Mag1
		Vector2X = Float Vector2X / Mag2
		Vector2Y = Float Vector2Y / Mag2
		Vector2Z = Float Vector2Z / Mag2

		Result1X# = Float Vector1Y * Vector2Z - Vector1Z * Vector2Y
		Result1Y# = Float Vector1Z * Vector2X - Vector1X * Vector2Z
		Result1Z# = Float Vector1X * Vector2Y - Vector1Y * Vector2X
		
		Vector1X = Float Clock_Points(Clock_Triangles(cnt + Current + 1 , 0) , 0) - Clock_Points(Clock_Triangles(cnt + Current + 1 , 1) , 0); Vector from
		Vector1Y = Float Clock_Points(Clock_Triangles(cnt + Current + 1 , 0) , 1) - Clock_Points(Clock_Triangles(cnt + Current + 1 , 1) , 1); point 1 to 0
		Vector1Z = Float Clock_Points(Clock_Triangles(cnt + Current + 1 , 0) , 2) - Clock_Points(Clock_Triangles(cnt + Current + 1 , 1) , 2)
		
		Vector2X = Float Clock_Points(Clock_Triangles(cnt + Current + 1 , 1) , 0) - Clock_Points(Clock_Triangles(cnt + Current + 1 , 2) , 0); Vector from
		Vector2Y = Float Clock_Points(Clock_Triangles(cnt + Current + 1 , 1) , 1) - Clock_Points(Clock_Triangles(cnt + Current + 1 , 2) , 1); point 1 to 0
		Vector2Z = Float Clock_Points(Clock_Triangles(cnt + 
