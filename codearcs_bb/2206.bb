; ID: 2206
; Author: zoom*
; Date: 2008-01-31 13:19:13
; Title: Some filters for image
; Description: Some filters for image

Graphics 512,512,0,2
SetBuffer FrontBuffer()


Dim iImage(512,512)

Dim mTransform(3,3) ;matrix for transformation

Dim mValue(3,3) ;matrix 3x3 focused on the pixel which has to be treated
Global factNorm = 0

LoadImageInBuffer( "essai2.jpg" )
;InitMatrixFlouUniforme();Just delete ";" before the line to  use this matrix
;InitMatrixPREWITT()
;InitMatrixROBERTS()
;InitMatrixSOBEL()
;InitMatrixKIRSCH()
;InitMatrixLOG()
ApplyTransformationMatrix()




Function LoadImageInBuffer( imageName$, color2baw = False)

image = LoadImage(imageName$)

DrawImage image,0,0
 

    For y = 0 To ImageHeight(image)

        For x = 0 To ImageWidth(image)
    
            
            GetColor x,y
            
            If (color2baw = True) Then
                
                iImage(x,y) = (ColorRed() + ColorGreen() + ColorBlue())/3
                
            Else
            
                iImage(x,y) = ColorRed()
                
            EndIf
                
            ;Color iImage(x,y),iImage(x,y),iImage(x,y)
            ;Plot x,y

                
        Next
            
    Next
    
End Function

factNorm = 0

;We load the matrix FlouUniforme in mTransform
Function InitMatrixFlouUniforme()
For i=0 To 2

    For j=0 To 2
    
        mTransform(i,j) = 1
        factNorm = factNorm + mTransform(i,j)
        
    Next

Next
factNorm = Abs(factNorm)
 
End Function

;On charge la matrice de PREWITT dans mTransform
Function InitMatrixPREWITT()
For i=0 To 2

    For j=0 To 2
    
        If j = 0 Then mTransform(i,j) = -1
        If j = 1 Then mTransform(i,j) = 0
        If j = 2 Then mTransform(i,j) = 1
        
    Next

Next
factNorm = 3

End Function

;On charge la matrice de ROBERTS dans mTransform
Function InitMatrixROBERTS()
For i=0 To 2

    For j=0 To 2
    
        mTransform(i,j) = 0
        
    Next

Next

mTransform(2,1) = +1
mTransform(1,2) = -1

factNorm = 1

End Function

;On charge la matrice de SOBEL dans mTransform
Function InitMatrixSOBEL()
For i=0 To 2

    For j=0 To 2
    
        mTransform(i,j) = 0
        
    Next

Next


mTransform(0,0) = -1
mTransform(0,1) = -2
mTransform(0,2) = -1

mTransform(2,0) = 1
mTransform(2,1) = 2
mTransform(2,2) = 1

factNorm = 4

End Function

;On charge la matrice de KIRSCH dans mTransform
Function InitMatrixKIRSCH()


mTransform(0,0) = -3
mTransform(0,1) = -3
mTransform(0,2) = -3

mTransform(1,0) = -3
mTransform(1,1) = 0
mTransform(1,2) = -3

mTransform(2,0) = 5
mTransform(2,1) = 5
mTransform(2,2) = 5

factNorm = 15

End Function

;On charge la matrice de Laplacian Of Gaussian dans mTransform
Function InitMatrixLOG()


mTransform(0,0) = 0
mTransform(0,1) = -1
mTransform(0,2) = 0

mTransform(1,0) = -1
mTransform(1,1) = 4
mTransform(1,2) = -1

mTransform(2,0) = 0
mTransform(2,1) = -1
mTransform(2,2) = 0

factNorm = 4

End Function

Function InitMatrixHarris()


mTransform(0,0) = 0
mTransform(0,1) = -1
mTransform(0,2) = 0

mTransform(1,0) = -1
mTransform(1,1) = 4
mTransform(1,2) = -1

mTransform(2,0) = 0
mTransform(2,1) = -1
mTransform(2,2) = 0

factNorm = 4

End Function

Function ApplyTransformationMatrix()

For y = 1 To 512-1

    For x = 1 To 512-1
    
        newValue = 0
    
        For j=0 To 2
            For i=0 To 2
            
                ;We treat the i,j value in the mValue matrix focused on the pixel
                ;therefore we do a simple matrix product...
                mValue(i,j) = iImage(x+i-1,y+j-1) *  mTransform(i,j)
                newValue = newValue + mValue(i,j)

            Next
        Next

        newValue = Abs(Int(newValue / factNorm))
        Color newValue, newValue, newValue
        Plot x,y
                
    Next
            
Next
End Function


Flip

WaitKey
End
