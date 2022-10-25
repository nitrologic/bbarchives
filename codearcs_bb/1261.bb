; ID: 1261
; Author: Klapster
; Date: 2005-01-19 09:52:22
; Title: Towers of Hanoi
; Description: Depth-first (recursive) solution to the Towers of Hanoi problem

;Tower of Hanoi - Depth first solution example
;Does not return fastest path to solution, just one possible path

;Coded by Tom Klapiscak, Jan 2005

;Any code here can be used/altered in anyway you please. A nod in your creds would be nice if you use ;)

;Hope this is of use to someone!!
;Tom


Graphics 320,240,16,2 : SetBuffer BackBuffer()

Global no=5 ; NUMBER OF TOWER SECTIONS - TRY CHANGING THIS

;States are represented like this: 111111, each digit represents one tower block, it's value being either
;1, 2 or 3 - which determines the pole it is currently on. Tower Sections go largest --> smallest.

Global initialstate$=String("1",no)
Global goalstate$=String("3",no)


Type rule ; Contains all possible movement rules
	Field a,b
End Type
;All possible rules
	rule_new(1,2)
	rule_new(3,1)
	rule_new(2,3)
	rule_new(2,1)
	rule_new(1,3)
	rule_new(3,2)

Type state ; Keeps track of which states have been visited already
	Field state$
End Type

Type path ; Used after problem has been solved to generate a path of steps used to visualise the solution
	Field state$
End Type

; ------- START MAIN PROGRAM ---------
;Solve!
	solve_depthfirst(initialstate)
;Visualise
	path.path = Last path
	While path.path<>Null
		draw(path_load(path))
		WaitKey()
		path.path = Before path
	Wend
; ------- END MAIN PROGRAM -----------

;Draws any particular state
Function draw(state$)
	Cls
	Local y[3]
	For i=1 To 3;Draw poles
		Color 100,100,100
			Rect i*100-5-50,100,10,110,True
	Next
	For i=1 To no;Draw tower sections
		Color 255,255,255
			Rect Int(Mid(state$,i,1))*100 - 50 - (7.5*(no+1-i)),200+y[Mid(state$,i,1)],15*(no+1-i),10,True
		y[Mid(state$,i,1)]=y[Mid(state$,i,1)]-10
	Next
	;Text 10,10,state
	Flip
End Function

Function solve_depthfirst(state$)	
	state_new(state$)
	If state=goalstate
		path_new(goalstate$);log state (just the goalstate in this case) in path on recursive backtrack
		Return True;backtrack - the problem has been solved!
	EndIf
	For rule.rule = Each rule
		newstate$=move(state$,rule\a,rule\b);generate new state using rule
		If newstate<>state And state_exists(newstate$)=False;if rule allowed for valid move and state has not been visited previously
			If solve_depthfirst(newstate$)=True ; Recurse parsing new state
				path_new(state$);log state in path on recursive backtrack
				Return True;backtrack - the problem has been solved!
			EndIf
		EndIf
	Next
End Function

;Returns state$ in original form if move is illegal
Function move$(state$,oldpos,newpos)
	For i=Len(state$) To 1 Step -1;start at smallest section (last) in state$
		char$=Mid(state$,i,1);isolate section's pole digit
		If char = oldpos;if relevant section
			;check not same as later number - a simple way of ensuring larger sections are never placed on top of smaller ones
			If i<no;don't need to for smallest
				For j=i+1 To Len(state$)
					If Mid(state$,j,1)=newpos Then Return state$;return state in original form because it's an illegal move (trying to place larger section on top of smaller one).
				Next
			EndIf
			newstate$=newstate$+newpos;add new pole digit for section
			oldpos=-1;ensure no more moves are made
		Else
			newstate$=newstate$+char;rebuild unaffected sections in state
		EndIf
	Next
	Return reverse(newstate);state is rebuilt in reverse, correct by reversing before returning newstate
End Function

;Returns reverse of txt$
Function reverse$(txt$)
	For i=Len(txt) To 1 Step -1
		newtxt$=newtxt$ + Mid(txt,i,1)
	Next
	Return newtxt
End Function

;---Rule Functions---
Function rule_new.rule(a,b)
	rule.rule = New rule
	rule\a=a:rule\b=b
	Return rule
End Function


;---State Functions---
Function state_new.state(statetxt$)
	state.state = New state
	state\state$ = statetxt$
	Return state
End Function

Function state_exists(statetxt$)
	For state.state = Each state
		If statetxt$=state\state$ Then Return True
	Next
	Return False
End Function


;---Path Functions---
Function path_new.path(state$)
	path.path = New path
	path\state = state$
	Return path
End Function

Function path_load$(path.path)
	Return path\state
End Function
