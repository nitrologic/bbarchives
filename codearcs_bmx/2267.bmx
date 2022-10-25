; ID: 2267
; Author: JoshK
; Date: 2008-06-11 16:58:59
; Title: Modified HTTPStream
; Description: Returns Null if the network file does not exist

Strict

Module BRL.HTTPStream

ModuleInfo "Version: 1.05"
ModuleInfo "Author: Mark Sibly"
ModuleInfo "License: Blitz Shared Source Code"
ModuleInfo "Copyright: Blitz Research Ltd"
ModuleInfo "Modserver: BRL"

ModuleInfo "History: 1.04 Release"
ModuleInfo "1.03: Added optional check for existing file"
ModuleInfo "1.04: Improved checking"
ModuleInfo "1.05: Went back to my way of checking for 401 and 404 errors and added optional username/password"
ModuleInfo "1.06: Did the 200 check the right way!"

Import BRL.SocketStream

'---------------------------------------------

Import "base64.bmx"

Global HTTPStreamFactoryCheckIfExists:Int
Global HTTPStreamFactoryUsername$
Global HTTPStreamFactoryPassword$

'---------------------------------------------


Type THTTPStreamFactory Extends TStreamFactory

	Method CreateStream:TStream( url:Object,proto$,path$,readable,writeable )
		If proto="http"
		
			Local i=path.Find( "/",0 ),server$,file$
			If i<>-1
				server=path[..i]
				file=path[i..]
			Else
				server=path
				file="/"
			EndIf
			
			Local stream:TStream=TSocketStream.CreateClient( server,80 )
			If Not stream Return
			
			stream.WriteLine "GET "+file+" HTTP/1.0"
			stream.WriteLine "Host: "+server
			
			
			'---------------------------------------------
			
			If HTTPStreamFactoryUsername<>"" And HTTPStreamFactoryPassword<>""
				Local s$=HTTPStreamFactoryUsername+":"+HTTPStreamFactoryPassword
				Local cs:Byte Ptr
				cs=s.tocstring()
				s=TBase64.encode(cs,s.length)
				MemFree cs
				stream.WriteLine "Authorization: Basic "+s
			EndIf
			
			'---------------------------------------------
			
			
			stream.WriteLine ""
			
			
			'---------------------------------------------
			
			If HTTPStreamFactoryCheckIfExists
				If Not s.contains("200")
					stream.close()
					Return
				EndIf
			EndIf
			
			'---------------------------------------------
			
			
			While Not Eof( stream )
				If Not stream.ReadLine() Exit				
			Wend

			Return stream
		EndIf
	End Method

End Type

New THTTPStreamFactory
