; ID: 2537
; Author: superStruct
; Date: 2009-07-21 16:55:02
; Title: ARGB Converter
; Description: Separates ARGB

Function Alpha(argb%)
	temp$ = Bin(argb)
	For i = 1 To 8
		alpha% = Val(Right(Left(temp,i),1))*2^(8 - i) + alpha
	Next
	Return alpha
End Function

Function Red(argb%)
	temp$ = Bin(argb)
	For i = 1 To 8
		red% = Val(Right(Left(temp,i+8),1))*2^(8 - i) + red
	Next
	Return red
End Function

Function Green(argb%)
	temp$ = Bin(argb)
	For i = 1 To 8
		green% = Val(Right(Left(temp,i+16),1))*2^(8 - i) + green
	Next
	Return green
End Function

Function Blue(argb%)
	temp$ = Bin(argb)
	For i = 1 To 8
		blue% = Val(Right(Left(temp,i+24),1))*2^(8 - i) + blue
	Next
	Return blue
End Function

Function Val#(StringNumeric$)

   Local Num# = 0
   Local Hex1 = ((Left$(StringNumeric$,1)="#") Or (Left$(StringNumeric$,1)="$"))
   Local Hex2 = (Left$(StringNumeric$,2)="0x")
   Local Binary = (Left$(StringNumeric$,1)="%")
   Local i,c
   
   If Hex1 Or Hex2
      StringNumeric$ = Upper(StringNumeric$)
      For i=(Hex1 + (Hex2 * 2) + 1) To Len(StringNumeric$) 
      	c = Asc(Mid$(StringNumeric$,i,1))
         Select True
            Case (c>=48 And c<=57)  ;0 through 9
               Num# = (Num# * 16) + c-48
            Case (c>=65 And c<=70)  ;A through F
               Num# = (Num# * 16) + c-55
            Default
               Return Num#                        
         End Select
      Next
   Else
      If Binary
         For i=2 To Len(StringNumeric$) 
            Select Mid$(StringNumeric$,i,1)
               Case "1"
                  Num# = (Num# * 2) + 1
               Case "0"
                  Num# = (Num# * 2)
               Default
                  Return Num#                        
            End Select
         Next
      Else
         Num# = StringNumeric$
      EndIf
   EndIf
   Return Num#
   
End Function
