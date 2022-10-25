; ID: 508
; Author: Techlord
; Date: 2002-11-24 17:16:27
; Title: Project PLASMA FPS 2004: Queue.bb
; Description: Binary Heap Priority Queue that sorts by lowest key

;Priority Queue - Binary Heap Sort by Lowest Key
;modified by Frankie 'Techlord' Taylor

;References  
;http://www.policyalmanac.org/games/binaryHeaps.htm
;http://www.developersdomain.com/vb/articles/queue.htm

;============================
;QUEUEITEM
;============================

Const QUEUEITEM_MAX=4096
Dim queueitemIndex.queueitem(QUEUEITEM_MAX)

Type queueitem
	Field id%
	Field key%
	Field dat%
End Type

Function queueitemStop()
	For this.queueitem=Each queueitem
		queueitemDelete(this)
	Next
End Function

Function queueitemNew.queueitem()
	this.queueitem=New queueitem
	this\id%=0
	this\key%=0
	this\dat%=0
	Return this
End Function

Function queueitemDelete(this.queueitem)
	Delete this
End Function

Function queueitemMimic(mimic.queueitem,this.queueitem)
	mimic\id%=this\id%
	mimic\key%=this\key%
	mimic\dat%=this\dat%
End Function

Function queueitemCreate.queueitem(id%=0,key%=0,dat%=0)
	this.queueitem=queueitemNew()
	this\id%=id%
	this\key%=key%
	this\dat%=dat%
	Return this
End Function

Function queueitemSwap(queueitem1.queueitem,queueitem2.queueitem)
	queueitemkey%=queueitem1\key%
	queueitemdat%=queueitem1\dat%
	queueitem1\key%=queueitem2\key%
	queueitem1\dat%=queueitem2\dat%
	queueitem2\key%=queueitemkey%
	queueitem2\dat%=queueitemdat%
End Function

;============================
;QUEUE
;============================
Const QUEUE_MAX=255
Dim queueId.queue(QUEUE_MAX)
Global queueIndex.stack=stackIndexCreate(QUEUE_MAX)
Global queueAvail.stack=stackIndexCreate(QUEUE_MAX)

Type queue
	Field id%
	Field size%
	Field queueitems%
	Field queueitem.queueitem[QUEUEITEM_MAX]
End Type

Function queueStart(n=2)
	For loop = 1 To n%
		queueCreate()
	Next
	DebugLog "Queues Initialized ["+Str(n)+"]"	
End Function

Function queueStop()
	For this.queue=Each queue
		queueDelete(this)
	Next
End Function

Function queueNew.queue()
	this.queue=New queue
	this\id%=0
	this\size%=0
	this\queueitems%=0
	this\id%=StackPop(queueIndex.stack)
	queueId(this\id)=this
	Return this
End Function

Function queueDelete(this.queue)
	queueId(this\id)=Null
	StackPush(queueIndex.stack,this\id%)
	Delete this
End Function

Function queueCreate.queue(size%=QUEUEITEM_MAX)
	this.queue=queueNew()
	this\queueitems%=0
	this\size%=size%
	For loop=0 To this\size% 
		this\queueitem.queueitem[loop]=queueitemCreate()
	Next
	Return this
End Function

Function queueDestroy(this.queue)
	For loop=0 To this\size%
		queueitemDelete(this\queueitem[loop])
	Next
	this\queueitems%=0 
	queueDelete(this)
End Function 

Function queuePush(this.queue,key%,dat%)
    this\queueitems%=this\queueitems%+1
    this\queueitem[this\queueitems%]\key%=key%
    this\queueitem[this\queueitems%]\dat%=dat%
	queueBuild(this,this\queueitems%)	
End Function

Function queuePop(this.queue)
    If this\queueitems%
		dat%=this\queueitem[1]\dat%
		queueitemMimic(this\queueitem[1],this\queueitem[this\queueitems%])
		this\queueitems%=this\queueitems%-1
        queueRebuild(this,1)
		Return dat%
    EndIf
End Function

Function queueBuild(this.queue,queuechild%)
    queueparent%=queuechild%/2
    If this\queueitem[queuechild%]\key%<this\queueitem[queueparent%]\key%
		queueitemSwap(this\queueitem[queuechild%],this\queueitem[queueparent%])
      	queueBuild(this,queueparent%)
    EndIf
End Function

Function queueRebuild(this.queue,queueparent%)
    queuechild%=2*queueparent%
	queuechild2%=queuechild%+1
    If queuechild%<this\queueitems%
        If this\queueitem[queuechild2%]\key%<this\queueitem[queuechild%]\key% queuechild%=queuechild2%
        If this\queueitem[queuechild%]\key%<this\queueitem[queueparent%]\key%
			queueitemSwap(this\queueitem[queueparent%],this\queueitem[queuechild%])
            queueRebuild(this,queuechild%)
        End If
    End If
End Function

Function queueDump(this.queue)
	For loop = 1 To this\size%
		value%=queuePop(this)
		If value% DebugLog value%
	Next
End Function

Function queuePushLargest(this.queue,key%,dat%)
    this\queueitems%=this\queueitems%+1
    this\queueitem[this\queueitems%]\key%=key%
    this\queueitem[this\queueitems%]\dat%=dat%
	queueBuildLargest(this,this\queueitems%)	
End Function

Function queuePopLargest(this.queue)
    If this\queueitems%
		dat%=this\queueitem[1]\dat%
		queueitemMimic(this\queueitem[1],this\queueitem[this\queueitems%])
		this\queueitems%=this\queueitems%-1
        queueRebuildLargest(this,1)
		Return dat%
    EndIf
End Function

Function queueBuildLargest(this.queue,queuechild%)
    queueparent%=queuechild%/2
    If this\queueitem[queuechild%]\key%>this\queueitem[queueparent%]\key%
		queueitemSwap(this\queueitem[queuechild%],this\queueitem[queueparent%])
      	queueBuildLargest(this,queueparent%)
    EndIf
End Function

Function queueRebuildLargest(this.queue,queueparent%)
    queuechild%=2*queueparent%
	queuechild2%=queuechild%+1
    If queuechild%<this\queueitems%
        If this\queueitem[queuechild2%]\key%>this\queueitem[queuechild%]\key% queuechild%=queuechild2%
        If this\queueitem[queuechild%]\key%>this\queueitem[queueparent%]\key%
			queueitemSwap(this\queueitem[queueparent%],this\queueitem[queuechild%])
            queueRebuildLargest(this,queuechild%)
        End If
    End If
End Function

Function queuePopLast(this.queue)
    If this\queueitems%
		dat%=this\queueitem[this\queueitems%]\dat%
		this\queueitems%=this\queueitems%-1
    EndIf
	Return dat%
End Function
