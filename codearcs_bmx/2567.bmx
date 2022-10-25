; ID: 2567
; Author: Nilium
; Date: 2009-08-25 02:03:21
; Title: Animating Fields
; Description: Code to animate the fields of objects over time.

Strict

Type Animation
	?Threaded
	Global AnimationsLock:TMutex = TMutex.Create()
	?
	Global Animations:TList = New TList
	Global AnimationTimer:TTimer = TTimer.Create(60, Null)
	
	Field o:Object		' Object being animated, used to keep pointer to object in scope during animation
	Field f:TField
	Field duration:Double
	Field stime:Int
	Field start:Double
	Field finish:Double
	Field fn:Double(s:Double,f:Double,t:Double)
'	Field last:Double ' check to see if the value has changed since last time
	
	Method Update(ctime%=-1)
		If ctime=-1 Then ctime = Millisecs()
		Local time! = (ctime-stime)/duration
		If time >= 1! Then
			?Threaded
			AnimationsLock.Lock
			?
			Animations.Remove(Self)
			?Threaded
			AnimationsLock.Unlock
			?
			If Not fn Then f.SetDouble(o, finish)
		Else
			Local nv!
			If fn Then
				nv = fn(start, finish, Min(time,1))
			Else
				nv = start+((finish-start)*Min(time,1))
			EndIf
			f.SetDouble(o, nv)
		EndIf
	End Method
	
	Function UpdateAnimations()
		?Threaded
		AnimationsLock.Lock
		?
		If Not Animations.IsEmpty() Then
			Local ctime% = Millisecs()
			Local anims:TList = Animations.Copy()
			For Local a:Animation = EachIn Animations
				a.Update(ctime)
			Next
		EndIf
		?Threaded
		AnimationsLock.Unlock
		?
	End Function
	
	Function tick_UpdateAnimations:Object(id%, data:Object, ctx:Object)
		Local event:TEvent = TEvent(data)
		If event And event.id = EVENT_TIMERTICK And event.source = AnimationTimer Then
			UpdateAnimations()
			Return Null ' event handled
		EndIf
		
		Return data
	End Function
	
	Function EnableAutoUpdate()
		AddHook(EmitEventHook, Animation.tick_UpdateAnimations, Null, 1000)
	End Function
	
	Function DisableAutoUpdate()
		RemoveHook(EmitEventHook, Animation.tick_UpdateAnimations, Null)
	End Function
End Type

Function Animate(obj:Object, value$, newvalue!, duration!=5000, fn:Double(start:Double, finish:Double, time:Double)=Null)
	Local a:Animation
	
	?Threaded
	AnimationsLock.Lock
	?
	
	If Not Animation.Animations.IsEmpty() Then
		For a = EachIn Animation.Animations
			If a.o = obj And a.f.Name().ToLower() = value.ToLower() Then
				a.start = a.f.GetDouble(obj)
				a.finish = newvalue
				a.duration = duration
				a.stime = Millisecs()
				?Threaded
				AnimationsLock.Unlock
				?
				Return
			EndIf
		Next
	EndIf
	
	a = New Animation
	a.o = obj
	a.f = TTypeID.ForObject(obj).FindField(value)
	a.duration = duration
	a.stime = Millisecs()
	a.start = a.f.GetDouble(obj)
	a.finish = newvalue
	a.fn = fn
	Animation.Animations.AddLast(a)
	
	?Threaded
	AnimationsLock.Unlock
	?
End Function
