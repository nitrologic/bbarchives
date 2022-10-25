; ID: 2066
; Author: grable
; Date: 2007-07-14 11:02:26
; Title: JSON Reader/Writer
; Description: Easy handling of JSON data

Rem
**********************************************************************************************************************************
* JSON Reader/Writer and generic handling
*
	
**********************************************************************************************************************************
* TJSONValue is the generic container for all JSON data types
*	
	Type TJSONValue
		' determines the type of value
		Field Class:Int 
		
		' returns value by index (only valid for arrays)
		Method GetByIndex:TJSONValue( index:Int)
		
		' returns value by name (only valid for objects)
		Method GetByName:TJSONValue( name:Object)
		
		Method SetByIndex( index:Int, value:TJSONValue)
		Method SetByName( name:Object, value:TJSONValue)
		
		' lookup value by string (either a number or a name, valid for arrays & objects)
		Method LookupValue:TJSONValue( value:Object)
		
		' returns properly indented source representation of JSON data
		Method ToSource:String( level:Int = 0)
		
		' returns JSON data as string
		Method ToString:String()
	EndType
	
	Type TJSONNumber Extends TJSONValue
		Field Value:Double
	EndType
	
	Type TJSONString Extends TJSONValue
		Field Value:String
	EndType
	
	Type TJSONBoolean Extends TJSONValue
		Field Value:Int
	EndType
	
	Type TJSONObject Extends TJSONValue
		Field Items:TMap ' holds actual fields
		Field List:TList ' holds field order 
	EndType
	
	Type TJSONArray Extends TJSONValue
		Field Items:TJSONValue[]
	EndType
	
	The methods are implemented in the various subclasses.
		
**********************************************************************************************************************************
* TJSON handles all reading/writing of json data, and allows for easy acces to elements via "paths"
*	
	Type TJSON
		' the root JSON value
		Field Root:TJSONValue
		
		' create a new JSON from any source
		Function Create:TJSON( source:Object)
	
		' read JSON data from a TJSONValue, TStream or String
		Method Read:TJSONValue( source:Object)
		
		' write JSON data to a TStream or a file (as String)
		Method Write( dest:Object)
		
		' parses a string into its JSON data representation
		Method ParseString:TJSONValue( s:Object)
		
		' lookup a JSON value at at specified path, returns NULL on failure
		Method Lookup:TJSONValue( path:String)
	
		' sets a JSON value at path to value, value can be a TJSONValue or JSON data as a string
		Method SetValue( path:String, value:Object)
		
		' returns the json value at specified
		Method GetValue:TJSONValue( path:String)	
		
		' returns blitz specific types from specified paths
		Method GetNumber:Double( path:String)
		Method GetString:String( path:String)
		Method GetBoolean:Int( path:String)
		
		' returns only these specific objects
		Method GetObject:TJSONObject( path:String)
		Method GetArray:TJSONArray( path:String)
	
		' get a blitz array from a JSON array or NULL on failure
		Method GetArrayInt:Int[]( path:String)
		Method GetArrayDouble:Double[]( path:String)
		Method GetArrayString:String[]( path:String)
	EndType
	
**********************************************************************************************************************************
* PATHS
*		
	identifiers are seperated with ".", and has special syntax for array indices
		
	example:
		"users.joe.age"		' direct access
		"users.joe.medals.0" 	' array index, arrays are 0 based
	
**********************************************************************************************************************************
* usage example:
*		
	Local json:TJSON = TJSON.Create( "{ string: 'abc', number: 1.0, boolean: true, object: { field: null }, array: [1,2,3] }")
		
	Print json.GetValue("string").ToString()			' prints "abc"
	Print json.GetValue("object.field").ToString()	' prints "null"
	Print json.GetValue("array").ToString()			' prints "[1,2,3]"
	Print json.GetValue("array.1").ToString()			' prints "2"
		
**********************************************************************************************************************************
* NOTES
*
	** most of the TJSON.GetXXX methods returns the JSON NULL type on failure if not specified
	** all identifiers are CASE SENSITIVE
	** the parser is as close as i could get it with my current understanding of JSON, please let me know if i missed something
	** see the bottom of this source file for more examples, or check out http://json.org/ for more info on JSON
	
***********************************************************	
* INFO
*
	author: grable
	email : grable0@gmail.com
	
EndRem

SuperStrict

Import BRL.LinkedList
Import BRL.Map


'
' JSON value classes
'
Const JSON_NULL:Int	= 1
Const JSON_OBJECT:Int	= 2
Const JSON_ARRAY:Int	= 3
Const JSON_STRING:Int	= 4
Const JSON_NUMBER:Int	= 5
Const JSON_BOOLEAN:Int	= 6


'
' used by TJSON / TJSONObject for identifier lookup
'
Type TJSONKey
	Field Value:String
	
	Method ToString:String()
		Return "~q" + Value + "~q"
	EndMethod
	
	Method Compare:Int( o:Object)
		Local key:TJSONKey = TJSONKey(o)
		If key Then Return Value.Compare( key.Value)		
		If String(o) Then Return Value.Compare( o)
		Return 1
	EndMethod
EndType


'
' JSON Value objects
'
Type TJSONValue Abstract
	Field Class:Int
	
	Method GetByIndex:TJSONValue( index:Int)
		Return Null
	EndMethod
	
	Method GetByName:TJSONValue( name:Object) 
		Return Null
	EndMethod	
	
	Method SetByIndex( index:Int, value:TJSONValue)
	EndMethod
	
	Method SetByName( name:Object, value:TJSONValue)
	EndMethod	
	
	Method LookupValue:TJSONValue( value:Object)
		Return Self
	EndMethod
	
	Method ToSource:String( level:Int = 0) Abstract
EndType

Type TJSONObject Extends TJSONValue
	Field Items:TMap = New TMap
	Field List:TList = New TList ' for keeping the order of fields
	
	Method New()
		Class = JSON_OBJECT
	EndMethod	

	Method ToString:String()
		Local s:String, lines:Int = 0
		If List.Count() <= 0 Then Return "{}"
		For Local o:TNode = EachIn List
			If lines > 0 Then s :+ ", "
			s :+ o._key.ToString() +": "
			Local jsv:TJSONValue = TJSONValue(o._value)
			If jsv.Class = JSON_STRING Then
				s :+ jsv.ToSource()
			Else
				s :+ jsv.ToString()
			EndIf
			lines :+ 1
		Next
		Return "{ "+ s +" }"
	EndMethod
	
	Method ToSource:String( level:Int = 0)
		Local s:String, lines:Int = 0
		If List.Count() <= 0 Then Return "{}"
		For Local o:TNode = EachIn List
			If lines > 0 Then s :+ ",~n" + RepeatString( "~t", level + 1)			
			s :+ o._key.ToString() +": "+ TJSONValue(o._value).ToSource( level + 1)
			lines :+ 1
		Next
		If lines > 1 Then Return "{~n"+ RepeatString( "~t", level + 1) + s + "~n" + RepeatString( "~t", level) + "}"
		Return "{ "+ s +" }"
	EndMethod		
	
	Method GetByName:TJSONValue( name:Object)
		Return TJSONValue( Items.ValueForKey( name))
	EndMethod
	
	Method SetByName( name:Object, value:TJSONValue)
		Local node:TNode
		If TJSONKey(name) Then
			Items.Insert( name, value)
			node = Items._FindNode( name)
			If Not List.Contains( node) Then List.AddLast( node)
		ElseIf String(name) Then
			Local s:String = String(name)
			If s.Length > 0 Then
				Items.Insert( s, value)
				node = Items._FindNode( s)
				If Not List.Contains( node) Then List.AddLast( node)
			EndIf
		EndIf
	EndMethod	
	
	Method LookupValue:TJSONValue( value:Object)
		If TJSONKey(value) Then
			Return GetByName( value)
		ElseIf String(value) Then
			If Not IsNumber( String(value)) Then Return GetByName( value)
		EndIf
	EndMethod
EndType

Type TJSONArray Extends TJSONValue
	Field Items:TJSONValue[]
	Field AutoGrow:Int = True
	
	Function Create:TJSONArray( size:Int)
		Local jso:TJSONArray = New TJSONArray
		jso.Items = New TJSONValue[ size]
		Return jso
	EndFunction
	
	Method New()
		Class = JSON_ARRAY
	EndMethod	

	Method ToString:String()
		Local s:String, lines:Int = 0
		If Items.Length <= 0 Then Return "[]"
		For Local o:TJSONValue = EachIn Items
			If lines > 0 Then s :+ ", "			
			If o.Class = JSON_STRING Then
				s :+ o.ToSource()
			Else
				s :+ o.ToString()
			EndIf			
			lines :+ 1
		Next
		Return "[ "+ s +" ]"
	EndMethod	
	
	Method ToSource:String( level:Int = 0)
		If Items.Length <= 0 Then Return "[]"
		Local s:String, lines:Int = 0
		For Local o:TJSONValue = EachIn Items
			If lines > 0 Then s :+ ",~n" + RepeatString( "~t", level + 1)
			s :+ o.ToSource( level + 1)
			lines :+ 1
		Next
		If lines > 1 Then Return "[~n" + RepeatString( "~t", level + 1) + s + "~n" + RepeatString( "~t", level) + "]"
		Return "[ "+ s +" ]"
	EndMethod
	
	Method GetByIndex:TJSONValue( index:Int)
		If (index >= 0) And (index < Items.Length) Then
			Return TJSONValue( Items[ index])
		EndIf
	EndMethod
	
	Method SetByIndex( index:Int, value:TJSONValue)
		If (index >= 0) And (index < Items.Length) Then
			Items[ index] = value
		ElseIf AutoGrow And (Index >= Items.Length) Then
			Local oldlen:Int = Items.Length
			Items = Items[..index + 1]
			For Local i:Int = oldlen Until Items.Length
				Items[i] = TJSON.NIL
			Next
			Items[index] = value
		EndIf
	EndMethod
	
	Method LookupValue:TJSONValue( value:Object)
		If TJSONKey(value) Then
			Local s:String = TJSONKey(value).Value
			If IsNumber( s) Then Return GetByIndex( s.ToInt())
		ElseIf String(value) Then
			If IsNumber( String(value)) Then Return GetByIndex( String(value).ToInt())
		EndIf	
	EndMethod
EndType


Type TJSONString Extends TJSONValue
	Field Value:String	
	
	Method New()
		Class = JSON_STRING
	EndMethod
	
	Function Create:TJSONString( value:String)
		Local jso:TJSONString = New TJSONString
		jso.Value = value
		Return jso
	EndFunction
		
	Method ToString:String()
		Return Value
	EndMethod
	
	Method ToSource:String( level:Int = 0)
		Return "~q" + Value + "~q"
	EndMethod
EndType

Type TJSONNumber Extends TJSONValue
	Field Value:Double
	
	Method New()
		Class = JSON_NUMBER
	EndMethod	

	Function Create:TJSONNumber( value:Double)
		Local jso:TJSONNumber = New TJSONNumber
		jso.Value = value
		Return jso
	EndFunction
	
	Method ToString:String()
		Return DoubleToString( Value)
	EndMethod	
	
	Method ToSource:String( level:Int = 0)
		Return DoubleToString( Value)
	EndMethod		
EndType

Type TJSONBoolean Extends TJSONValue
	Field Value:Int
	
	Method New()
		Class = JSON_BOOLEAN
	EndMethod	
	
	Function Create:TJSONBoolean( value:Int)
		Local jso:TJSONBoolean = New TJSONBoolean 
		jso.Value = value
		Return jso
	EndFunction
	
	Method ToString:String()
		If Value Then Return "true"
		Return "false"
	EndMethod	
	
	Method ToSource:String( level:Int = 0)
		If Value Then Return "true"
		Return "false"
	EndMethod		
EndType

Type TJSONNull Extends TJSONValue
	Method New()
		Class = JSON_NULL
	EndMethod

	Method ToString:String()
		Return "null"
	EndMethod
	
	Method ToSource:String( level:Int = 0)
		Return "null"
	EndMethod	
EndType



'
' Parses any string into its JSONValue representation
'
Type TJSONParser
	Const ARRAY_GROW_SIZE:Int = 32

	Field Source:String
	Field Index:Int
	Field MakeLowerCase:Int
	
	Method Parse:TJSONValue()
		Const OBJECT_START:Byte = Asc("{")
		Const OBJECT_STOP:Byte = Asc("}")		
		Const ARRAY_START:Byte = Asc("[")
		Const ARRAY_STOP:Byte = Asc("]")
		Const FIELD_SEP:Byte = Asc(":")
		Const ELEM_SEP:Byte = Asc(",")
		Const IDENT_START1:Byte = Asc("a")
		Const IDENT_STOP1:Byte = Asc("z")
		Const IDENT_START2:Byte = Asc("A")
		Const IDENT_STOP2:Byte = Asc("Z")
		Const UNDERSCORE:Byte = Asc("_")
		Const MINUS:Byte = Asc("-")		
		Const NUMBER_START:Byte = Asc("0")
		Const NUMBER_STOP:Byte = Asc("9")
		Const NUMBER_SEP:Byte = Asc(".")
		Const STRING_START1:Byte = Asc("~q")
		Const STRING_START2:Byte = Asc("'")
		Const STRING_ESC:Byte = Asc("\")
		Const SPACE:Byte = Asc(" ")
		Const TAB:Byte = Asc("~t")
		Const CR:Byte = Asc("~r")
		Const LF:Byte = Asc( "~n")
		
		Local c:Byte
		' skip whitspace & crlf		
		While Index < Source.Length
			c = Source[Index]			
			If (c = SPACE) Or (c = TAB) Or (c = CR) Or (c = LF) Then
				Index :+ 1
				Continue
			EndIf
			Exit
		Wend
		' at end allready ?
		If (Index >= Source.Length) Or (Source[Index] = 0) Then Return Null
		
		c = Source[Index]
		If c = OBJECT_START Then
			' OBJECT
			Local jso:TJSONObject = New TJSONObject
			Index :+ 1			
			While Index < Source.Length
				' skip whitespace & crlf
				While Index < Source.Length
					c = Source[Index]			
					If (c = SPACE) Or (c = TAB) Or (c = CR) Or (c = LF) Then
						Index :+ 1
						Continue
					EndIf
					Exit
				Wend
				
				If c = ELEM_SEP Then
					Index :+ 1
				ElseIf c = OBJECT_STOP
					Index :+ 1
					' return json object
					Return jso
				Else				
					Local start:Int = Index, idinstr:Int = False
					Local name:String
					If c = STRING_START1 Or c = STRING_START2 Then						
						' get name enclosed in string tags
						Local strchar:Byte = c
						Index :+ 1
						start = Index
						While (Index < Source.Length) And (Source[Index] <> strchar)
							If Source[Index] = STRING_ESC Then
								Index :+ 1
							EndIf
							Index :+ 1
						Wend
						name = Source[start..Index]					
						' escape string			
						'name = name.Replace( "\/", "/") ' wtf???
						name = name.Replace( "\~q", "~q")
						name = name.Replace( "\'", "'")
						name = name.Replace( "\t", "~t")
						name = name.Replace( "\r", "~r")
						name = name.Replace( "\n", "~n")
						name = name.Replace( "\\", "\")
						Index :+ 1
						idinstr = True
					Else
						' get name as an identifier
						Index :+ 1
						While Index < Source.Length
							c = Source[Index]
							If ((c >= IDENT_START1) And (c <= IDENT_STOP1)) Or ((c >= IDENT_START2) And (c <= IDENT_STOP2)) Or ..
								((c >= NUMBER_START) And (c <= NUMBER_STOP)) Or (c = UNDERSCORE) Or (c = MINUS) Then
								Index :+ 1
								Continue
							EndIf
							name = Source[start..Index]
							Exit 
						Wend
					EndIf									
					' skip whitespace & crlf
					While Index < Source.Length
						c = Source[Index]			
						If (c = SPACE) Or (c = TAB) Or (c = CR) Or (c = LF) Then
							Index :+ 1
							Continue
						EndIf
						Exit
					Wend
					' check for field seperator
					If c <> FIELD_SEP Then
						Error( "expected field seperator ~q:~q")
						Return Null
					EndIf
					Index :+ 1
					' parse value
					Local val:TJSONValue = Parse()
					If val = Null Then Return Null
					If idinstr Then
						Local key:TJSONKey = New TJSONKey
						key.Value = name
						jso.SetByName( key, val)
					Else
						jso.SetByName( name, val)
					EndIf
				EndIf
			Wend
		ElseIf c = ARRAY_START Then
			' ARRAY
			Local jso:TJSONArray = TJSONArray.Create( ARRAY_GROW_SIZE)
			Local count:Int = 0
			Index :+ 1			
			While Index < Source.Length
				' skip whitespace & crlf
				While Index < Source.Length
					c = Source[Index]			
					If (c = SPACE) Or (c = TAB) Or (c = CR) Or (c = LF) Then
						Index :+ 1
						Continue
					EndIf
					Exit
				Wend	
				' parse value
				If c = ELEM_SEP Then
					Index :+ 1
					count :+ 1
				ElseIf c = ARRAY_STOP Then
					Index :+ 1
					' return json array
					jso.Items = jso.Items[..count+1]					
					Return jso
				Else
					Local val:TJSONValue = Parse()
					If val = Null Then Return Null
					' expand array if needed
					If count >= jso.Items.Length Then
						jso.Items = jso.Items[..jso.Items.Length+ARRAY_GROW_SIZE]
					EndIf					
					jso.SetByIndex( count, val)
				EndIf
			Wend
		ElseIf c = STRING_START1 Or c = STRING_START2 Then			
			' STRING
			Local strchar:Byte = c
			Index :+ 1
			Local start:Int = Index
			While (Index < Source.Length) And (Source[Index] <> strchar)
				If Source[Index] = STRING_ESC Then
					Index :+ 1
				EndIf				
				Index :+ 1				
			Wend
			Index :+ 1
			' escape string
			Local s:String = Source[start..Index-1]
			's = s.Replace( "\/", "/") ' wtf???
			s = s.Replace( "\~q", "~q")
			s = s.Replace( "\'", "'")
			s = s.Replace( "\t", "~t")
			s = s.Replace( "\r", "~r")
			s = s.Replace( "\n", "~n")
			s = s.Replace( "\\", "\")
			' return json string
			Return TJSONString.Create( s)
			
		ElseIf (c >= NUMBER_START) And (c <= NUMBER_STOP) Then
			' NUMBER
			Local start:Int = Index, gotsep:Int = False
			' scan for rest of number
			Index :+ 1
			While Index < Source.Length
				c = Source[Index]
				If (c >= NUMBER_START) And (c <= NUMBER_STOP) Then
					Index :+ 1
					Continue
				ElseIf c = NUMBER_SEP Then
					If gotsep Then 
						Error( "invalid floating point number")
						Return Null
					EndIf
					gotsep = True
					Index :+ 1
					Continue
				EndIf
				Exit
			Wend
			' return json number
			Return TJSONNumber.Create( Source[start..Index].ToDouble())
			
		ElseIf (c >= IDENT_START1) And (c <= IDENT_STOP1)  Then
			' TRUE FALSE NULL		
			Local start:Int = Index
			' scan for rest of identifier
			While Index < Source.Length
				c = source[Index]
				If (c >= IDENT_START1) And (c <= IDENT_STOP1) Then
					Index :+ 1
					Continue
				EndIf
				Exit
			Wend
			' validate identifier
			Local s:String = Source[start..Index]
			If s = "false" Then Return TJSONBoolean.Create( False)
			If s = "true" Then Return TJSONBoolean.Create( True)
			If s = "null" Then Return TJSON.NIL
			Error( "expected ~qtrue~q,~qfalse~q Or ~qnull~q")
			Return Null
		Else
			DebugLog "unknown character: " + c + " => " + Chr(c)
		EndIf
	EndMethod
	
	Method Error( msg:String)
		DebugLog "JSON-PARSER-ERROR[ index:"+Index+" ]: " + msg
	EndMethod
EndType




'
' Main JSON object, allows access to values via paths and for reading/writing
'
Type TJSON
	Global NIL:TJSONValue = New TJSONNull
	
	Field Root:TJSONValue = NIL
	Field LookupKey:TJSONKey = New TJSONKey
	
	Function Create:TJSON( source:Object)
		Local json:TJSON = New TJSON
		json.Read( source)
		Return json
	EndFunction
	
	Method Read:TJSONValue( source:Object)
		Root = NIL
		If TJSONValue(source) Then
			' set root	
			Root = TJSONValue( source)
			Return Root
		ElseIf TStream(source) Then
			' read strings from stream
			Local s:String, stream:TStream = TStream(source)
			While Not stream.Eof()
				s :+ stream.ReadLine() + "~n"
			Wend
			' parse string
			Local parser:TJSONParser = New TJSONParser
			parser.Source = s
			Root = parser.Parse()
			If Root Then Return Root
			Root = NIL
		ElseIf String(source) Then
			' parse string
			Local parser:TJSONParser = New TJSONParser
			parser.Source = String(source)
			Root = parser.Parse()
			If Root Then Return Root			
			Root = NIL
		EndIf
		Return Null
	EndMethod
	
	Method Write( dest:Object)
		If TStream(dest) Then
			TStream(dest).WriteString( Root.ToSource())
		ElseIf String(dest) Then
			Local stream:TStream = WriteFile( String(dest))
			If Not stream Then Return
			stream.WriteString( Root.ToSource())
			stream.Close()
		EndIf
	EndMethod
	
	Method ParseString:TJSONValue( s:Object)
		If TJSONValue(s) Then Return TJSONValue(s)
		If Not String(s) Then Return NIL
		Local parser:TJSONParser = New TJSONParser
		parser.Source = String(s)
		Local val:TJSONValue = parser.Parse()
		If val Then Return val
		Return NIL
	EndMethod

	Method Lookup:TJSONValue( path:String)
		If (path.Length = 0) Or (path.ToLower() = "root") Then Return Root
		LookupKey.Value = GetNext( path, ".")
		Local val:TJSONValue = Root.LookupValue( LookupKey)
		If val Then
			Local last:TJSONValue = val
			While path.Length > 0
				last = val
				LookupKey.Value = GetNext( path, ".")
				val = last.LookupValue( LookupKey)
			Wend			
			Return val
		EndIf
	EndMethod
	
	Method SetValue( path:String, value:Object)
		LookupKey.Value = GetNext( path, ".")
		Local val:TJSONValue = Root.LookupValue( LookupKey)
		If val Then
			Local last:TJSONValue = Root
			While (path.Length > 0) And val
				last = val
				LookupKey.Value = GetNext( path, ".")
				val = last.LookupValue( LookupKey)
			Wend			
			If (last.Class = JSON_ARRAY) And IsNumber( LookupKey.Value) Then
				last.SetByIndex( LookupKey.Value.ToInt(), ParseString(value))
			ElseIf (last.Class = JSON_OBJECT) And (Not IsNumber( LookupKey.Value)) Then
				last.SetByName( LookupKey.Value, ParseString(value))
			EndIf
		Else
			If (Root.Class = JSON_ARRAY) And IsNumber( LookupKey.Value) Then
				Root.SetByIndex( LookupKey.Value.ToInt(), ParseString(value))
			ElseIf (Root.Class = JSON_OBJECT) And (Not IsNumber( LookupKey.Value)) Then
				Root.SetByName( LookupKey.Value, ParseString(value))
			EndIf			
		EndIf
	EndMethod
		
	Method GetValue:TJSONValue( path:String)
		Local val:TJSONValue = Lookup( path)
		If val Then Return val
		Return NIL
	EndMethod
	
	Method GetNumber:Double( path:String)
		Local val:TJSONValue = Lookup( path)
		If val And val.Class = JSON_NUMBER Then Return TJSONNumber(val).Value
		Return 0.0
	EndMethod
	
	Method GetString:String( path:String)
		Local val:TJSONValue = Lookup( path)
		If val And val.Class = JSON_STRING Then Return TJSONString(val).Value
		Return Null
	EndMethod	
	
	Method GetBoolean:Int( path:String)
		Local val:TJSONValue = Lookup( path)
		If val And val.Class = JSON_BOOLEAN Then Return TJSONBoolean(val).Value
		Return False
	EndMethod
	
	Method GetObject:TJSONObject( path:String)
		Local val:TJSONValue = Lookup( path)
		If val And val.Class = JSON_OBJECT Then Return TJSONObject(val)
		Return Null
	EndMethod
	
	Method GetArray:TJSONArray( path:String)
		Local val:TJSONValue = Lookup( path)
		If val And val.Class = JSON_ARRAY Then Return TJSONArray(val)
		Return Null
	EndMethod	

'
' not realy sure if these GetArrayXXX are necessary
'	
	Method GetArrayInt:Int[]( path:String)
		Local val:TJSONArray = GetArray( path)
		If val And (val.Items.Length > 0) Then
			Local a:Int[] = New Int[ val.Items.Length]
			For Local i:Int = 0 Until val.Items.Length
				Select val.Items[i].Class
					Case JSON_NUMBER
						a[i] = Int TJSONNumber( val.Items[i]).Value
					Case JSON_STRING
						a[i] = TJSONString( val.Items[i]).Value.ToInt()
					Case JSON_BOOLEAN
						a[i] = TJSONBoolean( val.Items[i]).Value
				EndSelect
			Next
			Return a
		EndIf
		Return Null
	EndMethod
	
	Method GetArrayDouble:Double[]( path:String)
		Local val:TJSONArray = GetArray( path)
		If val And (val.Items.Length > 0) Then
			Local a:Double[] = New Double[ val.Items.Length]
			For Local i:Int = 0 Until val.Items.Length
				Select val.Items[i].Class
					Case JSON_NUMBER
						a[i] = TJSONNumber( val.Items[i]).Value
					Case JSON_STRING
						a[i] = TJSONString( val.Items[i]).Value.ToDouble()
					Case JSON_BOOLEAN
						a[i] = Double TJSONBoolean( val.Items[i]).Value
				EndSelect
			Next
			Return a
		EndIf
		Return Null
	EndMethod	
	
	Method GetArrayString:String[]( path:String)
		Local val:TJSONArray = GetArray( path)
		If val And (val.Items.Length > 0) Then
			Local a:String[] = New String[ val.Items.Length]
			For Local i:Int = 0 Until val.Items.Length
				Select val.Items[i].Class
					Case JSON_NUMBER, JSON_STRING, JSON_BOOLEAN, JSON_NULL
						a[i] = val.Items[i].ToString()
					Case JSON_OBJECT
						a[i] = "{}"
					Case JSON_ARRAY
						a[i] = "[]"
				EndSelect
			Next
			Return a
		EndIf
		Return Null
	EndMethod	
		
	Method ToString:String()
		Return Root.ToString()
	EndMethod
	
	Method ToSource:String( level:Int = 0)
		Return Root.ToSource( level)
	EndMethod	
EndType



'
'MARK: Support Functions
'
Private

'
' Simple token seperator
'
Function GetNext:String( value:String Var, sep:String)	
	If (value.Length <= 0) Or (sep.Length <= 0) Then Return Null
	Local res:String, index:Int = value.Find( sep)
	If index = 0 Then
		value = value[1..]
		Return Null
	ElseIf index >= 1 Then
		res = value[..index]
		value = value[ 1 + res.Length..]
		Return res
	EndIf	
	res = value
	value = Null
	Return res
EndFunction

'
' Checks if a string is a number
'
Function IsNumber:Int( value:String)
	Const START:Int = Asc("0")
	Const STOP:Int = Asc("9")		
	For Local i:Int = 0 Until value.Length
		Local c:Byte = value[i]  
		If (c < START) Or (c > STOP) Then Return False
	Next
	Return True
EndFunction

'
' Returns a "pretty" floating point number
'
Function DoubleToString:String( value:Double)
	Const STR_FMT:String = "%f"
	Const CHAR_0:Byte = Asc("0")
	Const CHAR_DOT:Byte = Asc(".")
	Extern "C"
		Function modf_:Double( x:Double, iptr:Double Var) = "modf"
		Function snprintf_:Int( s:Byte Ptr, n:Int, Format$z, v1:Double) = "snprintf"
	EndExtern	

	Local i:Double
	If modf_( value, i) = 0.0 Then
		Return String.FromLong( Long i)
	Else
		Local buf:Byte[32]
		Local sz:Int = snprintf_( buf, buf.Length, STR_FMT, value)
		sz :- 1
		While (sz > 0) And (buf[ sz] = CHAR_0)
			If buf[ sz-1] = CHAR_DOT Then Exit
			sz :- 1
		Wend
		sz :+ 1
		If sz > 0 Then Return String.FromBytes( buf, sz)
	EndIf
	Return "0"
EndFunction

Function RepeatString:String( s:String, count:Int)
	Local res:String
	While count > 0
		res :+ s
		count :- 1
	Wend
	Return res	
EndFunction

Public



'
'MARK: various test cases, each in its own Rem/EndRem block
'

Rem
Local array:TJSONValue = TJSONArray.Create( 4)
array.SetByIndex( 0, TJSONString.Create( "string value"))
array.SetByIndex( 1, TJSONNumber.Create( 1.5))
array.SetByIndex( 2, TJSONBoolean.Create( True))
array.SetByIndex( 3, TJSON.NIL)

Local object_:TJSONValue = New TJSONObject
object_.SetByName( "first", TJSONString.Create( "string value"))
object_.SetByName( "second", TJSONNumber.Create( 1.5))
object_.SetByName( "third", TJSONBoolean.Create( True))
object_.SetByName( "fourth", TJSON.NIL)


Local json:TJSON = TJSON.Create( New TJSONObject)
json.Root.SetByName( "first", TJSONString.Create( "string value"))
json.Root.SetByName( "second", TJSONNumber.Create( 1.5))
json.Root.SetByName( "third", TJSONBoolean.Create( True))
json.Root.SetByName( "fourth", TJSON.NIL)
json.Root.SetByName( "array", array)
json.Root.SetByName( "object", object_)

Print json.ToSource()
EndRem


Rem
Local jsop:TJSONParser = New TJSONParser
Local json:TJSON = New TJSON
jsop.Source = LoadString( "test.json")
Print jsop.Source
json.Root = jsop.Parse()
If Not json.Root Then
	Print "~noops"
	End
EndIf
EndRem


Rem
Local json:TJSON = TJSON.Create( "[ 1,2,~q3.4 + 2~q,4,5 ]")
Print "--------------------------------------------------------------------"
Print json.ToSource()
Print "--------------------------------------------------------------------"
For Local s:String = EachIn json.GetArrayString("root")
	Print s
Next
EndRem


Rem
Const TEST_JSON:String = ..
"{" +..
"	first: ~qString value~q," +..
"	second: 1.5," +..
"	third: true," +..
"	fourth: null," +..
"	~qthis is an array~q: [" +..
"		~qstring value~q," +..
"		1.5," +..
"		true," +..
"		null" +..
"	]," +..
"	~qthis is an object~q: {" +..
"	first: ~qstring value~q," +..
"	second: 1.5," +..
"	third: true," +..
"	fourth: null" +..
"	}" +..
"}"

'Local json:TJSON = TJSON.Create( LoadString( "test.json"))
Local json:TJSON = TJSON.Create( TEST_JSON)


' change some values
'json.SetValue( "fifth", New TJSONObject)
'json.SetValue( "this is an array.4", New TJSONObject)
'json.SetValue( "this is an object.fifth", New TJSONObject)

Print "--------------------------------------------------------------------"
Print json.ToString()
Print "--------------------------------------------------------------------"
Print json.ToSource()
Print "--------------------------------------------------------------------"
Print json.GetValue( "first").ToString()
Print json.GetValue( "second").ToString()
Print json.GetValue( "third").ToString()
Print json.GetValue( "fourth").ToString()
Print json.GetValue( "fifth").ToString()
Print "--------------------------------------------------------------------"
Print json.GetValue( "this is an array").ToString()
Print "--------------------------------------------------------------------"
Print json.GetValue( "this is an object").ToString()
Print "--------------------------------------------------------------------"
Print json.GetValue( "this is an array.0").ToString()
Print json.GetValue( "this is an array.1").ToString()
Print json.GetValue( "this is an array.2").ToString()
Print json.GetValue( "this is an array.3").ToString()
Print json.GetValue( "this is an array.4").ToString()
Print "--------------------------------------------------------------------"
Print json.GetValue( "this is an object.first").ToString()
Print json.GetValue( "this is an object.second").ToString()
Print json.GetValue( "this is an object.third").ToString()
Print json.GetValue( "this is an object.fourth").ToString()
Print json.GetValue( "this is an object.fifth").ToString()
EndRem
