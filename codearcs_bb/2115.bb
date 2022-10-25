; ID: 2115
; Author: Jesse
; Date: 2007-09-30 18:51:24
; Title: OGL bitmap font
; Description: ogl fonts from image file

SuperStrict


Type TTexture

	Field texID:Int = 0



	Method CDelete()
		If(glIsTexture(texID)) glDeleteTextures(1, Varptr texID);
	End Method

	Method LoadTexture:Int(filename:String)

		Local glimage:TPixmap =LoadPixmap(filename);
		If(glimage = Null)Return False;
		Local data:Byte[,,]= New Byte[glimage.Width,glimage.Height,3]
		Local pp:Int = 0

		glGenTextures(1, Varptr texID);
		glBindTexture(GL_TEXTURE_2D, texID);
		glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MIN_FILTER,GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MAG_FILTER,GL_LINEAR);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, glimage.Width, glimage.Height, 0, GL_RGB, GL_UNSIGNED_BYTE, glimage.pixels)'data);

		Return True;
	End Method

	Method BindTexture()

		glBindTexture(GL_TEXTURE_2D, texID);
	End Method

	Method GetTexID:Int()
		Return texID;
	End Method

End Type

Type TFont

	
		Field tex:TTexture;
		Field FontSize:Float;
		Field fontColor:Float[4];

	
	Method Create()
		FontSize = 1.0;
		fontColor[0] = 1;
		fontColor[1] = 1;
		fontColor[2] = 1;
		fontColor[3] = 1;
		tex = New TTexture
		tex.LoadTexture("mypng.png");
	End Method


	Method DrawText(x:Int, y:Int, text:String)
		If(text=Null) Return;
		Local x1:Int=x
		Local y1:Int=y
		Local cx:Float
		Local cy:Float
		Local  i:Int
		Local offset:Int = 0
		glEnable(GL_BLEND);
		glDisable(GL_DEPTH_TEST);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE);
		tex.BindTexture();
		glColor4fv(fontColor);
		For i=0 Until text.length
			cx=(((text[i]+offset)/16.0) Mod 1);
			cy=((text[i]+offset)/16)/16.0;
			
			glBegin(GL_QUADS);
								
				glTexCoord2f(cx,cy);
				glVertex2i(x1,y1);
				
				glTexCoord2f(cx,(cy+0.0625));
				glVertex2i(x1,Int(16*FontSize)+y1);
				
				glTexCoord2f(cx+0.0625,(cy+0.0625))
				glVertex2i(Int(16*FontSize)+x1,Int(16*FontSize)+y1)
				
				glTexCoord2f(cx+0.0625,cy);
				glVertex2i(Int(16*FontSize)+x1,y1);
			
			glEnd();
			x1:+Int(13.0*(FontSize));

		Next
		glDisable(GL_BLEND);
		glEnable(GL_DEPTH_TEST);
	End Method

	Method SetSize(size:Float )
	
		FontSize = size;
	End Method

	Method SetColor(r:Float, g:Float, b:Float, a:Float)
		fontColor[0] = r;
		fontColor[1] = g;
		fontColor[2] = b;
		fontColor[3] = a;
	End Method
End Type

	GLGraphics 800,600
'	glEnable(GL_DEPTH_TEST);
	
'	glMatrixMode(GL_PROJECTION);
	glLoadIdentity();
	gluOrtho2D(0,800,600,0);
'	glMatrixMode(GL_MODELVIEW);

	glEnable(GL_CULL_FACE);
	glEnable(GL_TEXTURE_2D);
	Local font:Tfont = New Tfont
	font.Create()
Repeat
	Local alpha:Float = Abs(Sin(MilliSecs()/10.0))
	Local red:Float = Abs(Sin(MilliSecs()/100))
	Local green:Float = Abs(Cos(MilliSecs()/100))
	Local blue:Float = Abs(Sin(MilliSecs()/1000))
	glClear(GL_COLOR_BUFFER_BIT|GL_DEPTH_BUFFER_BIT)
	font.SetColor(red,green,blue,alpha)
	font.SetSize(2.0)
	font.drawtext(10,270,"abcdefghijklmnopqrstuvwxyz")

	Flip()
Until KeyDown(key_escape)
