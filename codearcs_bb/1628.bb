; ID: 1628
; Author: Nilium
; Date: 2006-02-27 00:15:00
; Title: Component-Object Entity System
; Description: The base of a component-object based entity system

Strict


Private

Const COM_UNKNOWN% = -1
Global maxCName%=0


Public

Function RegisterComponentName%( name% )
    name = Abs(name)+1
    If name > maxCName Then maxCName = name
    Return name
End Function

Type IComponent
    Field _disposed% = 0
    Field _link:TLink
    Field _name% = COM_UNKNOWN
    
    Method Update( go:IGameObject ) Abstract
    
    Method Dispose( )
        Assert _disposed=0, "Component is already disposed"
        _disposed = 1
        _name = COM_UNKNOWN
    End Method
End Type

Type IGameObject
    Field _components:IComponent[]
    
    Method New( )
        _components = New IComponent[maxCName]
    End Method
    
    'private - check components array length (and expand if neccessary -- it can only ever expand unless you modify it manually)
    Method CheckCLength( )
        If _components.Length <> maxCName Then..
            _components = _components[..maxCName]
    End Method
    
    Method GetComponent:IComponent( name% )
        If name = COM_UNKNOWN Then Return Null
        If name > _components.Length-1 Then Return Null ' It's obviously not going to have it then
        Return _components[name]
    End Method
    
    ' Components have to be added in the correct order, DO NOT FORGET THIS
    Method AddComponent( c:IComponent )
        If c._name = COM_UNKNOWN Or c._link Then Return ' We don't want to add a used or disposed/unknown component
        If GetComponent( c._name ) Then Return ' And we don't want two of the same component
        CheckCLength( )
        _components[c._name] = c
    End Method
    
    Method RemoveComponent( name% )
        If name = COM_UNKNOWN Then Return
        Local c:IComponent = GetComponent( name )
        If c Then c.Dispose( )
    End Method
    
    Method Update( )
        For Local i:Int = 0 To _components.Length-1
            If _components[i] Then _components[i].Update( Self )
        Next
    End Method
End Type
