; ID: 148
; Author: Shagwana
; Date: 2002-08-11 10:23:31
; Title: Bresenham line tracing (2d)
; Description: Lets you iterate through a bresenham line one coord at a time

;###################################################
;Stephen Greener (aka Shagwana) line trace engine
;Shagwana@sublimegames.com
;www.sublimegames.com
;###################################################
;[Inline] = best when made inline functions!
;This will trace every coord in the line apart from the starting coord
;Can be used to continue drawing line in the started direction!
;###################################################

;Used information in the algo
Type tBresenhemInfo
	Field iX1,iY1,iX2,iY2		;Coords of the line in question (from 1->2)
	Field iX,iY					;Current location in the line!
	Field iDeltaX,iDeltaY		;Distance to travel
	Field iAbsDeltaX,iAbsDeltaY	;Postive version of the number above (for speed)
	Field iIncX,iIncY			;What to increase by
	Field iIncOne,iIncTwo	
	Field iBuffer				;Counting buffer
	Field iXY					;Working buffer	
	Field iType					;What type of line this is (X over Y or Y over Z)
	Field iStepsNeeded			;How many iterations this line will take
	Field iCurrentStep			;Current stop out of the needed steps!
	End Type

;Spawn a line to trace
Function SpawnTrace.tBresenhemInfo(iX1,iY1,iX2,iY2)
	Local tTrace.tBresenhemInfo=New tBresenhemInfo
	;Store the coords for the trace
	tTrace\iX1=iX1
	tTrace\iY1=iY1
	tTrace\iX2=iX2
	tTrace\iY2=iY2
	;Current postion
	tTrace\iX=iX1
	tTrace\iY=iY1
	;Calculate distance to travel (delta)
	tTrace\iDeltaX=iX1-iX2					;Distance to travel
	tTrace\iDeltaY=iY1-iY2
	tTrace\iAbsDeltaX=Abs(tTrace\iDeltaX)	;Postive versions
	tTrace\iAbsDeltaY=Abs(tTrace\iDeltaY)
	;Set direction of the add in bresenhem line algo
	If tTrace\iDeltaX<0
		tTrace\iIncX=1
		Else
		tTrace\iIncX=-1
		EndIf
	If tTrace\iDeltaY<0
		tTrace\iIncY=1
		Else
		tTrace\iIncY=-1
		EndIf	
	;Setup the start of the algo loop
	If tTrace\iAbsDeltaX>tTrace\iAbsDeltaY
		tTrace\iType=0
		tTrace\iStepsNeeded=tTrace\iAbsDeltaX
		tTrace\iIncOne=2*tTrace\iAbsDeltaY
		tTrace\iIncTwo=2*(tTrace\iAbsDeltaY-tTrace\iAbsDeltaX)
		tTrace\iBuffer=tTrace\iIncOne-tTrace\iAbsDeltaX
		tTrace\iXY=iY1
		Else
		tTrace\iType=1
		tTrace\iStepsNeeded=tTrace\iAbsDeltaY
		tTrace\iIncOne=2*tTrace\iAbsDeltaX
		tTrace\iIncTwo=2*(tTrace\iAbsDeltaX-tTrace\iAbsDeltaY)
		tTrace\iBuffer=tTrace\iIncOne-tTrace\iAbsDeltaY
		tTrace\iXY=iX1
		EndIf
	tTrace\iCurrentStep=0
	Return tTrace.tBresenhemInfo
	End Function
	
;Returns the number of steps needed to complete the trace!
Function TraceStepsNeeded(tTrace.tBresenhemInfo) ;[Inline]
	If tTrace.tBresenhemInfo<>Null
		Return tTrace\iStepsNeeded
		EndIf
	End Function

;Will trace one along the given line, calculates the next coord towards the end coords
Function ProcessTrace(tTrace.tBresenhemInfo) ;[Inline]
	If tTrace.tBresenhemInfo<>Null
		Local bDone=False
		If tTrace\iCurrentStep>=tTrace\iStepsNeeded
			bDone=True
			EndIf
		tTrace\iCurrentStep=tTrace\iCurrentStep+1
		If tTrace\iType=0
			If tTrace\iBuffer<0
				tTrace\iBuffer=tTrace\iBuffer+tTrace\iIncOne
				Else
				tTrace\iBuffer=tTrace\iBuffer+tTrace\iIncTwo
				tTrace\iXY=tTrace\iXY+tTrace\iIncY
				EndIf
			tTrace\iX=tTrace\iX1+(tTrace\iCurrentStep*tTrace\iIncX)
			tTrace\iY=tTrace\iXY
			Else				
			If tTrace\iBuffer<0
				tTrace\iBuffer=tTrace\iBuffer+tTrace\iIncOne
				Else
				tTrace\iBuffer=tTrace\iBuffer+tTrace\iIncTwo
				tTrace\iXY=tTrace\iXY+tTrace\iIncX
				EndIf
			tTrace\iX=tTrace\iXY	
			tTrace\iY=tTrace\iY1+(tTrace\iCurrentStep*tTrace\iIncY)
			EndIf			
		Return bDone
		Else
		Return True
		EndIf
	End Function


;Return the current location in the trace!	
Function TraceXPos(tTrace.tBresenhemInfo) ;[Inline]	
	If tTrace.tBresenhemInfo<>Null
		Return tTrace\iX
		EndIf
	End Function 	
Function TraceYPos(tTrace.tBresenhemInfo) ;[Inline]
	If tTrace.tBresenhemInfo<>Null
		Return tTrace\iY
		EndIf
	End Function	
	
;Free used resources - can be used to stop a trace early :)
Function KillTrace(tTrace.tBresenhemInfo)	
	If tTrace.tBresenhemInfo<>Null
		Delete tTrace.tBresenhemInfo
		tTrace.tBresenhemInfo=Null
		EndIf
	End Function
	
;###################################################
;A example of using the functions above ...

Graphics 640,480,16,1
SetBuffer FrontBuffer()

SeedRnd(MilliSecs())

Const TraceAmount=50

Dim Traces.tBresenhemInfo(TraceAmount)
Dim TraceCounts(TraceAmount)
For N=0 To TraceAmount-1
	Traces.tBresenhemInfo(N)=SpawnTrace(Rand(GraphicsWidth()),Rand(GraphicsHeight()-17)+17,Rand(GraphicsWidth()),Rand(GraphicsHeight()-17)+17)
	TraceCounts(N)=TraceStepsNeeded(Traces.tBresenhemInfo(N))
	
	Color 255,0,0										;Big red rect at the start of a trace
	Rect Traces(N)\iX1-1,Traces(N)\iY1-1,3,3,False	
	Rect Traces(N)\iX1-2,Traces(N)\iY1-2,5,5,False
	Color 0,255,0										;Big green rect at the end of the trace
	Rect Traces(N)\iX2-1,Traces(N)\iY2-1,3,3,False
	
	Next



;Just to slow down the drawing of the line
T=CreateTimer(30)

iLoop=0			;Will count the steps in the line drawing also
Repeat

	VWait						;Comment out to see the speed :-)
	WaitTimer(T)				;Comment out to see the speed :-)
	SetBuffer FrontBuffer()

	bDone=True
	For tTrace.tBresenhemInfo =  Each tBresenhemInfo
		If ProcessTrace(tTrace.tBresenhemInfo)=False	
			bDone=False
			Color 128,128,128
			Plot tTrace\iX,tTrace\iY			
			Else
			KillTrace(tTrace.tBresenhemInfo)
			EndIf
		Next

	Color 0,0,0
	Rect 0,0,GraphicsWidth(),16
	Color 255,255,255
	If bDone=True
		Text 0,0,"Lines all drawn [biggest was "+iLoop+" steps]"
		VWait
		Else
		iLoop=iLoop+1	
		Text 0,0,"Processing step ("+iLoop+")"
		EndIf

	Until KeyDown(1) 
End	
