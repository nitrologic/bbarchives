; ID: 2770
; Author: Krischan
; Date: 2010-09-19 13:50:58
; Title: Elite Planet Name Generator
; Description: The algorithm the game 'Elite' used to create planet names

AppTitle "Elite Planet Name Generator"

Global seed%[2],elite$=True
Global syllables$="..lexegezacebisousesarmaindirea.eratenberalavetiedorquanteisrion"

If (elite) Then
    seed[0]=$5A4A
    seed[1]=$0248
    seed[2]=$B753
Else
    seed[0]=Rand($ffff)
    seed[1]=Rand($ffff)
    seed[2]=Rand($ffff)
    
EndIf

For i=0 To 20
    
    Local output$=CreateName()
    
    If output="Lave" Or output="Zaonce" Or output="Isinor" Then
        Color 0,255,0
        Print output+" ["+i+"]"
    Else
        Color 255,255,255
        Print output
    EndIf
    
Next


WaitKey
End

Function Tweakseed()
    
    Local temp%=(seed[0]+seed[1]+seed[2]) Mod $10000
    seed[0]=seed[1]
    seed[1]=seed[2]
    seed[2]=temp
    
End Function

Function CreateName$()
    
    Local longnameflag=seed[0] And $40
    Local planetname$=""
    Local c%,n%,d%
    
    For n=0 To 3
        
        d=((seed[2] Shr 8) And $1f) Shl 1
        Tweakseed()
        
        If n<3 Or longnameflag Then
			
            planetname=planetname+Mid(syllables,1+d,2)
            planetname=Replace(planetname,".","")
            
        EndIf
        
    Next
    
    planetname=Upper(Mid(planetname,1,1))+Mid(planetname,2,Len(planetname)-1)
    
    Return planetname
    
End Function
