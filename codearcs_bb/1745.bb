; ID: 1745
; Author: Paposo
; Date: 2006-07-03 14:45:48
; Title: Binary tree balanced
; Description: Abstract class for creating trees with O(log(n)) eficiency

SuperStrict

Rem
bbdoc: Arbol Rojo-Negro
End Rem
Module rvm.arbolbin

ModuleInfo "Version: 1.00"
ModuleInfo "Author: Ramon Vidal"
ModuleInfo "License: Public"


Private
Const NODONEGRO:Byte=False
Const NODOROJO:Byte=True
Const ARBOLASCENDENTE:Byte=True
Const ARBOLDESCENDENTE:Byte=False
Public

'	******************************************************************************************************************
'	Nodo binario con balanceo ROJO-NEGRO
'	Es la base para cualquier tipo de nodo balanceado ROJO-NEGRO
'	******************************************************************************************************************

Rem
bbdoc: Implementacion de nodo de arbol binario con balanceo Rojo-Negro
about:
Representa un nodo generico. Normalmente no sera usado por el usuario final <br>
Existen muchos metodos no documentados pero que se pueden usar para nuevas implementaciones de usuario. Consulte el fuente.
End Rem
Type TNodoRN 
	Field izq:TNodoRN=Null
	Field der:TNodoRN=Null
	Field padre:TNodoRN=Null
	Field color:Byte=NODONEGRO
	Field dato:TDatoNodo=Null
	
	'---------------------------------------------------------------------------------------------------------------------------------
	'	Crea un nuevo nodo

	Function create:TNodoRN(dato:TDatoNodo, padre:TNodoRN)
		Local nodo:TNodoRN= New TNodoRN
		nodo.dato=dato
		nodo.padre=padre
		Return nodo
	EndFunction

	'---------------------------------------------------------------------------------------------------------------------------------
	'	Obtiene el nodo raiz

	Method getRaiz:TNodoRN()
		Local p:TNodoRN=Self
		While(p.padre)
			p=p.padre
		Wend
		Return p
	EndMethod
	
	'---------------------------------------------------------------------------------------------------------------------------------
	'	Borra todos los datos de un nodo y elimina sus descendientes

	Method clear()
		If(izq) 
			izq.clear()
		EndIf
		If(der)
			der.clear()
		EndIf
		izq=Null
		der=Null
		padre=Null
		dato=Null
	EndMethod
	
	'---------------------------------------------------------------------------------------------------------------------------------
	'	Actualiza el dato asociado al nodo, devolviendo el dato anteriormente asociado

	Method setDato:TDatoNodo(dato:TDatoNodo)
		Local ant:TDatoNodo=self.dato
		self.dato=dato
		Return ant
	EndMethod
	
	'---------------------------------------------------------------------------------------------------------------------------------
	'	Obtiene el primer nodo segun orden ARBOLASCENDENTE o ARBOLDESCENDENTE

	Method getFirstNodo:TNodoRN(orden:Byte)
		Local retorno:TNodoRN=getRaiz()
		If(orden=ARBOLASCENDENTE)
			While(retorno.izq)
				retorno=retorno.izq
			Wend
		Else
			While(retorno.der)
				retorno=retorno.der
			Wend
		EndIf
		Return retorno
	EndMethod

	'---------------------------------------------------------------------------------------------------------------------------------
	'	Obtiene el ultimo nodo segun orden ARBOLASCENDENTE o ARBOLDESCENDENTE

	Method getLastNodo:TNodoRN(orden:Byte)
		Return getFirstNodo(Not orden)
	EndMethod
	
	'---------------------------------------------------------------------------------------------------------------------------------
	'	Obtiene el siguiente nodo al actual segun orden ARBOLASCENDENTE o ARBOLDESCENDENTE

	Method getNextNodo:TNodoRN(orden:Byte)
		Local retorno:TNodoRN
		
		If(orden=ARBOLASCENDENTE)
			If(self.der)						'	Si el nodo actual tiene rama derecha descendemos por ella
				retorno=Self.der
				While(retorno.izq)			'	Nos movemos hacia el mas pequeño
					retorno=retorno.izq
				Wend
			Else								'	Si no tiene ramas a la derecha hay que volver atras
			
				'	Hay que subir hacia atras hasta que el nodo actual sea un hijo izquierdo o sea raiz
				retorno=Self
				While(retorno)And(retorno.esRamaDer())
					retorno=retorno.padre
				Wend
				If(retorno)
					retorno=retorno.padre
				EndIf
			
			EndIf		
		Else
			If(self.izq)						'	Si el nodo actual tiene rama izquierda descendemos por ella
				retorno=self.izq
				While(retorno.der)			'	Nos movemos hacia el mas grande
					retorno=retorno.der
				Wend
			Else								'	Si no tiene ramas a la izquierda hay que volver atras
			
				'	Hay que subir hacia atras hasta que el nodo actual sea un hijo derecho o sea raiz
				retorno=Self
				While((retorno)And(retorno.esRamaIzq()))
					retorno=retorno.padre
				Wend
				If(retorno)
					retorno=retorno.padre
				EndIf
			
			EndIf		

		EndIf
		Return retorno
	EndMethod

	'---------------------------------------------------------------------------------------------------------------------------------
	'	¿Este nodo es una rama izquierda?

	Method esRamaIzq:Byte()
		If(padre) And (padre.izq=Self)
			Return True
		Else
			Return False
		EndIf
	EndMethod
	
	'---------------------------------------------------------------------------------------------------------------------------------
	'	¿Este nodo es una rama derecha?

	Method esRamaDer:Byte()
		If(padre) And (padre.der=Self)
			Return True
		Else
			Return False
		EndIf
	EndMethod

EndType

'	******************************************************************************************************************
'	Contenido estandar para cualquier TNodoRN
'	Es la base para implementar el contenido de los nodos
'	******************************************************************************************************************

Rem
bbdoc: Contenedor abstracto estandar de informacion asociada a un nodo
about:
Se usa para dar consistencia a la comparacion con independencia de los datos asociados al nodo <br>
El usuario solo necesitara esta informacion si decide crear una implementacion nueva a partir de TArbolRN
End Rem
Type TDatoNodo Abstract
Rem
bbdoc: Obtiene el objeto a comparar
returns:
Un objeto asociado al nodo
about:
Este metodo es usado por las funciones de comparacion para obtener el dato a comparar
End Rem
	Method getComparable:Object() Abstract
Rem
bbdoc: Obtiene el objeto que representa los datos contenidos en el nodo
returns:
Un objeto asociado al nodo
about:
Este metodo es usado por las funciones de recorrido
End Rem
	Method toObject:Object() Abstract
End Type

'	******************************************************************************************************************
'	Arbol binario abstracto
'	Es la base para cualquier tipo de arbol binario rojo-negro
'	******************************************************************************************************************

Rem
bbdoc: Clase abstracta base para el desarrollo de arboles binarios rojo-negro concretos
about:
Proporciona la gestion basica del arbol y funcionalidades de usuario <br>
En esta documentacion solo se incluiran los metodos de usuario final. <br>
Si se desea extender el arbol para crear una implementacion concreta propia debera consultarse el fuente, donde si estan
documentadas todas las funciones.
End Rem
Type TArbolRN Abstract

	Field orden:Byte=ARBOLASCENDENTE
	Field raiz:TNodoRN=Null
	Field size:Int=0
	
	Field comparar:Int(dato1:TDatoNodo, dato2:TDatoNodo)
	Field recorrido:Byte(dato:Object)


	'---------------------------------------------------------------------------------------------------------------------------------
	'	Accesorio: Obtiene el color de un nodo	

	Function colorOf:Byte(nodo:TNodoRN)
		If(nodo=Null)
			Return NODONEGRO
		Else
			Return nodo.color
		EndIf
	EndFunction

	'---------------------------------------------------------------------------------------------------------------------------------
	'	Accesorio: Obtiene el hijo izquierdo de un nodo

	Function leftOf:TNodoRN(nodo:TNodoRN)
		If(nodo=Null)
			Return Null
		Else
			Return nodo.izq
		EndIf
	EndFunction
	
	'---------------------------------------------------------------------------------------------------------------------------------
	'	Accesorio: Obtiene el hijo derecho de un nodo

	Function rightOf:TNodoRN(nodo:TNodoRN)
		If(nodo=Null)
			Return Null
		Else
			Return nodo.der
		EndIf
	EndFunction
	
	'---------------------------------------------------------------------------------------------------------------------------------
	'	Accesorio: Obtiene el padre de un nodo

	Function parentOf:TNodoRN(nodo:TNodoRN)
		If(nodo=Null)
			Return Null
		Else
			Return nodo.padre
		EndIf
	EndFunction

	'---------------------------------------------------------------------------------------------------------------------------------
	'	Accesorio: Actualiza el color de un nodo

	Function updateColor(nodo:TNodoRN, color:Byte)
		If(nodo) 
			nodo.color=color
		EndIf
	EndFunction

	'---------------------------------------------------------------------------------------------------------------------------------
	'	Comprueba si el arbol esta vacio

	Rem
	bbdoc: ¿Esta vacio el arbol?
	returns: True si esta vacio, false si contiene algun elemento
	about:
	Eficiencia constante O(k) 
	End Rem
	Method isEmpty:Byte()
		If(size=0)
			Return True
		Else
			Return False
		EndIf
	EndMethod
	
	'---------------------------------------------------------------------------------------------------------------------------------
	'	Obtiene el numero de elementos del arbol

	Rem
	bbdoc: Obtiene el numero de elementos que contiene el arbol
	returns: Devuelve un Int con el numero de elementos
	about:
	Eficiencia constante O(k)
	End Rem
	Method getSize:Int()
		Return size
	EndMethod

	'---------------------------------------------------------------------------------------------------------------------------------
	'	Marca el arbol como ascendente. Por defecto

	Rem
	bbdoc: Activa el orden ascendente del arbol
	about:
	Eficiencia constante O(k)
	End Rem
	Method setAscendente()
		orden=ARBOLASCENDENTE
	EndMethod

	'---------------------------------------------------------------------------------------------------------------------------------
	'	Marca el arbol como descendente	

	Rem
	bbdoc: Activa el orden descendente del arbol
	about:
	Eficiencia constante O(k)
	End Rem
	Method setDescendente()
		orden=ARBOLDESCENDENTE
	EndMethod

	'---------------------------------------------------------------------------------------------------------------------------------
	'	¿Es el arbol ascendente?

	Rem
	bbdoc: Comprueba si el arbol se vera en orden ascendente
	returns: True si el arbol es ascendente o false en caso contrario
	about:
	Eficiencia constante O(k)
	End Rem
	Method isAscendente:Byte()
		Return orden
	EndMethod

	'---------------------------------------------------------------------------------------------------------------------------------
	'	Obtener array

	Rem
	bbdoc: Obtiene un array con los elementos del arbol
	returns: Un array de objetos
	about:
	Eficiencia lineal O(n)
	End Rem
	Method toArray:Object[]()
		Local arr:Object[]=New Object[size]
		Local nn:Int=0
		For Local datos:Object=EachIn Self
			arr[nn]=datos
			nn:+1
		Next
		Return arr
	EndMethod
	
	'---------------------------------------------------------------------------------------------------------------------------------
	'	Reduce el numero de elementos en 1

	Method decrementSize()
		size:-1
	EndMethod
	
	'---------------------------------------------------------------------------------------------------------------------------------
	'	Incrementa el numero de elementos en 1

	Method incrementSize()
		size:+1
	EndMethod

	'---------------------------------------------------------------------------------------------------------------------------------
	'	Obtiene el menor nodo mayor o el mayor nodo menor

	Method sucesor:TNodoRN(t:TNodoRN)
		Local p:TNodoRN
		If(t=Null)
			Return Null
		ElseIf(t.der)
			p=t.der
			While(p.izq)
				p=p.izq
			Wend
			Return p
		Else
			p=t.padre
			Local ch:TNodoRN=t
			While( p And ch=p.der)
				ch=p
				p=p.padre
			Wend
			Return p
		EndIf
	EndMethod

	'---------------------------------------------------------------------------------------------------------------------------------
	'	Obtiene el nodo que contiene una determinada clave

	Method getNodo:TNodoRN(dato:TDatoNodo)
		Local p:TNodoRN=raiz
		While(p)
			Local cmp:Int=comparar(dato,p.dato)
			If(cmp=0)
				Return p
			ElseIf(cmp<0)
				p=p.izq
			Else
				p=p.der
			EndIf
		Wend
		Return Null	
	EndMethod

	'---------------------------------------------------------------------------------------------------------------------------------
	'	Obtiene el dato asociado a una clave

	Method getDato:TDatoNodo(dato:TDatoNodo)
		Local p:TNodoRN=getNodo(dato)
		If(p)
			Return p.dato
		Else
			Return Null
		EndIf
	EndMethod

	'---------------------------------------------------------------------------------------------------------------------------------
	'	Inicia un recorrido recursivo

	Rem
	bbdoc: Recorre recursivamente todo el arbol segun orden establecido
	returns: True si la funcion ha detenido el recorrido, false en caso contrario
	about:
	Requiere un puntero a una funcion de recorrido con la siguiente firma: <br>
		funcionrecorrido:byte(dato:Object) <br>
		El valor dato se obtiene del metodo toObject() de TDatoNodo
	Si la funcion devuelve true el recorrido se detendra, si devuelve false continua el recorrido con el siguiente nodo. <br>
	Eficiencia lineal O(n)
	End Rem
	Method recorrer:Byte(recorre:Byte Ptr)
		Local retorno:Byte=False
		If(raiz)
			recorrido=recorre
			If(isAscendente())
				retorno=recorrerNodosAsc(raiz)
			Else
				retorno=recorrerNodosDes(raiz)
			EndIf
			recorrido=Null
		EndIf
		Return retorno
	EndMethod
	
'	------------------------------------------------------------------------------------------------------------------
'	Obtiene un enumerador para poder usarse como colecciones

	Rem
	bbdoc: Obtiene un enumerador de los elementos del arbol
	returns: Un enumerador estandar
	about:
	Sobreescribe el metodo estandar. <br>
	Este metodo lo llama automaticamente la sentencia FOR ... EACHIN .. NEXT para recorridos <br>
	Los datos siempre se obtienen ordenados segun el orden establecido en el arbol <br>
	Eficiencia lineal O(n)
	End Rem
	Method ObjectEnumerator:EnumeradorAbstractoArbol()
		Local enum:EnumeradorArbolBin=New EnumeradorArbolBin
		If(raiz)
			enum.nodo=raiz.getFirstNodo(orden)
		EndIf
		enum.orden=orden
		Return enum
	End Method

	'---------------------------------------------------------------------------------------------------------------------------------
	'	Obtiene el primer nodo segun orden establecido

	Method getFirstNodo:TNodoRN()
		If(raiz=Null) 
			Return Null
		Else
			Return raiz.getFirstNodo(orden)
		EndIf		
	EndMethod

	'---------------------------------------------------------------------------------------------------------------------------------
	'	Obtiene el ultimo nodo segun orden establecido

	Method getLastNodo:TNodoRN()
		If(raiz=Null) 
			Return Null
		Else
			Return raiz.getLastNodo(orden)
		EndIf		
	EndMethod
	
	'---------------------------------------------------------------------------------------------------------------------------------
	'	Obtiene el siguiente nodo al actual segun orden establecido

	Method getNextNodo:TNodoRN(nodoActual:TNodoRN)
		If(nodoActual=Null)
			Return Null
		Else
			Return nodoActual.getNextNodo(orden)
		EndIf
	EndMethod

	'---------------------------------------------------------------------------------------------------------------------------------
	'	Obtiene el nodo que ocupa una determinada posicion segun orden establecido
	'	La posicion a buscar empieza con 0 hasta getSize()-1
	'	Este metodo tiene una eficiencia lineal O(n). No es practico usarlo

	Method getNodoAt:TNodoRN(posicion:Int)
		If(size=0)
			RuntimeError "El arbol esta vacio"
		ElseIf ((size<=posicion) Or (posicion<0))
			RuntimeError "Posicion fuera de rango"
		EndIf
		Local nodo:TNodoRN=getFirstNodo()
		Local cta:Int=0
		While(nodo)
			If(cta=posicion)
				Return nodo
			EndIf
			nodo=getNextNodo(nodo)
			cta:+1
		Wend
	EndMethod

	'---------------------------------------------------------------------------------------------------------------------------------
	'	Efectua una rotacion a la izquierda del nodo

	Method rotarIzq(p:TNodoRN)
		Local x:TNodoRN=p.der
		p.der=x.izq
		If(x.izq)
			x.izq.padre=p
		EndIf
		x.padre=p.padre
		If(p.padre=Null)
			raiz=x
		ElseIf(p.padre.izq=p)
			p.padre.izq=x
		Else
			p.padre.der=x
		EndIf
		x.izq=p
		p.padre=x
	EndMethod
	
	'---------------------------------------------------------------------------------------------------------------------------------
	'	Efectua una rotacion a la derecha del nodo

	Method rotarDer(p:TNodoRN)
		Local x:TNodoRN=p.izq
		p.izq=x.der
		If(x.der)
			x.der.padre=p
		EndIf
		x.padre=p.padre
		If(p.padre=Null)
			raiz=x
		ElseIf(p.padre.der=p)
			p.padre.der=x
		Else
			p.padre.izq=x
		EndIf
		x.der=p
		p.padre=x
	EndMethod

	'---------------------------------------------------------------------------------------------------------------------------------
	'	Balancea el arbol despues de una insercion

	Method corregirInsercion(x:TNodoRN)
		Local y:TNodoRN
		x.color=NODOROJO
		While(x And x<>raiz And x.padre.color=NODOROJO)
			If(parentOf(x)=leftOf(parentOf(parentOf(x))))
				y=rightOf(parentOf(parentOf(x)))
				If(colorOf(y)=NODOROJO)
					updateColor(parentOf(x),NODONEGRO)
					updateColor(y,NODONEGRO)
					updateColor(parentOf(parentOf(x)),NODOROJO)
					x=parentOf(parentOf(x))
				Else
					If(x=rightOf(parentOf(x)))
						x=parentOf(x)
						rotarIzq(x)
					EndIf
					updateColor(parentOf(x),NODONEGRO)
					updateColor(parentOf(parentOf(x)), NODOROJO)
					If(parentOf(parentOf(x)))
						rotarDer(parentOf(parentOf(x)))
					EndIf
				EndIf
			Else
				y=leftOf(parentOf(parentOf(x)))
				If(colorOf(y)=NODOROJO)
					updateColor(parentOf(x),NODONEGRO)
					updateColor(y,NODONEGRO)
					updateColor(parentOf(parentOf(x)),NODOROJO)
					x=parentOf(parentOf(x))
				Else
					If(x=leftOf(parentOf(x)))
						x=parentOf(x)
						rotarDer(x)
					EndIf
					updateColor(parentOf(x),NODONEGRO)
					updateColor(parentOf(parentOf(x)), NODOROJO)
					If(parentOf(parentOf(x)))
						rotarIzq(parentOf(parentOf(x)))
					EndIf
				EndIf
			EndIf
			raiz.color=NODONEGRO
		Wend
	EndMethod
	
	'---------------------------------------------------------------------------------------------------------------------------------
	'	Balancea el arbol despues de una eliminacion

	Method corregirBorrado(x:TNodoRN)
		Local sib:TNodoRN
		While(x<>raiz And colorOf(x)=NODONEGRO)
			If(x=leftOf(parentOf(x)))
				sib=rightOf(parentOf(x))
				If(colorOf(sib)=NODOROJO)
					updateColor(sib,NODONEGRO)
					updateColor(parentOf(x),NODOROJO)
					rotarIzq(parentOf(x))
					sib=rightOf(parentOf(x))
				EndIf
				If(colorOf(leftOf(sib))=NODONEGRO And colorOf(rightOf(sib))=NODONEGRO)
					updateColor(sib, NODOROJO)
					x=parentOf(x)
				Else
					If(colorOf(rightOf(sib))=NODONEGRO)
						updateColor(leftOf(sib),NODONEGRO)
						updateColor(sib,NODOROJO)
						rotarDer(sib)
						sib=rightOf(parentOf(x))
					EndIf
					updateColor(sib, colorOf(parentOf(x)))
					updateColor(parentOf(x), NODONEGRO)
					updateColor(rightOf(sib), NODONEGRO)
					rotarIzq(parentOf(x))
					x=raiz
				EndIf
			Else
				sib=leftOf(parentOf(x))
				If(colorOf(sib)=NODOROJO)
					updateColor(sib,NODONEGRO)
					updateColor(parentOf(x),NODOROJO)
					rotarDer(parentOf(x))
					sib=leftOf(parentOf(x))
				EndIf
				If(colorOf(leftOf(sib))=NODONEGRO And colorOf(rightOf(sib))=NODONEGRO)
					updateColor(sib, NODOROJO)
					x=parentOf(x)
				Else
					If(colorOf(leftOf(sib))=NODONEGRO)
						updateColor(rightOf(sib),NODONEGRO)
						updateColor(sib,NODOROJO)
						rotarIzq(sib)
						sib=leftOf(parentOf(x))
					EndIf
					updateColor(sib, colorOf(parentOf(x)))
					updateColor(parentOf(x), NODONEGRO)
					updateColor(leftOf(sib), NODONEGRO)
					rotarDer(parentOf(x))
					x=raiz
				EndIf			
			EndIf
		Wend
		updateColor(x,NODONEGRO)
	EndMethod

	'---------------------------------------------------------------------------------------------------------------------------------
	'	Inserta un nuevo dato en el arbol y balancea si es necesario

	Method putDato:TDatoNodo(datoNodo:TDatoNodo)
		Local t:TNodoRN=raiz
		Local cmp:Int
		
		If(t=Null)
			incrementSize()
			raiz=TNodoRN.create(datoNodo,Null)
			Return Null
		EndIf
		
		Repeat
			cmp=comparar(datoNodo,t.dato)
			If(cmp=0)
				Return setDatoExistente(t, datoNodo)
			ElseIf(cmp<0)
				If(t.izq)
					t=t.izq
				Else
					incrementSize()
					t.izq=TNodoRN.create(datoNodo, t)
					corregirInsercion(t.izq)
					Return Null
				EndIf
			Else
				If(t.der)
					t=t.der
				Else
					incrementSize()
					t.der=TNodoRN.create(datoNodo, t)
					corregirInsercion(t.der)
					Return Null
				EndIf
			EndIf
		Forever
	EndMethod

	'---------------------------------------------------------------------------------------------------------------------------------
	'	Este metodo se ejecuta cuando ya existe el dato que queremos insertar. 
	'	Por defecto se sustituye el dato, pero una clase heredada de TArbolRN puede sobreescribirlo
	
	Method setDatoExistente:TDatoNodo(nodo:TNodoRN, dato:TDatoNodo)
		Return nodo.setDato(dato)
	EndMethod

	'---------------------------------------------------------------------------------------------------------------------------------
	'	Elimina un dato del arbol
	'	No es necesario que el DatoNodo que se le pasa sea el original. Basta con que se pueda comparar
	'	Devuelve el DatoNodo original contenido en el arbol
	
	Method removeDato:TDatoNodo(dato:TDatoNodo)
		Local p:TNodoRN=getNodo(dato)
		If(p=Null)
			Return Null
		EndIf
		Local ant:TDatoNodo=p.dato
		removeNodo(p)
		Return ant
	EndMethod

	'---------------------------------------------------------------------------------------------------------------------------------
	'	Elimina un nodo del arbol y balancea si es necesario

	Method removeNodo(p:TNodoRN)
		Local s:TNodoRN
		Local rep:TNodoRN
		decrementSize()
		
		If(p.izq And p.der)
			s=sucesor(p)
			p.dato=s.dato
			p=s
		EndIf
		
		If(p.izq)
			rep=p.izq
		Else
			rep=p.der
		EndIf
		
		If(rep)
			rep.padre=p.padre
			If(p.padre=Null)
				raiz=rep
			ElseIf(p.padre.izq=p)
				p.padre.izq=rep
			Else
				p.padre.der=rep
			EndIf
			p.izq=Null 
			p.der=Null
			p.padre=Null
			If(p.color=NODONEGRO)
				corregirBorrado(rep)
			EndIf
		ElseIf(p.padre=Null)
			raiz=Null
		Else
			If(p.color=NODONEGRO)
				corregirBorrado(p)
			EndIf
			If(p.padre)
				If(p=p.padre.izq)
					p.padre.izq=Null
				ElseIf(p=p.padre.der)
					p.padre.der=Null
				EndIf
				p.padre=Null
			EndIf
		EndIf
	EndMethod

	'---------------------------------------------------------------------------------------------------------------------------------
	'	Borra todos los elementos del arbol
	Method clear()
		If(raiz)
			raiz.clear()
			size=0
		EndIf
	EndMethod

	'---------------------------------------------------------------------------------------------------------------------------------
	'	Comprueba la existencia de un dato
	Method existDato:Byte(dato:TDatoNodo)
		If(getNodo(dato))
			Return True
		Else
			Return False
		EndIf
	EndMethod

'	------------------------------------------------------------------------------------------------------------------
'	Auxiliar para recorridos Ascendente

	Method recorrerNodosAsc:Byte(Nodo:TNodoRN)
		
		If(nodo.izq)
			If(recorrerNodosAsc(nodo.izq))
				Return True
			EndIf
		EndIf
		If(recorrido(nodo.dato.toObject()))
			Return True
		EndIf
		If(nodo.der)
			If(recorrerNodosAsc(nodo.der))
				Return True
			EndIf
		EndIf
		Return False
		
	EndMethod

'	------------------------------------------------------------------------------------------------------------------
'	Auxiliar para recorridos Descendente

	Method recorrerNodosDes:Byte(Nodo:TNodoRN)
		
		If(nodo.der)
			If(recorrerNodosDes(nodo.der))
				Return True
			EndIf
		EndIf
		If(recorrido(nodo.dato.toObject()))
			Return True
		EndIf
		If(nodo.izq)
			If(recorrerNodosDes(nodo.izq))
				Return True
			EndIf
		EndIf		
		Return False
		
	EndMethod


EndType

'	******************************************************************************************************************
'	Enumerador de nodos
'	Es la base para cualquier tipo de enumerador de elementos de un arbol balanceado
'	******************************************************************************************************************

Type EnumeradorAbstractoArbol Abstract
	Method HasNext:Byte() Abstract
	Method NextObject:Object() Abstract
EndType
	
Type EnumeradorNodos Extends EnumeradorAbstractoArbol
	Field nodo:TNodoRN
	Field orden:Byte
	
	'---------------------------------------------------------------------------------------------------------------------------------
	'	¿Hay mas nodos?
	
	Method HasNext:Byte()
		Return (nodo<>Null)
	EndMethod

	'---------------------------------------------------------------------------------------------------------------------------------
	'	Devuelve el siguiente nodo

	Method NextObject:Object()
		If(nodo=Null) 
			RuntimeError "No hay mas elementos"
		EndIf
		Local obj:TNodoRN=nodo
		nodo=nodo.getNextNodo(orden)
		Return obj
	EndMethod

EndType	

Type EnumeradorArbolBin Extends EnumeradorNodos
	Method NextObject:Object()
		Local obj:TNodoRN=TNodoRN(super.NextObject())
		Local dn:TDatoNodo=obj.dato
		Return dn.toObject()
	EndMethod
EndType
