; ID: 1721
; Author: H&amp;K
; Date: 2006-05-26 05:55:51
; Title: Simple Timer
; Description: Type for a simple Timer

'==============================================================================
'
Type TBeat'An atuo call Timer Container
	'=======================================================================

	Field length:Int				'	Length of Beat in Milli seconds
	Field Tikker:TTimer				'	The actual Timer
	Field MyEvent:TEvent = New TEvent		'	The Event the Timer is tied to
	Field BeatFunction(Caller:TBeat,event:TEvent)	'	The Function to be called on Beat
	'=======================================================================

	Method Create:TBeat (length:Int = 1000,BeatFunction(Caller:TBeat,event:TEvent) = Null)
		Self.length	 = length			'	Let the New Beat length = length
		Local	hertz:Float	 = 1000.0 / length	'	Stupid Bmx Hertz 
		AddHook EmitEventHook,eventhook,Self		'	Start the Hook
		Self.Tikker		 = CreateTimer (hertz)	'	Create the timer
		Self.BeatFunction	 = BeatFunction		'	Set the Function to call
		Return Self					'Return the New TBeat
	End Method
			'-----------------------------------------------

	Function eventhook:Object(id:Int,data:Object,context:Object)
		If TBeat(context) TBeat(context).Ev TEvent(data);Return data	
	EndFunction
	'======================================================================

	Method Free()					
		RemoveHook EmitEventHook,eventhook	'	Release the Hook
		GCCollect()				'	Do an Garbage Collection
	End Method
			'----------------------------------------------

	Method Ev(event:TEvent)
		If event.id = EVENT_TIMERTICK and event.source =Tikker	'	See if the Event was A Beat
			BeatFunction(Self,event)				'	Pass control to
		EndIf								'	the right Function
	End Method
			'---------------------------------------------

	Method HowManyTicks:Int ()
		Return TimerTicks(Tikker)		'	Return the number of Ticks
	EndMethod
			'---------------------------------------------

	Method GetFrequancy:Float ()
		Return (1000.0 / length)		'	Return the Hertz#
	End Method 
	'======================================================================

End Type
'
'==============================================================================
'	Fields:	Length		:Int		-Length in MilliSeconds Before Beat
'		Tikker		:TTimer		-The actual timer
'		MyEvent		:TEvent		-Event Object to handle the tick
'		BeatFunction	:(TBeat,TEvent)	-Pointer to function taking the event
'
'	Methods	Create		:TBeat 	(Length, Ptr BeatFunction(TBeat,TEvent=Null))
'		EventHook	:Object	(Int,Odject,Object)
'		Free()				-Release the eventhook
'		HowManyTicks	:Int		-Returns the number of Ticks
'		Ev(TEvent)			-Catch any beats and call correct catch program
'==============================================================================
'	Function BeatFunction	(Caller:TBeat , event:TEvent)
'==============================================================================



'=============================================================================
'	To Use
'Just make a TBeat object as bellow, and the function
'you use will be called every xxx milliseconds
'Global BeatName:TBeat = New TBeat.Create (Millisecdelay:int, Ptr Function to call)
'=============================================================================
Global MainBeat:TBeat = New TBeat.Create (1000 , CatchMainBeat)
'#############################################################################
'
'	Function	CatchMainBeat
'		----------------------------------------------------------------
'	When MainBeat was created CatchMainBeat was passed To TBeat as the Function To call
'	
'#############################################################################
Function CatchMainBeat ( Caller:TBeat , event:TEvent)
Print "Hello"
End Function
'############################################################################
                                                
Repeat
Until KeyDown(KEY_SPACE)
