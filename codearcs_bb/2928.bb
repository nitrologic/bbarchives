; ID: 2928
; Author: Matt Merkulov
; Date: 2012-03-04 20:50:29
; Title: BlitzMax to Java
; Description: Converts BlitzMax code to Java

SuperStrict

Import bah.regex

Const InFile:String = "C:\Program Files\BlitzMax\mod\dwlab.mod\frmwork.mod\include\XML.bmx"
Const OutFile:String = "D:\projects\gruxion\src\base\XML.java"
Const DeleteComments:Int = True

Local File:TStream = ReadFile( InFile )
Local Text:String = ""
Local RemBlock:Int = False
While Not Eof( File )
	Local Line:String = ReadLine( File )
	Local Trimmed:String = Line.ToLower().Trim()
	If Trimmed.StartsWith( "?" ) Then Continue
	If DeleteComments Then
		If Trimmed.StartsWith( "'" ) Then Continue
		If Trimmed.StartsWith( "rem" ) Then
			RemBlock = True
		ElseIf Trimmed.StartsWith( "endrem" ) or Trimmed.StartsWith( "end rem" ) Then
			RemBlock = False
		Else If Not RemBlock Then
			if Text Then Text :+ "~n"
			Text :+ Line
			If Line.Trim() Then Text :+ ";"
		End If
	Else
		if Text Then Text :+ "~n"
		Text :+ Line
		If Line.Trim() Then Text :+ ";"
	End If
WEnd
CloseFile File

If DeleteComments Then Text = TRegEx.Create( "'.*" ).ReplaceAll( Text, "" )
Text = TRegEx.Create( "^((\t| )*)(Local |Global )" ).ReplaceAll( Text, "\1" )
Text = TRegEx.Create( "^((\t| )*)Field " ).ReplaceAll( Text, "\1public " )
Text = TRegEx.Create( "^((\t| )*)Const " ).ReplaceAll( Text, "\1public final " )
Text = TRegEx.Create( "^((\t| )*)Method (.*);" ).ReplaceAll( Text, "\1public \3 {" )
Text = TRegEx.Create( "^((\t| )*)Function (.*);" ).ReplaceAll( Text, "\1public static \3 {" )
Text = TRegEx.Create( "New (LT|)((\w|\d|_)*)" ).ReplaceAll( Text, "new \2()" )
Text = TRegEx.Create( "L_" ).ReplaceAll( Text, "" )
Text = TRegEx.Create( "^((\t| )*)If (.*)=(.*)" ).ReplaceAll( Text, "\1if \3==\4" )
Text = TRegEx.Create( " *== *~q~q" ).ReplaceAll( Text, ".isEmpty()" )
Text = TRegEx.Create( " *== *~q([^~q]*)~q" ).ReplaceAll( Text, ".equals( \1 )" )
Text = TRegEx.Create( "^((\t| )*)(ElseIf|Else If) (.*) Then((\t| )*);" ).ReplaceAll( Text, "\1} elseif( \4 ) {" )
Text = TRegEx.Create( "^((\t| )*)(ElseIf|Else If) (.*);" ).ReplaceAll( Text, "\1} elseif( \4 ) {" )
Text = TRegEx.Create( "^((\t| )*)If (.*) then((\t| )*);" ).ReplaceAll( Text, "\1if( \3 ) {" )
Text = TRegEx.Create( "^((\t| )*)If (.*) then (.*) else (.*);" ).ReplaceAll( Text, "\1if( \3 ) \4; else \5;" )
Text = TRegEx.Create( "^((\t| )*)If (.*) then (.*);" ).ReplaceAll( Text, "\1if( \3 ) \4;" )
Text = TRegEx.Create( "^((\t| )*)Else((\t| )*);" ).ReplaceAll( Text, "\1} else {" )
Text = TRegEx.Create( "^((\t| )*)For Local ((\w|\d|_)*):((\w|\d|_)*) *= *(.*) To (.*) Step (.*);" ).ReplaceAll( Text, "\1for( \3:\5=\7; \3 <= \8; \3 += \9 ) {" )
Text = TRegEx.Create( "^((\t| )*)For ((\w|\d|_)*) *= *(.*) To (.*) Step (.*);" ).ReplaceAll( Text, "\1for( \3=\5; \3 <= \6; \3 += \7 ) {" )
Text = TRegEx.Create( "^((\t| )*)For Local ((\w|\d|_)*):((\w|\d|_)*) *= *(.*) To (.*);" ).ReplaceAll( Text, "\1for( \3:\5=\7; \3 <= \8; \3++ ) {" )
Text = TRegEx.Create( "^((\t| )*)For ((\w|\d|_)*) *= *(.*) To (.*);" ).ReplaceAll( Text, "\1for( \3=\5; \3 <= \6; \3++ ) {" )
Text = TRegEx.Create( "^((\t| )*)For Local ((\w|\d|_)*):((\w|\d|_)*) *= *(.*) Until (.*);" ).ReplaceAll( Text, "\1for( \3:\5=\7; \3 <= \8; \3++ ) {" )
Text = TRegEx.Create( "^((\t| )*)For ((\w|\d|_)*) *= *(.*) Until (.*);" ).ReplaceAll( Text, "\1for( \3=\5; \3 < \6; \3++ ) {" )
Text = TRegEx.Create( "^((\t| )*)For Local ((\w|\d|_)*):((\w|\d|_)*) *= *EachIn (.*);" ).ReplaceAll( Text, "\1for( \3:\5@@ \7 ) {" )
Text = TRegEx.Create( "^((\t| )*)For ((\w|\d|_)*) *= *EachIn (.*);" ).ReplaceAll( Text, "\1for( \3@@ \5 ) {" )
Text = TRegEx.Create( "^((\t| )*)While *(.*);" ).ReplaceAll( Text, "\1while( \3 ) {" )
Text = TRegEx.Create( "^((\t| )*)Repeat((\t| )*);" ).ReplaceAll( Text, "\1while( true ) {" )
Text = TRegEx.Create( "^((\t| )*)Type (LT|)(.*);" ).ReplaceAll( Text, "\1public class \4 {" )
Text = TRegEx.Create( "^((\t| )*)(EndMethod|End Method|EndIf|End If|End Function|EndFunction|End Type|EndType|Next|WEnd|Forever)((\t| )*);" ).ReplaceAll( Text, "\1}" )
Text = TRegEx.Create( ":\+" ).ReplaceAll( Text, "+=" )
Text = TRegEx.Create( ":\-" ).ReplaceAll( Text, "-=" )
Text = TRegEx.Create( ":\*" ).ReplaceAll( Text, "*=" )
Text = TRegEx.Create( ":/" ).ReplaceAll( Text, "/=" )
Text = TRegEx.Create( ": *Int" ).ReplaceAll( Text, ":int" )
Text = TRegEx.Create( ": *Long" ).ReplaceAll( Text, ":long" )
Text = TRegEx.Create( ": *Float" ).ReplaceAll( Text, ":float" )
Text = TRegEx.Create( ": *Double" ).ReplaceAll( Text, ":double" )
Text = TRegEx.Create( "(:|new ) *TList" ).ReplaceAll( Text, "\1LinkedList" )
Text = TRegEx.Create( "(:|new ) *TMap" ).ReplaceAll( Text, "\1HashMap" )
Text = TRegEx.Create( " And " ).ReplaceAll( Text, " && " )
Text = TRegEx.Create( " Or " ).ReplaceAll( Text, " || " )
Text = TRegEx.Create( "Not " ).ReplaceAll( Text, "!" )
Text = TRegEx.Create( ">==" ).ReplaceAll( Text, ">=" )
Text = TRegEx.Create( "<==" ).ReplaceAll( Text, "<=" )
Text = TRegEx.Create( "<>" ).ReplaceAll( Text, "!=" )
Text = TRegEx.Create( "True" ).ReplaceAll( Text, "true" )
Text = TRegEx.Create( "False" ).ReplaceAll( Text, "false" )
Text = TRegEx.Create( "Null" ).ReplaceAll( Text, "null" )
Text = TRegEx.Create( "Self" ).ReplaceAll( Text, "this" )
Text = TRegEx.Create( "Return" ).ReplaceAll( Text, "return" )
Text = TRegEx.Create( "Extends" ).ReplaceAll( Text, "extends" )
Text = TRegEx.Create( "~~q" ).ReplaceAll( Text, "\~q" )
Text = TRegEx.Create( "~~n" ).ReplaceAll( Text, "\r\n" )
Text = TRegEx.Create( "~~t" ).ReplaceAll( Text, "\t" )
Text = TRegEx.Create( "((\w|\d|_)*) *: *(LT|)((\w|\d|_)*)" ).ReplaceAll( Text, "\4 \1" )
Text = TRegEx.Create( "\.Insert\(" ).ReplaceAll( Text, ".put(" )
Text = TRegEx.Create( "\.AddLast\(" ).ReplaceAll( Text, ".addLast(" )
Text = TRegEx.Create( "\.AddFirst\(" ).ReplaceAll( Text, ".addFirst(" )
Text = TRegEx.Create( "\.IsEmpty\(" ).ReplaceAll( Text, ".isEmpty(" )
Text = TRegEx.Create( "\.Contains\(" ).ReplaceAll( Text, ".contains(" )
Text = Text.Replace( "@@", ":" )
'Text = TRegEx.Create( "" ).ReplaceAll( Text, "" )

File = WriteFile( OutFile )
WriteLine( File, Text )
CloseFile File
