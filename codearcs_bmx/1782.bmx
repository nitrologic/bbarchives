; ID: 1782
; Author: splinux
; Date: 2006-08-08 13:24:40
; Title: Von Neumann Random Numbers Generator
; Description: Random numbers generator

'Von Neumann Random Numbers Generator
'getnumber() returns a number between 0 and 1
'rnddouble(min, max) return a double number between min and max
'rnd(min, max) return an integer number between min and max


''CLASS:
Type TRandom
  Field seed:Double
  Field last:Double

  Function create:TRandom(sd:Double)
    Local r:TRandom=New TRandom
    r.seed=sd
    r.last=sd
    Return r
  End Function

  Method setseed(sd:Double)
    self.seed=sd
  End Method

  Method getnumber:Double()
    Local n:Double=(16807*self.last+0) Mod 2147483647
    self.last=n
    n=n/2147483646
    Return n
  End Method

  Method RndDouble:Double(s:Double, e:Double)
    Local n:Double=self.getnumber()
    n=(e-s)*n+s
    Return n
  End Method

  Method Rnd:Int(s:Double, e:Double)
    Local n:Double=self.getnumber()
    n=(e-s)*n+s

    Local st:String=String(n)
    Local start=st.find(".")
    st=Mid(st, start+2, 1)

    Local n2:Int=Int(st)
    If n2>=5
      n=Ceil(n)
    Else
      n=Floor(n)
    EndIf

    Return Int(n)
  End Method
End Type




''EXAMPLE:
Global a:TRandom=TRandom.create(MilliSecs())

For i=0 To 100
  Print a.rnd(0, 100)
Next
