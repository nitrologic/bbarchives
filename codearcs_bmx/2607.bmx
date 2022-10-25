; ID: 2607
; Author: EdzUp[GD]
; Date: 2009-11-03 15:46:00
; Title: IP Address validator (IPv4)
; Description: This will validate a IPv4 IP address

Function isIPAddressValid:Byte( IPAddress:String )
	Local TempIP:String = IPAddress
	Local Occlet:String = ""
	Local OccletCount:Long = 0
	
	Local OccletTest:Long = 0
	
	For OccletTest = 1 To Len( IPAddress )
		If Mid( IPAddress, OccletTest, 1 )>="0" And Mid( IPAddress, OccletTest, 1 )<="9" Or Mid( IPAddress, OccletTest, 1 )="."
		Else
			Return False
		EndIf
	Next
	
	Local DotString:String = IPAddress
	DotString = Replace( DotString, ".", "" )			'remove the .'s there should only be three
	If Len( IPAddress )>Len( DotString )+3 Then Return False
	DotString = IPAddress
	DotString = Replace( DotString, "..", "" )		'ipaddress dots should not be together
	If Len( IPAddress )<> Len( DotString ) Then Return False
	If Right( IPAddress, 1 ) = "." Then Return False
	If Left( IPAddress, 1 ) = "." Then Return False
	
	If Instr( IPAddress, "." ) = 0
		Return False
	Else
		Occlet = Left( TempIP, Instr( TempIP, "." )-1 )
		TempIP = Right( TempIP, Len( TempIP ) -Instr( TempIP, "." ) )
		If ( Int( Occlet )<0 Or Int( Occlet )>255 ) Then Return False Else OccletCount :+ 1
		If Instr( TempIP, "." ) = 0 Then Return False
		Occlet = Left( TempIP, Instr( TempIP, "." )-1 )
		TempIP = Right( TempIP, Len( TempIP ) -Instr( TempIP, "." ) )
		If ( Int( Occlet )<0 Or Int( Occlet )>255 ) Then Return False Else OccletCount :+ 1
		If Instr( TempIP, "." ) = 0 Then Return False
		Occlet = Left( TempIP, Instr( TempIP, "." )-1 )
		TempIP = Right( TempIP, Len( TempIP ) -Instr( TempIP, "." ) )
		If ( Int( Occlet )<0 Or Int( Occlet )>255 ) Then Return False Else OccletCount :+ 1
		If ( Int( TempIP )<0 Or Int( TempIP )>255 ) Then Return False Else OccletCount :+ 1
	EndIf
	
	If OccletCount <4 Then Return False
	
	Return True
End Function
