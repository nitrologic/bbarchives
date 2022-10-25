; ID: 2667
; Author: Streaksy
; Date: 2010-03-18 10:16:32
; Title: Better WriteFile() and ReadFile() etc.
; Description: Faster file handling, and with optional RLE compression and encryption.

Global XMashKey$="This is the encryption key.  Make it as long and as random and as varied as you can, but make sure you use the same key to load files with which they were saved."



Global xDefaultFileSize=1000	;how big a bank is when first created by xWriteFile()
Global bankstep=1500		;how much is added to bank sizes when they get too small to contain the data (plus the size of the extra data)
Global maxfilehandles=500
Dim filehandle(maxfilehandles)
Dim filebank(maxfilehandles)
Dim filefn$(maxfilehandles)
Dim FileSiz(maxfilehandles)
Dim FileEnc(maxfilehandles)
Dim FileCmp(maxfilehandles)
Dim fileoffset(maxfilehandles)
Dim Filetyp(maxfilehandles) ;1 for read, 2 for write, 0 for closed, 3 for random (but i cant be arsed coding random)

Global XTokenID$=Chr(255)		;strings are tokenised when written to a file!
Global xTokens,MaxxTokens=255   ;long/common words can be replaced with a token!  Use XAddToken(word$) To define them
Dim xToken$(maxxTokens)




;DEMO
pic=LoadImage("C:\Blitz3D\Projects\dexter's test.png")
fil=xwritefile("moo.txt")
xwriteimage fil,pic
xclosefile fil

fil=xreadfile("moo.txt")
pic2=xreadimage(fil)
xclosefile fil

DrawImage pic2,0,0
WaitKey:DeleteFile "moo.txt":End




Function XSetKey(k$)
XMashKey$=k
End Function


Function XReadFile(fn$,enc=0,cmp=0)
sz=FileSize(fn)
fil=ReadFile(fn)
If fil=0 Then RuntimeError "Couldn't read from "+fn+"."
For t=1 To maxfilehandles
If filetyp(t)=0 Then
filesiz(t)=sz ;for XEof() etc
fileenc(t)=enc
filecmp(t)=cmp
fileoffset(t)=0
filetyp(t)=1
filebank(t)=CreateBank(sz)
filefn(t)=fn
ReadBytes filebank(t),fil,0,sz
CloseFile fil
If enc Then xmash filebank(t),sz
If cmp Then xdecompress filebank(t),sz
Return t
EndIf
Next
RuntimeError "Too many files open!"
End Function


Function XWriteFile(fn$,enc=0,cmp=0)
For t=1 To maxfilehandles
If filetyp(t)=0 Then
fileenc(t)=enc
filecmp(t)=cmp
fileoffset(t)=0
filetyp(t)=2
filebank(t)=CreateBank(xDefaultFileSize)
filefn(t)=fn
Return t
EndIf
Next
RuntimeError "Too many files open!"
End Function



Function XCloseFile(hnd)
If filetyp(hnd)=2 Then  		;closing a write handle, so write the whole lot then kill the bank
If filecmp(hnd) Then xcompress filebank(hnd),filesiz(hnd):filesiz(hnd)=BankSize(filebank(hnd))
If fileenc(hnd) Then xmash filebank(hnd),filesiz(hnd)
fil=WriteFile(filefn(hnd)):If fil=0 Then RuntimeError "Couldn't write to "+filefn(hnd)+"."
WriteBytes filebank(hnd),fil,0,filesiz(hnd)
CloseFile fil
FreeBank filebank(hnd)
filetyp(hnd)=0
EndIf
If filetyp(hnd)=1 Then  		;closing a read handle, so just kill the bank
FreeBank filebank(hnd)
filetyp(hnd)=0
EndIf
End Function


Function XEof(hnd)
If hnd=0 Or hnd>maxfilehandles Then RuntimeError "Invalid file handle."
If filetyp(hnd)=0 Then RuntimeError "Can't use closed file handle!"
If FileTyp(hnd)<>1 Then RuntimeError "Can't check end-of-file for a file being written!"
If fileoffset(hnd)=>FileSiz(hnd) Then Return True
End Function


Function XWriteInt(hnd,val)
If hnd=0 Or hnd>maxfilehandles Then RuntimeError "Invalid file handle."
If filetyp(hnd)=0 Then RuntimeError "Can't use a closed handle file!"
If filetyp(hnd)<>2 And filetyp(hnd)<>3 Then RuntimeError "Can't write to a non-writey file handle!"
offset=fileoffset(hnd):bnk=filebank(hnd)
If offset+4>filesiz(hnd) Then filesiz(hnd)=offset+4
If BankSize(bnk)<filesiz(hnd) Then ResizeBank bnk,filesiz(hnd)+bankstep
PokeInt(bnk,offset,val):fileoffset(hnd)=fileoffset(hnd)+4
End Function 


Function XWriteFloat(hnd,val#)
If hnd=0 Or hnd>maxfilehandles Then RuntimeError "Invalid file handle."
If filetyp(hnd)=0 Then RuntimeError "Can't use a closed handle file!"
If filetyp(hnd)<>2 And filetyp(hnd)<>3 Then RuntimeError "Can't write to a non-writey file handle!"
offset=fileoffset(hnd):bnk=filebank(hnd)
If offset+4>filesiz(hnd) Then filesiz(hnd)=offset+4
If BankSize(bnk)<filesiz(hnd) Then ResizeBank bnk,filesiz(hnd)+bankstep
PokeFloat(bnk,offset,val):fileoffset(hnd)=fileoffset(hnd)+4
End Function 


Function XWriteByte(hnd,val)
If hnd=0 Or hnd>maxfilehandles Then RuntimeError "Invalid file handle."
If filetyp(hnd)=0 Then RuntimeError "Can't use a closed handle file!"
If filetyp(hnd)<>2 And filetyp(hnd)<>3 Then RuntimeError "Can't write to a non-writey file handle!"
offset=fileoffset(hnd)
bnk=filebank(hnd)
If offset+1>filesiz(hnd) Then filesiz(hnd)=offset+1
If BankSize(bnk)<filesiz(hnd) Then ResizeBank bnk,filesiz(hnd)+bankstep
PokeByte(bnk,offset,val)
fileoffset(hnd)=fileoffset(hnd)+1
End Function 


Function XWriteString(hnd,val$,tokenise=1)
If hnd=0 Or hnd>maxfilehandles Then RuntimeError "Invalid file handle."
If filetyp(hnd)=0 Then RuntimeError "Can't use a closed handle file!"
If filetyp(hnd)<>2 And filetyp(hnd)<>3 Then RuntimeError "Can't write to a non-writey file handle!"
If tokenise Then If xtokens>0 Then val=xtokenise(val)
XWriteInt hnd,Len(val)
For t=1 To Len(val)
XWriteByte hnd,Asc(Mid(val,t,1))
Next
End Function 


Function XWriteLine(hnd,val$,tokenise=1)
If hnd=0 Or hnd>maxfilehandles Then RuntimeError "Invalid file handle."
If filetyp(hnd)=0 Then RuntimeError "Can't use a closed handle file!"
If filetyp(hnd)<>2 And filetyp(hnd)<>3 Then RuntimeError "Can't write to a non-writey file handle!"
If tokenise Then If xtokens>0 Then val=xtokenise(val)
For t=1 To Len(val)
XWriteByte hnd,Asc(Mid(val,t,1))
Next
XWriteByte hnd,13
XWriteByte hnd,10
End Function 


Function XWriteBytes(bnk2,hnd,ofs,cnt)
If hnd=0 Or hnd>maxfilehandles Then RuntimeError "Invalid file handle."
If filetyp(hnd)=0 Then RuntimeError "Can't use a closed handle file!"
If filetyp(hnd)<>2 And filetyp(hnd)<>3 Then RuntimeError "Can't write to a non-writey file handle!"
offset=fileoffset(hnd)
bnk=filebank(hnd)
If offset+cnt>filesiz(hnd) Then filesiz(hnd)=offset+cnt
If BankSize(bnk)<filesiz(hnd) Then ResizeBank bnk,filesiz(hnd)+bankstep
CopyBank bnk2,ofs,bnk,offset,cnt
fileoffset(hnd)=fileoffset(hnd)+cnt
End Function 






Function XReadByte(hnd)
If hnd=0 Or hnd>maxfilehandles Then RuntimeError "Invalid file handle."
If filetyp(hnd)=0 Then RuntimeError "Can't use a closed handle file!"
If filetyp(hnd)<>1 And filetyp(hnd)<>3 Then RuntimeError "Can't read from a non-ready file handle!"
val=PeekByte(filebank(hnd),fileoffset(hnd))
fileoffset(hnd)=fileoffset(hnd)+1
Return val
End Function


Function XReadInt(hnd)
If hnd=0 Or hnd>maxfilehandles Then RuntimeError "Invalid file handle."
If filetyp(hnd)=0 Then RuntimeError "Can't use a closed handle file!"
If filetyp(hnd)<>1 And filetyp(hnd)<>3 Then RuntimeError "Can't read from a non-ready file handle!"
val=PeekInt(filebank(hnd),fileoffset(hnd))
fileoffset(hnd)=fileoffset(hnd)+4
Return val
End Function


Function XReadFloat#(hnd)
If hnd=0 Or hnd>maxfilehandles Then RuntimeError "Invalid file handle."
If filetyp(hnd)=0 Then RuntimeError "Can't use a closed handle file!"
If filetyp(hnd)<>1 And filetyp(hnd)<>3 Then RuntimeError "Can't read from a non-ready file handle!"
val#=PeekFloat(filebank(hnd),fileoffset(hnd))
fileoffset(hnd)=fileoffset(hnd)+4
Return val
End Function


Function XReadString$(hnd,tokenise=0)
If hnd=0 Or hnd>maxfilehandles Then RuntimeError "Invalid file handle."
If filetyp(hnd)=0 Then RuntimeError "Can't use a closed handle file!"
If filetyp(hnd)<>1 And filetyp(hnd)<>3 Then RuntimeError "Can't read from a non-ready file handle!"
ln=XReadInt(hnd)
For p=1 To ln
val$=val$+Chr(XReadByte(hnd))
Next
If tokenise Then If xtokens>0 Then val=xdetokenise(val)
Return val
End Function


Function XReadLine$(hnd,tokenise=0)
If xeof(hnd) Then Return ""
If hnd=0 Or hnd>maxfilehandles Then RuntimeError "Invalid file handle."
If filetyp(hnd)=0 Then RuntimeError "Can't use a closed handle file!"
If filetyp(hnd)<>1 And filetyp(hnd)<>3 Then RuntimeError "Can't read from a non-ready file handle!"
For p=1 To 32000
If fileoffset(hnd)=>filesiz(hnd) Then Return val$;val$=val+Chr(13)+Chr(10):nored=1;RuntimeError "ReadLine timeout - end of file reached and no 13,10!"
If nored=0 Then val$=val$+Chr(XReadByte(hnd))
	If Len(val)>2 Then
	If Mid(val,p-1,1)=Chr(13) And Mid(val,p,1)=Chr(10) Then
	val=Left(val,Len(val)-2)
	If tokenise Then If xtokens>0 Then val=xdetokenise(val)
	Return val
	EndIf
	EndIf
Next
RuntimeError "ReadLine timeout - no 13,10 found!"
End Function


Function XReadBytes(bnk2,hnd,ofs,cnt)
If hnd=0 Or hnd>maxfilehandles Then RuntimeError "Invalid file handle."
If filetyp(hnd)=0 Then RuntimeError "Can't use a closed handle file!"
If filetyp(hnd)<>1 And filetyp(hnd)<>3 Then RuntimeError "Can't read from a non-ready file handle!"
offset=fileoffset(hnd)
bnk=filebank(hnd)
CopyBank bnk,offset,bnk2,ofs,cnt
fileoffset(hnd)=fileoffset(hnd)+cnt
End Function 


Function XMash(bnk,sz)		;for the reading/writing of encrypted files using mah xfile system... it gets called by XReadFile and XCloseFile
lk=Len(mashkey):kat=1
SeedRnd sz
b3=Rand(0,255)
For t=0 To sz-1
b1=PeekByte(bnk,t)
b3=b3+3:If b3>255 Then b3=b3-256
tik=tik+1:If tik=12 Then tik=0:b3=b3+Rand(3,121):If b3>255 Then b3=b3-256
b2=Asc(Mid(mashkey,kat,1)) Xor b3
PokeByte bnk,t,b1 Xor b2
kat=kat+1:If kat>lk Then kat=1
Next
End Function








Function xCompress(bnk,szz,cmp=0) ;does some RLE on the pesky zeroes
bnk2=CreateBank(szz*1.5)
t=0:t2=0
PokeByte bnk2,t2,cmp:t2=t2+1
Repeat
b=PeekByte(bnk,t):t=t+1
.rec
PokeByte bnk2,t2,b:t2=t2+1
If b<=cmp Then
If t=szz Then
PokeByte bnk2,t2,b:Goto donec
Else
	cnt=1
	For k=1 To 256
	bb=PeekByte(bnk,t):t=t+1
		If bb=b And t=szz Then	;end of bank, but last char was in the RLE chunk
		cnt=cnt+1;tot up that final char then write the count
		PokeByte bnk2,t2,cnt-1:t2=t2+1
		Goto donec
		EndIf
	If bb<>b And t=szz Then	;end of bank, but last char wasnt in the RLE chunk
	PokeByte bnk2,t2,cnt-1:t2=t2+1 ;write the count to conclude the RLE
	PokeByte bnk2,t2,bb:t2=t2+1:If bb=<cmp Then PokeByte bnk2,t2,0:t2=t2+1 ;write the new byte, plus a 0 (+1) if it needs a RLE count
	Goto donec
	EndIf
		If bb=b Then	;found another peice of the RLE chunk
		cnt=cnt+1;tot it up
		EndIf
		If bb<>b Then	;past the RLE chunk
		PokeByte bnk2,t2,cnt-1:t2=t2+1 ;write how many it had found
		b=bb:Goto rec ;restart the byte check sequence on the new byte
		EndIf
	If k=256 Then
	PokeByte bnk2,t2,255:t2=t2+1
	Goto donebatch
	EndIf
	Next
.donebatch
EndIf
EndIf
.donec
Until t=szz
ResizeBank bnk,t2:CopyBank bnk2,0,bnk,0,t2:FreeBank bnk2
End Function


Function xDecompress(bnk,szz)
bnk2=CreateBank(szz*1.5)
t=0:t2=0
cmp=PeekByte(bnk,t2):t=t+1
Repeat
b=PeekByte(bnk,t):t=t+1
PokeByte bnk2,t2,b:t2=t2+1:If t2+10>BankSize(bnk2) Then ResizeBank bnk2,BankSize(bnk2)+200
If b=<cmp Then
cnt=PeekByte(bnk,t):t=t+1
For m=1 To cnt
PokeByte bnk2,t2,b:t2=t2+1::If t2+10>BankSize(bnk2) Then ResizeBank bnk2,BankSize(bnk2)+200
Next
EndIf
Until t=szz
ResizeBank bnk,t2:CopyBank bnk2,0,bnk,0,t2:FreeBank bnk2
End Function


Function xTokenise$(t$)
lll=Len(t)
For tt=1 To xtokens
If lll=>Len(xtoken(tt)) Then t=Replace(t,xtoken(tt),xtokenid+Chr(tt))
Next
Return t
End Function

Function xDeTokenise$(t$)
If Len(t)< Len(xtokenid)+1 Then Return t
For tt=1 To xtokens
t=Replace(t,xtokenid+Chr(tt),xtoken(tt))
Next
Return t
End Function

Function XAddToken(t$)
If Len(t)<(Len(xtokenid)+1) Then RuntimeError "Pointless token: "+t+" (it's so short that the token would be larger!)"
If Len(t)=(Len(xtokenid)+1) Then RuntimeError "Pointless token: "+t+" (it's so short that the token would be the same size!)"
If xtokens=>maxxtokens Then RuntimeError "Too many tokens: "+t
xtokens=xtokens+1
xtoken(xtokens)=t
End Function

















Function XWriteImage(f,img)
XWriteInt f,ImageWidth(img)
XWriteInt f,ImageHeight(img)
LockBuffer ImageBuffer(img)
For x=0 To ImageWidth(img)-1
For y=0 To ImageHeight(img)-1
hue=ReadPixelFast(x,y,ImageBuffer(img))
r=(hue And $00FF0000) Shr 16:XWriteByte f,r
g=(hue And $0000FF00) Shr 8:XWriteByte f,g
b=(hue And $000000FF):XWriteByte f,b
Next
Next
UnlockBuffer ImageBuffer(img)
End Function


Function XWriteTexture(f,tex,Alphah=0)
XWriteInt f,TextureWidth(tex)
XWriteInt f,TextureHeight(tex)
XWriteByte f,alphah
LockBuffer TextureBuffer(tex)
For x=0 To TextureWidth(tex)-1
For y=0 To TextureHeight(tex)-1
hue=ReadPixelFast(x,y,TextureBuffer(tex))
r=(hue And $00FF0000) Shr 16:XWriteByte f,r
g=(hue And $0000FF00) Shr 8:XWriteByte f,g
b=(hue And $000000FF):XWriteByte f,b
If alphah Then a=(hue And $FF000000) Shr 24:XWriteByte f,a
Next
Next
UnlockBuffer TextureBuffer(tex)
End Function


Function XWriteBank(f,bnk)
XWriteInt f,BankSize(bnk)
XWriteBytes bnk,f,0,BankSize(bnk)
End Function




Function XReadTexture(f,flags=9)
w=XReadInt(f)
h=XReadInt(f)
alphah=XReadByte(f)
tex=CreateTexture(w,h,flags)
LockBuffer TextureBuffer(tex)
For x=0 To TextureWidth(tex)-1
For y=0 To TextureHeight(tex)-1
r=XReadByte(f)
g=XReadByte(f)
b=XReadByte(f)
If alphah Then a=XReadByte(f)
hue=R Shl 16 Or G Shl 8 Or B
WritePixelFast x,y,hue,TextureBuffer(tex)
Next
Next
UnlockBuffer TextureBuffer(tex)
Return tex
End Function



Function XReadImage(f,flags=9)
w=XReadInt(f)
h=XReadInt(f)
img=CreateImage(w,h)
LockBuffer ImageBuffer(img)
For x=0 To ImageWidth(img)-1
For y=0 To ImageHeight(img)-1
r=XReadByte(f)
g=XReadByte(f)
b=XReadByte(f)
hue=R Shl 16 Or G Shl 8 Or B
WritePixelFast x,y,hue,ImageBuffer(img)
Next
Next
UnlockBuffer ImageBuffer(img)
Return img
End Function

Function XReadBank(f)
siz=XReadInt(f)
bnk=CreateBank(siz)
XReadBytes bnk,f,0,siz
Return bnk
End Function
