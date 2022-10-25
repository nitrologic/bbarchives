; ID: 2122
; Author: GW
; Date: 2007-10-19 13:13:32
; Title: Simple Genetic Algorithm
; Description: A short example of GA evolution

rem
	small Genetic algorithm example
	by GW [jojo_dfb at Yahoo.com] 10.19.07
endrem

Strict
Framework brl.basic 
SeedRnd(MilliSecs())

Const MAXPOP = 300		'# of individuals in each generation 
Const MUTATECHANCE = 2'[percent]	' smaller is better [1 to 5]ish
Const ASCII1 = 30		' start of ascii range as chromosomes
Const ASCII2 = 140		' end of ascii range

Global Population$[]
Global INputSTRING$



INputSTRING = "A Genetic Algo example in BLITZMAX!! bigger strings take longer!!!!"
Population = New String[MAXPOP]

'INputSTRING = Input("Give some text to evolve:")
'------------------------------------------------------------------
Function GENPOP()
	'// Generate first random population //
	Local S$
	Local i,j
		For  I = 0 To MAXPOP-1
			For  j = 1 To INputSTRING.Length
				Local C$ = Chr(Rand(ASCII1,ASCII2))
				S :+ C
			Next 
			Population[i] = S
			S=""
		Next
End Function
'------------------------------------------------------------------
Function Fitness(instring$)
	'// this fitness func works but is non-optimal //
	Local i, val=0

	For i = 0 To instring.length-1
		val :+ Abs(instring[i] - INputSTRING[i])
	Next
	Return Val
End Function
'------------------------------------------------------------------
Function Fitness2(Instring$)
	'// a better fitness function //
	Local i, val=0
	For i = 0 To instring.length-1
		If instring[i] <> INputSTRING[i] Then val :+ 1	
	Next

	Return Val
End Function
'------------------------------------------------------------------
Function EvalPOP()
	'// Evaluate the population and flag the 2 fittest individuals
	'// we're only using 1 with mutation, but if you wanted to breed the 
	'// 2 best fit, there are flagged.
	For Local I = 0 To MAXPOP-1

		Local Val = Fitness2(Population[i])
		If Val <= Oldval1 Then
			Best2 = Best1
			Best1 = I
			Oldval2 = oldval1
			oldval1 = val
		End If
	Next
End Function
'------------------------------------------------------------------
Function BreedPOP()
	'// not really Breed, just mutate, but here is where a breeding func would go
	Local S$
	Local tPop$[MAXPOP]
	
	tpop[0] = Population[Best1]
	
	For Local I = 1 To MAXPOP-1
		tpop[I] = Mutate()
	Next
	
	Population = tpop
End Function
'------------------------------------------------------------------
Function Mutate$()
	Local Str$
	For Local I = 0 To Population[Best1].Length-1
		If Rand(1,100) <= MUTATECHANCE Then
			str :+ Chr(Rand(ASCII1,ASCII2))
		Else
			str :+ Chr(Population[Best1][I])
		End If
	Next 
	Return Str
End Function
'------------------------------------------------------------------

'------------------------------------------------------------------
GENPOP
	Global  Best1 = 999999
	Global  Best2 = 999999
	Global	Oldval1 = 999999
	Global  oldval2=  999999

Local loop1		
For loop1 = 1 To 1000000
	Oldval1 = 999999
	oldval2=  999999
	
	EvalPOP
		If Population[Best1].Contains(INputSTRING) Then Print "~nWINNER!!~n"; Exit
	BreedPOP

	If loop1 Mod 10 = 0 Then 
		Print Population[0]
	End If
Next


Print 
Print Population[Best1]
Print (Loop1 * MAXPOP)  + " individuals tested in " + Loop1 + " generations"
