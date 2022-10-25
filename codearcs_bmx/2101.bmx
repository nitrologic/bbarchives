; ID: 2101
; Author: daaan
; Date: 2007-09-07 03:36:55
; Title: Simple Parser &amp; Executer
; Description: Ask and ye shall receive.

Rem
	
	Simple Parser & Executer for hub
	
	Sample Program:
		
		Local a:String = "v=1.0,a=100,t=120,a=200,t=180,r=200,t=30,r=200"
		runString( a )
	
EndRem

Function runString( line:String )
	
	line = line.Replace( " ", "" )
	line = line.Replace( ",", "" )
	line = line.Replace( "=", "" )
	
	Local instructionChars:String	= "artv"
	Local instruction:String
	Local value:String
	
	For i = 0 To line.length-1
		
		Select Chr( line[ i ] )
			
			Case "a", "r", "t", "v"
				
				instruction = Chr( line[ i ] ).ToLower()
				
			Case "0", ".", "1", "2", "3", "4", "5", "6", "7", "8", "9"
				
				value :+ Chr( line[ i ] )
				
				If instructionChars.contains( Chr( line[ i+1 ] ) ) Then
					
					Select instruction
						
						Case "a"
							acceleration( value.ToDouble() )
						
						Case "r"
							Print "r( " + value.ToDouble() + " )"
						
						Case "t"
							turn( value.ToDouble() )
						
						Case "v"
							Print "v( " + value.ToDouble() + " )"
						
					End Select
					
					value = ""
					
				End If
			
		End Select
		
	Next
	
End Function

Function acceleration( val:Double )
	Print "acceleration( " + val + " )"
End Function

Function turn( val:Double )
	Print "turn( " + val + " )"
End Function
