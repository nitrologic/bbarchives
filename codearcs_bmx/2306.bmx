; ID: 2306
; Author: USNavyFish
; Date: 2008-08-31 01:08:29
; Title: Method Call Scheduler
; Description: Allows object methods to be called at a specific time in the future

Rem 
--------------------------------------------------------------------------------------------------------------

Event Scheduling Program
	Bryan Fishman (USNavyFish), AUG 2008


This program was designed to provide an easy means of scheduling the execution of events within a game
or simulation.  The system was designed for minimal overhead.  It is not designed for extremely high
time sensitivity, and use of a time resolution less than one hundredth of a second is not recomended.

Essentially, the system provides an array where each cell represents one 'moment' of time.  When events
are scheduled, this system places them in the appropriate cell which represents the moment of execution
for that event.   The system then simply checks one cell per 'moment' and executes any event stored there.
In this manner, the system can support an extremely large number of concurrent and overlapping events, without 
any performance loss, as the program is not required to loop through all scheduled events during every cycle.  


This program provides two Custom Types:

	 A"TFutureEvent" type with the following functions, methods, and fields:
		|| Field obj_:Object	The targe objet whose method will be called
		|| Field method_:TMethod This is the specific method which will be called
		|| Field args_:Object[] An object array containing the arguments to be passed
		||
		|| Function  Create:TFutureEvent( obj:Object,methodName:String,args:Object[] )
		||		-This function creates and returns the FutureEvent. 
		||		-'methodName:String' is the exact name of the method that will be called
		||
		|| Function invoke:object() executes the FutureEvent.


	A "TScheduler" type with the following functions and Globals  (There are no Methods or locals)

		|| Global TimeArray_Length:Int   			Size of the TimeArray (# of cells)
		|| Global TimeArray_StepDuration:Float   	Duration of time represented by each cell
		
		||		- Note that (Length * Duration) yields the furthest schedulable time
		||		- i.e. 3600 cells of 1 second duration each allows events to be scheduled 
		||			up to one hour (inclusive) in advance.
		||
		|| Global TimeArray:TList[]		Each cell stores a list of scheduled TFutureEvents
		|| Global CurrentIndex:Int		Current Index of the Time Array
		|| Global ScheduleTimer:TTimer  	System Timer for Scheduler object
		|| Global IsPaused:byte			System Timer is paused if 1, is not paused if 0
		||
		|| Function Initialize(Length:Int = 3600 , Seconds:Float = 1)
		||		- Call this once at the beginning of your program
		||		- For a schedule that can store events up to 24 hours (inclusive) into the future,
		||			and that has a schedule resolution of 0.1 seconds, use the following parameters:  
		||			Length = 3600 * 24 * 10
		||			Seconds = 0.1
		||		- Each Index specified by Length requires 4 bytes of memory. The above example requires
		||			a total of ~3.3 MB of memory at runtime.
		||
		|| Function Begin()
		||		- Activates the system timer
		||		- Call this once you wish to actually begin schedule execution.
		||		- Events may be scheduled both prior to and during the schedule's execution
		||
		|| Function ScheduleTimerHook:Object(id:Int, data:Object, context:Object)
		||		- Internal funciton, intercepts system timer hook (returns null if intercepted)
		||		- Calls the ScheduleAdvance and ScheduleExecute functions
		||
		|| Function ScheduleAdvance()
		||		- Internal function, advances current Schedule Index
		||
		|| Function ScheduleExecute(index:Int) 
		||		- Internal Function, executes all events scheduled for current time index.
		||		- This function removes all references to the scheduled events once executed
		||
		|| Function ScheduleEvent(event:TFutureEvent, SecondsDelay:Float)
		||		- Call this function to schedule an event which will execute once in the future,
		||			after the number of seconds specified by SecondsDelay has passed.
		||		- For example, when called with SecondsDelay = 20, the passed event will execute
		||			approximately 20 seconds after the "ScheduleEvent" function is called
		|| 		- Scheduling accuracy reduces as "SecondsDelay" approaches the TimeArray_StepDuration
		||			For best results, use a TimeArray_StepDuration value at least twice as short as
		||			your quickest-scheduled events.   For example, a program that regularly schedules
		||			events at intervals as short as 1 second apart should use a StepDuration value of
		||			at most 0.5 seconds. A value of 0.1 seconds wil provide much better accuracy.
		||			
		|| Function ScheduleSetPause(command:byte) 
		||		This function sets the Scheduler's "IsPaused" flag.
		||		When "IsPaused" = 1, normal execution occurs
		||		When "IsPaused" = 0, the scheduler 'ignores' the schedule timer's tick event.
		
		

Example usages of the scheduler function:


TScheduler.ScheduleEvent(TFutureEvent.Create(obj1 , "textoutput" , ["You Called Me!"]) , 2)
||
||This calls obj1's "textoutput" method, with the input parameter "You Called Me!", in two seconds from this call.


TScheduler.ScheduleEvent(TFutureEvent.Create(obj1 , "add" , [String(1)]) , 4) 
||
||This calls obj1's "add" method, with the input parameter of 1.  Note the floating point value must be converted
||into a string to be accepted by the function. This event will execute in four seconds.


TScheduler.ScheduleEvent(TFutureEvent.Create(obj1 , "multiparam" , [String(4.3),String(17),"TEXT"]) , 20) 
||
||This calls obj1's "multiparam" method, which accepts a float, an int, and a string as inputs to the method.
||Note how any non-string parameter inputs must be converted to strings using the String() function.  This
||event will be executed in 20 seconds.

	
--------------------------------------------------------------------------------------------------------------
End Rem

SuperStrict


Type TFutureEvent
	
	Field obj_:Object 
	Field method_:TMethod
	Field args_:Object[]
	
	Function Create:TFutureEvent( obj:Object,methodName:String,args:Object[] )
    		Local temp:TFutureEvent=New TFutureEvent
    		temp.obj_ = obj
    		temp.method_ = TTypeId.ForObject(obj).findMethod( methodName )
		temp.args_ = args
		
    		Return temp
  	End Function

	Method Invoke:Object() 
		Return method_.Invoke( obj_,args_ )
  	End Method
	
End Type



Type TScheduler
	Global TimeArray_Length:Int
	Global TimeArray_StepDuration:Float
	Global TimeArray:TList[]
	Global CurrentIndex:Int
	Global ScheduleTimer:TTimer	
	Global IsPaused:Byte
	
	
	Function Initialize(Length:Int = 3600 , Seconds:Float = 1) 
		
		DebugLog "Master Schedule Initialized"
		DebugLog "Schedule Outlook:    " + Length / Seconds + " Seconds"
		DebugLog "Schedule Resolution: " + Seconds + " Seconds"
		
		TimeArray_Length = Length + 1		'One is added so total duration is inclusive
		TimeArray_StepDuration = Seconds
		TimeArray = New TList[TimeArray_Length]
		CurrentIndex = -1
		
	End Function


	
	Function Begin() 
		AddHook EmitEventHook , ScheduleTimerHook
		ScheduleTimer = CreateTimer(1 / TimeArray_StepDuration)
		DebugLog "Schedule Execution Commenced at System Time: " + CurrentTime() 
	End Function
			
	
	
	Function ScheduleTimerHook:Object(id:Int, data:Object, context:Object)
		
		Local ev:TEvent = TEvent(data)
		
		If ev = Null Then Return Null
		
		If ev.id = EVENT_TIMERTICK
			If ev.source = ScheduleTimer 
				
				If IsPaused = 0
				
				ScheduleAdvance() 
				DebugLog "ScheduleTimer Fires. Executing Index["+CurrentIndex+"]"
				ScheduleExecute(CurrentIndex)
				
				EndIf	
				
				Return Null
			
			EndIf
		EndIf
		
		Return data
	End Function
	
	
	
	Function ScheduleAdvance() 
		CurrentIndex = (CurrentIndex + 1)	Mod TimeArray_Length
	End Function
	
	
	
	
	Function ScheduleExecute(index:Int) 
		If TimeArray[index]
			For Local event:TFutureEvent = EachIn TimeArray[index]
				If event.obj_
					event.invoke
				Else
					DebugLog "Object " + event.obj_.tostring() + " No longer Exists!"
				EndIf
			Next
			
			TimeArray[index] = Null	'Removes all references to TList and stored TFutureEvents
		EndIf

	End Function
	
	
	
	Function ScheduleEvent(event:TFutureEvent, SecondsDelay:Float)
		Local index:Int
		index = Ceil(SecondsDelay / TimeArray_StepDuration)
			
		If index > TimeArray_Length
			DebugLog "Critical Error!~nObject:~t" + event.obj_.tostring() 
			DebugLog "Scheduled Time (" + SecondsDelay+") converts to " + index + " future cells"
			DebugLog "This exceeds the length of schedulable time (" + TimeArray_Length + " cells)"
			
			End
		EndIf
					
		index :+ CurrentIndex + 1	
		index :Mod TimeArray_Length
				
		
		If TimeArray[index] = Null Then TimeArray[index] = CreateList() 
		
		ListAddLast TimeArray[index] , event
		
		DebugLog "Event Scheduled for object $" + event.obj_.tostring() + " in " + SecondsDelay + " seconds."

	End Function
	
	
	Function ScheduleSetPause(command:Byte) 
		IsPaused = command
	End Function
		
	
End Type





Type TTestType
	Field Value_:Float
	Field name_:String
	
	Function Create:TTestType(objname:String)
		Local temp:TTestType = New TTestType
		temp.name_= objname
		temp.value_ = 0
		Print temp.name_ + " Created: " + temp.value_
		Return temp
	End Function
	
	Method add:Float(num:Float)
		value_:+ num
		Print name_ +"'s Counter increased to "+value_
	End Method
	
	Method textoutput:String(output:String) 
		Print name_ +" Says: "+output
	End Method	
	
	
End Type



Function KeyHook:Object(id:Int , data:Object , context:Object) 
	Local ev:TEvent = TEvent(data) 
	
	If ev = Null Then Return Null
	
	Select ev.id
		Case EVENT_KEYDOWN
		Print "KEYDOWN~t" + ev.data
		
			Select ev.data
		
				Case 49
				TScheduler.ScheduleEvent(TFutureEvent.Create(obj1 , "textoutput" , ["You Called Me!"]) , 1)
				TScheduler.ScheduleEvent(TFutureEvent.Create(obj1 , "add" , [String(1)]) , 1) 
 				
				Case 50
				TScheduler.ScheduleEvent(TFutureEvent.Create(obj1 , "textoutput" , ["You Called Me!"]) , 2)
				TScheduler.ScheduleEvent(TFutureEvent.Create(obj1 , "add" , [String(1)]) , 2) 
				
				Case 51
				TScheduler.ScheduleEvent(TFutureEvent.Create(obj1 , "textoutput" , ["You Called Me!"]) , 4)
				TScheduler.ScheduleEvent(TFutureEvent.Create(obj1 , "add" , [String(1)]) , 4)
				
				Case 80
				If TScheduler.IsPaused
					TScheduler.ScheduleSetPause(0) 
				Else
					TScheduler.ScheduleSetPause(1) 
				EndIf
				
					
			End Select
				
		Case EVENT_KEYUP
		'Print "KEYUP~t"+ev.data
		
	End Select

	
	Return data
	
End Function		








Graphics 640 , 480

AddHook EmitEventHook , Keyhook


TScheduler.Initialize(3600*24*4,0.25)

Global obj1:TTestType = TTestType.Create("Object1") 

GCCollect
Print GCMemAlloced() + " Bytes Allocated"

Cls
Print "Press any key to begin!  Make all key presses to the graphics window."
DrawText "Press any key to begin!  Watch the console window.",0,0
Flip
WaitKey() 

TScheduler.Begin() 

Repeat

Cls	
DrawText "Press 1, 2, or 3 to schedule an event for 1, 2, or 4 seconds in the future" , 0 , 0
DrawText "Watch the Console for output." , 0 , 30
DrawText "Observe the TimeArray cells advancing once every " + TScheduler.TimeArray_StepDuration + " seconds." , 0 , 60
DrawText "Now Press a key, wait the appropriate amount of time,",0,90 
DrawText "And watch for the event response in the console!" , 20 , 110

Flip

Until KeyHit(key_escape) 


End
