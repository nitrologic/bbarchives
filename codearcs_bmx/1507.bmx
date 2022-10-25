; ID: 1507
; Author: Shagwana
; Date: 2005-10-28 12:07:23
; Title: Calc Angle Between Two Angles
; Description: Calculate the inbetween angle from one angle to the other

'Calculate the angle from Ang1 to Ang2. Valid angle values must be 0.0 to 360.0
Function CalcAngle:Float(Ang1:Float,Ang2:Float)
  If Ang1=Ang2
    'No angle to compute
    Return 0.0
    Else
    'There is an angle to compute
    Local fDif:Float=(Ang2-Ang1)   
    If fDif>=180.0
      fDif=fDif-180.0    'Correct the half
      fDif=180.0-fDif    'Invert the half
      fDif=0-fDif        'Reverse direction
      Return fDif
      Else
      If fDif<=-180.0
        fDif=fDif+180.0    'Correct the half
        fDif=180.0+fDif    
        Return fDif
        EndIf
      EndIf
    Return fDif
    EndIf
  End Function
