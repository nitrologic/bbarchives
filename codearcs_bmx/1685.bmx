; ID: 1685
; Author: AntMan - Banned in the line of duty.
; Date: 2006-04-23 07:37:19
; Title: TProfiler Class
; Description: An easy to use multi-function profiler

Strict
Type TLength
	Field time:Int
End Type

Type TCall
	
	Field name:String
	Field start
	Field Times:TList
	Field calls
	Method New()
		times = CreateList()
	End Method
		
End Type

tprofiler.calls = CreateList()
Type TProfiler

	Global calls:TList
	Function DumpLog( file:String )
		
		Local fi:TStream = WriteFile( file )
		
			WriteLine fi,"Aurora Profiler Log V1.0"
			For Local c:TCall = EachIn calls
				
				WriteLine fi,"----------------------------"
				Local totTime=0
				For Local t:TLength = EachIn c.times
					totTime:+t.time
				Next
				WriteLine fi,"Function:"+C.name+" Calls:"+c.calls+" Total:"+TotTime+" Avg:"+Float(TotTime)/Float(c.calls)
				WriteLine fi,"Total (Seconds):"+String( Float(tottime)/Float(1000) )
				WriteLine fi,"Avg (Seconds):"+String( (Float(TotTime)/Float(c.calls) ) / Float(1000) )
			Next
			
			
		CloseFile fi	
		
	End Function
			
	Function Enter( func:String )
		
		For Local call:tcall = EachIn calls
	
			If call.name = func 
					
				call.start = MilliSecs()
				call.calls:+1
				Return 
				
			EndIf

		Next
		
		Local call:TCall = New tcall
		calls.addlast( call )
		call.calls = 1 
		call.name = func
		call.start = MilliSecs()
			
	End Function

	Function Leave( func:String )
		
		For Local call:Tcall = EachIn calls
			
			If call.name = func
				
				Local l:TLength = New tlength
				l.time = MilliSecs()-call.start
				call.times.addlast( l )
				Return 
				
			End If
			
		Next
		
		RuntimeError "Unknown function"
		
	End Function

End Type
OnEnd( EndHook )
Function EndHook()

	Print "Dumping profile information."
	TProfiler.DumpLog("Profiler.txt")
	Print "Dumped."
	
End Function
