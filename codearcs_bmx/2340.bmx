; ID: 2340
; Author: Warpy
; Date: 2008-10-20 22:46:10
; Title: Code Execution Timers
; Description: some functions to help time different parts of your program

'the idea is you call startwatch before you do whatever it is you're timing, then endwatch after it's done
'you can then display all your stopwatches each frame, or print out all your times, or whatever


Global stopwatches:TList,numwatches

Type stopwatch
	Field ms1,ms2,n,txt$
	
	Method New()
		stopwatches.addlast Self
		ms1=MilliSecs()
		n=numwatches
		numwatches:+1
	End Method
	
End Type

Function startwatch()
	New stopwatch
End Function

Function endwatch(txt$)
	sw:stopwatch=stopwatch(stopwatches.removelast())
	sw.ms2=MilliSecs()
	sw.txt=txt
	stopwatches.addfirst sw
End Function


'this is a good example if you want to display your times on the screen
'call this every frame
Function drawwatches()
	For sw:stopwatch=EachIn stopwatches
		diff=sw.ms2-sw.ms1
		If diff>1000/60
			SetColor 255,0,0
		Else
			SetColor 255,255,255
		EndIf
		DrawRect 0,sw.n*22,diff*10,20
		DrawText sw.txt,250,sw.n*22
	Next

	stopwatches=New TList
	numwatches=0
End Function

'this will print the time on each stopwatch
Function printwatches()
	For sw:stopwatch=EachIn stopwatches
		Print sw.txt+": "+String(sw.ms2-sw.ms1)
	Next

	stopwatches=New TList
	numwatches=0
End Function
