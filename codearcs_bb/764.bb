; ID: 764
; Author: Adam_128
; Date: 2003-08-16 11:00:38
; Title: Simple Flakes
; Description: Snow flakes for your every day winter needs

;Simple Flakes by Adam Kewley
;
;press esc to exit

Graphics 640,480,32,1

Type flakes
 Field x#
 Field y#
 Field c
End Type


Global flake.flakes
Const TOTALFLAKES=400

SetBuffer(BackBuffer())

InitFlakes()

While Not(KeyDown(1))
 Cls
 UpdateFlakes()
 Flip
Wend


;------------------------------------------------------------------------------------------------------------------------

Function InitFlakes()

  For x = 1 To TOTALFLAKES
   flake.flakes = New flakes
   flake\x#=Rnd(640,-70)
   flake\y#=Rnd(480,0)
   flake\c=Rnd(4,0)
  Next

End Function

Function UpdateFlakes()

  For flake.flakes = Each flakes

   If flake\y#>480 Then 
    flake\x#=Rnd(640,-70)
    flake\y#=0
    flake\c=Rnd(4,0)
   End If

     Select flake\c    
      Case 1
       Color 80,80,80
       dir=Rnd(-.5,1)
       flake\x#=flake\x#+dir+.1
       flake\y#=flake\y#+.8
       Oval flake\x#,flake\y#,1,1,1
      Case 2
       Color 120,120,120
       dir=Rnd(-1,1.5)
       flake\x#=flake\x#+dir+.1
       flake\y#=flake\y#+1
       Oval flake\x#,flake\y#,2,2,1
      Case 3
       Color 180,180,180
       dir=Rnd(-1,2)
       flake\x#=flake\x#+dir+.1
       flake\y#=flake\y#+1.5
       Oval flake\x#,flake\y#,3,3,1
      Case 4
       Color 250,250,250
       dir=Rnd(-2,2.6)
       flake\x#=flake\x#+dir+.1
       flake\y#=flake\y#+2
       Oval flake\x#,flake\y#,4.5,4.5,1
     End Select   
  Next

End Function
