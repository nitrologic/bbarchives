; ID: 2928
; Author: Matt Merkulov
; Date: 2012-03-04 20:50:29
; Title: BlitzMax to Java 2.0
; Description: Converts BlitzMax code to Java

SuperStrict

Import bah.regex


Type TClass
	Global Classes:TMap = New TMap
	
	Field ID:String
	Field Package:String
	Field Name:String
	
	Function Create:TClass( Package:String, Name:String )
		Local Class:TClass = New TClass
		Classes.Insert( Name.ToLower(), Class )
		
		If Name.StartsWith( "LT" ) Then Name = Name[ 2.. ]
		If Name.StartsWith( "T" ) And Name.Length > 1 Then
			If Name[ 1 ] >= Asc( "A" ) And Name[ 1 ] <= Asc( "Z" ) Then Name = Name[ 1.. ]
		End If
		Name = Name[ 0..1 ].ToUpper() + Name[ 1.. ]
		
		Class.Name = Name
		Class.Package = Package
		Return Class
	End Function
End Type

TClass.Create( "java.lang", "System" )
TClass.Create( "java.lang", "Math" )
TClass.Create( "java.util", "LinkedList" )
TClass.Create( "java.util", "HashMap" )


Type TIdentifier
	Global Identifiers:TMap = New TMap
	
	Field Result:String
	Field Class:TClass
End Type

For Local Pair:String[] = Eachin [ ..
	[ "Byte", "byte" ], ..
	[ "Short", "short" ], ..
	[ "Int", "int" ], ..
	[ "Long", "long" ], ..
	[ "Float", "float" ], ..
	[ "Double", "double" ], ..
	[ "Object", "Object" ], ..
	..
	[ "And", "&&" ], ..
	[ "Or", "||" ], ..
	[ "Not", "!" ], ..
	[ "Mod", "%" ], ..
	..
	[ "String", "String" ], ..
	[ "Length", "length" ], ..
	[ "Find", "indexOf" ], ..
	[ "FindLast", "lastIndexOf" ], ..
	[ "Trim", "trim" ], ..
	[ "Replace", "replaceAll" ], ..
	[ "StartsWith", "startsWith" ], ..
	[ "EndsWith", "endsWith" ], ..
	[ "Join", "indexOf" ], ..
	[ "Split", "split" ], ..
	[ "ToLower", "toLowerCase" ], ..
	[ "ToUpper", "toUpperCase" ], ..
	[ "Length", "length()" ], ..
	..
	[ "TList", "LinkedList", "linkedlist" ], ..
	[ "IsEmpty", "isEmpty" ], ..
	[ "Contains", "contains" ], ..
	[ "AddFirst", "addFirst" ], ..
	[ "AddLast", "addLast" ], ..
	[ "First", "getFirst" ], ..
	[ "Last", "getLast" ], ..
	[ "Remove", "remove" ], ..
	[ "RemoveFirst", "removeFirst" ], ..
	[ "RemoveLast", "removeLast" ], ..
	[ "Clear", "clear" ], ..
	[ "ValueAtIndex", "get" ], ..
	[ "ToArray", "toArray" ], ..
	[ "Count", "size" ], ..
	..
	[ "TMap", "HashMap", "hashmap" ], ..
	[ "ValueForKey", "get" ], ..
	[ "Insert", "put" ], ..
	[ "Keys", "keySet" ], ..
	[ "Values", "values" ], ..
	..
	[ "Millisecs", "System.currentTimeMillis()", "system" ], ..
	..
	[ "Max", "Math.max", "math" ], ..
	[ "Min", "Math.min", "math" ], ..
	[ "Sin", "Math.sin", "math" ], ..
	[ "Cos", "Math.cos", "math" ], ..
	[ "Tan", "Math.tan", "math" ], ..
	[ "ACos", "Math.acos", "math" ], ..
	[ "ASin", "Math.asin", "math" ], ..
	[ "ATan", "Math.atan", "math" ], ..
	[ "ATan2", "Math.atan2", "math" ], ..
	[ "Floor", "Math.floor", "math" ], ..
	[ "Ceil", "Math.ceil", "math" ], ..
	[ "Abs", "Math.abs", "math" ], ..
	[ "Sqr", "Math.sqrt", "math" ], ..
	[ "Log", "Math.log", "math" ], ..
	[ "Exp", "Math.exp", "math" ], ..
	[ "Rnd", "Math.random", "math" ], ..
	[ "Pi", "PI", "math" ], ..
	..
	[ "True", "true" ], ..
	[ "False", "false" ], ..
	[ "Null", "null" ], ..
	..
	[ "Return", "return" ], ..
	[ "Exit", "break" ], ..
	[ "Super", "super" ], ..
	[ "Self", "this" ], ..
	[ "Extends", "extends" ], ..
	[ "New", "new" ] ..
]
	'[ "", "" ], ..
	
	Local Identifier:TIdentifier = New TIdentifier
	Identifier.Result = Pair[ 1 ]
	If Pair.Length = 3 Then Identifier.Class = TClass( TClass.Classes.ValueForKey( Pair[ 2 ] ) )
	TIdentifier.Identifiers.Insert( Pair[ 0 ].ToLower(), Identifier )
Next



Const Directory:String = "D:\temp\src"

GetClassNames( Directory )
ProcessDirectory( Directory )

Function GetClassNames( DirName:String, Package:String = "" )
	Local Dir:Int = ReadDir( DirName )
	Repeat
		Local FileName:String = NextFile( Dir )
		If Not FileName Then Exit
		If FileName = "." Or FileName = ".." Then Continue
		
		Local FullFileName:String = DirName + "\" + FileName
		If FileType( FullFileName ) = FILETYPE_DIR Then
			Local NewPackage:String
			If Package Then NewPackage = Package + "." + FileName Else NewPackage = FileName
			GetClassNames( FullFileName, NewPackage )
			Continue
		End If
		
		If Not FileName.ToLower().EndsWith( ".bmx" ) Then Continue
	
		DebugLog "Retrieving class names from " + FileName
		Local File:TStream = ReadFile( FullFileName )
		While Not Eof( File )
			Local Line:String = ReadLine( File ).Trim()
			If Line.ToLower().StartsWith( "type" ) Then
				Line = Line[ 4.. ].Trim() + " "
				TClass.Create( Package, Line[ ..Line.Find( " " ) ] )
			End If
		WEnd
	Forever
End Function

Global FileClasses:TMap

Function ProcessDirectory( DirName:String, Start:Int = -1 )
	If Start < 0 Then Start = DirName.Length
	
	Local Dir:Int = ReadDir( DirName )
	Repeat
		Local FileName:String = NextFile( Dir )
		If Not FileName Then Exit
		If FileName = "." Or FileName = ".." Then Continue

		Local FullFileName:String = DirName + "\" + FileName
		If FileType( FullFileName ) = FILETYPE_DIR Then
			ProcessDirectory( FullFileName, Start )
			Continue
		End If
	
		If Not FileName.ToLower().EndsWith( ".bmx" ) Then Continue
		
		FileClasses = New TMap
		
		DebugLog "Converting " + FileName
		Local File:TStream = ReadFile( FullFileName )
		Local Text:String = ""
		Local RemBlock:Int = False
		While Not Eof( File )
			Local Line:String = ReadLine( File )
			Local Trimmed:String = Line.ToLower().Trim()
			If Trimmed.StartsWith( "?" ) Then Continue
			if Text Then Text :+ "~n"
			If RemBlock Then
				If Trimmed = "endrem" or Trimmed = "end rem" Then
					Text :+ TRegEx.Create( "^((\t| )*)(EndRem|End Rem)" ).ReplaceAll( Line, "\1 */" )
					RemBlock = False
				Else
					Local N:Int = 0
					Repeat
						N = Line.Find( "#", N ) + 1
						If N = 0 or N = Line.Length Then Exit
						Line = Line[ ..N ] + Chr( Line[ N ] ).ToLower() + Line [ N + 1.. ]
					Forever
					
					Line = TRegEx.Create( "^((\t| )*)(bbdoc|about): *" ).ReplaceAll( Line, "\1" )
					Line = TRegEx.Create( "^((\t| )*)returns: *" ).ReplaceAll( Line, "\1@return " )
					Line = TRegEx.Create( "^((\t| )*)See also: *" ).ReplaceAll( Line, "\1@see " )
					Text :+ TRegEx.Create( "^((\t| )*)(.*)$" ).ReplaceAll( Line, "\1 * \3" )
				End If
			ElseIf Trimmed = "rem" Then
				Text :+ TRegEx.Create( "^((\t| )*)Rem" ).ReplaceAll( Line, "\1/**" )
				RemBlock = True
			Else
				If Trimmed.StartsWith( "import" ) Or Trimmed.StartsWith( "framework" ) Or Trimmed.StartsWith( "include" ) Then Continue
				If Line.Trim() Then
					Line :+ ";"
					
					Local IdentifierBegin:Int = -1
					Local Brackets:Int = False
					Local N:Int = 0 
					While N < Line.Length
						If Brackets Then
							If Line[ N ] = Asc( "~q" ) Then Brackets = False
						Else
							If Line[ N ] = Asc( "~q" ) Then
								Brackets = True
							ElseIf Line[ N ] = Asc( "'" ) Then
								Line = Line [ ..N ] + "//" + Line[ N + 1..Line.Length - 1 ]
								Exit
							ElseIf IdentifierBegin < 0 Then
								If IsIdentifier( Line[ N ], True ) Then IdentifierBegin = N
							ElseIf Not IsIdentifier( Line[ N ], False ) Then
								Local NewIdentifier:String = ConvertIdentifier( Line[ IdentifierBegin..N ] )
								Line = Line[ ..IdentifierBegin ] + NewIdentifier + Line[ N.. ]
								N = IdentifierBegin + NewIdentifier.Length
								IdentifierBegin = -1
							End If
						End If
						N :+ 1
					WEnd
					
					Text :+ ApplyRegExs( Line )
				End If
			End If
		WEnd
		CloseFile File
		
		If FileName.StartsWith( "LT" ) Then FullFileName = DirName + "\" + FileName[ 2.. ]
		If FileName[ 0 ] = Asc( "T" ) And FileName[ 1 ] >= Asc( "A" ) And FileName[ 1 ] <= Asc( "Z" ) Then FullFileName = DirName + "\" + FileName[ 1.. ]
		File = WriteFile( FullFileName[ ..FullFileName.Length - 4 ] + ".java" )
		
		Local Package:String = DirName[ Start + 1.. ].Replace( "\", "." )
		WriteLine( File, "package " + Package + ";" )
		For Local Class:TClass = Eachin FileClasses.Keys()
			if Class.Package <> Package Then WriteLine( File, "import " + Class.Package + "." + Class.Name + ";" )
		Next
		WriteLine( File, "" )
		
		WriteLine( File, Text )
		
		CloseFile File
	Forever
End Function

Function ApplyRegExs:String( Text:String )
	Text = TRegEx.Create( "^((\t| )*)Local " ).ReplaceAll( Text, "\1" )
	Text = TRegEx.Create( "^((\t| )*)Global " ).ReplaceAll( Text, "\1public static " )
	Text = TRegEx.Create( "^((\t| )*)Field " ).ReplaceAll( Text, "\1public " )
	Text = TRegEx.Create( "^((\t| )*)Const " ).ReplaceAll( Text, "\1public final " )
	
	Text = TRegEx.Create( "^((\t| )*)(Function|Method) *((\w|\d|_)*) *\(" ).ReplaceAll( Text, "\1\3 \4:void(" )
	Text = TRegEx.Create( "^((\t| )*)Method (.*);" ).ReplaceAll( Text, "\1public \3 {" )
	Text = TRegEx.Create( "^((\t| )*)Function (.*);" ).ReplaceAll( Text, "\1public static \3 {" )
	Text = TRegEx.Create( "^((\t| )*)Type (.*);" ).ReplaceAll( Text, "\1public class \3 {" )
	
	Local RegEx:TRegEx = TRegEx.Create( "^((\t| )*)If (.*)([^=])=([^=])(.*) Then" )
	While RegEx.Find( Text )
		Text = RegEx.ReplaceAll( Text, "\1if \3\4==\5\6 then" )
	Wend
	Text = TRegEx.Create( " *== *~q~q" ).ReplaceAll( Text, ".isEmpty()" )
	Text = TRegEx.Create( " *== *~q(.*?)~q" ).ReplaceAll( Text, ".equals( ~q\1~q )" )
	
	Text = TRegEx.Create( "^((\t| )*)(ElseIf|Else If) (.*?) Then((\t| )*);" ).ReplaceAll( Text, "\1} else if( \4 ) {" )
	Text = TRegEx.Create( "^((\t| )*)(ElseIf|Else If) (.*?)((\t| )*);$" ).ReplaceAll( Text, "\1} else if( \4 ) {" )
	Local LowerText:String =Text.Trim().ToLower()
	if LowerText.StartsWith( "if" ) Then If Not LowerText.Contains( "then" ) Then Text = Text[ ..Text.Length - 1 ] + "then;"
	Text = TRegEx.Create( "^((\t| )*)If (.*?) Then((\t| )*);$" ).ReplaceAll( Text, "\1if( \3 ) {" )
	Text = TRegEx.Create( "^((\t| )*)If (.*?) Then (.*?) else (.*?);$" ).ReplaceAll( Text, "\1if( \3 ) \4; else \5;" )
	Text = TRegEx.Create( "^((\t| )*)If (.*?) Then (.*?)((\t| )*);$" ).ReplaceAll( Text, "\1if( \3 ) \4;" )
	Text = TRegEx.Create( "^((\t| )*)If (.*?) ((\t| )*);$" ).ReplaceAll( Text, "\1if( \3 ) {" )
	Text = TRegEx.Create( "^((\t| )*)Else((\t| )*);" ).ReplaceAll( Text, "\1} else {" )
	
	Text = TRegEx.Create( "^((\t| )*)For Local ((\w|\d|_)*):((\w|\d|_)*) *= *(.*) To (.*) Step (.*);" ).ReplaceAll( Text, "\1for( \3:\5 = \7; \3 <= \8; \3 += \9 ) {" )
	Text = TRegEx.Create( "^((\t| )*)For ((\w|\d|_)*) *= *(.*) To (.*) Step (.*);" ).ReplaceAll( Text, "\1for( \3 = \5; \3 <= \6; \3 += \7 ) {" )
	Text = TRegEx.Create( "^((\t| )*)For Local ((\w|\d|_)*):((\w|\d|_)*) *= *(.*) To (.*);" ).ReplaceAll( Text, "\1for( \3:\5 = \7; \3 <= \8; \3++ ) {" )
	Text = TRegEx.Create( "^((\t| )*)For ((\w|\d|_)*) *= *(.*) To (.*);" ).ReplaceAll( Text, "\1for( \3 = \5; \3 <= \6; \3++ ) {" )
	Text = TRegEx.Create( "^((\t| )*)For Local ((\w|\d|_)*):((\w|\d|_)*) *= *(.*) Until (.*);" ).ReplaceAll( Text, "\1for( \3:\5 = \7; \3 <= \8; \3++ ) {" )
	Text = TRegEx.Create( "^((\t| )*)For ((\w|\d|_)*) *= *(.*) Until (.*);" ).ReplaceAll( Text, "\1for( \3 = \5; \3 < \6; \3++ ) {" )
	Text = TRegEx.Create( "^((\t| )*)For Local ((\w|\d|_)*):((\w|\d|_)*) *= *EachIn (.*);" ).ReplaceAll( Text, "\1for( \3:\5 @@ \7 ) {" )
	Text = TRegEx.Create( "^((\t| )*)For ((\w|\d|_)*) *= *EachIn (.*);" ).ReplaceAll( Text, "\1for( \3 @@ \5 ) {" )
	Text = TRegEx.Create( "^((\t| )*)While *(.*);" ).ReplaceAll( Text, "\1while( \3 ) {" )
	Text = TRegEx.Create( "^((\t| )*)Repeat((\t| )*);" ).ReplaceAll( Text, "\1while( true ) {" )
	Text = TRegEx.Create( "^((\t| )*)Select *(.*);" ).ReplaceAll( Text, "\1switch( \3 ) {" )
	Text = TRegEx.Create( "^((\t| )*)(EndMethod|End Method|EndIf|End If|End Function|EndFunction|End Type|EndType|Next|WEnd|Forever|End Select|EndSelect)((\t| )*);" ).ReplaceAll( Text, "\1}" )
	
	Text = TRegEx.Create( "new +((\w|\d|_)*)" ).ReplaceAll( Text, "new \1()" )
	Text = TRegEx.Create( "^(((\t| )*)case [^;^:]*);" ).ReplaceAll( Text, "\1@@" )
	Text = TRegEx.Create( "^(((\t| )*)default) *;" ).ReplaceAll( Text, "\1@@" )
	
	Text = TRegEx.Create( ":\+" ).ReplaceAll( Text, "+=" )
	Text = TRegEx.Create( ":\-" ).ReplaceAll( Text, "-=" )
	Text = TRegEx.Create( ":\*" ).ReplaceAll( Text, "*=" )
	Text = TRegEx.Create( ":/" ).ReplaceAll( Text, "/=" )
	
	Text = TRegEx.Create( ">==" ).ReplaceAll( Text, ">=" )
	Text = TRegEx.Create( "<==" ).ReplaceAll( Text, "<=" )
	Text = TRegEx.Create( "<>" ).ReplaceAll( Text, "!=" )
	
	Text = TRegEx.Create( "~~q" ).ReplaceAll( Text, "\~q" )
	Text = TRegEx.Create( "~~n" ).ReplaceAll( Text, "\r\n" )
	Text = TRegEx.Create( "~~t" ).ReplaceAll( Text, "\t" )
	Text = TRegEx.Create( "\.\.((\t| )*);$" ).ReplaceAll( Text, "" )
	
	Text = TRegEx.Create( "(\d):Byte" ).ReplaceAll( Text, "\1" )
	Text = TRegEx.Create( "(\d):Short" ).ReplaceAll( Text, "\1" )
	Text = TRegEx.Create( "(\d):Int" ).ReplaceAll( Text, "\1" )
	Text = TRegEx.Create( "(\d):Long" ).ReplaceAll( Text, "\1l" )
	Text = TRegEx.Create( "(\d):Float" ).ReplaceAll( Text, "\1f" )
	Text = TRegEx.Create( "(\d):Double" ).ReplaceAll( Text, "\1d" )
	Text = TRegEx.Create( "((\w|\d|_)*) *: *((\w|\d|_)*)" ).ReplaceAll( Text, "\3 \1" )
	
	Text = TRegEx.Create( "\$(\d)" ).ReplaceAll( Text, "0x\1" )
	Text = TRegEx.Create( "%(\d)" ).ReplaceAll( Text, "0b\1" )
	
	Text = Text.Replace( "@@", ":" )
	'Text = TRegEx.Create( "" ).ReplaceAll( Text, "" )
	
	Return Text
End Function

Function IsIdentifier:Int( Code:Int, StringBeginning:Int )
	If StringBeginning And Code >= Asc( "0" ) And Code <= Asc( "9" ) Then Return True
	If Code >= Asc( "A" ) And Code <= Asc( "Z" ) Then Return True
	If Code >= Asc( "a" ) And Code <= Asc( "z" ) Then Return True
	If Code = Asc( "_" ) Then Return True
End Function

Function ConvertIdentifier:String( Identifier:String )
	Local LowerCaseIdentifer:String = Identifier.ToLower()
	
	Local Class:TClass = TClass( TClass.Classes.ValueForKey( LowerCaseIdentifer ) )
	If Class Then
		FileClasses.Insert( Class, Null )
		Return Class.Name
	End If
	
	Local ID:TIdentifier = TIdentifier( TIdentifier.Identifiers.ValueForKey( LowerCaseIdentifer ) )
	If ID Then
		If ID.Class Then FileClasses.Insert( ID.Class, Null )
		Return ID.Result
	End If
	
	If Identifier.EndsWith( "@" ) Then Identifier = Identifier[ ..Identifier.Length - 1 ] + ":Byte"
	If Identifier.EndsWith( "%" ) Then Identifier = Identifier[ ..Identifier.Length - 1 ] + ":Int"
	If Identifier.EndsWith( "#" ) Then Identifier = Identifier[ ..Identifier.Length - 1 ] + ":Float"
	If Identifier.EndsWith( "!" ) Then Identifier = Identifier[ ..Identifier.Length - 1 ] + ":Double"
	If Identifier.EndsWith( "$" ) Then Identifier = Identifier[ ..Identifier.Length - 1 ] + ":String"
	
	If LowerCaseIdentifer.StartsWith( "l_" ) Then Identifier = Identifier[ 2.. ]
	Return Identifier[ 0..1 ].ToLower() + Identifier[ 1.. ]
End Function
