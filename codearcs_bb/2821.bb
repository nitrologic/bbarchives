; ID: 2821
; Author: Yasha
; Date: 2011-02-07 12:24:31
; Title: Max to Java
; Description: Take some of the annoyance out of converting code

' Simple tool to do some batch find-and-replace tasks on all BMX files in the current directory

SuperStrict

Import bah.regex


Local dir:Int = ReadDir(CurrentDir())

If Not dir Then RuntimeError "failed to read current directory"

Repeat
	Local fn:String = NextFile(dir)
	If fn = "" Exit
	If fn = "." Or fn = ".." Or ExtractExt(fn) <> "bmx" Or FileType(fn) <> 1 Then Continue
	
	Local bsource:String = LoadText(fn)
	Local jsource:String = ReformatSource(bsource)
	SaveText(jsource, StripExt(fn) + ".java")
Forever

CloseDir dir

End


Function ReformatSource:String(bsource:String)
	
	Global regexen:TList
	If regexen = Null Then initRegexen()
	Local processed:String = bsource
	
	For Local r:RPair = EachIn regexen
		processed = r.regx.replaceall(processed, r.repl)
	Next
	
	Return processed
	
	Function initRegexen()
		regexen = New TList		'The order of some of these is important
		
		AddRPair "(?<=\W)Function\h*([\w\d_]+\h*[:!%#\$])", "public static \1"		'Just drop the Function/Method where a type exists
		AddRPair "(?<=\W)Method\h*([\w\d_]+\h*[:!%#\$])", "public \1"
		AddRPair "(?<=\W)Function\h*([\w\d_]+\h*\()", "public static void \1"		'Otherwise, add void
		AddRPair "(?<=\W)Method\h*([\w\d_]+\h*\()", "public void \1"
		
		AddRPair "(?<=\W)(public\h+[^\n']+\))(?!\h+abstract)", "\1 {"		'Opening brace for methods (all of which are public)
		AddRPair "(?<=\W)public\h+([^\n']+\))\h+abstract", "public abstract \1"		'Move abstract to start
		
		AddRPair "(?<=\W)Then\h+([^\n']+)(?='|\r\n)", "{ \1 }"
		AddRPair "(?<=\W)Else(\W+)(?!If)", "} else {\1"		'Else without If
		AddRPair "(?<=\W)Else\h*(?=If\W)", "} else "	'Convert to normal If
		
		AddRPair "(?<=\W)If\W+([^\n'\{]+)(?=\{)", "if ( \1 ) "
		AddRPair "(?<=\W)If\W+([^\n'\{]+)(?='|\r\n)", "if ( \1 ) {"
		AddRPair "(?<=\W)While\W+([^\n']+)(?='|\r\n)", "while ( \1 ) {"
		AddRPair "(?<=\W)For\W+([^\n']+)(?='|\r\n)", "for ( \1 ) {"
		AddRPair "(?<=\W)Select\W+([^\n']+)(?='|\r\n)", "switch ( \1 ) {"
		AddRPair "(?<=\W)Until\W+([^\n']+)(?='|\r\n)", "} while (!( \1 ));"
		
		AddRPair "(end(\h*)(method|function|type|while|try|if|select))", "} // \1"
		AddRPair "(?<=\W)(next|wend)(?=\W)", "} // \1"
				
		AddRPair "Extends", "extends"	'For reasons of case sensitivity
		AddRPair "Abstract", "abstract"
		AddRPair "Null", "null"
		AddRPair "(?<=\W)Self(?=\W)", "this"	'Java doesn't call it Self
		AddRPair "Super", "super"
		AddRPair "Int", "int"
		AddRPair "Float", "float"
		AddRPair "String", "String"
		AddRPair "Return", "return"
		AddRPair "Case", "case"
		AddRPair "Default", "default"
		AddRPair "Throw", "throw"
		AddRPair "True", "true"
		AddRPair "False", "false"
		AddRPair "(?<=\W)Exit(?=\W)", "break"
		AddRPair "Continue", "continue"
		AddRPair "New\h+([\w\d_]+)", "new \1()"
		AddRPair "(DebugLog)", "// \1"		'Add these again later, ignore for now
		
		AddRPair "(?<=\W)(Local)(?=\h)", "/* \1 */"		'Keep the declaration, to help check converted code
		AddRPair "(?<=\W)Field\h*", "public "			'All BlitzMax fields are public
		AddRPair "(?<=\W)Global\h*", "public static "	'This will be wrong for globals in functions
		AddRPair "(?<=\W)(Const)\h*", "/* \1 */ public static final "
		
		AddRPair "([\w\d_]+)%", "int \1"		'Convert type sigils to keywords
		AddRPair "([\w\d_]+)#", "float \1"
		AddRPair "([\w\d_]+)!", "double \1"
		AddRPair "([\w\d_]+)\$", "String \1"
		AddRPair "([\w\d_]+)(\h*):(\h*)([\w\d_\[\]]+)", "\4 \1"		'Swap name:type declaration syntax around
		
		AddRPair "([\.=\+\-\*&\|<>]\h*)(int|float|double|String)\h", "\1"	'Remove converted inline-sigils where obvious
		AddRPair "(?<!\*)(/\h*)(int|float|double|String)\h", "\1"			'Slash only when not a closing comment
		
		AddRPair "(?<=\W)Repeat(?=\W)", "do {"
		AddRPair "(?<=\W)Forever(?=\W)", "} while (true);"
		AddRPair "<>", "!="
		AddRPair "=>", ">="
		AddRPair "=<", "<="
		AddRPair "(?<=\W)And(?=\W)", "&&"
		AddRPair "(?<=\W)Or(?=\W)", "||"
		AddRPair "(?<=\W)Not(?=\W)", "!"
		AddRPair "(?<=\W)Shl(?=\W)", "<<"
		AddRPair "(?<=\W)Shr(?=\W)", ">>"
		AddRPair "(?<=\W)Sar(?=\W)", ">>>"
		AddRPair "(?<=\W)Mod(?=\W)", "%"
		AddRPair ":(\+|-|\*|/|&|\||~~|<<|>>|>>>|%)", "\1="		'Change compound assignment style
		
		AddRPair "(?<=\W)(class)(?=\W)", "\1_"		'Add any Java keywords in use as identifiers here
		
		AddRPair "(abstract\h+)?type\h+([\w\d_]+(\hextends\h([\w\d_]+))?)", "public \1class \2 {"	'Type to class
		AddRPair "([^\s\{\};\.,])(\h*)(?='|\r\n|\})", "\1;\2"		'Add semicolons where statements end
		
		AddRPair "(\[[^,\n\]]+),", "\1]["		'Replace commas in array elements with ][ - may not want this
		
		AddRPair "'", "//"			'Comments
		AddRPair "(?<=\W)end(\h*)rem(?=\W)", "*/"
		AddRPair "(?<=\W)Rem(?=\W)", "/*"
		
		AddRPair "=\h*EachIn(?=\W)", " : "		'Once the type signatures are done this is safe
		
		AddRPair "(\[[^\n\]=]+)=", "\1=="			'Replace = with == within array[element] access
		'Try to replace = with == where appropriate - this may be horribly wrong!
		AddRPair "(?<=[\n;,\{])(((?!\(|\Wreturn\h|=)[^\n=;,\{])+(\(|\Wreturn\h|=)[^\n=;,]+)(?<=[^\!<>])=(?!=)", "\1=="
	End Function
	
	Function AddRPair(regx:String, repl:String)
		regexen.AddLast(RPair.Create(regx, repl))
	End Function
End Function

Type RPair
	Field regx:TRegEx, repl:String
	
	Function Create:RPair(regx:String, repl:String)
		Local r:RPair = New RPair
		r.regx = TRegEx.Create(regx) ; r.repl = repl
		Return r
	End Function
End Type
