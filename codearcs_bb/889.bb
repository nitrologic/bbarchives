; ID: 889
; Author: Techlord
; Date: 2004-01-16 04:45:59
; Title: Project PLASMA FPS 2004: Stack.bb
; Description: General Purpose FILO Stack

;============================
;STACK (FILO)
;============================
Type stack
	Field pointer%
	Field size%
	Field bank%
End Type

Function stackStop()
	For this.stack=Each stack
		stackDelete(this)
	Next
End Function

Function stackNew.stack()
	this.stack=New stack
	this\pointer%=0
	this\size%=0
	this\bank%=0
	Return this
End Function

Function stackDelete(this.stack)
	FreeBank this\bank%
	Delete this
End Function

Function stackManager()
	For this.stack=Each stack
	Next
End Function

Function stackCreate.stack(size%)
	this.stack=stacknew()
	this\size%=size%
	this\bank=CreateBank(4*this\size%)
	Return this
End Function

Function stackIndexCreate.stack(size%)
	this.stack=stackCreate(size%)
	For loop = size% To 1 Step -1
		stackPush(this,loop)
	Next		
	Return this
End Function

Function stackPush%(this.stack,value%)
	PokeInt(this\bank%,this\pointer%,value%)
	this\pointer%=this\pointer%+4
End Function

Function stackPop%(this.stack)
	this\pointer%=this\pointer%-4
	Return PeekInt(this\bank%,this\pointer%)
End Function
