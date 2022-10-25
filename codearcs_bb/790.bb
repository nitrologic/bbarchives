; ID: 790
; Author: MrCredo
; Date: 2003-09-07 09:09:03
; Title: CRC32 - Checksum calculator
; Description: Clean CRC32 - Checksum calculator for string/bank/file

Dim crc_table(255)
crc_init()

Print Hex$(crc_string("ABC"))
Input





Function crc_init()
  Local i
  Local j
  Local value

  For i=0 To 255
    value=i
    For j=0 To 7
      If (value And $1) Then 
        value=(value Shr 1) Xor $EDB88320
      Else
        value=(value Shr 1)
      EndIf
    Next
    crc_table(i)=value
  Next
End Function





Function crc_string(txt$)
  Local byte
  Local crc
  Local i
  Local size

  crc=$FFFFFFFF
  size=Len(txt$)
  For i=1 To size
    byte=Asc(Mid$(txt$,i,1))
    crc=(crc Shr 8) Xor crc_table(byte Xor (crc And $FF))
  Next
  Return ~crc
End Function






Function crc_bank(bank)
  Local byte
  Local crc
  Local i
  Local size

  crc=$FFFFFFFF
  size=BankSize(bank)-1
  For i=0 To size
    byte=PeekByte(bank,i)
    crc=(crc Shr 8) Xor crc_table(byte Xor (crc And $FF))
  Next
  Return ~crc
End Function





Function crc_file(name$)
  Local byte
  Local crc
  Local file

  crc=$FFFFFFFF
  file=ReadFile(name$)
  If file=0 Then Return
  While Not Eof(file)
    byte=ReadByte(file)
    crc=(crc Shr 8) Xor crc_table(byte Xor (crc And $FF))
  Wend
  Return ~crc
End Function
