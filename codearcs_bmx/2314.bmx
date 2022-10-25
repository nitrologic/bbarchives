; ID: 2314
; Author: DavidSimon
; Date: 2008-09-17 18:05:28
; Title: Simple OOP Database engine with Threaded loading *SVN Blitz Required*
; Description: Database library, that performs all it's loading in one thread per datarecord.

Type TZoneRecord Extends TDataRecord
	Field mZone:TMapZone
	Field mWorkZone:TMapZone
	
	Method New() 
		
		mDataType = "Zone"
		
	End Method

	Function CreateZoneRecord:TZoneRecord(z:TMapZone) 
		
		Local r:TZoneRecord = New TZoneRecord
			r.mZone = z
		Return r
		
	End Function
	
	Method SyncFrom() 
		
		mWorkZone = New TMapZone
		mWorkZone.mX = mZone.mX
		mWorkZone.mY = mZone.mY
		mworkZone.mZ = mZone.mZ
		mworkZone.mRadius = mZone.mRadius
		mWorkZone.Name = mZone.name
	'	mworkLight.mGreen = mLight.mGreen
	'	mWorkLight.mBlue = mLight.mBlue
		
	'	mWorkLight.mRange = mLight.mRange
			
	End Method
	
	Method SyncTo() 
		If mZone = Null
			mZone = TMapZone.CreateCircle("", 0, 0, 0, 0, Null) 
		End If
		mZone.Name = mworkzone.Name
		mZone.mx = mworkZone.mx
		mZone.my = mworkZone.my
		mZone.mz = mworkZone.mz
		
		mZone.mRadius = mworkZone.mRadius
		
	End Method
	
	Method WriteHeader() 
		
		mfile.WriteLine(mWorkZone.Name) 
		mfile.WriteFloat(mWorkZone.mX) 
		mfile.WriteFloat(mWorkZone.mY) 
		mFile.WriteFloat(mWorkzone.mZ) 
		
		mFile.WriteFloat(mWorkZone.mRadius) 
				
	End Method
	
	Method ReadHeader() 
		
		Local name:String = mfile.ReadLine() 
		Local lx:Float = mfile.ReadFloat() 
		Local ly:Float = mfile.ReadFloat() 
		Local lz:Float = mfile.ReadFloat() 
		
		Local lradius:Float = mfile.ReadFloat() 
		
		mWorkZone = TMapZone.CreateCircle(name, lx, ly, lz, lradius, Null) 
		 		
	End Method
	
End Type

Type TLightRecord Extends TDataRecord
	Field mLight:TMapLight
	Field mWorkLight:TMapLight
	
	Method New() 
		
		mDataType = "Light"
		
	End Method

	Function CreateLightRecord:TLightRecord(l:TMapLight) 
		
		Local r:TLightRecord = New TLightRecord
			
		r.mLight = l
		
		Return r
		
	End Function
	
	Method SyncFrom() 
		
		mWorkLight = New TMapLight
		mWorkLight.mX = mLight.mX
		mWorkLight.mY = mLight.mY
		mworklight.mZ = mLight.mZ
		
		mworkLight.mRed = mLight.mRed
		mworkLight.mGreen = mLight.mGreen
		mWorkLight.mBlue = mLight.mBlue
		
		mWorkLight.mRange = mLight.mRange
			
	End Method
	
	Method SyncTo() 
		If mlight = Null
			mLight = TMapLight.Create(0, 0, 0, 0, 0, 0, 0, Null) 
		End If
		mLight.mx = mworklight.mx
		mlight.my = mworklight.my
		mlight.mz = mworklight.mz
		
		mlight.mRed = mworklight.mRed
		mlight.mGreen = mworklight.mGreen
		mLight.mBlue = mWorkLight.mBlue
		
		mLight.mRange = mWorkLight.mRange
			
	End Method
	
	Method WriteHeader() 
		
		mfile.WriteFloat(mWorkLight.mX) 
		mfile.WriteFloat(mWorkLight.mY) 
		mFile.WriteFloat(mWorkLight.mZ) 
		
		mFile.WriteFloat(mWorkLight.mRed) 
		mFile.WriteFloat(Self.mWorkLight.mGreen) 
		mFile.WriteFloat(Self.mWorkLight.mBlue) 
		
		mFile.WriteFloat(Self.mWorkLight.mRange) 
		
		
	End Method
	
	Method ReadHeader() 
		
		Local lx:Float = mfile.ReadFloat() 
		Local ly:Float = mfile.ReadFloat() 
		Local lz:Float = mfile.ReadFloat() 
		
		Local lr:Float = mfile.ReadFloat() 
		Local lg:Float = mfile.ReadFloat() 
		Local lb:Float = mfile.ReadFloat() 
		
		Local lrange:Float = mfile.ReadFloat() 
		
		mWorkLight = TMapLight.Create(lx, ly, lz, lr, lg, lb, lrange, Null) 
		 
	End Method
	
End Type

Type TMapRecord Extends TDataRecord

	Field mMap:TMap
	Field mWorkMap:TMap
	
	Method New() 
		mDataType = "Map"
	End Method
	
	Function CreateMapRecord:TMapRecord(m:TMap) 
		
		Local r:TMapRecord = New TMapRecord
		
		r.mMap = m
		
		Return r
		
	End Function

	Method SyncFrom() 
		
		mWorkMap = New TMap
		mWorkMap.mMapWidth = mMap.mMapWidth
		mWorkMap.mMapHeight = mmap.mMapHeight
		mworkmap.mMapDepth = mmap.mMapDepth
		
		mworkmap.mTileWidth = mmap.mTileWidth
		mworkmap.mTileHeight = mmap.mTileHeight
		
		mworkmap.mTile = New TTile[mWorkMap.mMapWidth, mWorkMap.mMapHeight, mWorkMap.mMapDepth] 
		mworkmap.mDrawInfo = New TDrawInfo[mworkmap.mMapWidth, mworkmap.mMapHeight] 
		
		For Local x:Int = 0 Until mworkmap.mMapWidth
			For Local y:Int = 0 Until mworkmap.mMapHeight
				
				mworkmap.mDrawInfo[x, y] = mMap.mDrawInfo[x, y] 
				
				For Local z:Int = 0 Until mworkmap.mMapDepth
					mWorkMAp.mTile[x, y, z] = mMap.mTile[x, y, z] 
						
				Next
			Next
		Next
	
	End Method
	
	Method SyncTo() 
		If mMap = Null
			mMap = New TMap
		End If
		mMap.mMapWidth = mworkMap.mMapWidth
		mMap.mMapHeight = mworkmap.mMapHeight
		mmap.mMapDepth = mworkmap.mMapDepth
		
		mmap.mTileWidth = mworkmap.mTileWidth
		mmap.mTileHeight = mworkmap.mTileHeight
		
		mmap.mTile = New TTile[mWorkMap.mMapWidth, mWorkMap.mMapHeight, mWorkMap.mMapDepth] 
		mmap.mDrawInfo = New TDrawInfo[mworkmap.mMapWidth, mworkmap.mMapHeight] 
		
		For Local x:Int = 0 Until mworkmap.mMapWidth
			For Local y:Int = 0 Until mworkmap.mMapHeight
				
				mmap.mDrawInfo[x, y] = mworkMap.mDrawInfo[x, y] 
				
				For Local z:Int = 0 Until mworkmap.mMapDepth
					mMAp.mTile[x, y, z] = mworkMap.mTile[x, y, z] 
				Next
			
			Next
		Next		
			
	End Method
	
	Method WriteHeader() 
		
		mfile.WriteInt(mWorkMap.mMapWidth) 
		mfile.WriteInt(mWorkMap.mMapHeight) 
		mFile.WriteInt(mWorkMap.mMapDepth) 
		
		mFile.WriteInt(mWorkmap.mTileWidth) 
		mFile.WriteInt(Self.mWorkMap.mTileWidth) 
		
		For Local x:Int = 0 Until mworkmap.mMapWidth
		For Local y:Int = 0 Until mworkmap.mMapHeight
			For Local z:Int = 0 Until mworkmap.mMapDepth
				If mworkmap.mTile[x, y, z] <> Null
					mFile.WriteByte(1) 
					mFile.WriteLine(mworkmap.mTile[x, y, z].dPath) 
					mFile.WriteLine(mworkmap.mTile[x, y, z].nPath) 
				Else
					mFile.WriteByte(0) 
				EndIf
			Next
		Next
		Next
				
		
	End Method
	
	Method ReadHeader() 
		
		Local mw:Int = mfile.ReadInt() 
		Local mh:Int = mfile.ReadInt() 
		Local md:Int = mfile.ReadInt() 
		
		Local tw:Int = mfile.ReadInt() 
		Local th:Int = mfile.ReadInt() 
		
		mworkMap = TMap.Create(mw, mh, md, tw, th) 
		
		Local tl:TList = CreateList() 
				
		For Local x = 0 Until mw
			For Local y = 0 Until mh
				For Local z = 0 Until md
					Local it:Int = mfile.ReadByte() 
					If it
						Local t:TTile = New TTile
						t.dPath = mfile.ReadLine() 
						t.nPath = mfile.ReadLine() 
						mworkmap.mTile[x, y, z] = t
					End If
				Next
			Next
		Next
		
	End Method
	
End Type

Type TTextureRecord Extends TDataRecord

	Field mTexture:Texture

	Method New() 
	
		mDataType = "Texture"
		
	End Method
	
	Function CreateTextureRecord:TTextureRecord(tex:Texture) 
		
		Local r:TTextureRecord = New TTextureRecord
				
		r.mTexture = tex
		If r.mTexture.Path = ""
			Notify "Texture has no path. Aborting."
		EndIf
		
				
		Return r
	
	End Function
	
	Method SyncFrom() 
		
		mBuf = mTexture.buf
		mSize = mtexture.Width * mTexture.Height * mTexture.depth
		mbufstream = CreateRamStream(mbuf, msize, True, True) 
		InFile = False
	
	End Method
	
	Method SyncTo() 
		
		Local pa:String = mtexture.Path
		mTexture = Texture.FromBuf(mbuf, mtexture.Width, mtexture.Height, mtexture.depth) 
		mtexture.Path = pa
		'mTexture.buf = mBuf
		'mTexture.Bind() 
		'mTexture.Upload() 
		'mTexture.Unbind() 
			
	End Method
	
	Method WriteHeader() 
		
		mfile.WriteInt(mTexture.Width) 
		mFile.WriteInt(mTexture.Height) 
		mFile.WriteInt(mTexture.depth) 
		mFile.WriteLine(mTexture.Path) 
	End Method
	
	Method ReadHeader() 
		
		Local w:Int = mFile.ReadInt() 
		Local H:Int = mFile.ReadInt() 
		Local depth:Int = mFile.ReadInt() 
		Local path:String = mfile.ReadLine() 
	'	If mtexture = Null
			
			mTexture = Texture.Blank(w, h, depth) 
			mtexture.Path = path
	'	Else
			
	'		mtexture.Width = w
	'		mtexture.Height = h
	'		mtexture.depth = depth
		
	'	EndIf
		
	End Method

End Type
	
Type TDataRecord
	
	Field InMemory:Int, InFile:Int
	Field Name:String
	Field mDataType:String
	Field mBuf:Byte Ptr
	Field mSize:Int
	Field mBufStream:TRamStream
	Field mPath:String
	Field mFile:TStream
	
	Method SyncFrom() Abstract
	
	Method SyncTo() Abstract
			
	
	Method WriteHeader() Abstract
	
	Method ReadHeader() Abstract
	
	Method CreateBuffer(size:Int) 
		
		If mbuf <> Null
			MemFree mbuf
		EndIf
		mbuf = MemAlloc(size) 
		mbufstream = CreateRamStream(mbuf, msize, True, True) 
		msize = size
				
	End Method

	Method Open() 
		If FileType(mPath) = 0
			mFile = WriteFile(mPath) 
			If mFile = Null
				Notify "Could not create file:" + mPath + " Aborting program."
			End If
			CloseFile mfile
		EndIf
		mFile = OpenFile(mPath) 
	End Method
	
	
	Method Write() 
		
		Open() 
		
		mFile.Seek(0) 
		
		SyncFrom() 
		
		WriteHeader() 
		
		
		
		Local ed:Byte Ptr = MemAlloc(mSize * 2) 
		
		Local el:Int = mSize * 2
		
		compress2(ed, el, mbuf, msize, 9) 
		
		mFile.WriteInt(mSize) 
		mFile.WriteInt(el) 
		mFile.Write(ed, el) 
		
		MemFree ed
		
		Close() 
		
		
	End Method
	
	Field LoadThread:TLoadRecordThread
	
	
	Method IsReady:Int()
	
	
		If LoadThread = Null
		
			Return True
								
		EndIf
		
		Local ret:Int = False
		
		LoadThread.mMutex.Lock()
		ret = loadthread.mDone
		LoadThread.mMutex.Unlock()
		
		Return ret
	
	End Method
	
	Field mThreadDoned = False
	
	Method ThreadDone()
	
		If mThreadDoned = False
		
			mThreadDoned = True
			SyncTo()
			LoadThread = Null
		
		EndIf
		
	End Method
		
	Method Read() 
			
		LoadThread = New TLoadRecordThread
		LoadThread.mRecord = Self
		LoadThread.run()
		Print "Started Load Thread. Item:"+name+" DataType:"+mDataType+" Size:"+mSize
	
	End Method
	
	Method Close() 
	
		CloseFile mFile
	
	End Method
	
End Type

Type TLoadRecordThread Extends AThread

	Field mRecord:TDataRecord
	Field mDone:Int = False
	Field mMutex:AMutex 
	
	Method Init()
	
		mMutex = New AMutex
		mDone = False 
	
	End Method
	
	
	
	Method ThreadLogic:Object()
	
		Print "Loading DataRecord assets. Item:"+mRecord.name+" DataType:"+mRecord.mDataType+" Size:"+mRecord.mSize
		
		mRecord.Open() 
		
		Print "Open succesfull"
		
		mRecord.mFile.Seek(0) 
		
		Print "Seek succesfull"
		
		mRecord.ReadHeader() 
		
		Print "Read Header OK."
		
		Local os:Int = mRecord.mfile.ReadInt() 
		Local ds:Int = mRecord.mFile.ReadInt() 
		
		Print "Read Size OK."
		
		Local cb:Byte Ptr = MemAlloc(ds) 
		
		Print "Allocated Memory OK."
		
		mRecord.CreateBuffer(os) 
		
		Print "Create Internal Buffer OK."
		
		mRecord.mfile.Read(cb, ds) 
				
		Print "Read Record OK."
				
		uncompress(mRecord.mBuf, os, cb, ds) 
		
		Print "Uncompressed data into original form OK."
		
		mRecord.mSize = os
		
		Print "Set Size OK."
		
		mRecord.close() 
		
		Print "Closed Record OK."
		
		'mRecord.SyncTo() 
	
		Print "Synced Resource OK."
	
		mMutex.Lock()
		
		Print "Locked Mutex OK."
		
		mDone = True
		
		Print "Set External Variable OK."
		
		mMutex.unlock()
			
		Print "DataRecord assets loaded. Item:"+mRecord.name+" DataType:"+mRecord.mDataType+" Size:"+mRecord.mSize

	
	End Method
	

End Type 

Type TDatabase

	Field mName:String
	Field mAuthor:String
	Field mCopyright:String
	Field mPath:String
	Field mRecords:TList = CreateList() 
	Field mFile:TStream
	Field mMainPath:String
	
	Method Finished() 
		
		Save() 
			
	End Method
	
	Function Create:TDatabase(name:String, path:String) 
		
		Local r:TDatabase = New TDatabase
		
		r.mName = name
		
		r.mAuthor = "Dreambreaker Software"
		
		r.mCopyright = "(c)Dreambreaker Software 2008"
		
		r.mPath = path
		
		Local fp:String = Path + "DATA"
		If FileType(fp) = 0
			CreateDir(fp, True) 
		End If
		
		Local ind:String = path + ".wdb"
		r.mMainPath:String = ind
		Select FileType(ind) 
			Case 1
				 r.SyncIndex() 
		End Select
						
		Return r
		
	End Function

	Method AddRecord(t:TDataRecord, name:String) 
		
		t.mPath = mPath + "DATA\" + name
		t.InMemory = True
		t.InFile = False
		t.Name = name
		mRecords.addlast(t) 
		WriteIndex() 
		Save() 
		 
	End Method
	
	Method Save() 
		
		For Local r:TDataRecord = EachIn mRecords
			If r.InFile = False
				r.Write() 
				r.InFile = True
			End If
		Next
	
	End Method
	
	Method GetAllOfType:TList(name:String, LoadIfNotInMemory:Int = True) 
	
		 name:String = name.ToLower() 
	
		Local ret:TList = CreateList() 
		
		For Local r:TDataRecord = EachIn mrecords
	
			If r.mDataType.ToLower() = name.ToLower() 
				
				If LoadIfNotInMemory
				
					If r.InMemory = False
						Local ms:Int = MilliSecs() 
						r.read() 
						r.InMemory = True
						Print "Loaded Record:" + r.Name + " in ms:" + (MilliSecs() - ms) 
						
					End If
				
				End If
					
				ret.AddLast(r) 
			End If
	
		Next
		
		Return ret
		
	End Method
	
	Method FindRecord:TDataRecord(name:String, LoadIfNotInMemory:Int = True) 
		
		name = name.ToLower() 
		
		For Local r:TDataRecord = EachIn mRecords
			
			If r.Name.ToLower() = name
				If r.InMemory = False And LoadIfNotInMemory = True And r.LoadThread = Null
					r.Read() 
					r.InMemory = True
				EndIf
				Return r
			EndIf
					
		Next
		
	
		Print "Record List"
		
		For Local r:TDataRecord = EachIn mRecords
			Print "Record:" + r.Name
		Next
		Print "Fin"
		
		Notify "Record Named:" + name + " was not found in db."
		
	End Method
	
	Method SyncIndex() 

		mRecords.Clear() 
	
		Open() 
		
		mFile.Seek(0) 
		
		Local i:Int = mFile.ReadInt() 
		
		While i > 0
			
			
			
			Local path:String = mfile.ReadLine() 
			Local dtype:String = mFile.ReadLine() 
			Local siz:Int = mFile.ReadInt() 
			Local nam:String = mFile.ReadLine() 
			Local d:TDataRecord
			
			Select dtype.ToLower() 
				Case "zone"
					d = TDataRecord(New TZoneRecord) 
				Case "texture"
					d = TDataRecord(New TTextureRecord) 
				Case "map"
					d = TDataRecord(New TMapRecord) 
				Case "light"
					d = TDataRecord(New TLightRecord) 
				Default
					Notify "Unsupported Data Type."
			End Select
			
			d.mPath = path
			d.mDataType = dtype
			d.mSize = siz
			d.InMemory = False
			d.InFile = True
			d.Name = nam
			mRecords.AddLast(d) 
			i:-1
		Wend
		
		Close() 
				
	End Method
	
	Method WriteIndex() 
		
		Open() 
		
		mFile.Seek(0) 
		
		mFile.WriteInt(mRecords.Count()) 
		
		For Local r:TDataRecord = EachIn mRecords
			
			mFile.WriteLine(r.mPath) 
			mfile.WriteLine(r.mDataType) 
			mFile.WriteInt(r.mSize) 
			mFile.WriteLine(r.Name) 
								
		Next
		
		Close() 
	
	End Method
	
	Method Open() 
		If FileType(mMainPath) = 0
			mFile = WriteFile(mMainPath) 
			CloseFile mFile
		End If
		mFile = OpenFile(mMainPath, True, True) 
		If mFile = Null
			Notify "Could not open Database:" + mMainPath
			End
		End If
	End Method

	Method Close() 
		
		mFile.Close() 
			
	End Method
	
	Method SyncFrom() 
				
	
	End Method

End Type
'
'--[ A.B.E Threading Component ]--
'
'
'-[ 
'
'
'Threads are objects that you are supposed to extend to utilize their functionality.
'
'See the Examples/Threading test for an easy to follow example. 
'
'-] 

SuperStrict

Import pub.threads
Import brl.linkedList

Type AMutex

	Field mHandle:Int
	
	Method New()
	
		mHandle = CreateMutex()
	
	End Method
	
	Method Delete()
	
		CloseMutex(mHandle)
	
	End Method
	
	
	
	Method Lock()
	
		LockMutex(mHandle)
		
	End Method
	
	Method Unlock()
	
		UnlockMutex(mHandle)
		
	End Method
	
	
	

End Type


Type AThread
	
	Field mHandle : Int
	
	
	Method Init() Abstract 
	
	Method New()
		
		mHandle = 0
		WaitOnDelete = False 	
		
				
	End Method
	
	Field WaitOnDelete:Int 
	
	Method Delete()
	
		If WaitOnDelete
			WaitFor()
		Else
			detach()
		EndIf
		
	End Method
	
	
	Method Run()
		
		If mhandle <>0
			Detach()
		EndIf
		
		Init()
		
		mHandle = CreateThread(RunThread_A,Object(Self))
		
	End Method
	
	Method WaitFor:Object()
		If mHandle = 0 Return Null
		Local ret:Object = WaitThread(mHandle)
		mHandle = 0
		Return ret
		
	End Method
	
	
	Method Detach()
		If mhandle=0 Return
		DetachThread(mHandle)
		mHandle = 0
	End Method
	
	Method ThreadLogic:Object() Abstract 
	
	
End Type


Function RunThread_A:Object(thread:Object)

	Local t:AThread = AThread(thread)
	
	Return t.ThreadLogic()

End Function
