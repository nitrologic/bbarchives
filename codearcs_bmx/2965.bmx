; ID: 2965
; Author: Pineapple
; Date: 2012-07-30 13:05:26
; Title: Compute fast ceil/floor of log base 2
; Description: Considerably faster alternatives to using floats or doubles

'   --+-----------------------------------------------------------------------------------------+--
'     |   This code was originally written by Sophie Kirschner (sophiek@pineapplemachine.com)   |  
'     | It is released as public domain. Please don't interpret that as liberty to claim credit |  
'     |   that isn't yours, or to sell this code when it could otherwise be obtained for free   |  
'     |                because that would be a really shitty thing of you to do.                |
'   --+-----------------------------------------------------------------------------------------+--


SuperStrict



' Example code
Rem
Local ms%,nms%
Local i%,x!,y%
Const log2!=0.69314718055994529
local cycles%=20000000

' Use a long sequence of random numbers of various powers to avoid apparent speedups due to consistent branching
local ral% = 1024
local ra%[ral]
for local i% = 0 until ra.length
    local p% = rand(0,31)
    ra[i] = abs( (1 shl p) + rand(0,$ffff) )
next

' eat some cycles before going on with the important part
For i%=0 Until cycles
    x=Ceil(Log(i)/log2)
Next

' test using doubles
ms=MilliSecs()
For i%=0 Until cycles
    x=Ceil(Log( ra[i mod ral] )/log2)
Next
nms=MilliSecs()-ms
Print "Ceil(Log2(x)) using doubles: "+nms+" ms"

' test using clog2()
ms=MilliSecs()
For i%=0 Until cycles
    y=clog2( ra[i mod ral] )
Next
nms=MilliSecs()-ms
Print "Ceil(Log2(x)) using clog2(): "+nms+" ms"
EndRem 

Private
Global log2_array%[]=[  $80000000,$40000000,$20000000,$10000000,$08000000,$04000000,$02000000,$01000000, ..
                        $00800000,$00400000,$00200000,$00100000,$00080000,$00040000,$00020000,$00010000, ..
                        $00008000,$00004000,$00002000,$00001000,$00000800,$00000400,$00000200,$00000100, ..
                        $00000080,$00000040,$00000020,$00000010,$00000008,$00000004,$00000002,$00000001  ]
Public

Rem
bbdoc: Returns Ceil( Log2( x ) ) when x is a positive integer
about: Much faster than using floating point or double functions. Good for when you don't need to know the exact value.
EndRem
Function clog2%(x%)
    If (x & log2_array[$00]) Then Return (x<>log2_array[$00])
    If (x & log2_array[$01]) Then Return $01+(x<>log2_array[$01])
    If (x & log2_array[$02]) Then Return $02+(x<>log2_array[$02])
    If (x & log2_array[$03]) Then Return $03+(x<>log2_array[$03])
    If (x & log2_array[$04]) Then Return $04+(x<>log2_array[$04])
    If (x & log2_array[$05]) Then Return $05+(x<>log2_array[$05])
    If (x & log2_array[$06]) Then Return $06+(x<>log2_array[$06])
    If (x & log2_array[$07]) Then Return $07+(x<>log2_array[$07])
    If (x & log2_array[$08]) Then Return $08+(x<>log2_array[$08])
    If (x & log2_array[$09]) Then Return $09+(x<>log2_array[$09])
    If (x & log2_array[$0A]) Then Return $0A+(x<>log2_array[$0A])
    If (x & log2_array[$0B]) Then Return $0B+(x<>log2_array[$0B])
    If (x & log2_array[$0C]) Then Return $0C+(x<>log2_array[$0C])
    If (x & log2_array[$0D]) Then Return $0D+(x<>log2_array[$0D])
    If (x & log2_array[$0E]) Then Return $0E+(x<>log2_array[$0E])
    If (x & log2_array[$0F]) Then Return $0F+(x<>log2_array[$0F])
    If (x & log2_array[$10]) Then Return $10+(x<>log2_array[$10])
    If (x & log2_array[$11]) Then Return $11+(x<>log2_array[$11])
    If (x & log2_array[$12]) Then Return $12+(x<>log2_array[$12])
    If (x & log2_array[$13]) Then Return $13+(x<>log2_array[$13])
    If (x & log2_array[$14]) Then Return $14+(x<>log2_array[$14])
    If (x & log2_array[$15]) Then Return $15+(x<>log2_array[$15])
    If (x & log2_array[$16]) Then Return $16+(x<>log2_array[$16])
    If (x & log2_array[$17]) Then Return $17+(x<>log2_array[$17])
    If (x & log2_array[$18]) Then Return $18+(x<>log2_array[$18])
    If (x & log2_array[$19]) Then Return $19+(x<>log2_array[$19])
    If (x & log2_array[$1A]) Then Return $1A+(x<>log2_array[$1A])
    If (x & log2_array[$1B]) Then Return $1B+(x<>log2_array[$1B])
    If (x & log2_array[$1C]) Then Return $1C+(x<>log2_array[$1C])
    If (x & log2_array[$1D]) Then Return $1D+(x<>log2_array[$1D])
    If (x & log2_array[$1E]) Then Return $1E+(x<>log2_array[$1E])
    If (x & log2_array[$1F]) Then Return $1F+(x<>log2_array[$1F])
End Function

Rem
bbdoc: Returns Floor( Log2( x ) ) when x is a positive integer
about: Much faster than using floating point or double functions. Good for when you don't need to know the exact value.
EndRem
Function flog2%(x%)
    If (x & log2_array[$00]) Then Return $00
    If (x & log2_array[$01]) Then Return $01
    If (x & log2_array[$02]) Then Return $02
    If (x & log2_array[$03]) Then Return $03
    If (x & log2_array[$04]) Then Return $04
    If (x & log2_array[$05]) Then Return $05
    If (x & log2_array[$06]) Then Return $06
    If (x & log2_array[$07]) Then Return $07
    If (x & log2_array[$08]) Then Return $08
    If (x & log2_array[$09]) Then Return $09
    If (x & log2_array[$0A]) Then Return $0A
    If (x & log2_array[$0B]) Then Return $0B
    If (x & log2_array[$0C]) Then Return $0C
    If (x & log2_array[$0D]) Then Return $0D
    If (x & log2_array[$0E]) Then Return $0E
    If (x & log2_array[$0F]) Then Return $0F
    If (x & log2_array[$10]) Then Return $10
    If (x & log2_array[$11]) Then Return $11
    If (x & log2_array[$12]) Then Return $12
    If (x & log2_array[$13]) Then Return $13
    If (x & log2_array[$14]) Then Return $14
    If (x & log2_array[$15]) Then Return $15
    If (x & log2_array[$16]) Then Return $16
    If (x & log2_array[$17]) Then Return $17
    If (x & log2_array[$18]) Then Return $18
    If (x & log2_array[$19]) Then Return $19
    If (x & log2_array[$1A]) Then Return $1A
    If (x & log2_array[$1B]) Then Return $1B
    If (x & log2_array[$1C]) Then Return $1C
    If (x & log2_array[$1D]) Then Return $1D
    If (x & log2_array[$1E]) Then Return $1E
    If (x & log2_array[$1F]) Then Return $1F
End Function
