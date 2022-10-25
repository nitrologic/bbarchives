; ID: 3249
; Author: Flanker
; Date: 2016-01-24 14:17:39
; Title: Sudoku filling
; Description: Algorithm to randomly fill a plain sudoku grid

Graphics 640,480,32,2
SetBuffer BackBuffer()

SeedRnd MilliSecs()

; tableaux à déclarer pour générer le sudoku
Dim sudoku(8,8,9)
Dim solution(9)
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; EXAMPLE
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
While Not KeyHit(1)

	Cls

	time = MilliSecs()
	try = Sudoku_Generate()
	time = MilliSecs() - time
	
	passes# = passes + 1
	tries# = tries + try
	
	Color 255,255,255
	For y = 0 To 8
		For x = 0 To 8
			Text 20+20*x,20+20*y,sudoku(x,y,0)
		Next
	Next
	
	Color 80,80,80
	For y = 0 To 10
		Line 20*y-5,16,20*y-5,195
		For x = 0 To 10
			Line 15,20*x-4,195,20*x-4
		Next
	Next
	
	Color 150,0,0
	For y = 0 To 10
		If y = 1 Or y = 4 Or y = 7 Or y = 10 Then Line 20*y-5,16,20*y-5,195
		For x = 0 To 10
			If x = 1 Or x = 4 Or x = 7 Or x = 10 Then Line 15,20*x-4,195,20*x-4
		Next
	Next
	
	Color 255,255,255
	Text 20,220,try + " tries"
	Text 20,235,time + " ms"
	Text 20,270,"Generated grids : " + Int(passes)
	Text 20,285,"Average : " + tries/passes + " tries"
	Text 20,320,"Press a key to generate a new grid"
		
	Flip

	WaitKey()

Wend

End
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
Function Sudoku_Generate()

	; label de départ
	.retry
	try = try + 1 ; comptabilise le nombre d'essais
	
	; reset de la grille, la profondeur z représente les solutions encore valides (1) pour la case (x,y), et les solutions non valides (0)
	For x = 0 To 8
		For y = 0 To 8
			For z = 1 To 9
				sudoku(x,y,z) = 1 ; au départ toutes les solutions de 1 à 9 sont valides, la profondeur 0 correspond à la grille finale
			Next		
		Next
	Next
	
	For y = 0 To 8 ; on déscend de haut en bas
		For x = 0 To 8 ; et on travail sur les lignes
		
			solutions = 0
			For z = 1 To 9
				solutions = solutions + sudoku(x,y,z) ; on compte le total de solutions valides encore présente pour la case (x,y)
			Next
			
			If solutions = 0 Then Goto retry ; aucune solution, la grille est une impasse, on recommence
			
			Dim solution(solutions) ; on redimensionne l'array qui va stocker et servir à tirer au hasard une solution valide
			
			count = 0
			For z = 1 To 9
				If sudoku(x,y,z) = 1 ; 
					solution(count) = z ; on stock la solution à la suite de l'array
					count = count + 1
				EndIf
			Next
			
			random = solution(Rand(0,solutions-1)) ; on tire au hasard une des solutions de l'array
			
			For x2 = 0 To 8
				sudoku(x2,y,random) = 0 ; on enleve cette solution des autres cases de la ligne
			Next
			
			For y2 = 0 To 8
				sudoku(x,y2,random) = 0 ; on enleve cette solution des autres cases de la colonne
			Next
			
			; ensuite on enlève la solution dans la région 3x3 associée à la case (x,y)
			If x <= 2 And y <= 2 ; region haut gauche
				For x2 = 0 To 2
					For y2 = 0 To 2
						sudoku(x2,y2,random) = 0
					Next
				Next
			ElseIf x >= 3 And x <= 5 And y <= 2 ; region haut milieu
				For x2 = 3 To 5
					For y2 = 0 To 2
						sudoku(x2,y2,random) = 0
					Next
				Next
			ElseIf x >= 6 And y <= 2 ; region haut droite
				For x2 = 6 To 8
					For y2 = 0 To 2
						sudoku(x2,y2,random) = 0
					Next
				Next
			ElseIf x <= 2 And y >= 3 And y <= 5 ; region milieu gauche
				For x2 = 0 To 2
					For y2 = 3 To 5
						sudoku(x2,y2,random) = 0
					Next
				Next
			ElseIf x >= 3 And x <= 5 And y >= 3 And y <= 5 ; region milieu milieu
				For x2 = 3 To 5
					For y2 = 3 To 5
						sudoku(x2,y2,random) = 0
					Next
				Next
			ElseIf x >= 6 And y >= 3 And y <= 5 ; region milieu droite
				For x2 = 6 To 8
					For y2 = 3 To 5
						sudoku(x2,y2,random) = 0
					Next
				Next
			EndIf
			
			; NOTE : pas besoin d'intervenir sur les 3 régions du bas à partir du moment où les 3 régions du haut, et les 3 du milieu sont remplies
			;        la logique fait qu'on ne peut plus retrouver une même solution dans une même région
			
			sudoku(x,y,0) = random ; finalement on assigne notre solution à la case (x,y)
			
		Next
	Next
	
	Return try
		
End Function
