; ID: 504
; Author: Techlord
; Date: 2002-11-23 19:38:29
; Title: Project PLASMA FPS 2004: Action.bb
; Description: Action Module

;============================
;ACTION
;============================
Const ACTION_MAX=1024
Dim actionId.action(ACTION_MAX)

Type action
	Field id%
	Field typeid%
	Field funct%
	Field i%[4]
	Field f#[4]
	Field s$[4]
End Type

Function actionStop()
	For this.action=Each action
		actionDelete(this)
	Next
End Function

Function actionNew.action()
	this.action=New action
	this\id%=id%
	this\typeid%=0
	this\funct%=0
	Return this
End Function

Function actionDelete(this.action)
	Delete this
End Function

Function actionCopy.action(this.action)
	copy.action=New action
	copy\id%=this\id%
	copy\typeid%=this\typeid%
	copy\funct%=this\funct%
	For loop=1 To 4:copy\i%[loop]=this\i%[loop]:Next
	For loop=1 To 4:copy\f#[loop]=this\f#[loop]:Next
	For loop=1 To 4:copy\s$[loop]=this\s$[loop]:Next
	Return copy
End Function

Function actionMimic(mimic.action,this.action)
	mimic\id%=this\id%
	mimic\typeid%=this\typeid%
	mimic\funct%=this\funct%
	For loop=1 To 4:mimic\i%[loop]=this\i%[loop]:Next
	For loop=1 To 4:mimic\f#[loop]=this\f#[loop]:Next
	For loop=1 To 4:mimic\s$[loop]=this\s$[loop]:Next
End Function

Function actionCreate.action(id%,typeid%,funct%,i1%,i2%,i3%,i4%,f1#,f2#,f3#,f4#,s1$,s2$,s3$,s4$)
	this.action=actionNew()
	this\id%=id%
	this\typeid%=typeid%
	this\funct%=funct%
	this\i%[1]=i1%
	this\i%[2]=i2%
	this\i%[3]=i3%
	this\i%[4]=i4%
	this\f#[1]=f1#
	this\f#[2]=f2#
	this\f#[3]=f3#
	this\f#[4]=f4#
	this\s$[1]=s1$
	this\s$[2]=s2$
	this\s$[3]=s3$
	this\s$[4]=s4$
	Return this
End Function

Function actionCall(this.action)
	Select this\funct%
		Case 1 ;funct1(this\i%[1],this\i%[2])
		Case 2 ;funct2(this\i%[1],this\i%[2])
		;
		;
		;
	End Select
End Function
