; ID: 1582
; Author: Perturbatio
; Date: 2005-12-29 05:32:25
; Title: BMax CGI module
; Description: A simple CGI module for creating web based CGI apps.

Strict
Rem
bbdoc: Pert.CGI
End Rem
Module Pert.CGI
ModuleInfo "Module: Perturbatio's CGI Mod"
ModuleInfo "Version: 1.01"
ModuleInfo "Author: Kris Kelly (Perturbatio)"
ModuleInfo "License: Public Domain"
ModuleInfo "DocGroup: Pert"

Import BRL.Basic
Import BRL.System
Import BRL.Retro

Rem
bbdoc: HexToInt converts a hex value to integer
about: the value passed must be a string containing a valid Hex value ($ sign is optional)
EndRem
Function HexToInt:Int( HexStr:String )
	If HexStr.Find("$") <> 0 Then HexStr = "$" + HexStr$
	Return Int(HexStr)
End Function

Rem
bbdoc: IntToHex converts an integer to a hex string of the specified length
about: the value passed must be an integer, the returned value is a string  ($ sign is optional)
EndRem
Function IntToHexString:String(val:Int, chars:Int = 2, HexPrepend:String = "") 'BMax uses $ for the beginning of a hex string
	Local Result:String = Hex(val)
	Return HexPrepend + result[result.length-chars..]
End Function

Rem
bbdoc: THashEntry Type
about: The THashEntry Type is used in a THash and stores the keynames and values <br> Fields: Key:String  and Value:String
EndRem
Type THashEntry
	Field Key : String
	Field Value : String
End Type

Rem
bbdoc: THash Type
about: The THash Type is an extension of TList and so can be treated as such (with foreach, etc).
EndRem
Type THash Extends TList
	Rem
	bbdoc: Method Add
	about: returns a TLink pointing to a THashEntry<br> pass the Key and Value with optional AutoReplace (True/False)<br>
			NOTE:Autoreplace has not yet been implemented
	EndRem
	Method Add:TLink(Key:String, Value:String, AutoReplace=False)
		'need to add check to see if key already exists, 
		'if it exists and AutoReplace=True
		'then replace it, else exit the method 
		'returning the existing link
		Local tempEntry:THashEntry = New THashEntry
			tempEntry.Key = Key
			tempEntry.Value = Value
		
		Return InsertBeforeLink(tempEntry,_head )
	End Method

	Rem
	bbdoc: Method Get
	about: Returns the Value as a string, of the supplied Keyname (String)
	EndRem
	Method Get:String(Key : String)
		Local result : String = ""
		Local he : THashEntry
			For he = EachIn Self
				If he.Key.toUpper() = Key.toUpper() Then
					result = he.Value
					Exit
				EndIf
			Next
		Return result
	End Method
	
End Type


Rem
bbdoc: TCGI Type
about: The TCGI Type is used to get environment variables including the query string which is automatically split and placed in the field QueryHash which is of type THash
EndRem
Type TCGI
	'Environment Variables
	Field AUTH_TYPE : String
	Field CONTENT_TYPE : String
	Field CONTENT_LENGTH : String
	Field GATEWAY_INTERFACE : String
	Field HTTP_ACCEPT : String
	Field HTTP_ACCEPT_CHARSET : String
	Field HTTP_ACCEPT_LANGUAGE : String
	Field HTTP_COOKIE : String
	Field HTTP_FROM : String
	Field HTTP_HOST : String
	Field HTTP_RANGE : String
	Field HTTP_REFERER : String
	Field HTTP_USER_AGENT : String
	Field PATH_INFO : String
	Field PATH_TRANSLATED : String
	Field QUERY_STRING : String
	Field REMOTE_ADDR : String
	Field REMOTE_HOST : String
	Field REMOTE_IDENT : String
	Field REMOTE_USER : String
	Field REQUEST_METHOD : String
	Field SCRIPT_NAME : String
	Field SCRIPT_FILENAME : String
	Field SERVER_NAME : String
	Field SERVER_PORT : String
	Field SERVER_PROTOCOL : String
	Field SERVER_SOFTWARE : String
	Field QueryHash : THash
	Field Timeout:Int = 10000
	
	Rem
	bbdoc: Method FillHash()
	about: Fills the QueryHash field with the QUERY_STRING Keys and Values
	EndRem
	Method FillHash()
		Local s : String
		Local pos : Int
		Local tempEntry : THashEntry
		Local starttime = MilliSecs()
			
		s = QUERY_STRING
		
		While (Len(s)>0)
			pos = s.Find("&")'find the first occurance of the delimiter
			If Pos > -1 Then
				tempEntry:THashEntry = SplitQuery(s[0..Pos])
				QueryHash.Add(tempEntry.Key, tempEntry.Value)'add the first variable, excluding the delimiter
				s = s[Pos+1..]'remove the first variable and the & delimiter

			Else
				tempEntry:THashEntry = SplitQuery(s)
				QueryHash.Add(tempEntry.Key, tempEntry.Value)
				s = ""
			EndIf	
			
			If MilliSecs()-starttime>Timeout Then 
				PrintHTMLHeader()
				Print "timeout while filling query hash~n"
				Print s+"~n~n"
				Print Pos+"~n~n"
				Return
			EndIf
	
		Wend
   
	End Method
	
	Rem
	bbdoc: Method SplitQuery
	about: Returns a THashEntry from the supplied string (i.e. "name=bob")
	EndRem
	Method SplitQuery:THashEntry(Query:String)
		Local tempEntry:THashEntry = New THashEntry
		Local pos : Int

			pos = query.Find("=")'find the first occurance of the delimiter
			If Pos > -1 Then
				tempEntry.Key = DecodeString(Query[0..Pos])
				tempEntry.Value = DecodeString(Query[Pos+1..])
			EndIf	
		
		Return tempEntry
	End Method
	
	Rem
	bbdoc: Method PrintHTMLHeader
	about: Prints the standard HTML header, here for ease of use
	EndRem
	Function PrintHTMLHeader()
		Print "Content-type:  text/html~n~n"
	End Function
	
	Rem
	bbdoc: Method EncodeString
	about: Encodes a URL string and returns the result
	EndRem
	Function EncodeString:String(value:String, EncodeUnreserved:Int = False, UsePlusForSpace:Int = True)
		Local ReservedChars:String = "!*'();:@&=+$,/?%#[]~r~n"  'added space, newline and carriage returns
		Local rc:Int
		Local urc:Int
		Local s:Int
		Local result:String
	
		For s = 0 To value.length - 1
			If ReservedChars.Find(value[s..s + 1]) > -1 Then
				result:+ "%"+ IntToHexString(Asc(value[s..s + 1]))
				Continue
			ElseIf value[s..s+1] = " " Then
				If UsePlusForSpace Then result:+"+" Else result:+"%20"
				Continue
			ElseIf EncodeUnreserved Then
					result:+ "%" + IntToHexString(Asc(value[s..s + 1]))
				Continue
			EndIf
			result:+ value[s..s + 1]
		Next
	
		Return result
	End Function

	Rem
	bbdoc: Method DecodeString
	about: Decodes a URL encoded string and returns the result
	EndRem
	Function DecodeString:String(EncStr:String)
		Local Pos : Int = 0
		Local HexVal : String
		Local Result : String
		Local starttime:Int = MilliSecs()
	
		While Pos<Len(EncStr)
			If EncStr[Pos..Pos+1] = "%" Then
				HexVal = EncStr[Pos+1..Pos+3]
				Result :+ Chr(HexToInt(HexVal))
				Pos:+3
			ElseIf EncStr[Pos..Pos+1] = "+" Then
				Result :+ " "
				Pos:+1
			Else
				Result :+ EncStr[Pos..Pos+1]
				Pos:+1	
			EndIf
		Wend
		
		Return Result
	End Function
	
	Rem
	bbdoc: Method Create
	about: invoke to create an instance of the TCGI type
	EndRem
	Function Create:TCGI()
		Local tempCGI:TCGI = New TCGI
			tempCGI.AUTH_TYPE = getenv_("AUTH_TYPE")
			tempCGI.CONTENT_TYPE = getenv_("CONTENT_TYPE")
			tempCGI.CONTENT_LENGTH = getenv_("CONTENT_LENGTH")
			tempCGI.GATEWAY_INTERFACE = getenv_("GATEWAY_INTERFACE")
			tempCGI.HTTP_ACCEPT = getenv_("HTTP_ACCEPT")
			
			tempCGI.HTTP_ACCEPT_CHARSET = getenv_("HTTP_ACCEPT_CHARSET")
			tempCGI.HTTP_ACCEPT_LANGUAGE = getenv_("HTTP_ACCEPT_LANGUAGE")
			
			tempCGI.HTTP_COOKIE = getenv_("HTTP_COOKIE")
			tempCGI.HTTP_FROM = getenv_("HTTP_FROM")
			tempCGI.HTTP_HOST = getenv_("HTTP_HOST")
			tempCGI.HTTP_RANGE = getenv_("HTTP_RANGE")
			tempCGI.HTTP_REFERER = getenv_(".HTTP_REFERER")
			tempCGI.HTTP_USER_AGENT = getenv_("HTTP_USER_AGENT")
			tempCGI.PATH_INFO = getenv_("PATH_INFO")
			tempCGI.PATH_TRANSLATED = getenv_("PATH_TRANSLATED")
			tempCGI.QUERY_STRING = getenv_("QUERY_STRING")
			tempCGI.REMOTE_ADDR = getenv_("REMOTE_ADDR")
			tempCGI.REMOTE_HOST = getenv_("REMOTE_HOST")
			tempCGI.REMOTE_IDENT = getenv_("REMOTE_IDENT")
			tempCGI.REMOTE_USER = getenv_("REMOTE_USER")
			tempCGI.REQUEST_METHOD = getenv_("REQUEST_METHOD")
			tempCGI.SCRIPT_NAME = getenv_("SCRIPT_NAME")
			tempCGI.SCRIPT_FILENAME = getenv_("SCRIPT_FILENAME")
			tempCGI.SERVER_NAME = getenv_("SERVER_NAME")
			tempCGI.SERVER_PORT = getenv_("SERVER_PORT")
			tempCGI.SERVER_PROTOCOL = getenv_("SERVER_PROTOCOL")
			tempCGI.SERVER_SOFTWARE = getenv_("SERVER_SOFTWARE")
			
			tempCGI.QueryHash:THash = New THash
			tempCGI.FillHash()
			Return tempCGI
	End Function
	
	Rem
	bbdoc: Free an instance of TCGI
	about: pass an instance of TCGI to Free to remove it from memory
	EndRem
	Function Free(CGIInstance:TCGI)
		CGIInstance.QueryHash.Clear()
		CGIInstance.QueryHash = Null
		CGIInstance = Null
	End Function
End Type
