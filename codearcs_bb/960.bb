; ID: 960
; Author: Bot Builder
; Date: 2004-03-05 17:22:53
; Title: Bank peek/poke/read/write 1-32 bits using all math
; Description: Allows you to write data using odd numbers of bits. Includes a pokestring function with each charachter=6 bits

;Bank peek/poke/read/write bit by bit functions by Bot Builder
;The main use for these are in compression algos, or compact file formats.

Const resize=1 ;Whether or not to resize bank when trying to write out of bounds

Function peekbit(bank,pos)
 Return (PeekByte(bank,Floor(pos/8.0)) Shr (pos Mod 8))>0
End Function

Function pokebit(bank,pos,val)
 bpos=Floor(pos/8.0)
 If resize Then
  If bpos+1>BankSize(bank) Then ResizeBank bank,bpos+1
 EndIf
 If peekbit(bank,pos)=val Then Return Else PokeByte bank,bpos,PeekByte(bank,bpos)+(val*2-1)*2^(pos Mod 8)
End Function

Function peekdata(bank,start,count)
 For po=0 To count-1
  If peekbit(bank,po+start) Then ret=ret+2^po
 Next
 Return ret
End Function

Function pokedata(bank,start,count,val)
 For po=0 To count-1
  PokeBit bank,po+start,(val Shr po) Mod 2
 Next
End Function

Function PeekShortString$(bank,start) ;Keep in mind these only support 65535 chars, a-z, A-Z, 0-9, space, and . . You gain 2 bytes+(2 bits*number of letters) over blitz strings
 l=PeekShort(bank,start)
 If l=0 Then Return ""
 start=start+10
 For a=1 To l
  r=peekdata(bank,start+6*a,6)
  Select r
  Case 0
   ret$=ret$+" "
  Case 1
   ret$=ret$+"."
  Default
   If r<12 Then
    ret$=ret$+(r-2)
   ElseIf r<37 Then
    ret$=ret$+Chr$(r+53)
   Else
    ret$=ret$+Chr$(r+59)
   End If
  End Select
 Next
 Return ret$
End Function

Function PokeShortString(bank,start,strin$) ;Keep in mind these only support 65535 chars, a-z, A-Z, 0-9, space, and . . You gain 2 bytes+(2 bits*number of letters) over blitz strings
 Pokedata bank,start,16,Len(strin$)
 start=start+10
 For a=1 To Len(strin$)
  let$=Mid$(strin$,a,1)
  Select let$
  Case " "
   pokedata bank,start+6*a,6,0
  Case "."
   pokedata bank,start+6*a,6,1
  Default
   as=Asc(let$)
   If as>47 Then
    If as<58 Then
     pokedata bank,start+6*a,6,as-46
    ElseIf as>64 Then
     If as<91 Then
      pokedata bank,start+6*a,6,as-53
     ElseIf as>96 Then
      If as<123 Then pokedata bank,start+6*a,6,as-59 Else pokedata bank,start+6*a,6,0
     EndIf
    EndIf
   EndIf
  End Select
 Next
End Function

Global cpos=0
;The current position in a bank being read from,
;an important thing to note that this system will
;work differently than the blitz system, in that
;you can't use the convenience read/write functions
;on multiple banks because I can't keep track of
;the current position in multiple banks. Or at least,
;if I did it would be really slow. To change the
;position of read/write, set cpos to whatever
;you like (measured in absolute bits).

Function readbit(bank)
 Return peekbit(bank,cpos)
 cpos=cpos+1
End Function

Function writebit(bank,val)
 pokebit bank,cpos,val
 cpos=cpos+1
End Function

Function readdata(bank,length)
 cpos=cpos+length
 Return peekdata(bank,cpos-length,length)
End Function

Function writedata(bank,val,length)
 pokedata bank,cpos,length,val
 cpos=cpos+length
End Function

Function ReadShortString$(bank)
 strin$=PeekShortString$(bank,cpos)
 cpos=cpos+16+6*PeekShort(bank,cpos)
 Return strin$
End Function

Function WriteShortString(bank,strin$)
 PokeShortString bank,cpos,strin$
 cpos=cpos+16+6*Len(strin$)
End Function

;Demo:

Graphics 640,480

b=CreateBank(0) ;only 29 bytes for a 36 charachter string along with length storage!
WriteShortString b,"Hello. I am stored in a compact way."
cpos=0
Print ReadShortString$(b)
Print ""

cpos=0
b2=CreateBank(0)
in$=Input$("String you would like to store?")
ResizeBank b2,Ceil((15+6*Len(in$))/8.0)+1
WriteShortString b2,in$
ResizeBank b2,Ceil((3+cpos)/8.0)
DebugLog cpos
WriteData b2,Input("3-bit (0-7) integer you would like to store?"),3
ResizeBank b2,Ceil((5+cpos)/8.0)
WriteData b2,Input("5-bit (0-31) integer you would like to store?"),5
ResizeBank b2,Ceil((6+cpos)/8.0)
WriteData b2,Input("6-bit (0-63) integer you would like to store?"),6
Print ""
Print "Total size in bits:"+cpos
Print "Total size in bytes:"+(cpos/8.0)
Print ""
cpos=0
Print "String:"+ReadShortString$(b2)
DebugLog cpos
Print "3-bit:"+ReadData(b2,3)
Print "5-bit:"+ReadData(b2,5)
Print "6-bit:"+ReadData(b2,6)
