; ID: 2886
; Author: Matt Merkulov
; Date: 2011-09-01 23:21:30
; Title: Code statistics
; Description: Shows quantity of code lines, comments and different code items.

SuperStrict

Global Files:Int = 1
Global Comments:Int
Global CodeLines:Int
Global CommentLines:Int
Global Classes:Int
Global CommentedClasses:Int
Global Methods:Int
Global CommentedMethods:Int
Global Fields:Int
Global CommentedFields:Int
Global GlobalFunctions:Int
Global CommentedGlobalFunctions:Int
Global GlobalVariables:Int
Global CommentedGlobalVariables:Int
Global Constants:Int
Global CommentedConstants:Int

Local FileName:String = RequestFile( "Select bmx file to process...", "bmx" )
Print "File " + StripDir( FileName ) + ":"
ProcessFile( FileName )

Print ""
Print "Files: " + Files
Print "Code lines: " + CodeLines
Print "Comment lines: " + CommentLines
Print ""
Print "Classes: " + StatEntry( Classes, CommentedClasses, False )
Print "Class fields: " + StatEntry( Fields, CommentedFields )
Print "Class methods / functions: " + StatEntry( Methods, CommentedMethods )
Print ""
Print "Global functions: " + StatEntry( GlobalFunctions, CommentedGlobalFunctions, False )
Print "Global variables: " + StatEntry( GlobalVariables, CommentedGlobalVariables, False )
Print "Constants: " + StatEntry( Constants, CommentedConstants, False )
Print ""
Print "Total items: " + StatEntry( Classes + Methods + Fields + GlobalFunctions + GlobalVariables + Constants, CommentedClasses + ..
		CommentedMethods + CommentedFields + CommentedGlobalFunctions + CommentedGlobalVariables + CommentedConstants, False )

Function ProcessFile( FileName:String )
	Local File:TStream = ReadFile( FileName )
	Local OldDir:String = CurrentDir()
	ChangeDir( ExtractDir( FileName ) )
	
	Local RemFlag:Int
	Local InClass:Int
	Local Commented:Int
	Local FirstLines:Int = True
	While Not EOF( File )
		Local Line:String = Trim( ReadLine( File ) ).ToLower()
		If RemFlag Then
			If Line.StartsWith( "endrem" ) Or Line.StartsWith( "end rem" )  Then
				RemFlag = False
			Else
				If Line.StartsWith( "bbdoc" ) Then Commented = True
				If Not FirstLines Then CommentLines :+ 1
			End If
		Else If Line = "rem" Or Line.StartsWith( "rem " ) Then
			RemFlag = True
		Else If Line.StartsWith( "'" )
			If Not FirstLines Then CommentLines :+ 1
		Else If Line.StartsWith( "include" )
			ProcessFile( Line[ 8.. ].Replace( "~q", "" ) )
			Files :+ 1
		ElseIf Line <> "" Then
			FirstLines = False
			CodeLines :+ 1
			If Line.StartsWith( "type" ) Then 
				InClass = True
				Set( Classes, CommentedClasses, Commented )
			Else If Line.StartsWith( "endtype" ) Or Line.StartsWith( "end type" ) Then
				InClass = False
			Else If Line.StartsWith( "method" ) Then
				Set( Methods, CommentedMethods, Commented )
			Else If Line.StartsWith( "function" ) Then 
				If InClass Then Set( Methods, CommentedMethods, Commented ) Else Set( GlobalFunctions, CommentedGlobalFunctions, Commented )
			Else If Line.StartsWith( "field" ) Then
				Set( Fields, CommentedFields, Commented )
				Fields :+ CountCommas( Line )
			ElseIf Line.StartsWith( "global" ) Then
				Set( GlobalVariables, CommentedGlobalVariables, Commented )
			ElseIf Line.StartsWith( "const" ) Then
				Set( Constants, CommentedConstants, Commented )
			End If
		End If
	WEnd
	
	CloseFile( File )
	ChangeDir( OldDir )
End Function

Function Set( Items:Int Var, CommentedItems:Int Var, Commented:Int Var )
	Items :+ 1
	If Commented Then CommentedItems :+ 1
	Commented = False
End Function

Function CountCommas:Int( Line:String )
	Local Commas:Int
	Local Quotes:Int
	For Local N:Int = 0 Until Len( Line )
		If Line[ N ] = Asc( "~q" ) Then
			Quotes = Not Quotes
		ElseIf Not Quotes Then
			If Line[ N ] = Asc( "," ) Then Commas :+ 1
		End If
	Next
	Return Commas
End Function

Function TrimDouble:String( Value:Double )
	Local StringValue:String = Value
	Return StringValue[ ..StringValue.Find( "." ) + 3 ]
End Function

Function StatEntry:String( Items:Int, CommentedItems:Int, WithClasses:Int = True )
	Local ClassString:String = ""
	If WithClasses Then ClassString = TrimDouble( 1.0 * Items / ( Classes + ( Classes = 0 ) ) ) + " per class, "
	Return Items + " (" + ClassString + CommentedItems + " or " + TrimDouble( 100.0 * CommentedItems / ( Items + ( Items = 0 ) ) ) + "% commented)"
End Function
