; ID: 1807
; Author: dmaz
; Date: 2006-09-06 18:49:52
; Title: TListF
; Description: for blah = eachin list.From(start:TLink,howMany:int)

SuperStrict

Type TListF Extends TList
	Method From:TEnumerator( start:TLink, howMany:Int=-1 )
		Local e:TFromEnum = New TFromEnum
		e._link = start
		e._howMany = howMany
		Local enum:TEnumerator = New TEnumerator
		enum.enumerator = e
		Return enum
	End Method

	Method ReverseFrom:TEnumerator( start:TLink, howMany:Int=-1 )
		Local e:TReverseFromEnum = New TReverseFromEnum
		e._link = start
		e._howMany = howMany
		Local enum:TEnumerator = New TEnumerator
		enum.enumerator = e
		Return enum
	End Method
	
	Method LinksFrom:TEnumerator( start:TLink, howMany:Int=-1 )
		Local e:TLinksEnum = New TLinksEnum
		e._link = start
		e._howMany = howMany
		Local enum:TEnumerator = New TEnumerator
		enum.enumerator = e
		Return enum
	End Method

End Type

Type TEnumerator
	Field enumerator:TFromEnum

	Method ObjectEnumerator:TFromEnum()
		Return enumerator
	End Method
End Type

Type TFromEnum
	Field _link:TLink
	Field _howMany:Int

	Method HasNext:Int()
		Return (_link._value<>_link) And _howMany
	End Method

	Method NextObject:Object()
		Local value:Object=_link._value
		Assert value<>_link
		_link=_link._succ
		_howMany:-1
		Return value
	End Method
End Type

Type TReverseFromEnum Extends TFromEnum
	Method HasNext:Int()
		Return (_link._value<>_link) And _howMany
	End Method

	Method NextObject:Object()
		Local value:Object=_link._value
		Assert value<>_link
		_link=_link._pred
		_howMany:-1
		Return value
	End Method
End Type


Type TLinksEnum Extends TFromEnum
	Method NextObject:Object()
		Local rtn:Object=_link
		Assert _link._value<>_link
		_link=_link._succ
		_howMany:-1
		Return rtn
	End Method
End Type




' TEST PROGRAM
Type t
	Global gid:Int	= 0
	Global list:TListF = New TListF
	
	Field id:Int
	Field link:TLink
	
	Method New()
		id = gid
		gid :+ 1
		link = list.AddLast(Self)
	End Method
	
End Type

Local start:TLink

'create 16 of type t and record one in 'start'
For Local i:Int=0 To 15
	Local tmp:t = New t
	If tmp.id = 5 Then start=tmp.link
Next

Print "~nthe whole list the normal way"
For Local i:t = EachIn t.list
	Print i.id
Next

Print "~n5 from the start TLink "
For Local i:t = EachIn t.list.From(start,5)
	Print ">"+i.id
Next

Print "~nall from the start TLink "
For Local i:t = EachIn t.list.From(start,-1)
	Print ">"+i.id
Next

Print "~nall link from the begining"
For Local i:TLink = EachIn t.list.LinksFrom(t.list.FirstLink())
	Print ">"+i.ToString()+": "+t(i._value).id
Next

't.list.Clear
Print "~nreverse from last TLink"
For Local i:t = EachIn t.list.ReverseFrom(t.list.LastLink())
	Print ">"+i.id
Next
