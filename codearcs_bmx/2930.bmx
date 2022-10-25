; ID: 2930
; Author: matibee
; Date: 2012-03-10 09:41:46
; Title: Auto build numbers
; Description: A cheat for adding incremental build numbers

?DEBUG
	' choose a destination file here..
	
	IncrementAutoBuildNumber( "version.bmx" )

	' the destination file must already exist, and primed with the opening line..
	' Global AutoBuildNumber$ = "0.0.0"

	Function IncrementAutoBuildNumber( file$ )
		
		Local f:TStream = ReadFile( file$ )
		Local a$ = ReadLine(f)

		If ( Len(a$) = 0 )
			a$ = "Global AutoBuildNumber$" + "~q0.0.0~q"
		End If 
			
		CloseStream(f)
		
		Local q:Int = Instr( a$, "~q" ) + 1
		a$ = Mid$(a$, q, Len(a$) - q)
		'Print a$
		
		Local b$[] = a$.Split( "." )
		Local minor:Int = b$[1].ToInt()
		Local prefix:Int = b$[2].ToInt()
		
		prefix :+ 1
		If ( prefix > 9999 )
			minor :+ 1
			prefix = 0
		End If 	
		
		f = WriteFile( file$ )
		If ( f = Null ) Then 
			Notify ( "Auto build number failed" )
			Return 
		End If 
		
		WriteLine( f, "Global AutoBuildNumber$ = ~q" + b$[0] + "." + minor + "." + prefix + "~q" )
		CloseFile( f )
		
	End Function 
?
