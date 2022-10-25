; ID: 2268
; Author: JoshK
; Date: 2008-06-11 17:23:13
; Title: Directory Factory
; Description: Extendable class for reading the contents of a directory

Strict

Import brl.filesystem
Import brl.HTTPStream
Import brl.linkedlist

Private
Global directory_factories:TDirectoryFactory
Public

Function ReadDirectory:TList(path$)
	Local factory:TDirectoryFactory=directory_factories
	Local proto$
	Local i=path.Find( "::",0 )
	If i>-1
		proto$=path[..i].ToLower()
		path$=path[i+2..]
	EndIf
	While factory
		Local list:TList=factory.Read(proto,path)
		If list Return list
		factory=factory._succ
	Wend
EndFunction

Type TDirectoryFactory
	Field _succ:TDirectoryFactory
	
	Method New()
		_succ=directory_factories
		directory_factories=Self
	End Method
	
	Function Read:TList(proto$,path$)
		If proto<>"" Return
		path=RealPath(path)
		Local list:TList
		Local d=ReadDir(path)
		If Not d Return
		list=New TList
		Local file$
		Repeat
			file=NextFile(d)
			If Not file Exit
			list.addfirst(file)
		Forever
		CloseDir d
		Return list
	EndFunction
	
EndType

New TDirectoryFactory

Type THTTPDirectoryFactory Extends TDirectoryFactory
	
	Function Read:TList(proto$,path$)
		If proto<>"http" Return
		Local stream:TStream=ReadStream(proto+"::"+path)
		If Not stream Return
		Local s$
		While Not stream.Eof()
			s:+stream.ReadLine()+"~n"
		Wend
		Return ExtractHTMLLinks(s)
	EndFunction
	
	Function ExtractHTMLLinks:TList(html$)
		Local list:TList=New TList
		html=html.tolower()
		Local sarr$[]
		sarr=html.split("href=")
		For Local n=1 To sarr.length-1
			Local sarr2$[]
			sarr[n]=sarr[n].Trim()
			sarr2=sarr[n].split("~q")
			If sarr2.length>1
				If Not sarr2[1].contains("?")
					list.addfirst(sarr2[1])
					'Print sarr2[1]
				EndIf
			EndIf
		Next
		Return list
	EndFunction
	
EndType

New THTTPDirectoryFactory
