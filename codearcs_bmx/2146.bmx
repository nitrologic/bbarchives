; ID: 2146
; Author: JoshK
; Date: 2007-11-06 21:20:44
; Title: LinkObjects Module
; Description: Retrieve an object related to a pair of objects

Import brl.map

Rem
bbdoc:
EndRem
Module leadwerks.LinkObjects

Type TObjectLink
	Global map:TMap=New TMap

	Field o1:Object
	Field o2:Object

	Method Compare:Int(with:Object)
		p2:TObjectLink=TObjectLink(with)
		If o1.compare(p2.o1)=0
			If o2.compare(p2.o2)>0 Return 1
			If o2.compare(p2.o2)<0 Return -1
		Else
			If o1.compare(p2.o1)>0 Return 1
			If o1.compare(p2.o1)<0 Return -1
		EndIf
		Return 0
	EndMethod
	
	Method Destroy()
		o1=Null
		o2=Null
	EndMethod
	
	Method Delete()
		Destroy()
	EndMethod
	
	Function Create:TObjectLink(o1:Object,o2:Object)
		If o1=o2 Return Null
		p:TObjectLink=New TObjectLink
		p.o1=o1
		p.o2=o2
		If p.o1.compare(o2)<0
			o3:Object=p.o2
			p.o2=p.o1
			p.o1=o3
		EndIf
		Return p
	EndFunction
	
EndType

Rem
bbdoc:
EndRem
Function ClearObjectLinks(o:Object)
	For ol:TObjectLink=EachIn TObjectLink.map.keys()
		If ol.o1=o Or ol.o2=o TObjectLink.map.remove ol
	Next
EndFunction

Rem
bbdoc:
EndRem
Function LinkObjects(o1:Object,o2:Object,link:Object)
	p:TObjectLink=TObjectLink.Create(o1,o2)
	TObjectLink.map.Insert p,link
EndFunction

Rem
bbdoc:
EndRem
Function ObjectsLink:Object(o1:Object,o2:Object)
	p:TObjectLink=TObjectLink.Create(o1,o2)
	o:Object=TObjectLink.map.ValueForKey(p)
	Return o
EndFunction
