; ID: 1820
; Author: markcw
; Date: 2006-09-20 20:55:05
; Title: Read image and write to bb data file
; Description: For packing images in executable

;Read Image Write Data, on 21/9/06

Graphics 640,480,0,2
SetBuffer BackBuffer()

fileout$="temp.bb"
filein$="yourname.bmp" ;image in

image=LoadImage(filein$)
ok=ReadImageWriteData(filein$,fileout$,image)

;Main loop
While Not KeyHit(1)
 Cls

 DrawImage image,50,50 ;draw image in

 Text 0,0,"ok="+ok+" filein$="+filein$+" fileout$="+fileout$
 Text 0,20,"width="+ImageWidth(image)+" height="+ImageHeight(image)

 Flip
Wend

;Functions
Function ReadImageWriteData(filein$,fileout$,image)
 ;Read an image and write 24-bit color data to bb file
 ;filein$=image file, fileout$=bb file, image=image handle

 Local file,width,height,buffer,imagesize,x,y,rgb,nextrgb,count

 ;Write fileout.bb
 If fileout$="" Then Return False ;fail code
 file=WriteFile(fileout$)
 If Not file Then Return False ;write fail

 width=ImageWidth(image)
 height=ImageHeight(image)
 buffer=ImageBuffer(image)
 imagesize=width Or (height Shl 16)

 ;write title comment, image data label and data command
 WriteStringAscii(file,";"+fileout$)
 WriteByte file,13 : WriteByte file,10 ;newline
 WriteByte file,13 : WriteByte file,10 ;newline
 WriteStringAscii(file,"."+Left(filein$,Len(filein$)-4))
 WriteStringAscii(file,"_"+Right(filein$,3))
 WriteByte file,13 : WriteByte file,10 ;newline
 WriteStringAscii(file,"Data ")
 WriteValueAscii(file,imagesize) ;first value is width/height

 ;read pixels to 8-bit run/24-bit color integer
 LockBuffer buffer
 For y=0 To height-1
  For x=0 To width-1
   rgb=ReadPixelFast(x,y,buffer) And $00FFFFFF
   If x<width-1
    nextrgb=ReadPixelFast(x+1,y,buffer) And $00FFFFFF
   Else
    nextrgb=$FF000000
   EndIf
   If rgb=nextrgb Then count=count+1
   If rgb<>nextrgb Or x=width-1 Or count=254
    integer=(count+1) Or (rgb Shl 8)
    WriteByte file,Asc(",")
    WriteValueAscii(file,integer)
    If count=254 Then count=-1 Else count=0
   EndIf
  Next
 Next
 UnlockBuffer buffer

 ;write end comment
 WriteByte file,13 : WriteByte file,10 ;newline
 WriteStringAscii(file,";end "+Left(filein$,Len(filein$)-4))
 WriteStringAscii(file,"_"+Right(filein$,3))

 CloseFile file
 Return True ;success code

End Function

Function WriteValueAscii(file,value)
 ;file=file handle, value=byte/short/integer

 Local ascii$,i,char$
 ascii$=Str(value)
 For i=1 To Len(ascii$)
  char$=Mid(ascii$,i,1)
  WriteByte(file,Asc(char$))
 Next

End Function

Function WriteStringAscii(file,ascii$)
 ;file=file handle, ascii$=ascii string

 Local i,char$
 For i=1 To Len(ascii$)
  char$=Mid(ascii$,i,1)
  WriteByte(file,Asc(char$))
 Next

End Function
