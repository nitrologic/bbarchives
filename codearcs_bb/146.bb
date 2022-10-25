; ID: 146
; Author: Kostik
; Date: 2002-03-03 05:10:34
; Title: EXTREMELY FAST FADER...
; Description: EXTREMELY FAST FADER...

Graphics 640,480,32,1
Global fader_tmp=CreateImage(300,100)
Global fader_max=1
Dim    fader_bmp(fader_max)
Dim    fader_col(fader_max)
Dim    fader_r  (fader_max,15)
Dim    fader_g  (fader_max,15)
Dim    fader_b  (fader_max,15)



Delay 1000 ;example
loadfader 0,"bitmap.bmp"
animfader 0,170,190,0,2000
animfader 0,170,190,1,2000
freefader 0
FreeImage fader_tmp
WaitKey()
End



Function animfader(nr,x,y,mode,time)
  source=ImageBuffer(fader_tmp)
  dest  =BackBuffer()
  sizex =ImageWidth(fader_bmp(nr))
  sizey =ImageHeight(fader_bmp(nr))
  time2 =MilliSecs()
  Repeat
    time3=MilliSecs()-time2
    pro=(1000*time3)/time
    If pro>1000 Then pro=1000
    If mode=1 Then pro=1000-pro
    SetBuffer source
    DrawBlock fader_bmp(nr),0,0
    SetBuffer dest
    For i=0 To fader_col(nr)-1
      MaskImage fader_tmp,fader_r(nr,i),fader_g(nr,i),fader_b(nr,i)
      Color (fader_r(nr,i)*pro)/1000,(fader_g(nr,i)*pro)/1000,(fader_b(nr,i)*pro)/1000
      Rect x,y,sizex,sizey,1
      DrawImageRect fader_tmp,x,y,0,0,sizex,sizey
      CopyRect x,y,sizex,sizey,0,0,dest,source
    Next
    Flip
  Until time3>time
  If mode=0 Then DrawBlock fader_bmp(nr),x,y
  If mode=1 Then Color 0,0,0: Rect x,y,sizex,sizey,1
  Flip
End Function



Function freefader(nr)
  If nr<0 Then Return
  If nr>fader_max Then Return
  If fader_bmp(nr)=0 Then Return
  FreeImage fader_bmp(nr)
  fader_bmp(nr)=0
End Function



Function loadfader(nr,file$)
  If nr<0 Then Return
  If nr>fader_max Then Return
  If fader_bmp(nr)<>0 Then Return
  fader_bmp(nr)=LoadImage(file$)
  If fader_bmp(nr)=0 Then Return
  open=ReadFile(file$)
  SeekFile open,28
  depth=ReadShort(open)
  If depth<>4 Then
    CloseFile open
    freefader nr
    Return
  End If
  SeekFile open,54
  For i=0 To 15
    colb =ReadByte(open)
    colg =ReadByte(open)
    colr =ReadByte(open)
    dummy=ReadByte(open)
    If fader_col(nr)>0 Then
      For ii=0 To fader_col(nr)-1
        If fader_r(nr,ii)=colr And fader_g(nr,ii)=colg And fader_b(nr,ii)=colb Then
          colr=0
          colg=0
          colb=0
        End If
      Next
    End If
    If colr+colg+colb>0 Then
      fader_col(nr)=fader_col(nr)+1
      fader_r(nr,fader_col(nr)-1)=colr
      fader_g(nr,fader_col(nr)-1)=colg
      fader_b(nr,fader_col(nr)-1)=colb
    End If
  Next
  CloseFile open
  For i=0 To fader_col(nr)-2
    For ii=i+1 To fader_col(nr)-1
      If fader_r(nr,i)+fader_g(nr,i)+fader_b(nr,i)>fader_r(nr,ii)+fader_g(nr,ii)+fader_b(nr,ii) Then
        fader_r_tmp=fader_r(nr,i)
        fader_g_tmp=fader_g(nr,i)
        fader_b_tmp=fader_b(nr,i)
        fader_r(nr,i)=fader_r(nr,ii)
        fader_g(nr,i)=fader_g(nr,ii)
        fader_b(nr,i)=fader_b(nr,ii)
        fader_r(nr,ii)=fader_r_tmp
        fader_g(nr,ii)=fader_g_tmp
        fader_b(nr,ii)=fader_b_tmp
      End If
    Next
  Next
End Function
