; ID: 1993
; Author: skn3[ac]
; Date: 2007-04-19 06:05:32
; Title: Simple tprocess wrapper. eof() readline()
; Description: Easy to use process wrapper for simons free process module. Read process output.

Import pub.freeprocess

Type tproc Extends TProcess
	Method close:Int()
		super.close()
		terminate()
	End Method

	Method avail:Int()
		Return err And pipe And (err.bufferpos Or err.readavail() Or pipe.bufferpos Or pipe.readavail())
	End Method
	
	Method read:String()
		If err And (err.bufferpos > 0 Or err.readavail() > 0) Return err.ReadLine().Replace("~r","").Replace("~n","")
		If pipe And (pipe.bufferpos > 0 Or pipe.readavail() > 0) Return pipe.ReadLine().Replace("~r","").Replace("~n","")
	End Method

	Method readpipe:String()
		If pipe And (pipe.bufferpos > 0 Or pipe.readavail() > 0) Return pipe.ReadLine().Replace("~r","").Replace("~n","")
	End Method
	
	Method readerr:String()
		If err And (err.bufferpos > 0 Or err.readavail() > 0) Return err.ReadLine().Replace("~r","").Replace("~n","")
	End Method
	
	Method pipeavail:Int()
		Return pipe And (pipe.bufferpos > 0 Or pipe.readavail() > 0)
	End Method
	
	Method erravail:Int()
		Return err And (err.bufferpos > 0 Or err.readavail() > 0)
	End Method
	
	Method Eof:Int()
		If status() = 1 Return False
		If pipe And pipe.readavail() > 0 Return False
		If err And err.readavail() > 0 Return False
		If pipe And pipe.bufferpos > 0 Return False
		If err And err.bufferpos > 0 Return False
		Return True
	End Method

	Function Create:TProc(ncmd:String,nflags:Int)
		Local temp_proc:TProc
		Local infd,outfd,errfd	
		
		'do mac speciffic stuff
		?MacOS
		If FileType(ncmd)=2
			ncmd :+ "/Contents/MacOS/" + StripExt(StripDir(ncmd))
		EndIf
		?
		
		'create the proc object
		temp_proc = New TProc
		
		'setup the proc
		temp_proc.name = ncmd
		
		'attempt to start the process
		temp_proc.handle = fdProcess(ncmd,Varptr(infd),Varptr(outfd),Varptr(errfd),nflags)
		If Not temp_proc.handle Return Null
		
		'creat teh process pipes
		temp_proc.pipe = TPipeStream.Create(infd,outfd)
		temp_proc.err = TPipeStream.Create(errfd,0)
		
		'add process to process list
		If Not ProcessList ProcessList = New TList
		ProcessList.AddLast temp_proc
		
		'return the proc object
		Return temp_proc
	End Function
End Type

Function CreateProc:tproc(ncmd:String,nhidden:Int = True)
	Return tproc.create(ncmd,nhidden)
End Function
