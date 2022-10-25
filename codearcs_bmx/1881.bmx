; ID: 1881
; Author: Paposo
; Date: 2006-12-16 01:28:40
; Title: Priority Queue
; Description: Priority queue based in heap tree

The code is self explanatory. Sorry, comments are in spanish.

Bye,
    Paposo

[codebox]

SuperStrict

Private

'*******************************************************************************************************
Rem
	Asocia un objeto cualquiera a una prioridad.
	Proporciona un metodo para comparar las prioridades
	
EndRem

Type TDatoHeap
	Field dato:Object
	Field prioridad:Int
	
'-------------------------------------------------------------------------------------------------------	
'	Metodo comparador

	Method comparar:Int(obj:TDatoHeap)
		If(prioridad>obj.prioridad) 
			Return 1
		ElseIf (prioridad<obj.prioridad)
			Return -1
		Else
			Return 0
		EndIf
	EndMethod
	
'-------------------------------------------------------------------------------------------------------	
'	Funcion auxiliar para crear un objeto inicializado

	Function create:TDatoHeap(obj:Object, prior:Int)
		Local dat:TDatoHeap=New TDatoHeap
		dat.dato=obj
		dat.prioridad=prior
		Return dat
	EndFunction

EndType

Public

'*******************************************************************************************************
Rem
	Cola prioritaria basada en un arbol de pilon
	Ofrece una alta eficiencia pero puede llegar a consumir mucha memoria si se almacenan 
	muchos elementos.	
EndRem

Type TColaHeap

	Const CAPACIDAD_INICIAL:Int=3
	
	Field _cola:TDatoHeap[]
	Field _size:Int=0

'-------------------------------------------------------------------------------------------------------	
'	Constructor	

	Method New()
		_cola=New TDatoHeap[CAPACIDAD_INICIAL]
	EndMethod
	
'-------------------------------------------------------------------------------------------------------	
'	Crea una nueva cola con una determinada capacidad inicial
	
	Function create:TColaHeap(capacidadInicial:Int=TColaHeap.CAPACIDAD_INICIAL)
		Local pilon:TColaHeap=New TColaHeap
		pilon._cola=pilon._cola[..capacidadInicial]
		Return pilon
	EndFunction
	
'-------------------------------------------------------------------------------------------------------	
'	Metodo usado para la comparacion de prioridades

	Method _comparador:Int(dato1:TDatoHeap, dato2:TDatoHeap)
		Return dato1.comparar(dato2)
	EndMethod

'-------------------------------------------------------------------------------------------------------	
'	Añade un elemento a la cola con una determinada prioridad
'	Eficiencia O(log(n))

	Method push(obj:Object, prior:Int)
	
		Local dat:TDatoHeap=TdatoHeap.create(obj, prior)
			
		_size:+1
		If(_size>=_cola.length)
			_grow(_size)
		EndIf
		_cola[_size]=dat
		_fixup(_size)
		Return
	EndMethod
	
'-------------------------------------------------------------------------------------------------------	
'	Obtiene el siguiente elemento de la cola sin extraerlo
'	Eficiencia O(k)

	Method peek:Object()
		If(_size=0)
			Return Null
		EndIf
		Return _cola[1].dato
	EndMethod
	
'-------------------------------------------------------------------------------------------------------	
'	Extrae el siguiente elemento de la cola
'	Eficiencia O(log(n))

	Method pop:Object()
		Local retorno:TDatoHeap
		If(_size=0)
			Return Null
		EndIf
		
		retorno=_cola[1]
		_cola[1]=_cola[_size]
		_cola[_size]=Null
		_size:-1
		
		If(_size>1)
			_fixdown(1)
		EndIf
		
		Return retorno.dato
	EndMethod
	
'-------------------------------------------------------------------------------------------------------	
'	Vacia la cola
'	Eficiencia O(n)

	Method clear()
		For Local i:Int=1 To _size
			_cola[i]=Null
		Next
		_size=0
		_cola=_cola[..CAPACIDAD_INICIAL]
		
	EndMethod
	
'-------------------------------------------------------------------------------------------------------	
'	Metodo privado para hacer crecer la cola cuando es necesario

	Method _grow(index:Int)
		Local _sizeAct:Int=_cola.length
		If(index<_sizeAct)
			Return
		Else
			_sizeAct=_sizeAct Shl 2
		EndIf
		_cola=_cola[.._sizeAct]
	EndMethod
	
'-------------------------------------------------------------------------------------------------------	
'	Metodo privado que balancea el arbol al insertar

	Method _fixup(k:Int)
		Local j:Int
		Local tmp:TDatoHeap
		
		While(k>1)
			j=k Shr 1
			If(_comparador(_cola[j],_cola[k]) <=0)
				Exit
			EndIf
			tmp=_cola[j]
			_cola[j]=_cola[k]
			_cola[k]=tmp
			k=j
		Wend
	EndMethod	

'-------------------------------------------------------------------------------------------------------	
'	Metodo privado que balancea el arbol al extraer

	Method _fixdown(k:Int)
		Local j:Int
		Local tmp:TDatoHeap
		
		While(True)
			j=k Shl 1
			If(Not ( (j<=_size) And (j>0)))
				Exit
			EndIf
			If( (j<_size) And (_comparador(_cola[j],_cola[j+1])>0) )
				j:+1
			EndIf
			If(_comparador(_cola[k], _cola[j]) <= 0)
				Exit
			EndIf
			tmp=_cola[j]
			_cola[j]=_cola[k]
			_cola[k]=tmp
			k=j			
		Wend
	EndMethod
		
EndType

[/codebox]
