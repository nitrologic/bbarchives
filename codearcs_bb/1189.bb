; ID: 1189
; Author: Barliesque
; Date: 2004-11-08 06:45:41
; Title: Val()
; Description: Convert strings containing Hex, Binary or Floating Decimal values to a numeric value

;****************************************************
;**
;**  Val()  -  Converts a numeric value in a string
;**            to a decimal value.  Strings can contain:
;**               Integers
;**               Floating point numbers
;**               Hexadecimal
;**               Binary
;**
;**   For Hex values use any of these formats:
;**         0xFF  $FF  #FF
;**
;**   For Binary use this format:
;**         %11001010
;**
;**   NOTE:  Because this function returns all values
;**          as floating point, there is a limit to the
;**          range of values it can handle accurately.
;**          If you need integers that are larger than
;**          16777215 or smaller than -16777215 (12 bits)
;**          then you should remove the # from the
;**          function declaration and from the variable
;**          Num# to get the fullest accuracy for large
;**          integers.
;**
;**   Courtesy of Barliesque  :)
;**
;***************************************************

Local Value%

Print Val("37")
Print Val("3-7")
Print Val("50.1")
Print "0xFFFF = " + Int(Val("0xFFFF"))
Print "#E5 = " + Val("#E5")
Print "$FF = " + Val("$FF")
Value = Val("%10010010")
Print "%10010010 = " + Value
WaitKey

End

;****************************************************

Function Val#(StringNumeric$)

   Local Num# = 0
   Local Hex1 = ((Left$(StringNumeric$,1)="#") Or (Left$(StringNumeric$,1)="$"))
   Local Hex2 = (Left$(StringNumeric$,2)="0x")
   Local Binary = (Left$(StringNumeric$,1)="%")
   Local i,c
   
   If Hex1 Or Hex2
      StringNumeric$ = Upper(StringNumeric$)
      For i=(Hex1 + (Hex2 * 2) + 1) To Len(StringNumeric$) 
      	c = asc(Mid$(StringNumeric$,i,1))
         Select true
            Case (c>=48 and c<=57)  ;0 through 9
               Num# = (Num# * 16) + c-48
            Case (c>=65 and c<=70)  ;A through F
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
