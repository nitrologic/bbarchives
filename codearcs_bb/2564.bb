; ID: 2564
; Author: Bobysait
; Date: 2009-08-19 10:43:03
; Title: render in thread
; Description: deferred rendering using thread(FastPointer)

Graphics3D 800,600,0,2
SetBuffer BackBuffer()

; Register Pointer and Thread
	thread.TThread	=	New TThread
	hnd				=	Handle(thread)

	ptr				=	FunctionPointer()
						Goto skip
						TextureThreaded(0)
						.skip
	th				=	CreateThread (Ptr,hnd)
	thread\Thread	=	th
	thread\Ptr		=	Ptr

	Img=CreateImage(800,600)

	starttime=MilliSecs()

Repeat

	; delay / No delay
		If KeyHit(57)
			DelayThread=1-DelayThread
			Print thread\thread
			PauseThread(thread\thread)
				Repeat
					MyVars.ThreadVars=thread\Vars
					If MyVars<>Null	MyVars\Delayed=DelayThread:Exit
				Forever
			ResumeThread(thread\thread)
		EndIf

	; pause the thread to access thread variables
		PauseThread(thread\thread)
			; get the variable container
			MyVars.ThreadVars=thread\Vars
			; Check MyVars exists !
			If MyVars<>Null
				; thread state
				cur=MyVars\Percent
				; end of thread loop : Grab the image
				If thread\Ready=True
					threadloop	=	MyVars\loop
					threadRate	=	MyVars\Fps
					threadDelay	=	MyVars\Delayed
					CopyRect 0,0,800,600,0,0,ImageBuffer(MyVars\Img),ImageBuffer(Img)
				EndIf
				Thread\Ready=False
			EndIf
		ResumeThread(thread\thread)

	Cls
		time=MilliSecs()-starttime
		fpscount=fpscount+1
		If time>fpstime
			fpstime=time+1000
			fps=fpscount
			fpscount=0
		EndIf
		DrawImage Img,0,0
		loop=loop+1
		Color 0,0,0
		Rect 0,0,200,100,1
		Color 200,200,200
		Text 10,10,"Main loop/fps : "+loop+" ["+fps+"]"
		Text 10,25,"thread loops  : "+threadloop
		Text 10,40,"Thread Fps    : "+threadrate
		Text 10,55,"Thread Delayed: "+threadDelay
		Color 0,0,0
		Rect 08,78,104,24,1
		Color 100,100,100
		Rect 08,78,104,24,0
		Color 255-cur*2.5,cur*2.5,0
		Rect 08,78,cur,20,1
	Flip
Until KeyHit(1)
End







Type TThread
	; pointers
	Field Ptr%
	Field Thread%
	; thread state ( Ready=true +> Thread in end of loop , waiting for Ready=false )
	Field Ready%
	; specific variables for thread
	Field Vars.ThreadVars
End Type

Type ThreadVars
	Field Thread.TThread
    Field Loop%,time%,Starttime%,fps%,Delayed%

	; specific to the function
	Field Img%,ImgReady%,Percent%
End Type



Function TextureThreaded(Handle_Thread%)

	; create instance for variables storing
	MyVars.ThreadVars	=	New ThreadVars
	; setup
	Myvars\Delayed		=	True
	; link vars to thread
	thread.TThread		=	Object.TThread(Handle_Thread)
	Myvars\Thread		=	Thread
	; Link thread to vars
	Myvars\Thread\Vars	=	Myvars

	Myvars\Starttime	=	MilliSecs()

	MyVars\Img			=	CreateImage(800,600)

	Repeat
		Thread\Ready	=	False

		; thread Fps
		MyVars\time=MilliSecs()-MyVars\Starttime
		LoopCount=LoopCount+1

		If MyVars\time>fpsTime
			fpsTime=MyVars\time+1000
			MyVars\fps=LoopCount
			LoopCount=0
		EndIf

		; Thread Count loop
		MyVars\Loop=MyVars\Loop+1

		; thread delay
		If MyVars\Delayed	Delay DelayForThread

		LockBuffer(ImageBuffer(MyVars\Img))
		For j = 0 To 599
			MyVars\Percent=j/6
			For i = 0 To 799
				col=Rand(50,200)
				col=col Shl(16)+col Shl(8)+col
				WritePixelFast i,j,col,ImageBuffer(MyVars\Img)
			Next
		Next
		UnlockBuffer(ImageBuffer(MyVars\Img))

		; thread pause +> Image ready for capture.
		Thread\Ready	=	True
		Repeat
			Delay 1
		Until Thread\Ready=False
	Forever
End Function
