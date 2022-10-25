; ID: 1717
; Author: Nilium
; Date: 2006-05-19 23:57:05
; Title: Linked List &amp; Hash Table Collections
; Description: See title; better than BRL.LinkedList's TList

SuperStrict

Public

Global HashKey:Int(name:String) = __dbgHashKey

Private
' Thanks to Robert Jenkins for the original function
' Read his paper http://burtleburtle.net/bob/hash/evahash.html
' Used to give a clear stack trace
Function __dbgHashKey%( s$ )
    Local a%,b%,c%,l%,p@ Ptr,k@ Ptr

    p = s.ToCString( )
    k = p
    l = s.Length
    a = $9e3779b9
    b = a
    c = 52867

    While l >= 12
        a :+ k[0] + (k[1] Shl 8) + (k[2] Shl 16) + (k[3] Shl 24)
        b :+ k[4] + (k[5] Shl 8) + (k[6] Shl 16) + (k[7] Shl 24)
        c :+ k[8] + (k[9] Shl 8) + (k[10] Shl 16) + (k[11] Shl 24)
        ' mix
        a=a-b;  a=a-c;  a=a ~ (c Shr 13)
        b=b-c;  b=b-a;  b=b ~ (a Shl 8)
        c=c-a;  c=c-b;  c=c ~ (b Shr 13)
        a=a-b;  a=a-c;  a=a ~ (c Shr 12)
        b=b-c;  b=b-a;  b=b ~ (a Shl 16)
        c=c-a;  c=c-b;  c=c ~ (b Shr 5)
        a=a-b;  a=a-c;  a=a ~ (c Shr 3)
        b=b-c;  b=b-a;  b=b ~ (a Shl 10)
        c=c-a;  c=c-b;  c=c ~ (b Shr 15)
        k :+ 12
        l :- 12
    Wend

    c :+ s.Length

    Select l
        Case 11 c=c+(k[10] Shl 24)
        Case 10 c=c+(k[9] Shl 16)
        Case 9  c=c+(k[8] Shl 8)
        Case 8  b=b+(k[7] Shl 24);
        Case 7  b=b+(k[6] Shl 16);
        Case 6  b=b+(k[5] Shl 8);
        Case 5  b=b+k[4];
        Case 4  a=a+(k[3] Shl 24);
        Case 3  a=a+(k[2] Shl 16);
        Case 2  a=a+(k[1] Shl 8)
        Case 1  a=a+k[0];
    End Select
    ' mix
    a=a-b;  a=a-c;  a=a ~ (c Shr 13)
    b=b-c;  b=b-a;  b=b ~ (a Shl 8)
    c=c-a;  c=c-b;  c=c ~ (b Shr 13)
    a=a-b;  a=a-c;  a=a ~ (c Shr 12)
    b=b-c;  b=b-a;  b=b ~ (a Shl 16)
    c=c-a;  c=c-b;  c=c ~ (b Shr 5)
    a=a-b;  a=a-c;  a=a ~ (c Shr 3)
    b=b-c;  b=b-a;  b=b ~ (a Shl 10)
    c=c-a;  c=c-b;  c=c ~ (b Shr 15)

    MemFree( p )

    Return c
End Function
HashKey = __dbgHashKey


Public

Type IListIter
    Field l:ILink

    Method HasNext%( )
        Return l<>Null
    End Method

    Method NextObject:Object( )
        Local v:Object = l.v
        l = l.NextLink( )
        Return v
    End Method
End Type

Type ILink
    Field n:ILink
    Field p:ILink
    Field v:Object

    Method Remove( )
        v = Null
        n.p = p
        p.n = n
        p = Null
        n = Null
    End Method

    Method Swap( c:ILink )
        Local t:ILink = c.n
        c.n = n
        n = t
        t = c.p
        c.p = p
        p = t
    End Method

    Method NextLink:ILink( )
        If n.v <> n Then Return n
        Return Null
    End Method

    Method PreviousLink:ILink( )
        If p.v <> p Then Return p
        Return Null
    End Method

    Method Value:Object( )
        Return v
    End Method

    Method Valid%( )
        If v = Self Then Return False
        Return True
    End Method

    Method Compare%( obj:Object )
        Local olink:ILink = ILink(obj)
        If olink Then
            If Not olink.v And Not v Then
                Return 0
            ElseIf Not olink.v Then
                Return 1
            ElseIf Not v Then
                Return -1
            Else
                Return v.Compare( olink.v )
            EndIf
        EndIf
        Return 1
    End Method
End Type

Type IList              ' a circular doubly-linked list
    Field c:ILink       ' center link

    Method New( )
        c = New ILink
        c.n = c
        c.p = c
        c.v = c
    End Method

    Method Delete( )
        Clear( )
        c.v = Null
        c.n = Null
        c.p = Null
        c = Null
    End Method
    
    Method Clone:IList( )
        Local l:IList = New IList
        Local i:ILink = c.n
        While i <> c
            l.AddLast( i.v )
            i = i.n
        Wend
        Return l
    End Method

    Method AddFirst:ILink( obj:Object )
        Local i:ILink = New ILink
        i.v = obj
        AddLinkFirst( i )
        Return i
    End Method

    Method AddLast:ILink( obj:Object )
        Local i:ILink = New ILink
        i.v = obj
        AddLinkLast( i )
        Return i
    End Method

    Method AddLinkFirst( i:ILink )
        i.n = c.n
        i.p = c
        c.n.p = i
        c.n = i
    End Method

    Method AddLinkLast( i:ILink )
        i.p = c.p
        i.n = c
        c.p.n = i
        c.p = i
    End Method
    
    ' insert:ILink, original:ILink
    Method AddLinkAfter( i:ILink, o:ILink )
        i.n = o.n
        i.p = o
        o.n.p = i
        o.n = i
    End Method
    
    Method AddLinkBefore( i:ILink, o:ILink )
        i.p = o.p
        i.n = o
        o.p.n = i
        o.p = i
    End Method
    
    Method GetFirst:Object( )
        Local l:ILink = GetFirstLink( )
        If l Then Return l.v
        Return Null
    End Method

    Method GetFirstLink:ILink( )
        If c.n = c Then Return Null
        Return c.n
    End Method

    Method GetLast:Object( )
        Local l:ILink = GetLastLink( )
        If l Then Return l.v
        Return Null
    End Method

    Method GetLastLink:ILink( )
        If c.p = c Then Return Null
        Return c.p
    End Method

    Method Remove( obj:Object )
        Local i:ILink = c.n
        While i.v <> obj And i <> c
            i = i.n
        Wend
        If i = c Then Return
        i.Remove( )
    End Method

    Method Count%( )
        Local i:ILink = c.n
        Local n% = 0
        While i <> c
            i = i.n
            n :+ 1
        Wend
        Return n
    End Method

    Method ValueAtIndex:Object( idx% )
        Local n% = Count()
        If idx >= n Then
            Return Null
        EndIf
        
        Local i:ILink = c.n
        Local x:Int
        For x = 0 To idx-1
            i = i.n
        Next
        
        Return i.v
    End Method

    Method Merge( o:IList )
        If Not o Then Return
        Local i:ILink = o.c.n
        If Not i Then Return
        While i <> o.c
            AddLast( i.v )
            i = i.n
        Wend
    End Method
    
    Method FindLink:ILink( o:Object )
        Local i:ILink = c.n
        While i <> c
            If i.v = o Then Return i
            i = i.n
        Wend
        Return Null
    End Method

    Method Clear( )
        While c.n <> c
            c.n.Remove( )
        Wend
    End Method

    Method ToArray:Object[]( )
        Local arr:Object[Count()]
        Local l:ILink = c.n
        Local i% = 0
        While l <> c
            arr[i] = l.v
            l = l.n
            i :+ 1
        Wend
        Return arr
    End Method

    Method LinkArray:ILink[]( )
        Local arr:ILink[Count()]
        Local l:ILink = c.n
        Local i% = 0
        While l <> c
            arr[i] = l
            l = l.n
            i :+ 1
        Wend
        Return arr
    End Method

    Method Sort( )
        Local head:ILink = GetFirstLink()
		Local tail:ILink = GetLastLink()
		Local cnt:Int = Count()
		head.p = Null
		tail.n = Null
		head = IList._rec_sort( head, cnt )
		tail = head
		While tail.n
			tail = tail.n
		Wend
		head.p = c; head.p.n = head
		tail.n = c; tail.n.p = tail
    End Method
    
    Function _rec_sort:ILink( head:ILink, num% ) NoDebug
    	Local temp1:ILink, temp2:ILink
		Local ret:ILink
		
		If num <= 2 Then
			If num = 1 Then
				ret = head
			Else
				If head.v.Compare(head.n.v) < 0 Then
					ret = head
				Else
					temp1 = head
					temp2 = head.n
					temp1.p = temp2
					temp2.n = temp1
					temp1.n = Null
					temp2.p = Null
					ret = temp2
				EndIf
			EndIf
		Else
			temp2 = head
			Local n1%, n2%
			n1 = num/2
			n2 = num-n1
			
			For Local idx:Int = 1 To n1-1
				temp2 = temp2.n
			Next
			
			temp1 = temp2
			temp2 = temp2.n
			temp1.n = Null
			temp2.p = Null
			temp1 = head
			
			temp1 = IList._rec_sort( temp1, n1 )
			temp2 = IList._rec_sort( temp2, n2 )
			
			Local l1:Int = False
			ret = temp2
			
			If temp1.v.Compare(temp2.v) < 0 Then
				ret = temp1
				l1 = True
			EndIf
			
			While temp1 <> Null Or temp2 <> Null
				If l1 Then
					While temp1.n And temp1.n.v.Compare(temp2.v) < 0
						temp1 = temp1.n
					Wend
					temp2.p = temp1
					temp1 = temp1.n
					temp2.p.n = temp2
					If temp1 = Null Then
						Exit
					EndIf
				Else
					While temp2.n And temp2.n.v.Compare(temp1.v) < 0
						temp2 = temp2.n
					Wend
					temp1.p = temp2
					temp2 = temp2.n
					temp1.p.n = temp1
					If temp2 = Null Then
						Exit
					EndIf
				EndIf
				
				l1 = Not l1
			Wend
		EndIf
		
		Return ret
    End Function
    
    Method Reversed:IList( )
        Local n:IList = New IList
        Local i:ILink = c.n
        While i ' NexILink checks to see if links are valid and returns Null once the list reaches the center
            n.AddFirst( i.Value( ) )
            i = i.NextLink( )
        Wend
        Return n
    End Method

    Method ObjectEnumerator:IListIter( )
        Local i:IListIter = New IListIter
        i.l = c.n
        Return i
    End Method

    Method IsEmpty%()
        Return (c.n = c)
    End Method

    ' Stack functionality
    Method Push( obj:Object )
        AddLast( obj )
    End Method

    Method Pop:Object( )
        If c.n = c Then Return Null
        Local v:Object = GetLast( )
        GetLastLink( ).Remove( )
        Return v
    End Method

    Method Peek:Object( )
        Return GetLast( )
    End Method
End Type

Type IHashNode Extends ILink
    Field key:Int
End Type

Type IHashTable
    Field cnt%
    Field buckets:IList[256]
    Field combList:IList

    Method New( )
        For Local i:Int = 0 To 255
            buckets[i] = New IList
        Next
    End Method

    Method Clear( )
        For Local i:Int = 0 To 255
            buckets[i].Clear( )
        Next
        If combList Then combList.Clear( )
        combList = Null
    End Method

    Method Delete( )
        Clear( )
        For Local i:Int = 0 To 255
            buckets[i] = Null
        Next
        buckets = Null
    End Method

    Method Retrieve:Object( k:Int )
        Local i:ILink = buckets[k&255].c.n
        Local h:IHashNode
        While i And i.Valid( )
            h = IHashNode(i)
            If h And h.key = K Then
                Return i.v
            EndIf
            i = i.NextLink( )
        Wend
        Return Null
    End Method

    Method Insert( o:Object, k:Int )
        Local n:IHashNode = New IHashNode
        n.key = k
        n.v = o
        buckets[k&$FF].AddLinkFirst(n)
        cnt :+ 1
        combList = Null
    End Method

    Method Remove( k:Int )
        Local i:ILink = buckets[k&255].c.n
        Local h:IHashNode
        While i.Valid( )
            h = IHashNode(i)
            If h And h.key = K Then
                i.Remove( )
                cnt :- 1
                Return
            EndIf
            i = i.NextLink( )
        Wend
        combList = Null
    End Method

    Method ObjectEnumerator:IListIter( )
        If combList = Null Then
            combList = New IList
            For Local i:Int = 0 To 255
                combList.Merge( buckets[i] )
            Next
        EndIf
        Local i:IListIter = New IListIter
        i.l = combList.c.n
        Return i
    End Method
End Type
