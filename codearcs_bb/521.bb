; ID: 521
; Author: Techlord
; Date: 2002-12-05 12:35:32
; Title: Project PLASMA FPS 2004: Clock.bb
; Description: A simple 'count-down'  timing object

;============================
;CLOCK MODULE
;============================
Const CLOCK_MAX%=1024
Dim clockId.clock(CLOCK_MAX%)
Global clockIndex.stack=stackIndexCreate(CLOCK_MAX%)

Type clock
	Field id%
	Field typeid%
	Field value%
	Field count%
	Field state%
End Type

Function clockStop()
	For this.clock=Each clock
		clockDelete(this)
	Next
End Function

Function clockNew.clock()
	this.clock=New clock
	this\id%=0
	this\typeid%=0
	this\value%=0
	this\count%=0
	this\state%=0
	this\id%=StackPop(clockIndex.stack)
	clockId(this\id)=this
	Return this
End Function

Function clockDelete(this.clock)
	clockId(this\id)=Null
	StackPush(clockIndex.stack,this\id%)
	Delete this
End Function

Function clockUpdate()
	For this.clock=Each clock
		Select this\state%
			Case 1
				If this\count%=0 this\count%=this\value%
				this\count%=this\count%-1
		End Select
	Next
End Function

Function clockRead.clock(file)
	this.clock=New clock
	this\id%=ReadInt(file)
	this\typeid%=ReadInt(file)
	this\value%=ReadInt(file)
	this\count%=ReadInt(file)
	this\state%=ReadInt(file)
	Return this
End Function

Function clockWrite(file,this.clock)
	WriteInt(file,this\id%)
	WriteInt(file,this\typeid%)
	WriteInt(file,this\value%)
	WriteInt(file,this\count%)
	WriteInt(file,this\state%)
End Function

Function clockSave(filename$="Default")
	file=WriteFile(filename$+".clock")
	For this.clock= Each clock
		clockWrite(file,this)
	Next
	CloseFile(file)
End Function

Function clockOpen(filename$="Default")
	file=ReadFile(filename+".clock")
	Repeat
		clockRead(file)
	Until Eof(file)
	CloseFile(file)
End Function

Function clockCopy.clock(this.clock)
	copy.clock=New clock
	copy\id%=this\id%
	copy\typeid%=this\typeid%
	copy\value%=this\value%
	copy\count%=this\count%
	copy\state%=this\state%
	Return copy
End Function

Function clockMimic(mimic.clock,this.clock)
	mimic\id%=this\id%
	mimic\typeid%=this\typeid%
	mimic\value%=this\value%
	mimic\count%=this\count%
	mimic\state%=this\state%
End Function

Function clockCreate.clock(typeid%,value%,count%,state%)
	this.clock=clockNew()
	this\typeid%=typeid%
	this\value%=value%
	this\count%=count%
	this\state%=state%
	Return this
End Function

Function clockSet(this.clock,typeid%,value%,state%=1)
	this\typeid%=typeid%
	this\value%=value%
	this\count%=this\value%
	this\state%=state%
End Function

Function clockReset(this.clock)
	this\count%=this\value%
End Function
