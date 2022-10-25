; ID: 1468
; Author: deps
; Date: 2005-09-25 06:03:18
; Title: Game state engine
; Description: Makes it easier to split your game into different states

' make a new type that extends this one. Overload the methods.
Type GenericState

	' Overload this one and put startup code here (loading images, sounds, etc)
	Method startup()
	EndMethod
	
	
	' No drawing here! Only user input and other behind-the-scene stuff
	Method update()
	EndMethod
		
	
	' Draw things in this one. But no user input or logic code!
	Method draw()
	EndMethod
		
	
	' Save highscore/nullify variables/scream like a banshee
	Method shutdown()
	EndMethod


	' Called when the engine switches to a new state.
	Method on_hold()
	EndMethod
	
	
	' Called when the engine is done with the other state and switches back t this one
	Method resume()
	EndMethod


EndType



Type GameState

	Global state_list:TList
	Global current_state:GenericState

	Function Switch( new_state:GenericState )
		If Not state_list Then state_list = CreateList()
		
		state_list.addlast( new_state )
		If current_state Then current_state.on_hold()
		new_state.startup()
		current_state = new_state

	EndFunction
	
	' Called by the running state when it doesn't want to play anymore
	Function ExitState()
	
		If Not state_list Then RuntimeError("ExitState should only be called by running states!")
	
		current_state.shutdown()
		
		state_list.remove( current_state )
		
		If state_list.Count() > 0 Then
			current_state = GenericState( state_list.last() )
		
			current_state.resume()		
		EndIf
	
	EndFunction
	
	
	Function Run()
	
		If Not state_list Then RuntimeError("Switch to a new game state before calling GameState.Run()")
	
		While state_list.Count() > 0
	
			current_state.update()
			current_state.draw()
	
		Wend
	
	EndFunction
	
	

EndType
