; ID: 2174
; Author: Junkprogger
; Date: 2007-12-29 13:17:15
; Title: Unicode/UTF-8 en/decoding
; Description: UTF-8 en/decoding

SuperStrict
Framework brl.blitz
Import brl.system


Local text:String = "wünsche"
Local array:Byte[text.length]

For Local i:Int = 0 Until text.length
   array[i] = text[i]
Next

Local dec_text:String = TUTF8.utf8ToString(array)

Local utf8_text:String
For Local i:Int = 0 Until dec_text.length
   Local res:Byte[] = TUTF8.fromInt(dec_text[i])
   For Local b:Int = EachIn res
      utf8_text:+Chr(b)
   Next
Next


Notify text
Notify dec_text
Notify utf8_text

Type TUTF8
   Function getlength:Int(i:Int)
      Select True
         Case i<$7f                  Return 1
         Case i=>$7f And i<$7ff         Return 2
         Case i=>$7ff And i<$ffff      Return 3
         Case i=>$ffff And i<$1fffff      Return 4
         Case i=>$1fffff And i<$3ffffff   Return 5
         Case i=>$3ffffff And i<$7ffffff   Return 6
      End Select
   End Function
   
   Function fromInt:Byte[](code:Int)
      Local value:Byte[]
      Local length:Int = getlength(code)
      value = New Byte[length]
      Select length
         Case 1
            value[0] = code
         Case 2
            value[0] = 192+(code/64)
            value[1] = 128+(code Mod 64)
         Case 3
            value[0] = 224+(code/4096)
            value[1] = 128+((code/64) Mod 64)
            value[2] = 128+(code Mod 64)
         Case 4
            value[0] = 240+(code/262144)
            value[1] = 128+((code/4096) Mod 64)
            value[2] = 128+((code/64) Mod 64)
            value[3] = 128+(code Mod 64)
         Case 5
            value[0] = 248+(code/16777216)
            value[1] = 128+((code/262144) Mod 64)
            value[2] = 128+((code/4096) Mod 64)
            value[3] = 128+((code/64) Mod 64)
            value[4] = 128+(code Mod 64)
         Case 6
            value[0] = 252+(code/1073741824)
            value[1] = 128+((code/16777216) Mod 64)
            value[2] = 128+((code/262144) Mod 64)
            value[3] = 128+((code/4096) Mod 64)
            value[4] = 128+((code/64) Mod 64)
            value[5] = 128+(code Mod 64)
      End Select
      Return value
   End Function 
   
   Function utf8ToString:String(_data:Byte[])
      Local length:Int,str:String,b:Int,x:Int
      For Local i:Int = 0 Until _data.length
         b=_data[i]
         length = getlength(b)
         If (i+length-1)>_data.length Exit
         Select length
            Case 1
               x=b
            Case 2
               x=( ((b-192)*64) + (_data[i+1]-128) )
            Case 3
               x=( ((b-224)*4096) + ((_data[i+1]-128)*64) + (_data[i+2]-128) )
            Case 4
               x=( ((b-240)*262144) + ((_data[i+1]-128)*4096) + ((_data[i+2]-128)*64) + (_data[i+3]-128))
            Case 5
               x=( ((b-248)*16777216) + ((_data[i+1]-128)*262144) + ((_data[i+2]-128)*4096) + ((_data[i+3]-128)*64) + (_data[i+4]-128))
            Case 6
               x=( ((b-252)*1073741824) + ((_data[i+1]-128)*16777216) + ((_data[i+2]-128)*262144) + ((_data[i+3]-128)*4096) + ((_data[i+4]-128)*64) + (_data[i+5]-128))
         End Select
         str:+Chr(x)
         i:+(length-1)
      Next
      Return str
   End Function 
   
End Type
