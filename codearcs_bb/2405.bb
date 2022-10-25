; ID: 2405
; Author: Andy
; Date: 2009-02-03 01:14:27
; Title: Set individual bits in a bank
; Description: set the individual bits in a bank

; Create the bank to use
bnktest=CreateBank(120) 

; set the first 16 bits of the bank to alternating 1/0 
For q%=0 To 15 Step 2
bit_write(bnktest, q%, 1)
Next 

; Read and display the first 16 bits of the bank
For q%= 0 To 15
a=bit_read(bnktest,q%)
Print a
Next
Print
; Read and display the last bit position in the bank
Print "The last bit position in bank is "+bit_last(bnktest)

WaitKey()


Function bit_read(bank, offset%)
b=offset% Mod 8
c%=(offset%-b)/8
a=PeekByte(bank,c%)
Return (a Shr (b )) And 1
End Function

Function bit_write(bank, offset%, bit)
b=offset% Mod 8
c%=(offset%-b)/8
a=PeekByte(bank,c%)
If bit <>0 Then
   a=a Or (bit Shl (b))
Else
   a=a And (bit Shl (b))
EndIf
PokeByte bank,c%,a
End Function

Function bit_last(bank)
a%=BankSize(bank)*8
Return a%-1
End Function
