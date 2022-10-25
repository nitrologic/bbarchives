; ID: 2601
; Author: EsseEmmeErre
; Date: 2009-11-01 03:52:13
; Title: PearlDiary
; Description: Code to type text

;-> Pearl Diary Public by EsseEmmeErre
;d> 1 Nov 2009 from Pearl Diary
;-------------------------------
AppTitle("PearlDiary by EsseEmmeErre","Would you leave?")
Const FormaPiena=1
Const FormaVuota=0
Const TastoIndietroASCII=8
Const TastoTabulazioneASCII=9
Const TastoInvioASCII=13
Const TastoFugaASCII=27
Const TastoCursoreSopraASCII=28
Const TastoCursoreSottoASCII=29
Const TastoCursoreDestraASCII=30
Const TastoCursoreSinistraASCII=31
Const TastoSpazioASCII=32
ClsColor(255,255,255):Color(0,0,0)
FineDellaDigitazione=False
XCurs=0:XCursMax=GraphicsWidth()/FontWidth()-1
YCurs=0:YCursMax=GraphicsHeight()/FontHeight()-1
CaratteriMax=(XCursMax+1)*(YCursMax+1)
Dim Caratteri(CaratteriMax-1)
Repeat
 Cls()
 For Carattere=0 To CaratteriMax-1
  If Caratteri(Carattere)>0 Then
   XPosCarattere=Carattere Mod (XCursMax+1)*FontWidth()
   YPosCarattere=Carattere/(XCursMax+1)*FontHeight()
   Locate(XPosCarattere,YPosCarattere):Write(Chr$(Caratteri(Carattere)))
  EndIf
 Next
 If XCurs+YCurs*(XCursMax+1)<CaratteriMax Then
  Rect(XCurs*FontWidth(),YCurs*FontHeight(),FontWidth(),FontHeight(),FormaPiena)
 EndIf
 TastoPremutoASCII=WaitKey()
 Select TastoPremutoASCII
  Case TastoIndietroASCII
   XCurs=XCurs-1
   For Carattere=0 To CaratteriMax-1
    If Carattere>XCurs+YCurs*(XCursMax+1) Then Caratteri(Carattere-1)=Caratteri(Carattere)
   Next
  Case TastoTabulazioneASCII
   XCurs=XCurs+3
  Case TastoInvioASCII
   XCurs=0:If YCurs<YCursMax Then YCurs=YCurs+1   
  Case TastoFugaASCII
   FineDellaDigitazione=True
  Case TastoCursoreSopraASCII
   If YCurs>0 Then YCurs=YCurs-1
  Case TastoCursoreSottoASCII
   If YCurs<YCursMax Then YCurs=YCurs+1
  Case TastoCursoreDestraASCII
   XCurs=XCurs+1:If XCurs>XCursMax Then XCurs=0:YCurs=YCurs+1
  Case TastoCursoreSinistraASCII
   XCurs=XCurs-1:If XCurs<0 Then XCurs=XCursMax:If YCurs>0 Then YCurs=YCurs-1
  Default
   Caratteri(XCurs+YCurs*(XCursMax+1))=TastoPremutoASCII
   XCurs=XCurs+1:If XCurs>XCursMax Then XCurs=0:If YCurs<YCursMax Then YCurs=YCurs+1
 End Select
Until FineDellaDigitazione=True
End
