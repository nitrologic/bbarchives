; ID: 1505
; Author: markcw
; Date: 2005-10-28 07:30:44
; Title: Convert any file to bb data file
; Description: some code to convert binary data into bb data file

;Convert any file to bb data file, on 28/10/2005
;
;what it does: this will read any file's binary data and convert it
;to the Blitzbasic data stack format. The resulting .bb file can be
;used, for example, to "pack" image files into a Blitzbasic program 
;or, to store a file as code without the need for web hosting space.
;The example used the blitzclose.zip (see ../User Input/Blitz Close)

file$ = "blitzclose.zip" ;<- specify a file to convert to .bb data
datafile$ = "blitzclose.bb" ;<- specify the .bb file to create

filein = ReadFile(file$) ;Read file
size = FileSize (file$)
Dim array(size)
For s=1 To size ;read bytes to array
 byte = ReadByte(filein)
 array(s) = byte
Next
CloseFile(filein) ;End read file

; ;Paste this commented code at the start of the .bb file
; ;This code will just recreate the file from the data stack
;Restore startData ;init data pointer
;fileout = WriteFile("blitzclose.zip") ;<- specify the file to recreate
;While byte>=0
; Read byte ;get next byte
; WriteByte(fileout,byte)
;Wend
;CloseFile(fileout)
;WaitKey()

fileout = WriteFile(datafile$) ;Write file

temp$=";paste code here" ;comment
For s=1 To Len(temp$)
 char$=Mid$(temp$,s,1) : WriteByte(fileout,Asc(char$))
Next
WriteByte(fileout,13) : WriteByte(fileout,10) ;carr return & linefeed
temp$=".startData" ;first "Data " line
For s=1 To Len(temp$)
 char$=Mid$(temp$,s,1) : WriteByte(fileout,Asc(char$))
Next
WriteByte(fileout,13) : WriteByte(fileout,10) ;carr return & linefeed
temp$="Data " ;first "Data " line
For s=1 To Len(temp$)
 char$=Mid$(temp$,s,1) : WriteByte(fileout,Asc(char$))
Next

For i=1 To size ;write bytes from array to ascii data

 If count>16 ;new line
  count=0
  WriteByte(fileout,13) : WriteByte(fileout,10) ;carr return & linefeed
  temp$="Data "  ;next "Data " line
  For s=1 To Len(temp$)
   char$=Mid$(temp$,s,1) : WriteByte(fileout,Asc(char$))
  Next
 EndIf

 If array(i)<10 ;write single figures
  WriteByte(fileout,array(i)+48) ;units (ie. write ascii "0-9")
  count=count+1 ;(ie. comma if not last on line and not last byte)
  If count<=16 And i<>size Then WriteByte(fileout,Asc(",")) ;comma
 EndIf

 If array(i)>=10 And array(i)<100 ;write double figures
  num=array(i) : tens=0
  While num>=10 : num=num-10 : tens=tens+1 : Wend ;get tens & units
  WriteByte(fileout,tens+48) ;tens
  WriteByte(fileout,num+48)  ;units
  count=count+1
  If count<=16 And i<>size Then WriteByte(fileout,Asc(",")) ;comma
 EndIf

 If array(i)>=100 And array(i)<256 ;write treble figures
  num=array(i) : tens=0 : cents=0
  While num>=100 : num=num-100 : cents=cents+1 : Wend ;get cents
  While num>=10 : num=num-10 : tens=tens+1 : Wend     ;get tens & units
  WriteByte(fileout,cents+48) ;cents
  WriteByte(fileout,tens+48)  ;tens
  WriteByte(fileout,num+48)   ;units
  count=count+1
  If count<=16 And i<>size Then WriteByte(fileout,Asc(",")) ;comma
 EndIf

Next

CloseFile(fileout) ;End write file

Print "file in="+file$ ;tell us its done
Print "file size="+size
Print "file out="+datafile$

WaitKey()
