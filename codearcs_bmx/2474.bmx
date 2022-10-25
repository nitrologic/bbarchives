; ID: 2474
; Author: N
; Date: 2009-05-08 07:08:48
; Title: Notification Center
; Description: Types to send notifications, usually between threads.

SuperStrict

?Threaded
' Doesn't really affect anything important to wrap it in ?Threaded..?, but it
' does remove on addition function call at startup
Import Brl.Threads
?
Import Brl.LinkedList
Import Brl.Reflection		' For notifying objects

Private

Rem:doc
	Internal type for storing information about notification observers.
EndRem
Type TNotificationObserver {Immutable}
	Field name$ {ReadOnly}
	Field observer:Object {ReadOnly}
	Field invocation:TMethod {ReadOnly}
	Field forObject:Object {ReadOnly}
End Type

Public

Type TNotification {Immutable}
	Field _name:String {ReadOnly}
	Field _object:Object {ReadOnly}
	Field _userinfo:Object {ReadOnly}
	
	Rem:doc
		Initializes the notification with a {param:name},
		{param:associated object|obj}, and {param:user-info object|info}.
		@param:name The name of the notification.
		@param:obj The object associated with the notification.
		@param:info User info that accompanies the notification.
		@returns The notification.
	EndRem
	Method InitWithName:TNotification(name$, obj:Object, info:Object)
		name = name.Trim()
		Assert name, "Cannot initialize notification with empty name"
		_name = name
		_object = obj
		_userinfo = info
		Return Self
	End Method
	
	Rem:doc
		Makes a copy of the target notification.
		@return A copy of the target notification.
	EndRem
	Method Copy:TNotification()
		Return New TNotification.InitWithName(_name, _object, _userinfo)
	End Method
	
	Rem:doc
	Returns the name of the notification.
	EndRem
	Method Name$()
		Return _name
	End Method
	
	Rem:doc
	Returns the object associated with the notification.
	EndRem
	Method AssociatedObject:Object()
		Return _object
	End Method
	
	Rem:doc
	Returns the user-info object for the notification.
	EndRem
	Method UserInfo:Object()
		Return _userinfo
	End Method
End Type

Global NotificationTypeId:TTypeId = TTypeId.ForName("TNotification")

Public

Rem:doc
	Notification center type.
EndRem
Type TNotificationCenter
	Field _queue:TList {Restricted}
	Field _observers:TList {Restricted}
	
	?Threaded
	Global _mainCenterLock:TMutex
	Global _mainCenter:TNotificationCenter
	Global _default:TThreadData
	
	Field _lock:TMutex {Restricted}
	
	?Not Threaded
	Global _default:TNotificationCenter
	?
	
'#region Instance methods
	
	Method New()
		?Threaded
		_lock = TMutex.Create()
		?
		
		_queue = New TList
		_observers = New TList
	End Method
	
	Method Delete()
		ProcessQueue()
		?Threaded
		_lock.Close()
		?
	End Method
	
	Rem:doc
		Disposes of the notification center.  You probably don't need to call
		this for any reason, but it provides a means of potentially expediting
		the release of a notification center.
	EndRem
	Method Dispose()
		ProcessQueue()
		?Threaded
		
		If _default.GetValue() = Self Then
			_default.SetValue(Null)
		EndIf
		
		If CurrentThread() = MainThread() Then
			_mainCenterLock.Lock()
			If _mainCenter = Self Then
				_mainCenter = Null
			EndIf
			_mainCenterLock.Unlock()
		EndIf
		
		?Not Threaded
		
		If _default = Self Then
			_default = Null
		EndIf
		
		?
	End Method
	
	Rem:doc
		Makes the target object the default notification center for the
		current thread.
	EndRem
	Method MakeDefault()
		?Threaded
		_default.SetValue(Self)
		?Not Threaded
		_default = Self
		?
	End Method
	
	Rem:doc
		Posts a notification.  This will send the notification to all
		observers of the {param:notification}.
		
		@param:notification The name of the notification.
		@param:obj The object that spawned the notification, if any.
		@param:userinfo Any information to be passed along with the
		notification.  E.g., a map containing information pertinent to the
		notification.
	EndRem
	Method PostNotificationWithName(notification:String, obj:Object=Null, userinfo:Object=Null)
		PostNotification(New TNotification.InitWithName(notification, obj, userinfo))
	End Method
	
	Rem:doc
		Posts a notification.  This will send the notification to all
		observers of the {param:notification}.
		@param:notification The notification.
	EndRem
	Method PostNotification(notification:TNotification)
		?Threaded
		_lock.Lock()
		?
		Local observers:Object[] = _observers.ToArray()
		?Threaded
		_lock.Unlock()
		?
		
		Local assObj:Object = notification.AssociatedObject()
		Local assName:String = notification.Name()
		Local invoker:TMethod = Null
		Local args:Object[1]
		args[0] = notification
		For Local o:TNotificationObserver = EachIn observers
			If (o.forObject <> Null And o.forObject <> assObj) Or (o.name <> Null And o.name <> assName) Then
				Continue
			EndIf
			
			invoker = o.invocation
			If invoker Then
				invoker.Invoke(o.observer,args)
			Else
				o.observer.SendMessage(notification, Self)
			EndIf
		Next
	End Method
	
	'#region Queueing
	
	Rem:doc
		Posts any notifications currently in the queue.
	EndRem
	Method ProcessQueue()
		?Threaded
		_lock.Lock()
		?
		Local notifications:Object[] = _queue.ToArray()
		_queue.Clear()
		?Threaded
		_lock.Unlock()
		?
		
		For Local n:TNotification = EachIn notifications
			PostNotification(n)
		Next
	End Method
	
	Rem:doc
		Queues a {param:notification} with the notification center.
		
		@param:notification The name of the notification.
		@param:obj The object that spawned the notification, if any.
		@param:userinfo Any information to be passed along with the
		notification.  E.g., a map containing information pertinent to the
		notification.
	EndRem
	Method EnqueueNotificationWithName(notification:String, obj:Object=Null, userinfo:Object=Null)
		?Threaded
		_lock.Lock()
		?
		EnqueueNotification(New TNotification.InitWithName(notification, obj, userinfo))
		?Threaded
		_lock.Unlock()
		?
	End Method
	
	Rem:doc
		Queues a {param:notification} with the notification center.
		@param:notification The notification.
	EndRem
	Method EnqueueNotification(notification:TNotification)
		?Threaded
		_lock.Lock()
		?
		_queue.AddLast(notification)
		?Threaded
		_lock.Unlock()
		?
	End Method
	
	'#endregion
	
	'#region Observing
	
	Rem:doc
		Adds an {param:observer} for the specified {param:notification} and
		{param:object|forObject}.
		
		@param:observer The observing object.
		@param:methodName The name of the method that will be invoked for any
		observed notifications.  If the method cannot be found, notifications
		are passed to Object.SendMessage as the message, and the notification
		center as the context object.
		@param:notification The name of the notification to observe.  If null
		or empty, the object will observe all notification.
		@param:forObject The object to observe notifications from.  If null,
		the observer will receive all notifications.
		
		@note Due to the lack of weak references in BlitzMax, observers and
		any objects they are observing will remain in memory so long as they
		are observing/being observer.  To ensure you do not leak memory,
		remove the observer when disposing of objects or when the object no
		longer needs to observe notifications.
	EndRem
	Method AddObserver(observer:Object, methodName:String, notification:String=Null, forObject:Object=Null)
		Assert observer, "Null observer"
		
		methodName = methodName.Trim()
		Assert methodName.Length, "Empty method name"
		Local o:TNotificationObserver = New TNotificationObserver
		
		o.observer = observer
		o.forObject = forObject
		o.name = notification
		
		o.invocation = TTypeId.ForObject(observer).FindMethod(methodName)
		If o.invocation Then
			' ensure the method only accepts the notification
			Local args:TTypeId[] = o.invocation.ArgTypes()
			Assert args.Length=1 And args[0] = NotificationTypeId, ..
				"Invalid parameters for method ~q"+methodName+"~q; must be "+methodName+"(n:TNotification)"
		EndIf
		
		?Threaded
		_lock.Lock()
		?
		_observers.AddLast(o)
		?Threaded
		_lock.Unlock()
		?
	End Method
	
	Rem:doc
		Removes an {param:observer} for the specified {param:notification} and
		{param:object|forObject}.
		
		@param:observer The observer.
		@param:notification The notification being observed.  If null, will
		match any instance of the observer.
		@param:forObject The object being observed.  If null, will match any
		instance of the observer.
	EndRem
	Method RemoveObserver(observer:Object, notification:String=Null, forObject:Object=Null)
		If observer = Null Then Return
		?Threaded
		_lock.Lock()
		?
		Local observers:Object[] = _observers.ToArray()
		If notification And forObject Then
			For Local o:TNotificationObserver = EachIn observers
				If o.observer <> observer Then
					Continue
				EndIf
				
				If o.name = notification And o.forObject = forObject Then
					_observers.Remove(o)
				EndIf
			Next
		ElseIf notification Then
			For Local o:TNotificationObserver = EachIn observers
				If o.observer <> observer Then
					Continue
				EndIf
				
				If o.name = notification Then
					_observers.Remove(o)
				EndIf
			Next
		ElseIf forObject Then
			For Local o:TNotificationObserver = EachIn observers
				If o.observer <> observer Then
					Continue
				EndIf
				
				If o.forObject = forObject Then
					_observers.Remove(o)
				EndIf
			Next
		Else
			For Local o:TNotificationObserver = EachIn observers
				If o.observer = observer Then
					_observers.Remove(o)
				EndIf
			Next
		EndIf
		?Threaded
		_lock.Unlock()
		?
	End Method
	
	'#endregion
	
'#endregion
	
'#region Class methods
	
	Rem:doc
		Internal, do not call.
		
		@note There should not be any side-effects from calling this method,
		but it is not intended to be called more than once, and this module
		calls it.
	EndRem
	Function Initialize()
		?Threaded
		If Not _mainCenterLock Then
			_mainCenterLock = TMutex.Create()
		EndIf
		
		If Not _default Then
			_default = TThreadData.Create()
		EndIf
		?
		DefaultCenter()
	End Function
	
	Rem:doc
		Posts queued notifications for the default center of the calling
		thread.
	EndRem
	Function Process()
		Local center:TNotificationCenter = DefaultCenter()
		center.ProcessQueue()
	End Function
	
	Rem:doc
		Gets the default notification center for the calling thread.
		@return The default notification center for the calling thread.
	EndRem
	Function DefaultCenter:TNotificationCenter()
		?Threaded
			Local center:TNotificationCenter = TNotificationCenter(_default.GetValue())
			
			If center = Null Then
				center = New TNotificationCenter
				_default.SetValue(center)
				
				If CurrentThread() = MainThread() Then
					_mainCenterLock.Lock()
					_mainCenter = center
					_mainCenterLock.Unlock()
				EndIf
			EndIf
			
			Return center
		?Not Threaded
			If _default = Null Then
				_default = New TNotificationCenter
			EndIf
			Return _default
		?
	End Function
	
	Rem:doc
		Gets the default notification center for the main thread.
		@return The default notification center for the main thread.
	EndRem
	Function DefaultCenterForMainThread:TNotificationCenter()
		?Threaded
		Return TNotificationCenter._mainCenter
		?Not Threaded
		Return TNotificationCenter.DefaultCenter()
		?
	End Function
	
'#endregion
End Type
TNotificationCenter.Initialize()
