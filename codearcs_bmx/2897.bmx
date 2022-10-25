; ID: 2897
; Author: skn3[ac]
; Date: 2011-10-27 08:42:24
; Title: image cache across canvas (alternative to GLShareContexts on multiple canvas)
; Description: This is a solution to sharing images across multiple canvas. The module will manage creating duplicate copies so that each canvas can have its own version of an image

Module skn3.cache

SuperStrict

'imports
Import brl.map
Import brl.linkedlist
Import brl.pixmap
Import brl.glmax2d

'internal
Private
Global cacheGroups:TMap = CreateMap()
Global cacheShared:Int = False
Global cacheImageFlags:Int = 0
Public

'classes
Type Skn3CacheGroup
	Field path:String
	Field pixmap:Int = False
	Field instances:TList = CreateList()
End Type

Type Skn3CacheInstance
	Field group:Skn3CacheGroup
	Field image:TImage
	Field flags:Int
	Field refCount:Int
	Field link:TLink
	Field pointers:TList = CreateList()
	
	'internal
	Method AddPointer:Skn3CachePointer()
		' --- return a new pointer to this instance ---
		Local pointer:Skn3CachePointer = New Skn3CachePointer
		pointer.instance = Self
		pointer.link = pointers.AddLast(pointer)
		Return pointer
	End Method
	
	Method copy:Skn3CacheInstance()
		' --- create a copy of this particular instance ---
		'reuse or copy instances
		If cacheShared = True
			'reuse
			'increase reference count
			refCount :+ 1
			
			'return instance
			Return Self
		Else
			'create new instance
			Local instance:Skn3CacheInstance = New Skn3CacheInstance
			instance.group = group
			instance.image = LoadImage(image.pixmaps[0],flags)
			instance.flags = flags
			instance.refCount = 1
			instance.link = group.instances.AddLast(instance)
			
			'return instance
			Return instance
		EndIf
	End Method
End Type

Type Skn3CachePointer
	'this is a "handle" which should be stored by things using the cache.
	'by storing this handle it means the internal data within can change without having to update the code using cache
	'eg a new image, instance, blah can be swapped in and code can still use cache.image to reference the image
	'the module will handle the pointers and such!
	
	'the pointer should be used for all external operations as that way we can prevent any issues with referenceing a nulled instance
	Field instance:Skn3CacheInstance
	Field link:TLink
	
	'api
	Method Free()
		' --- this will free the instance the pointer points to ---
		If instance
			'remove the pointer from instance
			link.remove()
			
			'decrease instance reference count and see if the instance should then be freed
			instance.refCount :- 1
			If instance.refCount = 0
				'remove all other pointers from the instance
				If instance.pointers.isEmpty() = False
					For Local pointer:Skn3CachePointer = EachIn instance.pointers
						pointer.instance = Null
						pointer.link = Null
					Next
					instance.pointers.Clear()
				EndIf
			
				'free the image
				instance.image = Null
				instance.flags = 0
				
				'remove from cache group
				instance.link.remove()
				instance.group = Null
				
				'remove cache group if its instances are empty
				If instance.group.instances.isEmpty() cacheGroups.remove(instance.group.path)
			EndIf
			
			'null the pointer
			instance = Null
			link = Null
		EndIf
	End Method
	
	Method copy:Skn3CachePointer()
		' --- this will create a copy of the instance and return a new pointer ---
		If instance Return instance.copy().AddPointer()
	End Method
	
	Method Change(url:Object)
		' --- this will change the cached image using the supplied pixmap ---
		'this function will go through and make sweeping changes, all instances and pointers to instances will now be altered!
		'we can only change if the pointer is still pointing
		If instance
			Local newGroup:Skn3CacheGroup
			Local oldInstance:Skn3CacheInstance
			Local oldPointer:Skn3CachePointer
			Local newInstance:Skn3CacheInstance
			
			'check if it is a pixmap or file
			Local cachePath:String
			Local pixmap:TPixmap = TPixmap(url)
			Local path:String
			If pixmap = Null
				'get cache path
				path = String(url)
				cachePath = "file::"+path.ToLower()
			Else
				'get cache path
				cachePath = "pixmap::"+String(Long(pixmap.Pixels))
			EndIf
			
			'check to see if there is an existing group and that teh group is not the same as the current one
			newGroup = Skn3CacheGroup(cacheGroups.ValueForKey(cachePath))
			If newGroup <> Null And instance.group = newGroup Return
			
			'check if we are changing to the group or creating a new one
			If newGroup
				'the image is currently cached so we just need to do a mass alteration and reinstance
				Local oldGroup:Skn3CacheGroup = instance.group
				
				'scan all group instances (connected to THIS pointer .. not the existing one... ok .. got that ? :D)
				For oldInstance = EachIn oldGroup.instances
					'create a new instance from the existing group
					newInstance = Skn3CacheInstance(newGroup.instances.First()).copy()
					
					'update all of the pointers
					For oldPointer = EachIn oldInstance.pointers
						'update 
						oldPointer.instance = newInstance
						oldPointer.link = newInstance.pointers.AddLast(oldPointer)
					Next
				Next
				
				'get rid of the old group
				cacheGroups.remove(oldGroup.path)
			Else
				'the image is not currently cached so we have to load it and alter the existing cache
				'if there is no pixmap Then Load it now, this is if a path is provided
				If pixmap = Null pixmap = LoadPixmap(path)
				
				'update all the instance images
				For oldInstance = EachIn instance.group.instances
					oldInstance.image = LoadImage(pixmap,oldInstance.flags)
				Next
				
				'update the group cache
				cacheGroups.remove(instance.group.path)
				instance.group.path = cachePath
				cacheGroups.Insert(cachePath,instance.group)
			EndIf
		EndIf
	End Method
	
	Method Draw(x:Float,y:Float)
		' --- draw the image ---
		If instance DrawImage(instance.image,x,y)
	End Method
	
	Method DrawPortion(destinationX:Float,destinationY:Float,destinationWidth:Float,destinationHeight:Float,sourceX:Float,sourceY:Float,sourceWidth:Float=0,sourceHeight:Float=0)
		' --- draw a portion of the image ---
		If instance DrawSubImageRect(instance.image,destinationX,destinationY,destinationWidth,destinationHeight,sourceX,sourceY,sourceWidth,sourceHeight)
	End Method
	
	Method Tile(x:Float,y:Float)
		' --- tile the image ---
		If instance TileImage(instance.image,x,y)
	End Method
	
	Method Width:Int()
		' -- get width of image ---
		If instance Return instance.image.Width
	End Method
	
	Method Height:Int()
		' -- get height of image ---
		If instance Return instance.image.Height
	End Method
End Type

'api
Function LoadCache:Skn3CachePointer(url:Object)
	' --- cache an image from disk ---
	Local cachePath:String
	
	'check if it is a pixmap or file
	Local pixmap:TPixmap = TPixmap(url)
	Local path:String
	If pixmap = Null
		'get cache path
		path = String(url)
		cachePath = "file::"+path.ToLower()
	Else
		'get cache path
		cachePath = "pixmap::"+String(Long(pixmap.Pixels))
	EndIf
	
	'look for existing map
	Local group:Skn3CacheGroup = Skn3CacheGroup(cachegroups.ValueForKey(cachePath))
	
	'create new one if it doesn't exist
	If group = Null
		'load the timage object from pixmap or path
		Local image:TImage
		If pixmap = Null
			image = LoadImage(path,cacheImageFlags)
		Else
			image = LoadImage(pixmap,cacheImageFlags)
		EndIf
		
		'make sure the image loaded
		If image = Null Return Null
		
		'create the cache group
		group = New Skn3CacheGroup
		group.pixmap = pixmap <> Null
		group.path = cachePath
		
		'add the group to the global cache map
		cacheGroups.Insert(group.path,group)
		
		'create first instance
		Local instance:Skn3CacheInstance = New Skn3CacheInstance
		
		instance.group = group
		instance.image = image
		instance.flags = cacheImageFlags
		instance.refCount = 1
		
		'add the instance to the group
		instance.link = group.instances.AddLast(instance)
		
		'return a pointer to the instance
		Return instance.AddPointer()
	Else
		'reuse or copy instances and return a new pointer to it
		'all in one swoop we will return a pointer to duplicated/referenced instance
		Return Skn3CacheInstance(group.instances.First()).copy().AddPointer()
	EndIf
End Function

Function SetCacheShared(shared:Int)
	' --- specify if images should be reference counted instead of duplicated ---
	'this will only alter image data loaded after calling teh command
	cacheShared = shared
End Function
