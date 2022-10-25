; ID: 427
; Author: MuffinRemnant
; Date: 2002-09-15 10:44:39
; Title: LSystem Fractals
; Description: Simple implementation of LSystem fractals

;------------------------------------------------------
;
; Program:
;
; LSystem3 v1.1
;
; 
; Description:
;
; Simple Lindermayer (LSystem) Fractal Generator
; (Blitz Basic 2D)
;
;
; Author:
;
; Paul Robinson (MuffinRemnant)
; email paulrobinson123@aol.com
;
;
; Changes in this version:
;
; - removed recursive function to increase speed (!)
; - added mouse controls for zoom and move
;
; Date:
;
; 13/09/02
; 
;
;------------------------------------------------------
;
; Notes:
;
; What does this program do? It generates simple LSystem 
; fractals. Essentially this type of fractal consists of
; an 'axiom' (the starting information) and a 'rule' or
; re-write string, which together can generate complex
; and sometimes interesting patterns.
;
; It works like this.....
; Take the axiom string (say "F+F+F") and for each occurence
; of F replace it with the rule string (say "F-FF+F).
;
; In this example the new axiom string would become...
;
; (F-FF+F) + (F-FF+F) + (F-FF+F)
;
; We can apply the rule to the new axiom string again to get
; another (longer) axiom and so on.
;
; When we've done this enough times we can render the resulting
; string interpreting each character as follows...
;
; 
;		F = Move forward and draw line
;		+ = Turn by turning_angle
;		- = Turn by -turning_angle
;		G = Go forward do not draw line
;		R = Reverse do not draw line
;
; This program operates on a limited 'command set' compared to
; many LSystem programs - some have the facility to change drawing
; colours, have incremental angle changes, render polygons etc etc
;
; These are all very straightforward to implement but they can reduce
; the speed of the program considerably - as will long axiom/rule strings
; (or running with Debug enabled!).
; 
;
;
;
; I've rewritten this program using arrays,banks and strings
; and found this version to be the best. If you can think of a quicker
; way to accomplish the same result - LET ME KNOW!
;
;
;
;
;
; A few interesting combinations:
;
; 'Creature'
; Axiom			F+F+F+F
; Rule			F-GF-F-
; Iterations 	6
; Turning angle 89
;
;
; 'Starfish Spawn'
; Axiom			F+FF-F
; Rule			F-F-FF-F
; Iterations	5
; Turning angle 99
;
;
; 'Tri-lobe'
; Axiom			F+F+F
; Rule			F-FF
; Iterations	8
; Turning angle	77
;
;
; 'Spirograph'
; Axiom			F+F-F
; Rule			F+F+F
; Iterations	5
; Turning angle 84
;
;
; 'FIL Soup'
; Axiom			F+FF+F
; Rule			F+FGF	
; Iterations	5
; Turning angle 96
;
;
; 'Sun Crescent'
; Axiom			F-F-F-F
; Rule			FGF+G	
; Iterations	8
; Turning angle 99
;
;
; 'Octospiral'
; Axiom			F+F++F
; Rule			F+F-G	
; Iterations	9
; Turning angle 90
;
;
; 'Muscle man'
; Axiom			F
; Rule			FR+F-GG
; Iterations	12
; Turning angle 94
;
;
;	
;------------------------------------------------------
;
;
; Controls (such as they are)...
;
; Left mouse button - zoom in
; Right mouse button - zoom out
; Both mouse buttons - drag fractal
;
; + and - keys for next and previous iteration
;
; esc - exit
;
;
;
; For most axiom/rule sets going above iteration 10 will
; be very slow!
;
;
;
; You need to change the axiom/rule by modifying the
; globally declared string before running the program
;
;------------------------------------------------------






Graphics 800,600,16,1
SetBuffer BackBuffer()



; Globals
;
Global axiom$="F+F+F+F"
Global rule$="FF-FGF"
Global temp$
Global t2$
Global turning_angle=89
Global zoom#=6

; locals
Local startx#=400, starty#=300
Local iteration=3

Local time1,time2			; for timing
Local time3,time4

Local mmx, mmy, xs, ys		; for mouse



; trig look ups
Dim sinlut#(360)
Dim coslut#(360)

; init trig tables
recalc_lut()






; create initial view
Cls
; expand the original axiom by i iterations
temp$=axiom$
time1=MilliSecs()
expand(iteration)
time2=MilliSecs()
Flip
	






;main loop
While Not KeyDown(1)
	
	
	Cls
	
	
	; display fractal and information
	time3=MilliSecs()
	render(startx, starty)
	time4=MilliSecs()
	
	Color 255,255,255
	Text 0, 0, "String size " + Len(temp$) + " bytes"
	Text 0, 16, "Iterations " + iteration
	Text 0, 32, "Zoom " + Int(zoom) + "X"
	Text 0, 64, "Expansion time " + (time2-time1) + " milliseconds"
	Text 0, 80, "Render time " + (time4-time3) + " milliseconds"
	
	; great mouse cursor!
	Rect MouseX(), MouseY(), 4, 4, 1
	
	Color 0,255,0
	
	
	Flip
	
	
	
		
	button$="none"
	If MouseDown(1) Then button$="left"
	If MouseDown(2) Then button$="right"
	If MouseDown(1) And MouseDown(2) Then button$="both"
	
	
	; zoom in
	If button$="left" Then
	
		zoom = zoom + 0.2
		recalc_lut()
	
	EndIf
	
	; zoom out
	If button$="right" Then
	
		If zoom > 1.2 Then
			zoom = zoom - 0.2
			recalc_lut()
		EndIf
		
	EndIf	
	
	; click and drag
	If button$="both" 
	
		xs = MouseXSpeed()
		ys = MouseYSpeed()
		
		startx=startx+xs
		starty=starty+ys
		
	EndIf
	
	mmx=MouseXSpeed()
	mmy=MouseYSpeed()
	
	
	; iteration up/down keys...
	;
	;
	
	If KeyDown(13) Then ; "="
		iteration=iteration+1
		temp$=axiom$
		time1=MilliSecs()
		expand(iteration)
		time2=MilliSecs()		
		While KeyDown(13)
			FlushKeys()
		Wend	
	EndIf
	
	If KeyDown(12) Then ; "-"
		If iteration > 1 Then iteration=iteration-1
		temp$=axiom$
		time1=MilliSecs()
		expand(iteration)
		time2=MilliSecs()		
		While KeyDown(12)
			FlushKeys()
		Wend	
	EndIf


Wend
End







; recalculate the trig look up tables
; including current zoom factor
Function recalc_lut()


	For loop=0 To 359
	
		sinlut(loop)=Sin(loop) * zoom
		coslut(loop)=Cos(loop) * zoom
		
	Next



End Function












Function expand(n)


	Repeat
	

		
		;replace each occurence of F with rewrite rule
		t2$ = ""
		lng = Len(temp$) + 1
		p=1
		
		Repeat
			
			c$ = Mid$(temp$, p, 1)
			If c$ = "F" Then
		
				t2$=t2$ + rule$
				
				Else
				
				t2$=t2$ + c$
			
			EndIf
			
			p=p+1
			
		Until p=lng
		
		temp$=t2$
		
			

		n=n-1

	Until n=0
	

End Function




; render the 'command sequence' in temp$
Function render(x#, y#)

	Local loop=1, l=Len(temp$), angle=0, ox#, oy#, cv#, sv#, col=128
	
	Color 0,col,0
	
	Repeat



		
		c$=Mid$(temp$, loop, 1)
		
		Select c$
		
			Case "F"

				ox=x
				oy=y			
				x=x+cv
				y=y+sv
				
				Line ox, oy, x, y
				;Rect x,y,4,4,0
				
			Case "G"
				
				ox=x
				oy=y			
				x=x+cv
				y=y+sv
							
				
				
			Case "R"
				
				ox=x
				oy=y			
				x=x-cv
				y=y-sv
											
			
			Case "C"
				
				col=col+1
				Color col,0,0
				
				
			Case "+"
			
				angle=angle+turning_angle
				
				If angle > 359 Then angle = angle - 360

				cv=coslut(angle)
				sv=sinlut(angle)
				
				
			Case "-"
			
				angle=angle-turning_angle
				
				If angle < 0 Then angle = angle + 360

				cv=coslut(angle)
				sv=sinlut(angle)

		End Select
		
		loop=loop+1
	
	Until loop > l



End Function
