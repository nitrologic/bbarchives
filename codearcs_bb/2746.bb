; ID: 2746
; Author: Krischan
; Date: 2010-07-31 05:09:57
; Title: Realistic Model of the Solar System
; Description: Realistic 2D-Model of the Solar System considering the N-body problem

; Realistic 2D-Model of the solar system 1.0
; considering the N-body problem
; with real object data from 01/01/2000

AppTitle "Realistic 2D-Model of the solar system 1.0"

; parameters
scrX	= 800		; screen width
scrY	= 600		; screen height
scrD	= 32		; color depth
scrT	= 2			; windows / fullscreen

pn		= 10		; number of planets
ps		= 3			; size of planets
zoom	= 200		; zoom factor (1=far, 250=near, 200=inner system, 5=complete)
tracers	= 1			; planet tracers on/off
sunmove = 1			; correct sun movement on/off
sunmass#= 1.0		; sun mass (1=standard)
TIM#	= 0			; reset time factor
TIMINT#	= 0.5		; increase x sun day per cycle, higher values means precision loss!
					; ex. >10 mercury gets lost

; gauss gravitional constant
K1# = .01720209895
K2# = K1# * K1#

; fields
Dim X#(pn), Y#(pn), Z#(pn), VX#(pn), VY#(pn), VZ#(pn), MASS#(pn), ro(pn), gr(pn), bl(pn)
Dim name$(pn), oldx#(pn), oldy#(pn), DEG#(pn), MU#(pn), SEMI#(pn), ECCEN#(pn)

; read planet data
Dim pla$(pn,11)
Restore planetdata
For p=0 To pn-1
	For q=0 To 11
		Read pla$(p,q)
		name$(p)=pla$(p,0)							; Name
		X#(p)=pla$(p,1)								; X-Position
		Y#(p)=pla$(p,2)								; Y-Position
		Z#(p)=pla$(p,3)								; Z-Position
		VX#(p)=pla$(p,4)							; Speed X
		VY#(p)=pla$(p,5)							; Speed Y
		VZ#(p)=pla$(p,6)							; Speed Z
		MASS#(p)=Float(pla$(p,7))*10^Int(pla$(p,8))	; Mass
		ro(p)=pla$(p,9)								; Red
		gr(p)=pla$(p,10)							; Green
		bl(p)=pla$(p,11)							; Blue
	Next
Next

; Object Data from 01/01/2000
.planetdata
;    Name              POS X         POS Y         POS Z             Vx             Vy             Vz   Mass*10^X      R     G     B
Data "Sonne"   ,   0.0000000 ,   0.0000000 ,   0.0000000 ,  0.000000000 ,  0.000000000 ,  0.000000000 , 1.991 , 30 , 255 , 255 ,   0
Data "Merkur"  ,  -0.1778023 ,  -0.3863251 ,  -0.1879025 ,  0.020335410 , -0.007559570 , -0.006147710 , 3.191 , 23 , 255 ,  64 ,   0
Data "Venus"   ,   0.1787301 ,  -0.6390267 ,  -0.2987722 ,  0.019469170 ,  0.004915870 ,  0.000978980 , 4.886 , 24 , 255 , 192 , 128
Data "Erde"    ,  -0.3305873 ,   0.8497269 ,   0.3684325 , -0.016483420 , -0.005365460 , -0.002326460 , 5.979 , 24 ,   0 ,   0 , 255
Data "Mars"    ,  -1.5848092 ,  -0.3648638 ,  -0.1244522 ,  0.003821510 , -0.011241840 , -0.005259630 , 6.418 , 23 , 255 ,   0 ,   0
Data "Jupiter" ,   4.1801700 ,  -2.5386080 ,  -1.1900210 ,  0.004106423 ,  0.006125327 ,  0.002525539 , 1.901 , 27 , 128 , 255 ,   0
Data "Saturn"  ,  -4.6197080 ,  -8.2374610 ,  -3.2033610 ,  0.004647751 , -0.002328957 , -0.001161564 , 5.684 , 26 , 255 , 255 , 128
Data "Uranus"  ,  -3.7245900 , -17.1975200 ,  -7.4791700 ,  0.003833665 , -0.000845721 , -0.000424809 , 8.682 , 25 ,   0 , 255 , 255
Data "Neptun"  ,   1.9138100 , -27.9215500 , -11.4762000 ,  0.003118271 ,  0.000233303 ,  0.000017967 , 1.027 , 26 ,   0 , 128 , 255
Data "Pluto"   , -23.2285900 , -18.5272000 ,   1.2167500 ,  0.002066577 , -0.002488884 , -0.001397200 , 1.080 , 24 , 128 , 128 , 128

; Normalize Mass to sun
For I = 1 To pn-1
	MASS#(I) = MASS#(I) / MASS(0)
Next
MASS#(0) = sunmass#

; create reduced mass in gauss units
For I = 1 To pn-1
	MU#(I) = K2# * (1 + MASS#(I))
Next

; init screen
Graphics scrX,scrY,scrD,scrT
SetBuffer BackBuffer()

; main loop
While Not KeyHit(1)
	
	; increase time by factor
	TIM# = TIM# + TIMINT#
	
	; draw tracers
	For i=0 To pn-1
		If tracers Then Color 32,32,32 Else Color 0,0,0
		Oval(scrX/2+oldx#(i),scrY/2+oldy#(i),ps,ps,1)
	Next
	
	; calculations and output
	Gosub NewV
	Gosub NewP
	
	Rect 0,0,150,42,1
	Color 255,255,255
	
	Text 0, 0,"Days:  "+Int(TIM#)
	Text 0,10,"Years: "+TIM#/365.25
	Text 0,20,"Time:  "+TIMINT#
	Text 0,30,"Zoom:  "+zoom
	
	; Keyboard shortcuts
	; ------------------
	; Arrows up/down		Zoom in/out
	; Arrows left/right		speed increase/decrease
	; Space					Tracers
	; F1					inner system
	; F2					complete system
	; F3					double sun mass
	; F4					half sun mass
	; F5					earth = 0.2 sun masses :-)
	
	kp=0
	If KeyDown(200) Then zoom=zoom+1:If zoom>250 Then zoom=250
	If KeyDown(208) Then zoom=zoom-1:If zoom<1 Then zoom=1
	If KeyDown(203) Then TIMINT#=TIMINT#-.5:If TIMINT#<.5 Then TIMINT#=.5
	If KeyDown(205) Then TIMINT#=TIMINT#+.5:If TIMINT#>25 Then TIMINT#=25
	If KeyHit(57) Then tracers=1-tracers:Cls
	If KeyDown(59) Then zoom=200:Cls
	If KeyDown(60) Then zoom=5:Cls
	If KeyDown(61) Then MASS#(0)=2
	If KeyDown(62) Then MASS#(0)=0.5
	If KeyDown(63) Then MASS#(3)=.2
	
	Flip 1
	
Wend

End


;  calc new speed
.NewV
For I = 0 To pn-1
	
	AX# = 0
	AY# = 0
	AZ# = 0
	
	For J = 0 To pn-1
		If (J = I) Then Goto skip
		XJI# = X#(J) - X#(I)
		YJI# = Y#(J) - Y#(I)
		ZJI# = Z#(J) - Z#(I)
		R# = Sqr(ZJI# * ZJI# + YJI# * YJI# + XJI# * XJI#)
		R3# = R# * R# * R#
		COEFF# = K2# * MASS#(J) / R3#
		AX# = COEFF# * XJI# + AX#
		AY# = COEFF# * YJI# + AY#
		AZ# = COEFF# * ZJI# + AZ#
		.skip
	Next
	
	VX#(I) = VX#(I) + AX# * TIMINT#
	VY#(I) = VY#(I) + AY# * TIMINT#
	VZ#(I) = VZ#(I) + AZ# * TIMINT#
	
Next

Return

; calc new positions and draw planets
.NewP
For i=0 To pn-1
	
	; calc new position
	X#(i) = X(i) + VX#(i) * TIMINT#
	Y#(i) = Y(i) + VY#(i) * TIMINT#
	Z#(i) = Z(i) + VZ#(i) * TIMINT#
	
	; include zoom factor
	sx#=X#(i)*zoom
	sy#=Y#(i)*zoom
	
	; correct sun movement
	If i=0 And sunmove=1 Then
		sunx#=sx#
		suny#=sy#
	EndIf
	
	; new position with zoom and sun movement
	sx#=sx#-sunx#
	sy#=sy#-suny#
	
	; save old position for tracers
	oldx#(i)=sx#
	oldy#(i)=sy#
	
	; draw planets
	Color ro(i),gr(i),bl(i)
	Oval(scrX/2+sx#,scrY/2+sy#,ps,ps,1)
Next

Return
