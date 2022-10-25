; ID: 2937
; Author: Spencer
; Date: 2012-03-20 17:13:45
; Title: Simple B3D Reference Array
; Description: Reference Array. Can be used to pass arrays of various sizes to and from functions

;*******************************************************************************************
;Type TArray : A Reference Array good for passing arrays between functions
;
; -Functions-                                   -Example-
; CreateArray( array_size )                     Global A = CreateArray(42)
; ArraySet( array , index, value$ )             ArraySet(A, 30, "String value at index 30")
; ArrayGet$( array , index )                    Print ArrayGet(A, 30)
; ArraySize( array )                            Print ArraySize(A)
; DeleteArray( array )                          DeleteArray(A)
;
;*******************************************************************************************
Type TArray
    Field Size
    Field PtrSize
    Field PtrList$
End Type

Type TElement
    Field Value$
End Type

Function CreateArray(Size)
    Local Array.TArray = New TArray
    Local ElementCount = 0
    Local Element.TElement
    Array\Size = Size
    Array\PtrSize = Len(Str(Size))
    For ElementCount = 1 To Size
        Element = New TElement
        Array\PtrList = Array\PtrList + RSet(Handle(Element),Array\PtrSize)
    Next
    Return Handle(Array)
End Function

Function DeleteArray(ArrayPtr)
    Local Array.TArray = Object.TArray(ArrayPtr)
    Local ElementCount = 0
    Local ElementPtr
    Local Element.TElement
    Repeat
        ElementPtr =Int(Trim(Mid(Array\PtrList,1 + (ElementCount*Array\PtrSize),Array\PtrSize)))
        Element = Object.TElement(ElementPtr)
        Delete Element    
        ElementCount=ElementCount+1
        If ElementCount = Array\Size Then
            Exit
        EndIf
    Forever
    Delete Array
End Function

Function TArray_GetElementObject.TElement(ArrayPtr,Index)
    Local Array.TArray = Object.TArray(ArrayPtr)
    Local ElementPtr = Int(Trim(Mid(Array\PtrList,1 + (Index * Array\PtrSize),Array\PtrSize)))
    Return Object.TElement(ElementPtr)
End Function

Function ArraySize(ArrayPtr)
    Local Array.TArray = Object.TArray(ArrayPtr)
    Return Array\Size
End Function

Function ArrayGet$(ArrayPtr,Index)
    Local Element.TElement = TArray_GetElementObject(ArrayPtr,Index)
    Return Element\Value
End Function

Function ArraySet$(ArrayPtr, Index, Value$)
    Local Element.TElement = TArray_GetElementObject(ArrayPtr,Index)
    Element\Value = Value
End Function

;*******************************************************************************************
;END Type TArray 
;*******************************************************************************************



Global A = CreateArray(42)

ArraySet(A,31,"Value at index 31")
Print ArrayGet(A,31)
Print ArraySize(A)    

ExampleFunction(A)

Print ArrayGet(A,23)
Input "Done..."

ArraySet(A,0,"Hello world")

Function ExampleFunction(array)
    Print "Array[0] = " + ArrayGet(array,0)
    Print "ArraySize = " + ArraySize(array)
    ArraySet(A,23, "Hello world")
End Function
