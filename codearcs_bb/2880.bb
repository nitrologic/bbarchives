; ID: 2880
; Author: Charrua
; Date: 2011-08-17 06:37:02
; Title: Mouse Gestures Recogn
; Description: Use of the LevenshteinDistance algorithm for mouse gestures recogn

; --------------------------------------------------------------------------
;
; Mouse Gestures Recogn
; Autor: Juan Ignacio Odriozola
;
; Based on: http://www.bytearray.org/?p=91
; 
; The basic idea is: 
;	if MouseMoving Identify 8 Mouse Directions (like West, NorthWest, North And so on) And record a sequence of Directions
;	if MouseStoppedMovement compare the sequence of movements recorded with a List of Sequences
;	
;	to compare strings is used the LevenshteinDistance, the selected pattern will be the one that has the minimum distance.
;
;	The Gestures are defined in the file "Gestos.txt"
;	Each line started with a SPACE or a Semicolon (" ",";") will be skiped
; 	each line with a gesture definition will have 2 fields comma separeted like: A,71
;	This indicates that the Gesture to construct the letter "A" (first field) is comprised by 2 mouse movements: 7 and then 1
;	the mouse orientations are as follows:
;
;											   5 	6	 7
;											    \   |   /
;											4 <-- Start -->	0
;												/   |   \
;											   3	2	1
;
;	for example East is 0, West is 4 and so on
;
;	At first the code loads the "Gestos.txt" and generates an image based on the traces wich is Drawn as a helpper
;	The first trace is green colored, all traces are lines ended with a dot
;
;	there are some commented lines in the "Gestures.txt" with optional patterns for some Gestures
;
; 	for example the "8" could be: 4321234567654	(yes all that) but with some imagination the following is ok "8" : 3136
;	like the 7 segment displays, it all depends how much simplification we are pretending
;
;	I'm focused on few movements because the final version is for a kid with "so much motor problems" 
;	(don´t know the exact expression in english)
;
;---------------------------------------------------------------------------




Graphics 800,600, 0, 2

HidePointer()


Local InputString$
Local AcumTraces$
Local LastGesture$
Local MouseDirection, LikeThis$

Local State=2		;valor aceptado
Local Moving=-1
Local Transition=-1
Local Count=0
Local LastMove=-1
Const TransitionThreshold = 1, StopThreshold = 15

Type tGestures
	Field Gesture$
	Field Pattern$
End Type

Type tHistory
	Field Gesture$	;"A"
	Field Find$		;"710"
	Field Pattern$	;"71"
	Field Dist
End Type

LoadGestures()

Local Imagen = GenerarImagen()

Local h.tHistory
Local i, c

While Not(KeyHit(1))

	Cls
	
	Text 10, 10, "Accum Mouse Traces   : "+AcumTraces
	Text 10, 30, "Last Validated Trace : "+LastGesture
	Text 10, 50, "Gesture Associated   : "+LikeThis
	Text 10, 90, "Input Text           : "+InputString
	
	i=30
	c=1
	
	Color 0,255,0
	Text 430,10,"Pattern"
	Text 560,10,"Traces"
	Text 670,10,"Dif"
	Text 700,10,"Gesture"
	Color 255,255,255
	
	For h=Each tHistory
		
		Text 430,i,h\Pattern
		Text 560,i,h\Find
		Text 670,i,h\Dist
		Text 700,i,h\Gesture
		
		i=i+20
		c=c+1
		
		If c=14 Then
			Delete First tHistory
			Exit
		End If
	Next
	
	DrawImage Imagen,0,GraphicsHeight()-ImageHeight(Imagen)
	
	MouseDirection = EstimoOrientacion()
	
	Select State
			
		Case 1
			If MouseDirection=Transition Then
				Count=Count+1
				If MouseDirection=-1 Then
					If Count >= StopThreshold Then
						State = 2
						Moving = MouseDirection
					End If
				Else 
					If Count >= TransitionThreshold Then
						State = 2
						Moving = MouseDirection
					End If
				End If
			Else
				If MouseDirection=Moving Then
					State=2
				Else
					Transition=MouseDirection
					Count=0
				End If
			End If
			
		Case 2
			If MouseDirection<>Moving Then
				State = 1
				Count = 0
				Transition=MouseDirection
			End If	
			
	End Select
	
	If Moving<>LastMove Then
		
		If LastMove=-1 Then
			AcumTraces=""
		End If
		
		LastMove = Moving	;recuerdo mi ultima tendencia
		
		If Moving <> - 1 Then
			AcumTraces = AcumTraces + Str(Moving)	;si me muevo anoto secuencia
		End If
		
	End If
	
	If Moving=-1 Then
		If AcumTraces<>"" Then
			LikeThis = BuscarParecidos(AcumTraces)
			If LikeThis<>"" Then
				Select LikeThis
					Case "SPACE"
						InputString=InputString+" "
					Case "DOT"
						InputString=InputString+"."
					Case "BACKSPACE"
						If InputString<>"" Then InputString = Left(InputString,Len(InputString)-1)
					Default
						InputString=InputString+LikeThis
				End Select
				LastGesture=AcumTraces
			End If
			AcumTraces = ""
		End If
	End If
	
	If KeyHit(57) Then
		InputString=""
		Delete Each tHistory
	End If
	
	MoveMouse(400,300)
	Delay 20

Wend

End 

Function EstimoOrientacion()	;Estimate mouse movement
	
	Local x#, y#, r, Prueba#, Alfa#
	Local strOut$
	
	x = MouseXSpeed()
	y = MouseYSpeed()
	
	Alfa# = ATan2(y,x)
	If Alfa < 0 Then Alfa = 360+Alfa
	
	If x=0 And y=0 Then
		r=-1
	Else
		r=0
		Prueba#=22.5
		If Alfa>22.5 And Alfa<337.5 Then
			
			While Alfa>Prueba#
				r = r + 1
				Prueba# = Prueba#+45
			Wend
		End If
	End If
	
	Return r
	
End Function

Dim Ld(1,1)

Function Min#(a#, b#)

	If a<b Then
		Return a
	Else
		Return b
	End If
	
End Function

Function Max#(a#, b#)
	
	If a>b Then
		Return a
	Else
		Return b
	End If
	
End Function

Function AddGesture.tGestures(Gesture$, Pattern$)

	Local g.tGestures=New tGestures
	g\Pattern = Pattern
	g\Gesture = Gesture
	Return g
	
End Function


Function LevenshteinDistance(String1$, String2$)
	
	;calculates the minimun amount of basic operations required to transmorm String1 into String2
	;3 basic operations are considered: Character Delet, Insert, Substitution
	
	Local Len1=Len(String1)
	Local Len2=Len(String2)
	Dim Ld(Len1, Len2)

	Local i, j, Cost, s1$, s2$

	If Len1 = 0 Then Return Len2
	If Len2 = 0 Then Return Len1
	
	For i=0 To Len1
		Ld(i,0)=i
	Next
	For j=0 To Len2
		Ld(0,j)=j
	Next

	For i=1 To Len1
	
		For j=1 To Len2
		
			s1$ = Mid(String1,i,1)
			s2$ = Mid(String2,j,1)
			If s1 = s2 Then
				Cost=0
			Else
				Cost=1
			End If
			
			Ld(i,j) = Min( Min( Ld(i-1,j)+1 , Ld(i,j-1)+1 ), Ld(i-1,j-1)+Cost)	;deletion, insertion, substitution
			
		Next
	Next
	
	Return Ld(Len1, Len2)

End Function

Function BuscarParecidos$(Buscar$)	;search match

	Local Gesto.tGestures
	Local MinDistance=1000
	Local g$="", d, Pattern$
	Local h.tHistory
	
	;calculo la distancia de la cadena a buscar contra los gestos almacenados
	;me quedo con la distancia menor. Luego evaluo que la distancia sea menor que el 30%
	;respecto al largo de la cadena.
	
	For Gesto = Each tGestures
		d = LevenshteinDistance(Buscar, Gesto\Pattern)
		If d<MinDistance Then
			MinDistance = d
			Pattern = Gesto\Pattern
			g = Gesto\Gesture
		End If
	Next
	
	If Float(MinDistance/Float(Len(Buscar))) < 0.3 Then
		h=New tHistory
		h\Gesture = g
		h\Find = Buscar
		h\Pattern = Pattern
		h\Dist = MinDistance
		Return g
	Else
		Return ""
	End If
	
End Function

Function LoadGestures()
	
	Local g.tGestures
	Local Entrada = ReadFile("Gestos.txt")
	Local StrEntrada$,Pos
	
	While Not(Eof(Entrada))
		StrEntrada = ReadLine(Entrada)
		If Left(StrEntrada,1)<>";" And Left(StrEntrada,1)<>" " Then
			Pos = Instr(StrEntrada,",")
			g=New tGestures
			g\Gesture = Mid(StrEntrada,1,Pos-1)
			g\Pattern = Right( StrEntrada,Len(StrEntrada)-Pos)
		End If
	Wend
	
End Function

Function GenerarImagen(Create=True)
	
	Local g.tGestures
	Local i,j, txt$
	Local Ancho=60, Alto=100
	Local x, y, x1, y1, xDir, yDir, xDirOld, yDirOld
	Local idx, Secuencia$
	
	Local Imagen = CreateImage(800,Alto*3)
	
	
	If Create=False Then
		Return LoadImage("Patrones.bmp")
	End If
	
	SetBuffer ImageBuffer(Imagen)
	ClsColor 0,0,0
	Cls
	Color 255,255,255
	
	Local xMin, yMin, xMax, yMax
	Local AnchoTrazado, AltoTrazado
	Local Paso
	
	For g = Each tGestures
		
		Color 255,255,255
		Rect i*Ancho,j*Alto,Ancho,Alto,False
		
		Select g\Gesture
			Case "SPACE"
				txt = "SPC"
			Case "DOT"
				txt = "DOT"
			Case "BACKSPACE"
				txt = "<--"
			Default
				txt = g\Gesture
		End Select
		
		Color 255,255,255
		Text i*Ancho+5,j*Alto+5,txt
		
		Secuencia = g\Pattern
		
	;primero calculo x,y minimas y maximas, para saber:
	;a cuanto ajustar el paso (largo de cada linea)
	;donde poner x,y iniciales para que el trazado quede centrado
		
		x=0
		y=0
		
		xMax=-1000
		xMin=+1000
		yMax=-1000
		yMin=+1000
		
		xMax=0
		xMin=0
		yMax=0
		yMin=0
		
		For idx=1 To Len(Secuencia)
			
			Select Mid(Secuencia,idx,1)
					
				Case "0"
					xDir=1
					yDir=0
					
				Case "1"
					xDir=1
					yDir=1
					
				Case "2"
					xDir=0
					yDir=1
					
				Case "3"	
					xDir=-1
					yDir=1
					
				Case "4"
					xDir=-1
					yDir=0
					
				Case "5"
					xDir=-1
					yDir=-1 
					
				Case "6"
					xDir=0
					yDir=-1
					
				Case "7"
					xDir=1
					yDir=-1
					
			End Select
			
			x = x + xDir
			y = y + yDir
			
			
			xMin = Min(x,xMin)
			yMin = Min(y,yMin)
			
			xMax = Max(x,xMax)
			yMax = Max(y,yMax)
			
		Next
		
		AnchoTrazado = Max(xMax-xMin, 1)
		AltoTrazado = Max(yMax-yMin, 1)
		
		Paso = Min(Ancho/AnchoTrazado,Alto/AltoTrazado)/2
		
		DebugLog Paso+" "+AnchoTrazado+" "+AltoTrazado
		
		x = i*Ancho + (Ancho-Paso*AnchoTrazado)/2 - xMin*Paso/2 +5
		y = j*Alto + (Alto-Paso*AltoTrazado)/2 - yMin*Paso/2 
		
		;ahora hago los trazados
		
		For idx=1 To Len(Secuencia)
			
			Select Mid(Secuencia,idx,1)
					
				Case "0"
					xDir=1
					yDir=0
					
				Case "1"
					xDir=1
					yDir=1
					
				Case "2"
					xDir=0
					yDir=1
					
				Case "3"	
					xDir=-1
					yDir=1
					
				Case "4"
					xDir=-1
					yDir=0
					
				Case "5"
					xDir=-1
					yDir=-1 
					
				Case "6"
					xDir=0
					yDir=-1
					
				Case "7"
					xDir=1
					yDir=-1
					
			End Select
			
			x1 = x + xDir*Paso
			y1 = y + yDir*Paso
			
			If idx=1 Then
				Color 0,255,0
			Else
				Color 255,255,255
			End If
			
			Line x, y, x1, y1
			
			x=x1
			y=y1
			
			Oval x-1,y-1,3,3,True
			
			If xDir=0 And xDirOld=0 Then
				x=x-yDirOld*4
			End If
			
			If yDir=0 And yDirOld=0 Then
				y=y-xDirOld*4
			End If
			
			xDirOld = xDir
			yDirOld = yDir
			
		Next
		
		i=i+1
		
		If i > (GraphicsWidth()/Ancho)-1 Then
			j=j+1
			i=0
		End If
		
	Next
	
	SaveImage Imagen,"Patrones.bmp"
	
	SetBuffer FrontBuffer()
	
	Return Imagen
	
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D
