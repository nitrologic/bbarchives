; ID: 708
; Author: Prof
; Date: 2003-05-29 14:34:50
; Title: Pic-2-DATA utility
; Description: Include image DATA in your source code

; Pic-2-Data by Prof - Include images in your source as DATA statements
;
; Only use for small images like sprites / icons etc. 
;
; WARNING! -  Even though this program converts each pixel and stores it
; as a shortened Hex ARGB value, (i.e. $0000FF00 is stored as $FF00),
; the file size can still be very large.
;
; The program also gives you an option to save the decoder in the
; source code as well.
;
;
; Main Window area
WinWidth=320:WinHeight=200
WinTitle$=WinTitle$+"Pic2Data v1.4"
WinX=(ClientWidth(Desktop())-WinWidth)/2
WinY=(ClientHeight(Desktop())-WinHeight)/2
Global Window = CreateWindow(WinTitle$,WinX,WInY,WinWidth,WinHeight,0,1) 

;Create a canvas
Global canvas=CreateCanvas( 0,0,GadgetWidth(Window),GadgetHeight(Window),Window)
SetGadgetLayout canvas,1,0,1,0
SetBuffer CanvasBuffer(canvas)
ClsColor 0,0,0:Cls
Color 200,200,200:
Text 75,20,"Pic2Data V1.4"
Text 15,50,"Designed By The Prof (c)2003"
Text 35,80,"For Blitzers Everywhere!"


;vars
Global font=LoadFont("Arial",14,False,False,False)
SetFont font
Global ImageID=0
Global ImageLoaded=False
Global PicWidth,PicHeight
Global ButtonHeight=20,ButtonWidth=60

;Create some buttons
ButtonY=1
ReadMe=CreateButton("Read Me",GadgetWidth(canvas)-ButtonWidth-7,ButtonY,ButtonWidth,ButtonHeight,Canvas,1)
ButtonY=ButtonY+ButtonHeight+1
Load=CreateButton("Load Pic",GadgetWidth(canvas)-ButtonWidth-7,ButtonY,ButtonWidth,ButtonHeight,Canvas,1)
ButtonY=ButtonY+ButtonHeight+1
Save=CreateButton("Save Data",GadgetWidth(canvas)-ButtonWidth-7,ButtonY,ButtonWidth,ButtonHeight,Canvas,1)
ButtonY=ButtonY+ButtonHeight+1
Quit=CreateButton("- Exit -",GadgetWidth(canvas)-ButtonWidth-7,ButtonY,ButtonWidth,ButtonHeight,Canvas,1)


;Main loop
While WaitEvent()<>$803
  ID=EventID()
  If ID=$401 
     If EventSource()=Quit Then ExitProgram
     If EventSource()=ReadMe Then ReadMe
     If EventSource()=Load Then LoadFile
     If EventSource()=Save
        If ImageLoaded=True
           SaveAsHexData
        Else
           Notify("Please load an image first")
        EndIf
     EndIf
  EndIf
Wend
FreeGadget(Window)
End

; ************************************

Function SaveAsHexData()
  f$=RequestFile("Save as DATA file (.bb)","bb", True)
  If f$<>""
     Decoder=Confirm("Save the decoder as well?")
     FileOut=WriteFile(f$) ; open a file to write to
     If Decoder=True Then WriteHexDecoder(FileOut)

     ; Write out width & height of the image first!
     WriteLine(FileOut,"Data "+Str(PicWidth)+","+Str(PicHeight))

     ; Now encode the image & write to the file
     CurrentLineLength=0:MaxLineLength=10
     TempLine$=""
     For y=0 To PicHeight-1
         For x=0 To PicWidth-1
             GetColor(x,y)
             r=ColorRed():g=ColorGreen():b=ColorBlue()
             rgb=GetRGB(r,g,b)
             TempRGB$=Str(Hex$(rgb))
             RealRGB$="$"+StripLeftZerosFromHexString(TempRGB$)
             
             If CurrentLineLength=0
                TempLine$="Data "
             EndIf
             TempLine$=TempLine$+RealRGB$
             CurrentLineLength=CurrentLineLength+1
             If CurrentLineLength<MaxLineLength
                TempLine$=TempLine$+"," ; add a comma to the last blue
             EndIf
             If CurrentLineLength=MaxLineLength
                WriteLine(FileOut,TempLine$)
                CurrentLineLength=0:TempLine$=""
             EndIf
         Next
     Next
     If CurrentLineLength>0 ; write out the last line
        L=Len(TempLine$)
        TempLine$=Mid$(TempLine$,1,L-1); get rid of last comma
        WriteLine(FileOut,TempLine$)
     EndIf
     CloseFile(FileOut)
  EndIf
End Function

; *****************************************

Function GetRGB(red,green,blue)
	Return blue Or (green Shl 8) Or (red Shl 16)
End Function

; *****************************************

Function StripLeftZerosFromHexString$(v$)
  l=Len(v$):t$="":pos=1

  ; Go through the string until not a zero
  c$=Mid$(v$,Pos,1)
  While c$="0" And Pos<L
    Pos=Pos+1
    c$=Mid$(v$,Pos,1)
  Wend

  ; Now create the new string from this position
  For P=Pos To L
     c$=Mid$(v$,P,1)
     t$=t$+c$
  Next
  Return t$
End Function

; *****************************************

Function LoadFile()
  f$=RequestFile$("Load an image file (bmp,jpg,png)","bmp,jpg,png")
  If f$<>""
     Cls:ImageID=LoadImage(f$)
     PicWidth=ImageWidth(ImageID)
     PicHeight=ImageHeight(ImageID)
     Viewport 0,0,GadgetWidth(Canvas)-ButtonWidth-10,GadgetHeight(Canvas)-35
     DrawBlock ImageID,0,0:Color 200,200,200
     Viewport 0,0,GadgetWidth(Window),GadgetHeight(Window)

     If PicWidth>(GadgetWidth(Canvas)-ButtonWidth-10)
        PicWidth=(GadgetWidth(Canvas)-ButtonWidth-10)
     EndIf
     If PicHeight>GadgetHeight(Canvas)-35
        PicHeight=GadgetHeight(Canvas)-35
     EndIf

     Pixels=PicWidth*PicHeight
     SizeOriginal=FileSize(f$)

     Text GadgetWidth(canvas)-ButtonWidth-7,85,"Image Size"
     Text GadgetWidth(canvas)-ButtonWidth-7,95,Str(PicWidth)+" x "+Str(PicHeight)
     Text GadgetWidth(canvas)-ButtonWidth-7,115,"File Size"
     Text GadgetWidth(canvas)-ButtonWidth-7,125,Str(SizeOriginal)
     
     ImageLoaded=True
  EndIf
End Function

; ******************************************

Function ReadMe()
 t$="Pic2Data By The Prof (c)2003"+Chr$(13)+Chr$(13)
 t$=t$+"Converts an image into DATA statements"+Chr$(13)
 t$=t$+"so you can include them in Blitz Basic."+Chr$(13)+Chr$(13)
 t$=t$+"The resulting file is VERY LARGE! Only useful for"+Chr$(13)
 t$=t$+"things like sprites/icons (max 50x50 pixels)"+Chr$(13)+Chr$(13)
 t$=t$+"TIP: ensure desktop is set to 32-Bit colour and"+Chr$(13)
 t$=t$+"only convert images that are small in size."+Chr$(13)
 Notify(t$)
End Function

; ******************************************

Function ExitProgram()
  q=Confirm("Do you really want to quit?")
  If q=True
     FreeGadget(Window):End
  EndIf
End Function

; ******************************************

Function WriteHexDecoder(FileOut)
  WriteLine(FileOut,"Graphics 320,200,32,2")
  WriteLine(FileOut,";")
  WriteLine(FileOut,"DisplayImage(0,0)")
  WriteLine(FileOut,"Waitkey():End")
  WriteLine(FileOut,";")
  WriteLine(FileOut,";*****************************")
  WriteLine(FileOut,"Function DisplayImage(StartX,StartY)")
  WriteLine(FileOut,"  Read Width,Height")
  WriteLine(FileOut,"  For Y=StartY To StartY+(Height-1)")
  WriteLine(FileOut,"    For X=StartX To StartX+(Width-1)")
  WriteLine(FileOut,"      Read rgb")
  WriteLine(FileOut,"      r=GetRed(rgb):g=GetGreen(rgb):b=GetBlue(rgb)")
  WriteLine(FileOut,"      Color r,g,b:Plot x,y")
  WriteLine(FileOut,"    Next")
  WriteLine(FileOut,"  Next") 
  WriteLine(FileOut,"End Function")
  WriteLine(FileOut,";")
  WriteLine(FileOut,"Function GetRed(rgb)") 
  WriteLine(FileOut,"  Return rgb Shr 16 And %11111111") 
  WriteLine(FileOut,"End Function") 
  WriteLine(FileOut,";")
  WriteLine(FileOut,"Function GetGreen(rgb)") 
  WriteLine(FileOut,"  Return rgb Shr 8 And %11111111") 
  WriteLine(FileOut,"End Function") 
  WriteLine(FileOut,";")
  WriteLine(FileOut,"Function GetBlue(rgb)") 
  WriteLine(FileOut,"  Return rgb And %11111111") 
  WriteLine(FileOut,"End Function")
  WriteLine(FileOut,";")
  WriteLine(FileOut,";Use 'Restore' and a .Label if using more images.")
End Function
