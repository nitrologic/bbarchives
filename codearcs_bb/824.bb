; ID: 824
; Author: Ziltch
; Date: 2003-11-11 08:44:24
; Title: Read Comma Delimited String
; Description: Use ReadCSVString$(Stream) to read quote enclosed comma delmited data into strings.

Function ReadCSVString$(Stream)
; ADAmor Ziltch Nov 2003

  local qte$,Ccnt,byt,char$
  qte$=chr(34)
  OutString$ = ""
  Ccnt = 0
  while char$<>qte$ and eof(stream)=0
    Ccnt = Ccnt + 1
    byt= readbyte(stream)
    char$=chr(byt)
  wend
  char$=""
  while char$<>qte$ and eof(stream)=0
    Ccnt = Ccnt + 1
    byt= readbyte(stream)
    char$=chr(byt)
    if char$ <> qte$ then OutString$=OutString$+char$
  wend
  
  return OutString$
End Function
