; ID: 2752
; Author: EsseEmmeErre
; Date: 2010-08-16 16:04:37
; Title: Diagonal stripes
; Description: How to generate stripes

;-> Strisce Diagonali by Stefano Maria Regattin
;i> Monday 16 August 2010
;-------------------------
AppTitle("Striscie diagonali by Stefano Maria Regattin")
AltezzaFinestra=GraphicsHeight()
LarghezzaFinestra=GraphicsWidth()
LarghezzaStriscia=20
For YPunto=0 To AltezzaFinestra-1
 For XPunto=0 To LarghezzaFinestra-1
  Punto=XPunto+YPunto
  If Punto Mod LarghezzaStriscia=LarghezzaStriscia-1 Then
   If StrisciaGialla=False
    StrisciaGialla=True
   Else
    StrisciaGialla=False
   EndIf
  EndIf
  If StrisciaGialla=False
   Rosso=95:Verde=95:Blu=95
  Else
   Rosso=191:Verde=191:Blu=0
  EndIf
  Color(Rosso,Verde,Blu):Plot(XPunto,YPunto)
 Next
 If YPunto Mod LarghezzaStriscia=LarghezzaStriscia-1 Then
  If StrisciaGialla=False
   StrisciaGialla=True
  Else
   StrisciaGialla=False
  EndIf
 EndIf
Next
MouseWait()
End
