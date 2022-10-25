; ID: 2689
; Author: Bobysait
; Date: 2010-04-02 12:19:17
; Title: TFactory Class
; Description: Extend TMap to a Factory class designed for Game Engine and else

SuperStrict

'#region TFactory
	
	'#region Factory Base Type
	
	' - Protected from user -
		Private
		
		Type __TFactory__ extends tmap
			
			Field _AutoInc		:Int		=	0
			Field _Default		:String		=	""
			Global _LastName	:String		=	""
			
			' - generate a valid unique name to insert in a factory hashmap -
			
			Method AutoName		:String			( Name:String="" )
				
				' if empty name -> fill it with the default name
				If Name="" Then Name = _Default
				
				Local OrigName:String = Name
				
				' check if the map countais this name
				If Not(Contains(Name)) Then If Name<>_Default Then Return Name
				
				Repeat
					
					Name = OrigName+_AutoInc
					
					If Not(Contains(Name)) Then Return Name
					
					_AutoInc :+ 1
					
					If _AutoInc=0 Then Return ""
					
				Forever
				
				Return ""
				
			End Method
			
			Method Insert ( Key:Object,Value:Object )
				Insert2 ( String(Key),Value )
			End Method
			
			' - Insert a new object in the factory -
			
			Method Insert2	:String		( Name:String, obj:Object, autoname:Byte=True )
				
				_LastName = Name
				
				' no empty name
				If Name=""
					
					If autoname=False Then Return ""
					
					_LastName = Self.autoname ( Name )
					
				EndIf
				
				' no entries with same name
				If Contains ( _LastName )
					
					If autoname=False Then Return ""
					
					_LastName = Self.autoname ( _LastName )
					
				EndIf
				
				' remove old link to the Hack map
				Remove ( Name )
				
				' insert the name in the hash- map
				Super.Insert ( _LastName, obj )
				
				Return _LastName
				
			End Method
			
		End Type
		
	'#end region
	
	
	'#region public Factory
	
	' - Public factory SDK -
		
		Public
		
		Rem
			bbdoc: TFactory class is an extension of TMap
			about: TFactory is an extension of TMap, and provide an easy way to deal with "unique" named object
		EndRem
		Type TFactory Extends __TFactory__
			
			Rem
				bbdoc: Create a factory object
				about: create a factory with parameters @DefaultName as the defaultname for autonaming and @CounterStart for auto-increment
			EndRem
			Function Create :TFactory ( DefaultName:String, CounterStart:Int = 0 )
				
				Local factory:TFactory		=	New TFactory
				factory._AutoInc			=	CounterStart
				factory._Default			=	DefaultName
				
				Return factory
				
			End Function
			
			Rem
				bbdoc: Insert an object in the specified @factory
				about: insert an object initialised with the name @Name, the name will then be automaticaly formated to be unique
				check the formated name with #FactorylastRegistered
			endrem
			Method InsertName		:String		( Name:String, obj:Object, autoname:Byte=True )
				Return Super.Insert2( Name,obj,autoname)
			End Method
			
			Rem
				bbdoc: Return the last registered name inserted in the specified @factory
				about: 
			endrem
			Method LastRegistered	:String		( )
				Return _LastName
			End Method
			
		End Type
		
		Rem
			bbdoc: Create a factory object
			about: create a factory with parameters @DefaultName as the defaultname for autonaming and @CounterStart for auto-increment
		EndRem
		Function CreateFactory :TFactory( DefaultName:String="", CounterStart:Int = 0 )
			Return TFactory.Create(DefaultName,CounterStart)
		End Function
		
		Rem
			bbdoc: Insert an object in the specified @factory
			about: insert an object initialised with the name @Name, the name will then be automaticaly formated to be unique
			check the formated name with #FactorylastRegistered
		endrem
		Function FactoryInsert :String ( Factory:TFactory, Obj:Object, Name :String, autoname:Byte = True )
			Return Factory.InsertName ( Name, Obj, autoname )
		End Function
		
		Rem
			bbdoc: Return the last registered name inserted in the specified @factory
			about: 
		endrem
		Function FactorylastRegistered:String(factory:TFactory)
			Return factory.LastRegistered()
		End Function
		
	'#end region
	
'#end region
