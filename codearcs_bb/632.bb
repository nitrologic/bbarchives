; ID: 632
; Author: sswift
; Date: 2003-03-23 02:31:52
; Title: Swift Event System
; Description: An event system for triggering events in your game, or animating objects.

; -------------------------------------------------------------------------------------------------------------------
; Swift Event System for Blitz Basic - Copyright 2003 Shawn C. Swift - sswift@earthlink.net
; -------------------------------------------------------------------------------------------------------------------
; 
; License:
; 	
; 	You may use tand modify this code for free.
;
; 	If you find it useful, a small donation would be appreciated!
; 	To send a donation, go to http://www.paypal.com and send your donation to sswift@earthlink.net.
;
; --
;
; Code usage:
;
;	1. Add fields for data you want to store with your events to the Event type.
;
;	2. Add code to the Event_Start, Event_Active, and Event_End functions to perform specific actions for each type
; 	of event in your game.
;
; 	3. Create an event using NewEvent().
;
;  	4. Call UpdateEvents() every game loop, passing it the current game time.
;
; -- 
;
; Notes:
;
; 	ALL events trigger Event_Start() when they begin, Event_Active() every update during the time they are active,
; 	and Event_End() when they expire.  These triggers can all occur, in that order, in a single update.
;
;	If you want an event to trigger another event when it expires, place code in Event_End() which calls NewEvent().
; -------------------------------------------------------------------------------------------------------------------
                   

Type Event

	Field Event_Type

	Field Time_Start
	Field Time_End
	
	Field Active
	

	; -- Add data for your events here:
	; -- All variables below are not used by the system and can be deleted at will.

	Field Data_Int[3]
	;Field Data_Float#[3]
	;Field Data_String$[1]
		
End Type


; -------------------------------------------------------------------------------------------------------------------
; Call this function to create a new event. 
; The function returns a pointer to the newly created event so you can add data to it.
;
; Time_Start and Time_End are optional.
;
; If Time_Start is not specified, the event will trigger on the next call to UpdateEvents().
; If Time_End is not specified, the event will trigger AND expire on the next call to UpdateEvents().
; -------------------------------------------------------------------------------------------------------------------
Function NewEvent.Event(Event_Type, Time_Start=-1, Time_End=-1)

	If (Time_End = -1) Then Time_End = Time_Start

	ThisEvent.Event = New Event
	
	ThisEvent\Event_Type   = Event_Type
	ThisEvent\Time_Start   = Time_Start
	ThisEvent\Time_End     = Time_End
	ThisEvent\Active       = False

	Return ThisEvent
	
End Function


; -------------------------------------------------------------------------------------------------------------------
; Call this function every loop to update your events.
;
; You should pass the current time, in milliseconds, to the function.
; (Time must always be a positive value.)
;
; (The reason I don't just grab millisecs() is so that your game time can be independent of the system time, which
; allows you to do effects like slow-mo and fast-forward while still having your events trigger at the right time.)
; -------------------------------------------------------------------------------------------------------------------
Function UpdateEvents(Time)

	; Loop through all events.
	For ThisEvent.Event = Each Event

		; If it past the start time for this event...
		If (Time >= ThisEvent\Time_Start)  
				
			; If the event is not currently active...
			If Not ThisEvent\Active 
	
				; Call the code which should be executed when the event starts, and set the event as active. 
				Event_Start(ThisEvent, Time)
				ThisEvent\Active = True

			EndIf
		
			; Call the code which should be run continuously while this event is in progress.
			Event_Active(ThisEvent, Time)

			; If it is time for this event to end...		
			If Time >= ThisEvent\Time_End

				; Call the code which should be executed when this event ends, and delete the event.
				Event_End(ThisEvent, Time)
				Delete ThisEvent
				
			EndIf
		
		EndIf	

	Next

End Function


; -------------------------------------------------------------------------------------------------------------------
; This function is called when an event first starts.
; -------------------------------------------------------------------------------------------------------------------
Function Event_Start(ThisEvent.Event, Time)

	Select ThisEvent\Event_Type
	
	
	End Select

End Function 


; -------------------------------------------------------------------------------------------------------------------
; This function is called continuously while an event is in progress. 
;
; To find out how much time has passed since an event started when animating an object, do this:
; Time_Elasped = ThisEvent\Time_Start - Time
;
; To find out where, 0..1, in the event you are do this:
; Event_Point# = Float(ThisEvent\Time_Start-Time) / Float(ThisEvent\Time_End-ThisEvent\Time_Start)
;
; I don't do this for all events because some events are instantaneous, which would cause a divide by 0 error, and
; others trigger immediately and have a Time_Start of -1.
; -------------------------------------------------------------------------------------------------------------------
Function Event_Active(ThisEvent.Event, Time)

	Select ThisEvent\Event_Type
	
	
	End Select

End Function


; -------------------------------------------------------------------------------------------------------------------
; This function is called when an event ends.
; -------------------------------------------------------------------------------------------------------------------
Function Event_End(ThisEvent.Event, Time)

	Select ThisEvent\Event_Type
	
	
	End Select

End Function
