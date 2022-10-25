; ID: 1755
; Author: daaan
; Date: 2006-07-18 03:49:01
; Title: Evaluate String Arithmetic
; Description: Updated on August 17th 2006!

' string evaluator by Daniel Wooden
' email questions to dan.wooden@gmail.com

Const STACKSIZE = 25
Global SplitStack:String[ STACKSIZE ]

Function Evaluate:Float( Line:String )
	
	Local PointA:Int = 0
	Local PointB:Int = 0
	
	Local EditLine:String = Line.Replace( " ", "" )
	Local CurrentChr:String = ""
	Local Scope:String = ""
	Local ScopeToReplace:String = ""
	
	While EditLine.Contains( "(" )
		
		For i = 0 To EditLine.Length
			CurrentChr = Mid( EditLine, i, 1 )
			If CurrentChr = "(" Then
				PointA = i
			ElseIf CurrentChr = ")" Then
				PointB = i
			End If
			If PointB <> 0 Then
				Scope = Mid( EditLine, PointA+1, PointB-PointA-1 )
				ScopeToReplace = Mid( EditLine, PointA, PointB-PointA+1 )
				EditLine = EditLine.Replace( ScopeToReplace, CalculateScope( Scope ) )
				PointA = 0
				PointB = 0
			End If
		Next
		
	Wend
	
	Return CalculateScope( EditLine ).ToFloat()
	
End Function

Function CalculateScope:String( Line:String )
	
	Local Index:Int = 0
	Local CurrentChr:String = ""
	Local CurrentLine:String = ""
	
	For i = 0 To Line.Length
		CurrentChr = Mid( Line, i, 1 )
		If CurrentChr <> "*" And CurrentChr <> "/" And CurrentChr <> "+" And CurrentChr <> "-" Then
			CurrentLine :+ CurrentChr
		Else
			SplitStack[ Index ] = CurrentLine
			CurrentLine = ""
			Index :+ 1
			SplitStack[ Index ] = CurrentChr
			Index :+ 1
		End If
	Next
	SplitStack[ Index ] = CurrentLine
	
	#DoItAgain
	For i = 0 To STACKSIZE-1
		
		If SplitStack[ i ] = "*" Or SplitStack[ i ] = "/" Then
			Select SplitStack[ i ]
				Case "*"
					SplitStack[ i-1 ] = SplitStack[ i-1 ].ToFloat() * SplitStack[ i+1 ].ToFloat()
					SplitStack[ i ] = ""
					SplitStack[ i+1 ] = ""
				Case "/"
					SplitStack[ i-1 ] = SplitStack[ i-1 ].ToFloat() / SplitStack[ i+1 ].ToFloat()
					SplitStack[ i ] = ""
					SplitStack[ i+1 ] = ""
			End Select
			
			For j = i To STACKSIZE-3
				SplitStack[ j ] = SplitStack[ j+2 ]
			Next
			
		End If
		
	Next
	
	' Lame hack... I'll fix the real problem later.
	For i = 0 To STACKSIZE-1
		If SplitStack[ i ] = "*" Or SplitStack[ i ] = "/" Then
			Goto DoItAgain
		End If
	Next
	
	While SplitStack[ 1 ] <> ""
		For i = 0 To STACKSIZE-1
			
			If SplitStack[ i ] = "+" Or SplitStack[ i ] = "-" Then
				Select SplitStack[ i ]
					Case "+"
						SplitStack[ i-1 ] = SplitStack[ i-1 ].ToFloat() + SplitStack[ i+1 ].ToFloat()
						SplitStack[ i ] = ""
						SplitStack[ i+1 ] = ""
					Case "-"
						SplitStack[ i-1 ] = SplitStack[ i-1 ].ToFloat() - SplitStack[ i+1 ].ToFloat()
						SplitStack[ i ] = ""
						SplitStack[ i+1 ] = ""
				End Select
				
				For j = i To STACKSIZE-3
					SplitStack[ j ] = SplitStack[ j+2 ]
				Next
				
			End If
			
		Next
		
	Wend
	
	Return SplitStack[ 0 ]
	
End Function
