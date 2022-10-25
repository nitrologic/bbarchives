; ID: 619
; Author: Difference
; Date: 2003-03-13 16:38:41
; Title: LoadAnimGif
; Description: Load animated GIF

; Animated Gif file loader by Peter Scheutz 2003.03.13
; Load animated Gif and returns an animImage
; Stores number of frames in gifframecount

Global gifframecount

; useage:
myanim=LoadAnimGif("mygif.gif")



Function LoadAnimGif(fname$)

	Local fbank
	Local f
	Local thegif
	Local animPic
	Local count
	Local framecount
	fbank=CreateBank(FileSize(fname$))

	f=OpenFile(fname$)
		ReadBytes fbank,f,0,BankSize(fbank)
	CloseFile f
	
	; This is a quick hack and not quite good enough,
	; as it could count too many frames
	; Works for all the gifs I tested though...
	; Looks for "Gif magic marker"
	For n=0 To BankSize(fbank)-1
		If PeekByte(fbank,n)=0
		If PeekByte(fbank,n+1)=33
		If PeekByte(fbank,n+2)=249
			;DebugLog "Gif Magic found at: " + n
			framecount=framecount+1
		EndIf
		EndIf
		EndIf
	
	Next

	FreeBank fbank

	thegif=OpenMovie(fname$)

	animPic=CreateImage(MovieWidth(thegif),MovieHeight(thegif),framecount)

	SetBuffer BackBuffer()
	
	count=0
	While MoviePlaying(thegif) And count<framecount
		DrawMovie thegif,0,0	
		GrabImage animPic,0,0,count
		count=count+1
	Wend

	CloseMovie thegif
	
	Cls
	Flip

	; store framecount in global variable
	gifframecount=framecount

	Return animPic

End Function
