; ID: 3152
; Author: Pakz
; Date: 2014-10-19 13:31:36
; Title: Topdown Homing Missiles Example
; Description: Topdown 2d Homing Missiles SourceCode.

; (fixed) Homing Missile Example by Rudy van Etten (pakz)
Graphics 640,480,32,2
SetBuffer BackBuffer()
SeedRnd MilliSecs()

Global maxnums = 10
Global nums = maxnums
Dim sx#(nums) ; missile x coord
Dim sy#(nums) ; missile y coord
Dim sa(nums) ; missile angle

; give the missiles a random start location
For i=0 To nums
	sx(i) = Rand(GraphicsWidth())
	sy(i) = Rand(GraphicsHeight())
Next
timer = CreateTimer(60)

While KeyDown(1) = False
	WaitTimer timer
	Cls
	; if mouse right then remove one missile
	If MouseHit(2) And nums>0 Then nums=nums-1
	; if mouse left then add one missile
	If MouseHit(1) And nums<maxnums Then nums=nums+1 : sx(nums) = Rand(GraphicsWidth()) : sy(nums) = Rand(GraphicsHeight())
	; loop through all active missiles
	For i=0 To nums	
		; get the angle of the missile versus the mouse
		a = getangle(MouseX(),MouseY(),sx(i),sy(i))
		; a1 wil count down until it reaches the target angle.
		; it starts at the current missile angle
		a1 = sa(i)
		; v1 adds up 1 every step. it is used to see if left v1 or right v2 is bigger
		v1 = 0
		exitloop = False
		While exitloop = False
			a1 = a1 - 1
			v1 = v1 + 1
			; if near target angle
			If RectsOverlap(a1,a1,4,4,a,a,4,4) Then exitloop = True
			; boundries
			If a1 =< -180 Then a1 = 181	
		
		Wend
		exitloop = False
		a1 = sa(i)
		v2 = 0
		While exitloop = False
			a1 = a1 + 1
			v2 = v2 + 1
			If KeyDown(57) Then DebugLog a1
			If RectsOverlap(a1,a1,4,4,a,a,4,4) Then exitloop = True
			If a1 >= 180 Then a1 = -181
		Wend
		; If go left is shorter turn then decrease angle by value else increase
		If v1 > v2 Then sa(i) = sa(i) - 3 Else sa(i) = sa(i) + 3
		; bounds
		If sa(i) > 180 Then sa(i) = -180
		If sa(i) < -180 Then sa(i) = 180
		; Move the missile on the screen
		sx(i) = sx(i) + Cos(sa(i))*1
		sy(i) = sy(i) + Sin(sa(i))*1
		; Draw the missile
		Color 255,255,0
		Oval sx(i)-3,sy(i)-3,6,6,True	
	Next
	Color 255,255,255
	Text 0,GraphicsHeight()-16,"Left mouse button to add (<max) missile, Right mouse button to remove missile."
	Flip
Wend
End

Function getangle(x1,y1,x2,y2)
         Local dx = x2 - x1
         Local dy = y2 - y1
         Return ATan2(dy,dx)+360 Mod 360
End Function
