; ID: 1508
; Author: deps
; Date: 2005-10-29 01:56:26
; Title: Simple Event Handling
; Description: BlitzMax port of Gamedev.net tutorial source

Type Tevent
	Field t:Int
	Field arg1:Int, arg2:Int
EndType


Type EventHandler

	' Overload this
	Method EventHandler( e:Tevent ) 
	EndMethod


	Field _nexthandler:EventHandler

	Method init()
		EventDispatcher.RegisterHandler(Self)
	EndMethod
	

	Method GetNextHandler:EventHandler()
		Return _nexthandler
	EndMethod
	

	Method SetNextHandler( n:EventHandler )
		_nexthandler = n
	EndMethod
	
	
	Method SendEvent( event_type:Int, arg1:Int = 0, arg2:Int = 0 )
		EventDispatcher.SendEvent( event_type, arg1, arg2 )
	EndMethod

EndType




Type EventDispatcher

	Global _devicelist:EventHandler

	Function RegisterHandler( device:EventHandler )
		device.SetNextHandler( _devicelist )
		_devicelist = device
	EndFunction
	
	Function SendEvent( event_type:Int, arg1:Int = 0, arg2:Int = 0 )
		Local e:Tevent = New Tevent
		e.t = event_type
		e.arg1 = arg1
		e.arg2 = arg2
		Local curDevice:EventHandler = _deviceList
		While curDevice
			curDevice.EventHandler(e)
			curDevice = CurDevice.GetNextHandler()
		Wend
	EndFunction


EndType
