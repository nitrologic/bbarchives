; ID: 906
; Author: Techlord
; Date: 2004-02-03 12:54:07
; Title: Project PLASMA FPS 2004: Worker.bb
; Description: Worker Module aka Helper

;============================
;WORKER MODULE
;============================
Const WORKER_MAX%=8
Dim workerId.worker(WORKER_MAX%)
Global workerIndex.stack=stackIndexCreate(WORKER_MAX%)
Global workerAvail.stack=stackIndexCreate(WORKER_MAX)
Global worker.worker

Type worker
	Field id%
	Field typeid%
	Field entity%
	Field vector.vector
	Field collision%
End Type

Function workerStart()
	For loop = 1 To WORKER_MAX
		workerCreate()
	Next
	DebugLog "Worker Initialized ["+Str(WORKER_MAX)+"]"
End Function

Function workerStop()
	For this.worker=Each worker
		workerDelete(this)
	Next
End Function

Function workerNew.worker()
	this.worker=New worker
	this\id%=0
	this\typeid%=0
	this\entity%=0
	this\vector.vector=vectorNew()
	this\collision%=0
	this\id%=StackPop(workerIndex.stack)
	workerId(this\id)=this
	Return this
End Function

Function workerDelete(this.worker)
	workerId(this\id)=Null
	StackPush(workerIndex.stack,this\id%)
	vectorDelete(this\vector.vector)
	Freeentity this\entity%
	Delete this
End Function

Function workerUpdate()
	For this.worker=Each worker
	Next
End Function

Function workerCopy.worker(this.worker)
	copy.worker=New worker
	copy\id%=this\id%
	copy\typeid%=this\typeid%
	copy\entity%=this\entity%
	copy\vector.vector=vectorCopy(this\vector.vector)
	copy\collision%=this\collision%
	Return copy
End Function

Function workerMimic(mimic.worker,this.worker)
	mimic\id%=this\id%
	mimic\typeid%=this\typeid%
	mimic\entity%=this\entity%
	vectorMimic(mimic\vector.vector,this\vector.vector)
	mimic\collision%=this\collision%
End Function

Function workerCreate.worker()
	this.worker=workerNew()
	this\entity%=CreateSphere(8)
	EntityAlpha this\entity%,0
	EntityType this\entity%,3
	Return this
End Function

Function workerSet(this.worker,typeid%,entity%,vector.vector,collision%)
	this\typeid%=typeid%
	this\entity%=entity%
	this\vector.vector=vector.vector
	this\collision%=collision%
End Function
