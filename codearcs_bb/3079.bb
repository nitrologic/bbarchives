; ID: 3079
; Author: Madk
; Date: 2013-09-23 12:30:50
; Title: Pick random element from list or array
; Description: Convenience functions for getting random things

' 	--+-----------------------------------------------------------------------------------------+--
'	  | This code was originally written by Sophie Kirschner (meapineapple@gmail.com) and it is |  
' 	  | released as public domain. Please do not interpret that as liberty to claim credit that |  
' 	  | is not yours, or to sell this code when it could otherwise be obtained for free because |  
'	  |                    that would be a really shitty thing of you to do.                    |
' 	--+-----------------------------------------------------------------------------------------+--

SuperStrict

Import brl.random
Import brl.linkedlist

Function RandFromArray:Object(array:Object[])
	Assert array
	Return array[Rand(0,array.length-1)]
End Function
Function RandFromList:Object(list:TList)
	Assert list
	Return list.valueatindex(Rand(0,list.count()-1))
End Function
Function RandLinkFromList:TLink(list:TList)
	Assert list
	Local link:TLink=list._head._succ,index%=Rand(0,list.count()-1)
	While link<>list._head
		If index=0 Then Return link
		link=link._succ;index:-1
	Wend
	Return Null
End Function

' Example code

Rem

SeedRnd MilliSecs()

Local testarray$[]=["a","b","c","d","e","f"]
Local testlist:TList=ListFromArray(testarray)
Print String(RandFromArray(testarray))
Print String(RandFromList(testlist))
Print String(RandLinkFromList(testlist)._value)

EndRem
