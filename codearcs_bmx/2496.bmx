; ID: 2496
; Author: Warpy
; Date: 2009-06-03 18:31:52
; Title: Iterate over any object!
; Description: iterate over a collection, after it's been cast to Object

Function iterate:iterator(o:Object)
	i:iterator=New iterator
	i.o=o
	Return i
End Function

Type iterator
	Field o:Object
	
	Method objectenumerator:iterenum()
		tt:TTypeId=TTypeId.ForObject(o)
		If tt.extendstype(ArrayTypeId)
			ae:arriterenum=New arriterenum
			ae.tt=tt
			ae.o=o
			ae.length=tt.arraylength(o)
			Return ae
		Else
			oe:objiterenum=New objiterenum
			m:TMethod=tt.findmethod("objectenumerator")
			If Not m Return
			oe.enum:Object=m.invoke(o)
			oe.hn:TMethod=TTypeId.ForObject(oe.enum).findmethod("hasnext")
			oe.no:TMethod=TTypeId.ForObject(oe.enum).findmethod("nextobject")
			Return oe
		EndIf
	End Method
End Type

Type iterenum
	Method hasnext() Abstract
	Method nextobject:Object() Abstract
End Type

Type objiterenum Extends iterenum
	Field enum:Object
	Field hn:TMethod
	Field no:TMethod
	
	Method hasnext()
		Return Int(String(hn.invoke(enum)))
	End Method
	
	Method nextobject:Object()
		Return no.invoke(enum)
	End Method
End Type

Type arriterenum Extends iterenum
	Field o:Object,tt:TTypeId
	Field i,length
	
	Method hasnext()
		Return i<length
	End Method
	Method nextobject:Object()
		o2:Object=tt.getarrayelement(o,i)
		i:+1
		Return o2
	End Method
End Type

l:TList=New TList
l.addlast "whoah"
l.addlast "bing"

Local ss$[]=["hello","there"]

l2:TList=New TList
l2.addlast l
l2.addlast ss
For o:Object=EachIn l2
	For s$=EachIn iterate(o)
		Print s
	Next
Next
