; ID: 1597
; Author: Malice
; Date: 2006-01-14 15:28:46
; Title: Quick Planets
; Description: Texture spheres to resemble planets

Function createplanettexture(t)

t=CreateTexture(256,256)

SetBuffer TextureBuffer(t)

ClsColor 0,0,0
Cls

SeedRnd MilliSecs()
colred=Rand(80,160)
colgreen=Rand(80,160)
colblue=Rand(80,160)

d=Rand(6)

For g=0 To 32
For h=32 To 0 Step-1

If d=0
Color g*4,colgreen+g,h*2
Rect 0,g,256,1,1
Color h*3,colgreen+g,g*3
Rect 0,g+h+32,256,1,1
Color g*4,colgreen+g,h*4
Rect 0,g+64,256,1,1
Color h*2,colgreen+g,g*2
Rect 0,g+h+96,256,1,1
Color g*5,colgreen+g,h*3
Rect 0,g+128,256,1,1
Color h*3,colgreen+g,g*4
Rect 0,g+h+160,256,1,1
Color g*2,colgreen+g,h*3
Rect 0,g+192,256,1,1
Color h*3,colgreen+g,g*2
Rect 0,g+h+224,256,1,1
Color g*5,colgreen+g,h*3
Rect 0,g+224,256,1,1
Color h*3,colgreen+g,g*4
Rect 0,g+h+256,256,1,1
End If


If d=1
Color colred+g+h,colgreen+g/2,h*2
Rect 0,g,256,1,1
Color colred+g-h,colgreen+h/2,h*2
Rect 0,g+h+32,256,1,1
Color colred+g+h,colgreen+g/2,h*2
Rect 0,g+64,256,1,1
Color colred+g-h,colgreen+h/2,h*2
Rect 0,g+h+96,256,1,1
Color colred+g+h,colgreen+g/2,h*2
Rect 0,g+128,256,1,1
Color colred+g-h,colgreen+h/2,h*2
Rect 0,g+h+160,256,1,1
Color colred+g+h,colgreen+g/2,h*2
Rect 0,g+192,256,1,1
Color colred+g-h,colgreen+h/2,h*2
Rect 0,g+h+224,256,1,1
Color colred+g+h,colgreen+g/2,h*2
Rect 0,g+224,256,1,1
Color colred+g-h,colgreen+h/2,h*2
Rect 0,g+h+256,256,1,1
End If

If d=2

For i=1 To 300

lx=Rand(256)
ly=Rand(256)

Color colred+(ly/10),colgreen+(ly/10),colblue
Line lx,ly,lx+(Rand(5,30)),ly
Color colred,colgreen,colblue
Line lx,ly+1,lx+(Rand(5,25)),ly+1
Next

EndIf

If d=3
Color Colred/2,colgreen/2,g+colblue/10
Rect 0,g,256,1,1
Color Colred/2,colgreen/2,g+32+colblue/10
Rect 0,g+32,256,1,1
Color Colred/2,colgreen/2,g+64+colblue/10
Rect 0,g+64,256,1,1
Color Colred/2,colgreen/2,g+96+colblue/10
Rect 0,g+96,256,1,1
Color Colred/2,colgreen/2,g+128+colblue/10
Rect 0,g+128,256,1,1
Color Colred/2,colgreen/2,g+160+colblue/10
Rect 0,g+160,256,1,1
Color Colred/2,colgreen/2,g+192+colblue/10
Rect 0,g+192,256,1,1
Color Colred/2,colgreen/2,g+224+colblue/10
Rect 0,g+224,256,1,1
EndIf















If d=5
for i=1 to 500
Color colred+(Rand(h)),colgreen+(Rand(g)),colblue+(Rand(h))
Plot Rand(255),Rand(255)
next
EndIf









Next
Next


If d=3
lx1=Rand(64,192)
ly1=Rand(64,192)

For g=1 To 5000
mx1=lx1+(Rand(-32,32))
my1=ly1+(Rand(-32,32))
If mx1<1 Then mx1=1
If mx1>254 Then mx1=254
If my1<1 Then my1=1
If my1>254 Then my1=254

Color colred*2,colgreen*1.5,colblue*0.7
Line lx1,ly1,mx1,my1
Color colred,colgreen,colblue/2
Line lx1-1,ly1-1,mx1-1,my1-1
lx1=mx1
ly1=my1
Next

EndIf

If d=4
Color colred/2,colgreen/2,colblue/2
Rect 0,0,255,255,1
For g=1 To 20000
mx1=Rand(-64,255)
my1=Rand(-64,255)
lx1=Rand(16)
Color colblue+lx1,colblue+lx1,colblue+lx1
Oval mx1+(lx1/2),my1+(lx1/2),lx1,lx1,0
Color colblue/2,colblue/2,colblue/2
Oval mx1+(lx1/2),my1+(lx1/2),lx1-1,lx1-1,0
Next
EndIf

If d=6
Color colred,colgreen,colblue
Rect 0,0,255,255,1
For g=1 To 500
mx1=Rand(-64,255)
my1=Rand(-64,255)
lx1=Rand(64)
Color colblue+lx1,colred+lx1,colgreen+lx1
Oval mx1+(lx1/2),my1+(lx1/2),lx1,lx1,0
Color colblue/2,colblue/2,colblue/2
Oval mx1+(lx1/2),my1+(lx1/2),lx1-1,lx1-1,0
Next
EndIf









If d<>2 And Rand(10)<3 And d<3
lx1=Rand(64,192)
ly1=Rand(64,192)
For g=20 To 0 Step -1
Color colred+(g*2),colgreen-g,colblue/2
Oval lx1-g,ly1-g,g*2,g,1
Next
EndIf


If d=2 And Rand(10)>7

For g=1 To 10

lx1=Rand(64,192)
Ly1=Rand(64,192)

For h=1 To 50
mx=lx1+(Rand(-30,30))
my=ly1+(Rand(-30,30))

Color colred+((lx-mx))/8,colgreen-((ly-my)/8),colblue/2
Oval mx,my,Rand(16),Rand(16),1

Next
Next
EndIf

SetBuffer BackBuffer() 
End Function
