; ID: 2415
; Author: Krischan
; Date: 2009-02-21 16:56:13
; Title: Color Gradient
; Description: Creates a color gradient between some colors

; Photoshop Gradient Simulation
; by Krischan webmaster(at)jaas.de
;
; use this to create a color gradient array and use it on a heightmap or whatever
; it calculates the steps between some given colors (like in photoshop)

AppTitle "Photoshop Gradient Simulation"

Graphics 768,512,32,2

Dim GradientR%(0),GradientG%(0),GradientB%(0),Percent#(0),Red%(0),Green%(0),Blue%(0)

; Example: Planet
Restore Planet
CreateGradient(9,255)
For i=0 To 255
	Color GradientR(i),GradientG(i),GradientB(i)
	Line 0,i,384,i
Next

; Example: German National Flag
Restore Germany
CreateGradient(6,255)
For i=0 To 255
	Color GradientR(i),GradientG(i),GradientB(i)
	Line 0,255+i,384,255+i
Next

; Example: simple black to white
Restore Greyscale
CreateGradient(2,255)
For i=0 To 255
	Color GradientR(i),GradientG(i),GradientB(i)
	Line 384,i,768,i
Next

; Example: Full Color Spectrum
Restore Spectrum
CreateGradient(11,255)
For i=0 To 255
	Color GradientR(i),GradientG(i),GradientB(i)
	Line 384,255+i,768,255+i
Next

WaitKey

End

Function CreateGradient(colors%,steps%)
	
	Dim GradientR(steps),GradientG(steps),GradientB(steps),Percent(colors),Red(colors),Green(colors),Blue(colors)
	
	Local i%,pos1%,pos2%,pdiff%
	Local rdiff%,gdiff%,bdiff%
	Local rstep#,gstep#,bstep#
	Local counter%=1
	
    ; read color codes
	For i=1 To colors : Read Percent(i),Red(i),Green(i),Blue(i) : Next
	
    ; calculate gradient
	While counter<colors
		
        ; transform percent value into step position
		pos1%=Percent(counter)*steps/100
		pos2%=Percent(counter+1)*steps/100
		
        ; calculate position difference
		pdiff%=pos2-pos1
		
        ; calculate color difference
		rdiff%=Red(counter)-Red(counter+1)
		gdiff%=Green(counter)-Green(counter+1)
		bdiff%=Blue(counter)-Blue(counter+1)
		
        ; calculate color steps
		rstep#=rdiff*1.0/pdiff
		gstep#=gdiff*1.0/pdiff
		bstep#=bdiff*1.0/pdiff
		
        ; calculate "in-between" color codes
		For i=0 To pdiff
			
			GradientR(pos1+i)=Int(Red(counter)-(rstep*i))
			GradientG(pos1+i)=Int(Green(counter)-(gstep*i))
			GradientB(pos1+i)=Int(Blue(counter)-(bstep*i))
			
		Next
		
        ; increment counter
		counter=counter+1
		
	Wend
	
End Function

.Planet
Data   0.0,255,255,255   ; white: snow
Data   5.0,179,179,179   ; grey: rocks
Data  15.0,153,143, 92   ; brown: tundra
Data  25.0,115,128, 77   ; light green: veld
Data  49.9, 42,102, 41   ; green: grass
Data  50.0, 69,108,118   ; light blue: shore
Data  51.0, 17, 82,112   ; blue: shallow water
Data  75.0,  9, 62, 92   ; dark blue: water
Data 100.0,  2, 43, 68   ; very dark blue: deep water

.Germany
Data   0.0,  0,  0,  0   ; black
Data  33.3,  0,  0,  0   ; black
Data  33.3,255,  0,  0   ; red
Data  66.6,255,  0,  0   ; red
Data  66.6,255,224,  0   ; gold
Data 100.0,255,224,  0   ; gold

.Greyscale
Data   0.0,  0,  0,  0   ; schwarz
Data 100.0,255,255,255   ; weiss

.Spectrum
Data   0.0,255,  0,  0   ; red
Data  10.0,255,128,  0   ; orange
Data  20.0,255,255,  0   ; yellow
Data  30.0,128,255,  0   ; yellow-green
Data  40.0,  0,255,  0   ; green
Data  50.0,  0,255,128   ; green-cyan
Data  60.0,  0,255,255   ; cyan
Data  70.0,  0,128,255   ; light blue
Data  80.0,  0,  0,255   ; blue
Data  90.0,128,  0,255   ; violet blue
Data 100.0,255,  0,255   ; violet
