; ID: 61
; Author: TFT (der Falke)
; Date: 2001-09-27 02:42:12
; Title: QuickSort
; Description: The Fast Quick Sort algo

Print
Print" BlitzBasic V 1.60"
Print
Print" QuickSort v0.4 (C) by TFT (der Falke) Rev.0001-2"
Print
Print" Code date 23.8.2001 / 24.8.2001"
Print" SerNr: 2001.0002-0
Print" EMail tft@optima-code.de
Print" Inter http://www.optima-code.de
Print
Delay 5000

Dim c(1000000)

For i=0To 1000000
c(i)=-Rnd(100000)
Next

t=MilliSecs()
a=quicksort(0,1000000)
t=MilliSecs()-t

Print "1000000 Element mit QickSort"
Print "Time "+Str$(t)

For i=0To 20
Print c(i)
Next

Repeat
Until KeyHit(1)

End

Function quicksort(l,r)
  Local p,q,h
  p=l
  q=r
  x=c((l+r)/2)
  Repeat
    While c(p)<x
      p=p+1
    Wend
    While x<c(q)
      q=q-1
    Wend
    If p>q Then Exit
	;SWAP------------------
	h=c(q)
	c(q)=c(p)
	c(p)=h
	;----------------------
    p=p+1
    q=q-1
    If q<0 Then Exit
  Forever 
  If l<q Then a=quicksort(l,q)
  If p<r Then a=quicksort(p,r)
  Return True
End Function
