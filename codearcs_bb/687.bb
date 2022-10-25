; ID: 687
; Author: Michael Reitzenstein
; Date: 2003-05-13 05:12:08
; Title: ReadFileAsString$( ) Fast
; Description: Faster version of Peter Scheutz's ReadFileAsString$( )

Function ReadFileAsString$( File$ )

	Local File_Handle = OpenFile( File$ )
	Local Return_String$
	Local Start_Chars$
	
	If Not File_Handle
		
		Return
		
	EndIf
	
	If FileSize( File$ ) < 4
		
		While Not Eof( File$ )
			
			Return_String$ = Return_String$ + Chr( ReadByte( File_Handle ) )
			
		Wend
		
		Return Return_String$
		
	EndIf
	
	Start_Chars$ = Chr( ReadByte( File_Handle ) ) + Chr( ReadByte( File_Handle ) ) + Chr( ReadByte( File_Handle ) ) + Chr( ReadByte( File_Handle ) )
	SeekFile File_Handle, 0
	WriteInt File_Handle, FileSize( File$ ) - 4 
	SeekFile File_Handle, 0

	Return_String$ = Start_Chars$ + ReadString( File_Handle )
		
	SeekFile File_Handle, 0

	For Count = 1 To 4
		
		WriteByte File_Handle, Asc( Mid( Start_Chars$, Count, 1 ) )
		
	Next			

	Return Return_String$
	
End Function
