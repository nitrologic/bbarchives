; ID: 2351
; Author: Plash
; Date: 2008-10-31 19:36:02
; Title: ListAppendList and InsertListBeforeLink
; Description: More extra list functions - adding lists together

Function ListAppendList:Int(dest:TList, from:TList)
	Local copy:TList
	If dest = Null Or from = Null Or from.Count() = 0 Then Return False
	copy = from.Copy()
	InsertListBeforeLink(copy, dest._head)
	Return True
End Function

Function InsertListBeforeLink(thislist:TList, beforethis:TLink)
	Local this:TLink = thislist.FirstLink(), last:TLink = thislist.LastLink()
	'this._succ = beforethis
	this._pred = beforethis._pred
	this._pred._succ = this
	last._succ = beforethis
	beforethis._pred = last
End Function
