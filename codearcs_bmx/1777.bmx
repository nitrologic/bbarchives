; ID: 1777
; Author: Chroma
; Date: 2006-08-05 19:05:30
; Title: Instr2
; Description: Returns the index of the specific separator number in the string.

Function Instr2:Int(str:String,sub:String,occ:Int)
	Local i:Int,f:Int,index:Int
	For i = 1 To occ
		f = Instr(str,sub,index)
		index = f + 1
	Next
	Return f
End Function


Function GetPacketValue(strPkt,strSep,intValue)
   Select intValue
      Case 1
         Return Mid( strPkt, 1, Instr2( strPkt, Chr(44), 1) - 1) )
      Case 2
         index1 = Instr2( strPkt, Chr(44), 1)
         index2 = Instr2( strPkt, Chr(44), 2)
         Return Mid( strPkt, index1+1, index2-index1)
      Case 3
         'blah blah
   End Select
End Function
