; ID: 2464
; Author: Otus
; Date: 2009-04-17 07:05:21
; Title: Actors
; Description: Actor model with threading

SuperStrict

Rem
bbdoc: Actors
about:
Implementation of the actor model for BlitzMax.

See Wikipedia:Actor_model for a description.
Works both threaded and non-threaded, though there 
are obviously differences. Methods and types state 
when they are threaded-only.

There are basically two ways to create actors: Inherit 
one of the abstract types or use #CreateActor and the 
like, passing a callback.

Important notes:

When you use threaded mode, actor implementations 
have been made thread safe, but you must still be 
careful. Eg. your message objects are only accessed 
by one actor thread at a time, but you must not 
modify them after sending. Strings - being immutable 
- are good message objects.

While actors process messages on a FIFO-basis, there
is no guarantee that a #TThreadPool will finish on 
messages in order. For tasks that must be done 
sequentially, you can try #TMessage.Chain or implement
your own delayed sending.

Note that #TActor objects are shared by all messages. I.e. 
Receive is called on the same object for each message.
In threaded mode this means that you should not use fields
to store message specific data for your implementation of 
Receive unless you know what you are doing.
End Rem
'Module Otus.Actor

'ModuleInfo "Version: 1.11"
'ModuleInfo "Author: Jan Varho"
'ModuleInfo "License: Public Domain"
'ModuleInfo "Copyright: Jan Varho (jan@varho.org)"

'ModuleInfo "History: 1.10 Release"
'ModuleInfo "History: Restructured thread management to TThreadPool"
'ModuleInfo "History: Stopping threads no longer works"
'ModuleInfo "History: 1.20 Release"
'ModuleInfo "History: Waiting uses semaphores in threaded mode"
'ModuleInfo "History: Removed a lot of redundant locking"
'ModuleInfo "History: Clarified docs and code"


Import BRL.LinkedList

?threaded
Import BRL.Threads
?



Rem
bbdoc: Base type for all actors
about:
Create actors by inheriting (Extending) one of the abstract types, or using #CreateActor.
The abstract #Receive method must be implemented in inheriting types.
Add an actor to a #TActorGroup (or a #TThreadPool) using #SetGroup.

In threaded mode, #TActorThread can also be used as a base type for convenience.
End Rem
Type TActor Abstract
	
	Rem
	bbdoc: Send a message to the actor
	returns: A #TMessage object, or Null if failed
	End Rem
	Method Send:TMessage(msg:Object)
		Return TMessage._Create(msg, Self)
	End Method
	
	Rem
	bbdoc: Receive a message
	returns: The result of the message
	about:
	This method should not be called by the user.
	Instead, it needs to be implemented in inheriting
	types and is called internally. Use #Process to
	receive and process messages.
	End Rem
	Method Receive:Object(msg:Object) Abstract
	
	Rem
	bbdoc: Process a message
	returns: True or False, if no message in queue
	about:
	In threaded mode, Process blocks for thread
	pooled actors until there is a message to receive.
	End Rem
	Method Process:Int()
		Local m:TMessage = _Get()
		If Not m Return False
		m._SetDone Receive(m._msg)
		Return True
	End Method
	
	Rem
	bbdoc: Set the #TActorGroup of the actor
	about:
	Actors can only belong to one group.
	End Rem
	Method SetGroup(group:TActorGroup)
?threaded
		_mutex.Lock
?
		If _group Then _group._RemoveActor(Self)
		_group = group
		If _group Then _group._AddActor(Self)
?threaded
		_mutex.Unlock
?
	End Method
	
	' Private
	
	Field _queue:TList = New TList
	
	Field _group:TActorGroup = Null
	
?threaded
	Field _mutex:TMutex = CreateMutex()
	
	Method _Put(msg:TMessage)
		_mutex.Lock
		_queue.AddLast msg
		If _group Then _group._semaphore.Post
		_mutex.Unlock
	End Method
	
	Method _Get:TMessage()
		_mutex.Lock
		If _queue.IsEmpty()
			_mutex.Unlock
			Return Null
		End If
		Local ret:TMessage = TMessage( _queue.RemoveFirst() )
		_mutex.Unlock
		Return ret
	End Method
	
	Method _Cancel%(msg:TMessage)
		_mutex.Lock
		Local ret% = _queue.Remove(msg)
		_mutex.Unlock
		Return ret
	End Method
	
?Not threaded
	Method _Put(msg:TMessage)
		_queue.AddLast msg
	End Method
	
	Method _Get:TMessage()
		Return TMessage(_queue.RemoveFirst())
	End Method
	
	Method _Cancel%(msg:TMessage)
		Return _queue.Remove(msg)
	End Method
?
	
End Type



Rem
bbdoc: Tracks a message
about:
Sending a message to an actor returns a TMessage object
that tracks the message. You can then use #IsDone to see
if the message has been processed and #Result or #Wait to 
get the result. You can also #Cancel messages that are
in queue.

#Chain allows a message to be sent after another completes.
Only one message can be chained to a message, but chains 
may be arbitrarily long.
End Rem
Type TMessage
	
?threaded
	Rem
	bbdoc: Attempt to cancel a message
	returns: True if the message was successfully canceled
	about: Cancel may fail if the message is already done
	or is being processed in another thread.
	End Rem
	Method Cancel:Int()
		_mutex.Lock
		If _done Or _canceled
			_mutex.Unlock
			Return _canceled
		End If
		
		_canceled = _actor._Cancel(Self)
		If _canceled
			If _chain Then _chain.Cancel
			_semaphore.Post
		End If
		
		_mutex.Unlock
		Return _canceled
	End Method
	
	Rem
	bbdoc: Chain a message after this one
	returns: A new #TMessage tracking the new message or Null
	about: Chaining fails if the message is already canceled.
	#Chain throws an error if you attempt to chain more than
	one message to a single #TMessage.
	End Rem
	Method Chain:TMessage(actor:TActor, msg:Object=Null)
		_mutex.Lock
		If _canceled
			_mutex.Unlock
			Return Null
		Else If _chain
			_mutex.Unlock
			Throw "Cannot chain two messages."
		End If
		
		_chain = New TMessage
		_chain._actor = actor
		_chain._msg = msg
		
		If _done _SendChain
		
		_mutex.Unlock
		Return _chain
	End Method
	
?Not threaded
	Method Cancel:Int()
		If _done Or _canceled Return _canceled
		
		_canceled = _actor._Cancel(Self)
		If _canceled And _chain Then _chain.Cancel
		
		Return _canceled
	End Method
	
	Method Chain:TMessage(actor:TActor, msg:Object=Null)
		If _canceled Return Null
		If _chain Throw "Cannot chain two messages."
		
		_chain = New TMessage
		_chain._actor = actor
		_chain._msg = msg
		
		If _done _SendChain
		
		Return _chain
	End Method
?
	
	Rem
	bbdoc: Check if done
	returns: True if the message has been processed
	End Rem
	Method IsDone:Int()
		Return _done
	End Method
	
	Rem
	bbdoc: Check if canceled
	returns: True if the message has been canceled
	about: Messages can be canceled using #Cancel.
	End Rem
	Method IsCanceled:Int()
		Return _canceled
	End Method
	
	Rem
	bbdoc: Find the result of the message
	returns: The result of the message, or Null if not done
	about:
	Note that Null is also a valid result value. Use #IsDone,
	if you need to know whether the message has been processed.
	End Rem
	Method Result:Object()
		Return _result
	End Method
	
?threaded
	Rem
	bbdoc: Wait for the result (threaded only)
	returns: The result of the message after done.
	about:
	Be careful not to introduce deadlocks!
	
	If the message is/was canceled, #Wait returns Null.
	End Rem
	Method Wait:Object()
		If Not (_canceled Or _done)
			_semaphore.Wait
			_semaphore.Post		'In case someone else is waiting
		End If
		Return _result
	End Method
?
	
	' Private
	
	Field _msg:Object, _actor:TActor
	
	Field _result:Object = Null
	
	Field _chain:TMessage = Null
	
	Field _done:Int = 0, _canceled:Int = 0
	
?threaded
	Field _mutex:TMutex = CreateMutex()
	
	Field _semaphore:TSemaphore = CreateSemaphore(0)
?
	
	Method _SendChain()
		If Not _chain Return
		If Not _chain._msg Then _chain._msg = _result
		_chain._actor._Put _chain
		_chain = Null
	End Method
	
	Method _SetDone(result:Object)
?threaded
		_mutex.Lock
?
		_result = result	' Note order - ensures correct 
		_done = True		' behavior on IsDone? -> Result
		_SendChain
?threaded
		_semaphore.Post
		_mutex.Unlock
?
	End Method
	
	Function _Create:TMessage(msg:Object, actor:TActor)
		Local m:TMessage = New TMessage
		m._msg = msg
		m._actor = actor
		actor._Put m
		Return m
	End Function
	
End Type

Rem
bbdoc: A group of actors
about:
Actors can be created as a part of a group.
They then process messages jointly, for example using a single thread.

Actors can be added to a group using #TActor.SetGroup.
A single message from a group can be processed using #Process.
In threaded mode it blocks while there are no messages.

Actor groups can also be created using a TThreadPool for automatic thread management.
End Rem
Type TActorGroup
	
?threaded
	Rem
	bbdoc: Process a message
	returns: True if a message was processed
	about:
	Processes a single message from an actor in the group.
	Uses roughly a round robin order.
	
	In threaded mode #Process blocks until there is something to process!
	End Rem
	Method Process:Int()
		_semaphore.Wait
		_mutex.Lock
		
		For Local a:TActor = EachIn _actors
			Local m:TMessage = a._Get()
			If Not m Continue
			
			' TODO: Use links to enumerate -> RemoveLink
			_actors.Remove a
			_actors.AddLast a
			
			_mutex.Unlock
			m._SetDone a.Receive(m._msg)
			Return True
		Next
		
		_mutex.Unlock
		Return False
	End Method
	
?Not threaded
	Method Process:Int()
		For Local a:TActor = EachIn _actors
			Local m:TMessage = a._Get()
			If Not m Continue
			
			_actors.Remove a
			_actors.AddLast a
			
			m._SetDone a.Receive(m._msg)
			Return True
		Next
		Return False
	End Method
?
	
	' Private
	
	Field _actors:TList = New TList
	
?threaded
	Field _mutex:TMutex = CreateMutex()
	
	Field _semaphore:TSemaphore = CreateSemaphore(0)
	
	Method _AddActor(actor:TActor)
		_mutex.Lock
		_actors.AddLast actor
		_mutex.Unlock
	End Method
	
	Method _RemoveActor(actor:TActor)
		_mutex.Lock
		_actors.Remove actor
		_mutex.Unlock
	End Method
	
?Not threaded
	Method _AddActor(actor:TActor)
		_actors.AddLast actor
	End Method
	
	Method _RemoveActor(actor:TActor)
		_actors.Remove actor
	End Method
?
	
End Type

?threaded

Rem
bbdoc: A threaded actor group (threaded only)
about:
A thread pool is a threaded actor group, 
available only in threaded mode. It uses 
a number of threads to process messages 
from all actors in the group roughly in
a round-robin order.
The number of threads backing the actors 
can be set using #SetThreads (default 1).
End Rem
Type TThreadPool Extends TActorGroup
	
	Rem	bbdoc: The default number of threads
	End Rem
	Const NUM_THREADS:Int = 1
	
	Rem
	bbdoc: Modify the number of threads
	about:
	New threads are created instantly. Old threads stop 
	only after they run out of messages.
	End Rem
	Method SetThreads(num:Int)
		_mutex.Lock
		If num < _threads
			For Local i:Int = num Until _threads
				_semaphore.Post
			Next
		Else
			For Local i:Int = _threads Until num
				CreateThread(_ProcessMessages, Self)
			Next
		End If
		_threads = num
		_mutex.Unlock
	End Method
	
	' Private
	
	Field _threads:Int = NUM_THREADS
	
	Method New()
		For Local i:Int = 0 Until _threads
			CreateThread(_ProcessMessages, Self)
		Next
	End Method
	
	Function _ProcessMessages:Object(data:Object)
		Local group:TActorGroup = TActorGroup(data)
		
		Try
			While group.Process()
			Wend
		Catch o:Object
			Print o.ToString()
			Print "THREAD TERMINATED UNEXPECTEDLY"
		End Try
		Return Null
	End Function
	
End Type

Rem
bbdoc: A threaded actor (threaded only)
about:
You can inherit TActorThread to create actors with 
dedicated threads, or use #CreateActorThread.

If you inherit this type, you need to implement #Receive.
End Rem
Type TActorThread Extends TActor Abstract
	
	Method SetGroup(group:TActorGroup)
		Throw "Actor threads have a private group"
	End Method
	
	Method New()
		Local t:TThreadPool = New TThreadPool
		If t._threads <> 1 Then t.SetThreads 1
		_group = t
		_group._AddActor Self
	End Method
	
End Type

?

Rem
bbdoc: Create an actor based on a callback
about:
You can use this to easily create simple actors without
needing to inherit #TActor. The @receive callback is required.
End Rem
Function CreateActor:TActor( receive:Object(msg:Object), group:TActorGroup = Null )
	Assert receive Else "Callback required"
	Return TCallbackActor._Create(receive, group)
End Function

Rem
bbdoc: Create an actor group
about:
You can create actors into the group using #CreateActor,
or add them using #TActor.SetGroup.
End Rem
Function CreateGroup:TActorGroup()
	Return New TActorGroup
End Function

?threaded

Rem
bbdoc: Create an actor thread based on a callback (threaded only)
about:
You can use this to easily create simple actor threads without
needing to inherit #TActorThread or create a #TThreadPool.
The @receive callback is required.
End Rem
Function CreateActorThread:TActorThread( receive:Object(msg:Object) )
	Assert receive Else "Callback required"
	Return TCallbackActorThread._Create(receive)
End Function

Rem
bbdoc: Create an actor thread pool based on a callback (threaded only)
about:
You can use this to easily create simple pools of actor threads without
needing to create a #TThreadPool. The @receive callback is required,
as is the maximum number of threads.
End Rem
Function CreateActorThreadPool:TActor( receive:Object(msg:Object), threads% )
	Assert receive Else "Callback required"
	Assert threads > 0 Else "At least one thread required"
	Local pool:TThreadPool = New TThreadPool
	pool.SetThreads threads
	Return TCallbackActor._Create(receive, pool)
End Function

Rem
bbdoc: Create a threaded actor group (threaded only)
about:
You can create actors into the group using #CreateActor,
or add them using #TActor.SetGroup.
End Rem
Function CreateThreadPool:TThreadPool( threads:Int )
	Local p:TThreadPool = New TThreadPool
	p.SetThreads threads
	Return p
End Function

?

' Private implementation types for callback actors

Type TCallbackActor Extends TActor
	
	Field _receive:Object(msg:Object)
	
	Method Receive:Object(msg:Object)
		Return _receive(msg)
	End Method
	
	Function _Create:TCallbackActor( receive:Object(msg:Object), group:TActorGroup )
		Local a:TCallbackActor = New TCallbackActor
		a._receive = receive
		a.SetGroup group
		Return a
	End Function
	
End Type

?threaded

Type TCallbackActorThread Extends TActorThread
	
	Field _receive:Object(msg:Object)
	
	Method Receive:Object(msg:Object)
		Return _receive(msg)
	End Method
	
	Function _Create:TCallbackActorThread( receive:Object(msg:Object) )
		Local a:TCallbackActorThread = New TCallbackActorThread
		a._receive = receive
		Return a
	End Function
	
End Type

?
