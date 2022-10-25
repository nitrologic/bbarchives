; ID: 1637
; Author: ANIMAL
; Date: 2006-03-10 14:06:57
; Title: MISSILE COMAND
; Description: TYPE N ARRAYS

; MY FREANDS SAY MY CODE IS SCRUFFY I THINK THERE RIGHT
; BUT IT WORKS AND THATS THE MAIN THING 
; I DIDT GO TO COLLOAGE I LERNT ME SELF 
; THIS IS A SMALL MISSILE COMMAND ROTINE 
; IT HELPS TELL THE DIFRENCE BETWEEN

;								 TYPES And ARRAYS 

; IT ALL SO RESOLVES THE QUESTION WITCH IS FASTER WELL THAY ARE  BOTH FAST
; JUST DEPENDS WHAT U USE THEM FOR TYPE  IS SO MUCH BETTER
; BUT THE  ARRAY U DOWNT HAVE TO FINE WHAT YOUR LOOK CUZE ITS GOT IN INDEX NUMBER IE  A(1) A(2)

; THAY BOTH HAVE VERY COOL TO USE ; A BEGINNER SHOULD LERN THEM BOTH
; BEFOR ATTEMPTING 3D SUPPER COOL GRAPHICS 
; PS I HOPE MY CODE ISNT TO SCRUFFY LOL
; YOURS A.B COX ( AKA ANIMAL ) WELL HERE ANY WAY


	Graphics 800,600,16,1		; SORRY NO GRAPHICS TEST O WELL
	SetBuffer BackBuffer()
	HidePointer    ;    use this  in full screen mode	
	
	Global ant,CITY 
	
	miss=3   ; HOW MANY MISSILES 		
	h=1  ; holds the misile flag you are firing		
	Const LUNCH=50   
	;Const colr=9437584 ; window mode 
	Const colr=9437328  ; fullscreen mode
			
			
	 ; 	A CUSTOM TYPE THE NEW WAY		
	Type missiles
	Field	ox 
	Field	oy
	Field	ny
	Field	nx
	End Type
	
	
	
	;	 AN ARRAY THE OLD WAY 	WHITCH ONE IS FASTER THE OLD OR THE NEW
	Dim x#(200)
	Dim y#(300)
	Dim r(200)
	Dim flag(200)
	Dim v#(200)
	Dim dx(200)
	Dim flag(200)
		
		
	For t=1 To miss
		m.missiles = New missiles
		m\Ox=400
		m\Ox=Rnd(800)
		m\Ny=0
		m\NX=Rnd(800)
	Next
		
		makecircles() ; GO MAKE SOME CIRCLES
	

		
		 ClsColor 400,000,400  ; THE COLOUR OF THE BACK GROUND
		
		
	m.missiles = First missiles			
	Color 255,255,255 
		
	While Not KeyDown(1)
		mx=MouseX()
		my=MouseY()
		Cls
		
		; draw a cross in full screen mode 
		Line MX,MY,MX+6,MY
		Line MX+3,MY-3,MX+3,MY+3	
		
		If MouseDown(1)
		 H=H+1 ; I SED IT WAS A FLAG OF SOME SORT
		
		If H>LUNCH Then H=1
			 If flag(H)=0 And MouseDown (1) 
			
				flag(H)=1
				x#(H)=mx
				y#(H)=my
				v#(h)=-0.5
				
				Line 400,600,mx,my:DX(H)=1
			EndIf
		
		EndIf
	
	
    For T=1 To LUNCH
		
		If flag(T)>0 
			r(T)=r(T)+dx(t)
						
			 DrawImage ant,x#(t),y#(t),r(t)
			
			 x#(T)=x#(T)+V#(T)
			 y#(T)=y#(T)+V#(T)
		
			 If r(T)>50 Then flag(T)=2:DX(T)=-1:v#(t)=1
			
					If r(T)<1 
	 					r(T)=0
						flag(T)=0
						x(T)=0
						y(T)=0
						DX(T)=0
					EndIf
			
		  EndIf 
		
	Next
		
		
		
	For m.missiles = Each missiles
		
		m\ny=m\ny+3
		
u=ReadPixelFast (m\nx,m\ny) And $FFFFFF ; JUST SOMETIMES THIS IS EASYER THAN COLLISION TESTING EM 
		
		If m\ny>600 Or U<>colr
			m\ox=Rnd(800)
			If m\ox<400  Then m\nx=m\ox-(Rnd(50))
			If m\ox>400 Then m\nx=m\ox+(Rnd(50))
			If m\ox=400 Then m\nx=400
				m\oy=0
				m\ny=0
		EndIf
			Line m\ox,m\oy,m\nx,m\ny
	Next		
		
		Text 100,100,U
		DrawImage city,0,500,0  
		
		Delay 10 ; TO SLOW THINGS DOWN FOR ME P4 3.0GIG HP
	Flip ; SOMETHING ABOUT DOUBLE BUFFERING
Wend ;		 END OFF THE LOOP
		
		
		
		Function makecircles()
		; THIS IS THE ONLY FUNCTION IM USING CUZ THE THING WILL RUN TO FAST TRY IT AND SEE
		
ant=CreateImage (100,100,60)
ql=1:xl#=100:yl#=100
For tl=0 To 100 Step 2
ql=ql+1
xl#=xl#+0.1:yl#=yl#+0.1
Color 200+Rnd(40),Rnd(50),0 
Oval xl#,yl#,tl,tl

GrabImage ant,100,100,ql
Cls
;Flip
Next
Cls

Color 255,0,0
CITY=CreateImage (800,100) 
Rect 0,50,800,50,1
Rect 100,30,50,50,1
Rect 110,20,30,20,1
Rect 90,40,70,20,1
GrabImage CITY,0,0
End Function
