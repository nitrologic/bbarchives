; ID: 2349
; Author: Plash
; Date: 2008-10-30 18:43:46
; Title: MoveObjectUp and MoveObjectDown
; Description: Two functions for moving objects in a list

Function MoveObjectUp:Int(list:TList, obj:Object)
  Local link:TLink, prev:TLink
	
	If list.Count() = 0 Then Return False
	
	link = list.FindLink(obj)
	If link = Null Then Return False
	
	prev = link.PrevLink()
	If prev = Null Then Return False ' Already at the top of the list
	
	link = Null
	list.Remove(obj)
	list.InsertBeforeLink(obj, prev)
	
	Return True
	
End Function

Function MoveObjectDown:Int(list:TList, obj:Object)
  Local link:TLink, nextlink:TLink
	
	If list.Count() = 0 Then Return False
	
	link = list.FindLink(obj)
	If link = Null Then Return False
	
	nextlink = link.NextLink()
	If nextlink = Null Then Return False ' Already at the bottom of the list
	
	link = Null
	list.Remove(obj)
	list.InsertAfterLink(obj, nextlink)
	
	Return True
	
End Function
