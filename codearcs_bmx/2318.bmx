; ID: 2318
; Author: Kurator
; Date: 2008-09-19 12:46:14
; Title: WorkingQueue
; Description: Implementation of a WorkingQueue with a ThreadPool

SuperStrict

' *** THREADPOOL CLASSES START ***

Type TWorkQueue

	Field numThreads:Int
	Field workerThreads:TThreadPoolThread[]
	Field workQueue:TList
	Field mutexWorkQueue:TMutex
	
	Field _asyncThread:TThread
	
	Function Create:TWorkQueue(numThreads:Int = 10)
		Local t:TWorkQueue = New TWorkQueue
		t.numThreads= numThreads
		t.workerThreads= New TThreadPoolThread[numThreads]
		t.workQueue= New TList
		t.mutexWorkQueue= CreateMutex()
		
		For Local i:Int = 0 To numThreads-1
			t.workerThreads[i] = TThreadPoolThread.Create( QueueWrapper, Object(t) )
		Next
		
		Return t
	EndFunction
	
	Method ProcessQueueAsync()
		If _asyncThread = Null 
			_asyncThread = CreateThread(_AsyncWrapper, Object(Self))
			'Print "Created AsyncThread"
		EndIf
	EndMethod
	
	Method WaitForEmptyQueue()
		WaitThread(_asyncThread)
	EndMethod
	
	Function _AsyncWrapper:Object(data:Object)
		Local q:TWorkQueue = TWorkQueue(data)
		If data 
			While q.DoWork()
				Delay(0)
			Wend
		EndIf
		Return data
	EndFunction
	
	Function QueueWrapper:Object( data:Object)
		
		Local myTask:TWorkQueue = TWorkQueue(data)
		myTask.Execute()
	
	EndFunction
	
	Method AddTask(obj:TTask)
		LockMutex(mutexWorkQueue)
		  workQueue.AddLast(obj)
		UnlockMutex(mutexWorkQueue)		
	EndMethod
	
	Method DoWork:Int()
		If Not workQueue.IsEmpty()
			LockMutex(mutexWorkQueue)
			  Local t:TTask = TTask(workQueue.First())
			UnlockMutex(mutexWorkQueue)
			
			Local i:Int = 0

			While i < numThreads
				If workerThreads[i].GetStatus() = False
					workerThreads[i].SetFunction(t.func, t.data)
					LockMutex(mutexWorkQueue)
					  workQueue.RemoveFirst
					UnlockMutex(mutexWorkQueue)
					workerThreads[i].Start()
					Exit
				Else
					i :+ 1
				EndIf
			Wend

			Return True
		Else
			Return False
		EndIf
	EndMethod
	
	Method Execute()
	
		' Dummy
		
	EndMethod

EndType


Type TTask

	Field func:Object( data:Object )
	Field data:Object
	
	Method Run()
		If func Then func(data)
	EndMethod
	
	Function Create:TTask(func:Object( data:Object ), data:Object )
		Local t:TTask = New TTask
		t.func = func
		t.data = data
		Return t

	EndFunction
	
EndType

Type TThreadPoolThread
	
	Field id:TThread
	Field mutex:TMutex
	
	Field func:Object( data:Object )
	Field data:Object
	
	Field working:Int

	
	Function Create:TThreadPoolThread(func:Object( data:Object ), data:Object)
		Local t:TThreadPoolThread= New TThreadPoolThread
		t.id    = CreateThread( ThreadWrapper, Object(t) )
		t.mutex = CreateMutex()
		t.func = func
		t.data = data
		t.working = False
		
		LockMutex(t.mutex)

		Return t

	EndFunction
	
	Function ThreadWrapper:Object( data:Object)
		
		Local myTask:TThreadPoolThread= TThreadPoolThread(data)
		myTask.Execute()
	
	EndFunction
	
	Method SetFunction(func:Object( data:Object ), data:Object)
		Self.func = func
		Self.data = data
		Self.working = False
	EndMethod

	Method GetStatus:Int()
		Return working
	EndMethod
	
	
	Method Destroy()
		DetachThread(id)
		id = Null
		CloseMutex(mutex)
		mutex = Null
		working = False
		func = Null
		data = Null
	EndMethod
	
	Method Execute()

		Repeat
			
			If Self.working = True 
				working = Self.DoWork()
			EndIf
			
			If Self.working = False
				LockMutex(mutex)
			EndIf
	
		Forever
	
	EndMethod
	
	Method DoWork:Int()
		If func Then func(data)
		Return False
	EndMethod
	
	Method Start()
		UnlockMutex(mutex)
		Self.working = True
	EndMethod
	
	Method Suspend()
		Self.working = False
	EndMethod
	
EndType

' *** THREADPOOL CLASSES END ***


' ***** EXAMPLE START ****
' Demo Functions, just generate some heavy Workload :)


Function fTest1:Object(data:Object)
	Local superCalcResult:Double
	For Local x:Int = 0 To 10000
		superCalcResult = x*Cos(x)+Tan(x)*x/x
	Next
EndFunction

Function fTest2:Object(data:Object)
	Local superCalcResult:Double
	For Local x:Int = 0 To 10000
		superCalcResult = x*Cos(x)+Tan(x)*x/x
	Next
EndFunction


'Create a WorkQueue with 8 preinitalised Threads waiting for work
Local q:TWorkQueue = TWorkQueue.Create(8)


'Create a LOT of Tasks to be done

Local test1:TTask[] = New TTask[200]
Local test2:TTask[] = New TTask[200]


'Assign the Functions you want to be done to the Tasks
'I'm pretty lazy - using, just the same functions :)
For Local i:Int = 0 To 199
	test1[i] =  TTask.Create(fTest1, String(i))
	test2[i] =  TTask.Create(fTest2, String(i))
Next

Print "Initialized..."
' To prove that ther is no CPU time wasted with 8 Threads waiting for work (look at the Taskmanager)
Delay 1000
Print "Starting..."


'Assign the 400 Tasks to the WorkQeue

For Local i:Int = 0 To 199
	q.AddTask(test1[i])
	q.AddTask(test2[i])
Next

Local starttime:Int = MilliSecs()

'While Not KeyHit(KEY_ESCAPE)
	'Update the WorkQueue
'	If Not q.DoWork() Exit
'	Delay(100)
	'Print q.queue.Count()
	'it should also be possible to but this loop in an extra thread, maybe in future :)
'Wend

Print "Processing Queue..."
q.ProcessQueueAsync()
Print "Waiting for empty Queue..."
q.WaitForEmptyQueue()

Print "Duration "+String(MilliSecs()-starttime)+" ms"
