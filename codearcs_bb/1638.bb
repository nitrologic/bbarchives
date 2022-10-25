; ID: 1638
; Author: ANIMAL
; Date: 2006-03-10 17:19:28
; Title: MISSLE COMMAND
; Description: PARTICLES

; THIS SHOULD RUN IN VERSIONS OF BLITZ 2D PLUS 3D MAX

; MY FRENDS SAY MY CODE IS SCRUFFY I THINK THERE RIGHT
; BUT IT WORKS AND THATS THE MAIN THING 
; I DIDT GO TO COLLOAGE I LERNT ME SELF 
; THIS IS A SMALL MISSILE COMMAND ROTINE 
; IT HELPS YOU LEARN TYPES THE QUIK WAY

;								 TYPES And NOT ARRAYS 

; 


;						 PARTICLES MAD EASY WITH MISSILE COMMAND 


;	TYPES AND ARRAYS 
; THAY ARE BOTH  VERY COOL TO USE ; A BEGINNER SHOULD LERN THEM BOTH
; BEFOR ATTEMPTING 3D SUPPER COOL GRAPHICS 
; PS I HOPE MY CODE ISNT TO SCRUFFY LOL
; EASY AS  A.B.C  ( AKA ANIMAL ) WELL HERE ANY WAY 



	; THIS IS NOT IN 3D BUT WILL CONVERT VERY EASY

	Graphics 800,600,16,1		; SORRY NO GRAPHICS TEST O WELL FULL SCREEN
	;Graphics 800,600,16,2		; SORRY NO GRAPHICS TEST O WELL WINDOW MODE

	SetBuffer BackBuffer()
	HidePointer    ;    use this  in full screen mode	HIDES THE MOUSE U KNOW
	
	Global ant,GROUND,CITY ;ONLY FREE NEEDED
		
	Const miss=10   ; HOW MANY MISSILES 			
	;Const colr=9437584 ; window mode  COLOUR TEST
	Const colr=9437328  ; fullscreen mode COLOUR TEST
			
			
	 ; 	A CUSTOM TYPE FOR THERE WEPONS		
	Type missiles
	Field	ox 
	Field	oy
	Field	ny
	Field	nx
	End Type
	
	; ANOTHER CUSTOM TYPE FOR YOUR WEPON
	
	
	Type WEP
	Field X#
	Field Y#
	Field R#
	Field V#
	Field DX
	Field flag
	End Type
	

			
		
	For t=1 To miss
		m.missiles = New missiles
		m\Ox=400
		m\Ox=Rnd(800)
		m\Ny=0
		m\NX=Rnd(800)
	Next
		
		makecircles() ; GO MAKE SOME CIRCLES
	

		 ClsColor 400,000,400  ; THE COLOUR OF THE BACKGROUND
		
		
	m.missiles = First missiles	
	W.WEP = First WEP		
	Color 255,170,0 
		
		
		; PLEASE DELETE THE REM STATMENTS TO MAKE PROGRAM EASY TO READ FROM HERE
		
		
		; BUT READ THEM FIRST
		
	While Not KeyDown(1)
		mx=MouseX()
		my=MouseY()
		Cls
		
		; draw a cross 
		Line MX,MY,MX+6,MY
		Line MX+3,MY-3,MX+3,MY+3	
	
	; THIS IS IT A CONSTANT NEW WEPONS ROTINE 
	; ALLSO USED FOR PATICAL EXPLOSIONS IN 3D MODE WITH A LITTLE WORK
	; AND 3D WEPONDS ROKETS AND MACHINE GUNS QUICK AND SIMPLE 
	; UNLIKE THE OTHERS IV DOWNLOLDED ALL VERY LONG WINDED  
	; BUT I MUST SAY A LOT MORE ADVANCED THAN MINE 
	;											     ILL HAVE TO LEARN A ?
	; DO  REMEBER TO DELETE THE TYPE VAR= AFTER U HAVE FINSHED WITH IT ( SAVE YOUR MEMORY )
	; PS  GOT LOTS OF MEMORY LOL  SO I DIDENT BOTHER
		
		If MouseDown (1)  ; NEW EXPLOTION ON MOUSE CLICK TADA ( OR ENTITY )
							;       IF U WISH TO USE AN ENTITY USE THE Z VECTOR 
							;		 LEAVE THE FLASHY STUFF TO THE EXSPERTS
							;
							;		IE ( MOVEENTITY BLA,0,0,W\Z ) IN BLTZ3D AND MAX
							
							;			           ( DONT FORGET IN MAX TO INT ALL VARS FIRST)
							
 
				w.WEP = New WEP
				w\X#=0
				w\Y#=0
				w\R#=0
				w\V#=0
				w\DX=0
				w\flag=0
				
				W\flag=1
				W\x#=mx
				W\y#=my
				
				
				
				W\v#=-0.5 
				; W\V#=-0.5  IS AN OFFSET BECUSE OVAL DOSENT DRAW CIRCLES FROM THE MIDDLE EM
				;
				;  DRAW A REAL CIRCLE FROM THE MIDDLE And ANY X,Y Origin U CHOSE
				;
				; X=300:Y=300:R=50	; R BEING THE RADIUS X AND Y POINT OF ORIGIN
				; 
				; For ANGLE=0 To 359 
				; 	
				;	NX=X+R*Sin(ANGLE):NY=Y+R*Cos(ANGLE)
				; 	
				;		Line X,Y,NX,NY
				; 			X=NX:Y=NY
				; NEXT
				;
				; FROM THE MIDDLE
				; ALSO GOOD FOR SENDING 3D ENTITYS AROUND IN CIRCLES LWITH POSITION ENTITY
				
 				Line 400,600,mx,my ; DRAW A LINE TO YOUR EXPLOSION
				w\DX=1 ; W\DX IS THE AMOUST IM MOVEING MY OFFSET BY 1 
		EndIf
		
		
		; HERE IT IS WEPONS GO BANG 
		
    For W.WEP = Each WEP
		
		If W\flag>0 
			W\r=W\r+W\dx ;THIS IS WHERE U MOVE GO BANG OR LUNCH MISSILE BULLET BOME SPACEINVADER
			
			; ANT IS THE NAME OF MY  CIRCLES IMAGE	
			 DrawImage ant,W\x#,W\y#,W\r ; WEPON BANG MISILE BOME EXPLODE WHAT EVERR U LIKE 
										; W\R IS USED FOR MY IMAGE FRAME  AT THE MOMENT
										; BUT YOU CAN USE IT FOR SAY AN ENTITY >??????		
			 W\x#=W\x#+W\V# ; THIS BIT PUTS ME CIRCLES CENTRE
			 W\y#=W\y#+W\V# ; THIS BIT PUTS ME CIRCLES CENTRE
		
			 If W\r>50  ; THIS BIT SWITCS MODE FROM OUT TO IN
			W\flag=2
			W\DX=-1 ; NOW DX SETS -1 TO GOT BACK TO ORIGINAL POS 
			W\v#=1
			EndIf
			
					If W\r<1 ; THIS BIT KILLS THE EXPLOTION
	 					W\r=0      ; R IS MY LIFE TIMMER R=0 END OF EXPLOSION OR DISTANCE TRAVALED
						W\flag=0   ; SET FLAG TO 0 SO IT DOSENT APEAR AGAIN
						
						W\x#=0		; QUICK CLEANUP
						W\y#=0		;
						W\DX=0		; CLEAN UP END
					EndIf
			
		  EndIf 
		
	Next
		
; AND THATS THAT CONSTANT WEPONS THE EASY WAY IN 32 LINES OF CODE ( IF U DOUBLE THEM UP LESS )


		
		
	For m.missiles = Each missiles
		
		m\ny=m\ny+3 ; MOVE THE MISSILE
		
; GET THE COLOUR OFF THE SCREEN IN FROUNT OF YOU MISSILE		
u=ReadPixelFast (m\nx,m\ny) And $FFFFFF ; JUST SOMETIMES THIS IS EASYER THAN COLLISION TESTING EM 
		
		If m\ny>600 Or U<>colr ; DO SOME TESTS 
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
		DrawImage CITY,100,500,0
		DrawImage CITY,350,500,0
		DrawImage CITY,650,500,0
		DrawImage GROUND,0,500,0  
		
		
		
		Delay 10 ; TO SLOW THINGS DOWN 
	
	
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

Color 255,0,0 ; RED
GROUND=CreateImage (800,100) 
Rect 0,50,800,50,1
GrabImage GROUND,0,0



Cls

CITY=CreateImage (100,50)
Color 255,255,0 ; YELLOW

Rect 10,30,50,50,1

Rect 20,20,30,20,1
Color 0,255,0 ; GREEN
Rect 0,40,70,20,1
GrabImage CITY,0,0


End Function
