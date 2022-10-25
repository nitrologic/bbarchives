; ID: 908
; Author: Techlord
; Date: 2004-02-03 13:06:58
; Title: Project PLASMA FPS 2004: Vector.bb
; Description: Vector Code Module

;============================
;VECTOR
;============================
Type vector
	Field x#
	Field y#
	Field z#
End Type

Function vectorStop()
	For this.vector=Each vector
		vectorDelete(this)
	Next
End Function

Function vectorNew.vector()
	this.vector=New vector
	this\x#=0.0
	this\y#=0.0
	this\z#=0.0
	Return this
End Function

Function vectorDelete(this.vector)
	this\z#=0.0
	this\y#=0.0
	this\x#=0.0
	Delete this
End Function

Function vectorManager()
	For this.vector=Each vector
	Next
End Function

Function vectorRead.vector(file)
	this.vector=New vector
	this\x#=ReadFloat(file)
	this\y#=ReadFloat(file)
	this\z#=ReadFloat(file)
	Return this
End Function

Function vectorWrite(file,this.vector)
	WriteFloat(file,this\x#)
	WriteFloat(file,this\y#)
	WriteFloat(file,this\z#)
End Function

Function vectorCopy.vector(this.vector)
	copy.vector=New vector
	copy\x#=this\x#
	copy\y#=this\y#
	copy\z#=this\z#
	Return copy
End Function

Function vectorMimic(mimic.vector,this.vector)
	mimic\x#=this\x#
	mimic\y#=this\y#
	mimic\z#=this\z#
End Function

Function vectorCreate.vector(x#,y#,z#)
	this.vector=vectorNew()
	this\x#=x#
	this\y#=y#
	this\z#=z#
	Return this
End Function

Function vectorSet(this.vector,x#,y#,z#)
	this\x#=x#
	this\y#=y#
	this\z#=z#
End Function
