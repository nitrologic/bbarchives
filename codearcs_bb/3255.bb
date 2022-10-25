; ID: 3255
; Author: Casaber
; Date: 2016-02-11 09:22:07
; Title: Mode7 &amp; live 2d tiles
; Description: Mode 7 and Live 2d tiles

Rem ------------------------------------------
 ___     ____  _____  _____   ______   ______  
|    \  /    |/     \|     \ |   ___| |___   | 
|     \/     ||     ||      \|   ___|   /   /  
|__/\__/|____|\_____/|______/|______|  |___|   

' Check the source for the important lines to comment / uncomment one at a time.
' 
' In 2d mode you can draw, showing the beginning of collision detection of tiles.
' And you may scroll around in this 16MB world, create any size you need.

EndRem

Global buffer:Int[128*128]
Local img2:TPixmap=LoadPixmap(LoadBank("http::s14.postimg.org/xotmro9vh/128x1282.png"))
For yy=0 To 127 ; For xx=0 To 127 ; buffer(xx+yy*128) = ReadPixel(img2,xx,yy) ; Next ; Next
AddHook FlipHook,irq,Null ; Global fps:Int , fpstotal:Int , msstart:Float , ms:Float = MilliSecs()

Global map[4096,4096] ; For yy=0 To 316 ; For xx=0 To 475; map[xx,yy]=Rnd(127) ; Next ; Next
For temp=0 To 255 ; map[temp,0]=temp ; Next
Global cmap[4096,4096] ; For yy=0 To 316 ; For xx=0 To 475; cmap[xx,yy]=Rnd(15) ; Next ; Next
Global wx:Int , wy:Int , cx:Int , c:Int = buffer[0] , cols:Int[16] , s:Int = 2
RestoreData colors ; For temp=0 To 15 ; ReadData cols[temp] ; Next
writetomap "                                            ",0,0
writetomap " These letters And symbols are live tiles   ",0,1
writetomap " Changing tiles, color of tiles and         ",0,2
writetomap " even the atlas at pixel level, each frame  ",0,3
writetomap " perfect base for platformers or shootemups ",0,4
writetomap "                                            ",0,5

Global xres:Int = 1024 , yres:Int = 768 , pixels:Int[xres*yres] ' Set resolution here.
Local a:Float = 0 , x:Float = 0 , y:Float = 0 , dx:Float = 0 , dy:Float = 0 , speed:Float = 0
Global space_z:Float = 50 , horizon:Int = 20, scale:Float = 1000 , obj_scale:Float = 50 ' Set view and scale here.
Global pmap:Int[1024*1024] ; Global img:TPixmap = LoadPixmap(LoadBank("http::static.monstermmorpg.com/images/maps/Meteor-Falls.png"))
For y=0 To 1023 ; For x=0 To 1023 ; pmap(x+y*1024) = ReadPixel(img,x,y) ; Next ; Next ' Load png 1024 x 1024

GLGraphics xres,yres,0,60,GRAPHICS_BACKBUFFER | GRAPHICS_DEPTHBUFFER ; glViewport 0,0,xres,yres ' Set 0 for Windowed, 32 for Fullscreen here.
glMatrixMode GL_PROJECTION ; glLoadIdentity ; glOrtho 0,xres,yres,0,0,1 ; glPixelZoom 1,-1 ; glRasterPos2i 0,0 '; HideMouse

Repeat

' Uncomment this if you want only live tiles, this example uses punctated letters and symbols as tiles. This makes for beutiful dynamic landscapes of gameplay.
' For yy = 0 To 1023 ; For xx = 0 To 1023 ; pmap[yy*1024+xx] = 0 ; Next ; Next

' Comment this if you want only static landscape.
alive ; display wx,wy,0,0,1024,1024 ' Render tiles

speed = speed + KeyDown(KEY_UP)*0.1-KeyDown(KEY_DOWN)*0.1 ; speed=Max(-5,Min(5,speed))
a = a               - KeyDown(KEY_LEFT)+KeyDown(KEY_RIGHT) ; a = a Mod 360
dx = speed * Cos (a) ; dy =  speed * Sin (a) ; x :+ dx ; y :+ dy
drawplane a,x,y

' For normal 2d tiles, uncomment this.
' readinputs ; for yy = 0 To yres ; For xx = 0 To 1023 ; pixels[yy*xres+xx] = pmap[yy*1024+xx]  ; Next ; Next
glDrawPixels xres,yres,GL_BGRA,GL_UNSIGNED_BYTE,pixels
	Delay 1 ; Flip 1
Until KeyHit(KEY_ESCAPE) Or AppTerminate()
End

Function drawplane(a:Float,cx:Float,cy:Float)
	Local sx:Int , sy:Int , d:Float , hs:Float , maskx:Int = 1023 , masky:Int = 1023 
	Local dx:Float , dy:Float , spx:Float,spy:Float 
	For sy = 0 Until yres-1 Step 2
		d = space_z*scale / Float(sy + horizon) ; hs = d / scale ; dx = -Sin(a) * hs ; dy = Cos(a) * hs
		spx = cx + d*Cos(a) - Float(xres) / 2 * dx ; spy = cy + d*Sin(a) - Float(xres) / 2 * dy
		For sx = 0 Until xres-1 ; pixels[sx+sy*xres] = pmap[(Int(spx) & maskx) + ( (Int(spy) & masky)*1024)] ; spx :+ dx ; spy :+ dy ; Next
	Next
End Function

Function alive()
	For temp=1 To 16 ; buffer[Int(Rand(0,15))+Int((Rand(0,15))*128)]=$ffffffff * Int(Rand(0,1)) ; Next
	cx=(cx+1) Mod 128 ; For tempy=0 To 15 ; For temp=0 To 15+32 ; buffer[16+temp+((0+tempy)*128)]=buffer[0+temp+cx+(16+tempy)*128] ; Next ; Next ; For temp=1 To 1600 ; cmap[Rnd(400),Rnd(250)]=Rnd(16) ; Next
End Function

Function display(wx,wy,ofx=0,ofy=0,sx=640,sy=400) 
	scrx = wx & 15 ; scry = wy & 15 ; mapx = wx Shr 4 ; mapy = wy Shr 4
	cnty = 0 ; For y = mapy To mapy+(sy Shr 4+1)
	cntx = 0 ; For x = mapx To mapx+(sx Shr 4+1)
	tilex = map(x,y) & 15 ; tiley = map(x,y) Shr 4
	colour = cols(cmap(x,y)) ; xx = cntx - scrx + ofx ; yy=cnty - scry + ofy
	For ty = 0 To 7 ; For tx = 0 To 7
	If (xx+tx+tx >= ofx) And (yy+ty+ty >= ofy) And (xx+tx+tx < ofx+sx) And (yy+ty+ty < ofy+sy).. ' Mind the steps
 	 And buffer[tilex Shl 3 + tx + (tiley Shl 3 + ty)*128] <> c Then pmap[xx+tx+tx+(yy+ty+ty)*1024] = colour 
	Next ; Next
	cntx = cntx+16 ; Next
	cnty = cnty+16 ; Next
End Function

Function readinputs()
	wx=wx-s*KeyDown(KEY_LEFT)+s*KeyDown(KEY_RIGHT) ; wy=wy-s*KeyDown(KEY_UP)+s*KeyDown(KEY_DOWN) ; wx=Max(0,Min(10000,wx)) ; wy=Max(0,Min(10000,wy))
	mx = MouseX() ; my = MouseY() ; xxx = (wx+mx) Sar 4 ; yyy = (wy+my) Sar 4 ; If MouseDown(1) Then map(xxx,yyy) = 42 ; cmap(xxx,yyy) = 7
End Function

Function writetomap(t$,x,y)
	For tempx = 0 To Len(t$)-1 ; temp = Asc(Mid$(t$,1+tempx,1)) ; map[tempx+x,y] = temp ; cmap[tempx+x,y] = 14 ; Next
End Function

Function irq:Object(id,data:Object,context:Object)	
	Return data
End Function

#colors
DefData $000000,$FFFFFF,$68372B,$70A4B2,$6F3D86,$588D43,$352879,$B8C76F,$6F4F25,$433900,$9A6759,$444444,$6C6C6C,$9AD284,$6C5EB5,$959595
