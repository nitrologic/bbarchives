; ID: 3138
; Author: Kryzon
; Date: 2014-07-20 02:26:53
; Title: Callback Timer
; Description: A modified BRL.Timer object that on each tick calls a BlitzMax function that you specify.

Import BRL.Timer

Type TCallbackTimer

	Method Ticks:Int()
		Return _ticks
	End Method
	
	Method Stop()
		If Not _handle Return
		bbTimerStop _handle,Self
		_handle=0
		_func=Null
		_data=Null
	End Method
	
	Method Fire()		
		If Not _handle Return
		_ticks:+1
		
		'Invoke the function with the data argument.
		
		_func( _data )		
	End Method

	Method Wait:Int()
		If Not _handle Return -1
		Local n:Int
		Repeat
			WaitSystem
			n=_ticks-_wticks
		Until n
		_wticks:+n
		Return n
	End Method
	
	Method SetCallback( newFunc( data:Object ) )
		_func = newFunc
	End Method

	Method SetData( newData:Object )
		_data = newData
	End Method

	Method GetData:Object()
		Return _data		
	End Method
	
	Function Create:TCallbackTimer( hertz#, func( data:Object ), newData:Object = Null )
		Local t:TCallbackTimer=New TCallbackTimer
		Local handle:Int=bbTimerStart( hertz,t )
		If Not handle Return Null
		t._func=func
		t._handle=handle
		t._data=newData
		Return t
	End Function

	Field _ticks:Int
	Field _wticks:Int
	Field _func( data:Object )
	Field _handle:Int
	Field _data:Object

End Type

Function CreateCallbackTimer:TCallbackTimer( hertz#, func( data:Object ), newData:Object = Null )
	Return TCallbackTimer.Create( hertz, func, newData )
End Function

Function SetTimerCallback( cTimer:TCallbackTimer, func( data:Object ) )
	cTimer.SetCallback( func )
End Function

Function SetTimerData( cTimer:TCallbackTimer, newData:Object = Null )
	cTimer.SetData( newData )
End Function

Function GetTimerData:Object( cTimer:TCallbackTimer )
	Return cTimer.GetData()
End Function

Function WaitCallbackTimer:Int( cTimer:TCallbackTimer )
	Return cTimer.Wait()
End Function

Function StopCallbackTimer( cTimer:TCallbackTimer )
	cTimer.Stop()
End Function
