; ID: 461
; Author: Vertex
; Date: 2002-10-16 12:13:37
; Title: Milkshape
; Description: Load Milkshape3D-files

; -------------------------------------------------------------------------------------------
; Milkshape-Importer 2.3 by Vertex                                                          :
;                                                                                           :
; Feature:                                                                                  :
;  - Shininess                                                                              :
;  _ Diffuselighting                                                                        :
;  - Alphamaps                                                                              :
;  - Alphatexture                                                                           :
;  - Spheremapping                                                                          :
;                                                                                           :
; To do:                                                                                    :
;  - Including Animation                                                                    :
; -------------------------------------------------------------------------------------------

; -------------------------------------------------------------------------------------------
Function LoadMS3D(File$,Parent = 0)
    Local Stream,Vertices,Triangles,Groups,Materials
    Local Animation,Joints,Mesh,crtMaterials,crtGroups

    Stream = ReadFile(File$)
    If Stream = False Then
        Return False
    EndIf

    If ms3dCheckHeader(Stream) = False Then
        CloseFile Stream
        Return False
    EndIf

    Vertices  = ms3dLoadVertices(Stream)
    Triangles = ms3dLoadTriangles(Stream)
    Groups    = ms3dLoadGroups(Stream)
    Materials = ms3dLoadMaterials(Stream)
    ;Animation = ms3dLoad_Animation(Stream)
    ;Joints    = ms3dLoad_Joints(Stream)

    CloseFile Stream

    If Parent Then
        Mesh = CreateMesh(Parent)
    Else
        Mesh = CreateMesh()
    EndIf 

    crtMaterials = ms3dCreateMaterials(File$,Materials)
    crtGroups    = ms3dCreateGroups(Mesh,Groups,crtMaterials)
                   ms3dCreateVertices(Vertices,Triangles,crtGroups)
                   ms3dCreateTriangles(Triangles,crtGroups)
    Return Mesh
End Function
; -------------------------------------------------------------------------------------------

; -------------------------------------------------------------------------------------------
Function ms3dCheckHeader(Stream)                                    ;         Header        :
    Local I,ID$,Version                                             ;-----------------------:
                                                                    ;                       :
    For I = 1 To 10                                                 ;                       :
        ID$ = ID$ + Chr$(ReadByte(Stream))                          ; ID                    :
    Next                                                            ;                       :
    If ID$ <> "MS3D000000" Then                                     ;                       :
        Return False                                                ;                       :
    EndIf                                                           ;                       :
                                                                    ;                       :
    Version = ReadInt(Stream)                                       ; Version               :
    If Version <> 4 Then                                            ;                       :
        Return False                                                ;                       :
    EndIf                                                           ;                       :
                                                                    ;                       :
    Return True                                                     ;                       :
End Function                                                        ;                       :
; -------------------------------------------------------------------------------------------

; -------------------------------------------------------------------------------------------
Function ms3dLoadVertices(Stream)                                   ;        Vertices       :
    Local VertexCount,Vertices,I                                    ;-----------------------;
                                                                    ;                       :
    VertexCount = ReadShort(Stream)                                 ;                       :
    Vertices    = CreateBank(VertexCount * 13 + 2)                  ;                       :
    PokeShort     Vertices,0,VertexCount                            ;                       :
                                                                    ;                       :
    For I = 1 To VertexCount                                        ;                       :
        SeekFile  Stream,FilePos(Stream) + 1                        ; Flags                 :
        ReadBytes Vertices,Stream,(I - 1) * 13 + 02,12              ; Position              :
        PokeByte  Vertices,(I - 1) * 13 + 14,ReadByte(Stream)       ; BoneID                :
        SeekFile  Stream,FilePos(Stream) + 1                        ; ReferenceCount        :
    Next                                                            ;                       :
                                                                    ;                       :
       Return Vertices                                              ;                       :
End Function                                                        ;                       :
; -------------------------------------------------------------------------------------------

; -------------------------------------------------------------------------------------------
Function ms3dLoadTriangles(Stream)                                  ;       Triangles       ;
    Local TriangleCount,Triangles,I                                 ;-----------------------:
                                                                    ;                       :
    TriangleCount = ReadShort(Stream)                               ;                       :
    Triangles     = CreateBank(TriangleCount * 67 + 2)              ;                       :
    PokeShort       Triangles,0,TriangleCount                       ;                       :
                                                                    ;                       :
       For I = 1 To TriangleCount                                   ;                       :
        SeekFile  Stream,FilePos(Stream) + 2                        ; Flags                 :
        ReadBytes Triangles,Stream,(I - 1) * 67 + 02,6              ; Vertexindices         :
        ReadBytes Triangles,Stream,(I - 1) * 67 + 08,36             ; Vertexnormals         :
        ReadBytes Triangles,Stream,(I - 1) * 67 + 44,24             ; Vertextexcoords       :
        SeekFile  Stream,FilePos(Stream) + 1                        ; Smoothinggroup        :
        PokeByte  Triangles,(I - 1) * 67 + 68,ReadByte(Stream)      ; Groupindex            :
    Next                                                            ;                       :
                                                                    ;                       :
    Return Triangles                                                ;                       :
End Function                                                        ;                       :
; -------------------------------------------------------------------------------------------
Function ms3dLoadGroups(Stream)                                     ;         Groups        :
    Local GroupCount,Groups,I,Dummy                                 ;-----------------------:
                                                                    ;                       :
    GroupCount = ReadShort(Stream)                                  ;                       :
    Groups     = CreateBank(GroupCount + 2)                         ;                       :
    PokeShort    Groups,0,GroupCount                                ;                       :
                                                                    ;                       :
    For I = 1 To GroupCount                                         ;                       :
        SeekFile Stream,FilePos(Stream) + 1                         ; Flags                 :
        SeekFile Stream,FilePos(Stream) + 32                        ; Name                  :
        Dummy  = ReadShort(Stream)                                  ; numTriangle           :
        SeekFile Stream,FilePos(Stream) + (Dummy * 2)               ; Triangle              :
        PokeByte Groups,I - 1 + 2,ReadByte(Stream)                  ; Materialindex         :
    Next                                                            ;                       :
                                                                    ;                       :
    Return Groups                                                   ;                       :
End Function                                                        ;                       :
; -------------------------------------------------------------------------------------------

; -------------------------------------------------------------------------------------------
Function ms3dLoadMaterials(Stream)                                  ;       Materials       :
    Local MaterialCount,Material,I                                  ;-----------------------:
                                                                    ;                       :
    MaterialCount = ReadShort(Stream)                               ;                       :
    Material      = CreateBank(MaterialCount * 277 + 2)             ;                       :
    PokeShort       Material,0,MaterialCount                        ;                       :
                                                                    ;                       :
    For I = 1 To MaterialCount                                      ;                       :
        SeekFile  Stream,FilePos(Stream) + 32                       ; Name                  :
        SeekFile  Stream,FilePos(Stream) + 16                       ; Ambient RGBA          :
        ReadBytes Material,Stream,(I - 1) * 277 + 2,12              ; Difusse RGB           :
        SeekFile  Stream,FilePos(Stream) + 4                        ; Difusse A             :
        SeekFile  Stream,FilePos(Stream) + 16                       ; Specular RGBA         :
        SeekFile  Stream,FilePos(Stream) + 16                       ; Emissive RGBA         :
        PokeFloat Material,(I - 1) * 277 + 14,ReadFloat#(Stream)    ; Shininess             :
        PokeFloat Material,(I - 1) * 277 + 18,ReadFloat#(Stream)    ; Transparency          :
        PokeByte  Material,(I - 1) * 277 + 22,ReadByte(Stream)      ; Mode                  :
        ReadBytes Material,Stream,(I - 1) * 277 + 023,128           ; Filename              :
        ReadBytes Material,Stream,(I - 1) * 277 + 151,128           ; Alphamap              :
    Next                                                            ;                       :
                                                                    ;                       :
    Return Material                                                 ;                       :
End Function                                                        ;                       :
; -------------------------------------------------------------------------------------------

; -------------------------------------------------------------------------------------------
Function ms3dLoadAnimation(Stream)                                  ;       Animation       :
    Local Animation                                                 ;-----------------------:
                                                                    ;                       :
    Animation = CreateBank(12)                                      ;                       :
    PokeFloat Animation,0,ReadFloat#(Stream)                        ; Aniamations FPS       :
    PokeFloat Animation,0,ReadFloat#(Stream)                        ; Curent time           :
    PokeInt   Animation,0,ReadInt(Stream)                           ; Total frames          :
                                                                    ;                       :
End Function                                                        ;                       :
; -------------------------------------------------------------------------------------------
    
; -------------------------------------------------------------------------------------------
Function ms3dLoadJoints(Stream)                                     ;         Joints        :
    Local JointCount,Joints,I,KeyRotCount,KeyPosCount               ;-----------------------:
                                                                    ;                       :
    JointCount = ReadShort(Stream)                                  ;                       :
    Joints     = CreateBank(JointCount * 100 + 2)                   ;                       :
    PokeShort    Joints,0,JointCount                                ;                       :
                                                                    ;                       :
    For I = 1 To JointCount                                         ;                       :
        SeekFile      Stream,FilePos(Stream) + 1                    ; Flags                 :
        ReadBytes     Joints,Stream,(I - 1) * 100 + 02,32           ; Name                  :
        ReadBytes     Joints,Stream,(I - 1) * 100 + 34,32           ; Parentname            :
        ReadBytes     Joints,Stream,(I - 1) * 100 + 66,12           ; Rotation              :
        ReadBytes     Joints,Stream,(I - 1) * 100 + 78,12           ; Position              :
        KeyRotCount = ReadShort(Stream)                             ; Keycount of rotation  :
        KeyPosCount = ReadShort(Stream)                             ; Keycount of position  :
        KeyRot      = ms3dLoadKeyRot(Stream,KeyRotCount)            ; Keyframes of rotation :
        KeyPos      = ms3dLoadKeyPos(Stream,KeyPosCount)            ; Keyframes of position :
        PokeShort     Joints,(I - 1) * 100 + 90,KeyRotCount         ;                       :
        PokeShort     Joints,(I - 1) * 100 + 92,KeyPosCount         ;                       :
        PokeShort     Joints,(I - 1) * 100 + 94,KeyRot              ;                       :
        PokeShort     Joints,(I - 1) * 100 + 96,KeyPos              ;                       :
    Next                                                            ;                       :
                                                                    ;                       :
    Return Joints                                                   ;                       :
End Function                                                        ;                       :
; -------------------------------------------------------------------------------------------

; -------------------------------------------------------------------------------------------
Function ms3dLoadKeyRot(Stream,Count)                               ;         KeyRot        :
    Local KeyRot,I                                                  ;-----------------------:
                                                                    ;                       :
    KeyRot = CreateBank(Count * 16)                                 ;                       :
                                                                    ;                       :
    For I = 1 To Count                                              ;                       :
       PokeFloat KeyRot,(I - 1) * 16 + 00,ReadFloat#(Stream)        ; Time                  :
       ReadBytes KeyRot,Stream,(I - 1) * 16 + 04,12                 ; Rotation              :
    Next                                                            ;                       :
                                                                    ;                       :
    Return KeyRot                                                   ;                       :
End Function                                                        ;                       :
; -------------------------------------------------------------------------------------------

; -------------------------------------------------------------------------------------------
Function ms3dLoadKeyPos(Stream,Count)                               ;         KeyPos        :
    Local KeyPos,I                                                  ;-----------------------:
                                                                    ;                       :
    KeyPos = CreateBank(Count * 16)                                 ;                       :
                                                                    ;                       :
    For I = 1 To Count                                              ;                       :
       PokeFloat KeyPos,(I - 1) * 16 + 00,ReadFloat#(Stream)        ; Time                  :
       ReadBytes KeyPos,Stream,(I - 1) * 16 + 04,12                 ; Position              :
    Next                                                            ;                       :
                                                                    ;                       :
    Return KeyRot                                                   ;                       :
End Function                                                        ;                       :
; -------------------------------------------------------------------------------------------

; -------------------------------------------------------------------------------------------
Function ms3dCreateMaterials(File$,BnkMaterials)
    Local MaterialCount,Material,I,Mode,J
    Local Texturefile$,Alphafile$,Texture,Alphamap
    Local R,G,B,Brush

    MaterialCount = PeekShort(BnkMaterials,0)
    Material      = CreateBank(MaterialCount * 4)

    For I = 1 To MaterialCount
        If PeekByte(BnkMaterials,(I - 1) * 277 + 22) = 128 Then
            Mode = 64
        Else 
            Mode = 1
        EndIf

        For J = (I - 1) * 277 + 023 To (I - 1) * 277 + 151
            If PeekByte(BnkMaterials,J) <> 0 Then
                Texturefile$ = Texturefile$ + Chr$(PeekByte(BnkMaterials,J))
            Else
                Exit
            EndIf
        Next
        Texturefile$ = ms3dGetPath$(File$,Texturefile$)

        For J = (I - 1) * 277 + 151 To (I - 1) * 277 + 289
            If PeekByte(BnkMaterials,J) <> 0 Then
                Alphafile$ = Alphafile$ + Chr$(PeekByte(BnkMaterials,J))
            Else
                Exit
            EndIf
        Next
        Alphafile$ = ms3dGetPath$(File$,Alphafile$)

        Texture = LoadTexture(Texturefile$,Mode)
        R = PeekFloat#(BnkMaterials,(I - 1) * 277 + 02) * 255.0
        G = PeekFloat#(BnkMaterials,(I - 1) * 277 + 06) * 255.0
        B = PeekFloat#(BnkMaterials,(I - 1) * 277 + 10) * 255.0

        If Texture = False Then
            Brush = CreateBrush(R,G,B)
        Else
            FreeTexture Texture
            Alphamap = LoadTexture(Alphafile$)
            If Alphamap Then
                Texture = LoadTexture(Texturefile$,Mode + 2)
                ms3dSetAlphamap(Texture,Alphamap)
                FreeTexture Alphamap
            Else
                Texture = LoadTexture(Texturefile$,Mode)
            EndIf
            Brush = CreateBrush()
            BrushTexture Brush,Texture
            BrushColor   Brush,R,G,B
            FreeTexture  Texture
        EndIf
        BrushShininess Brush,PeekFloat#(BnkMaterials,(I - 1) * 277 + 14)
        BrushAlpha     Brush,PeekFloat#(BnkMaterials,(I - 1) * 277 + 18)

        PokeInt Material,(I - 1) * 4,Brush
    Next

    Return Material
End Function 
; -------------------------------------------------------------------------------------------

; -------------------------------------------------------------------------------------------
Function ms3dCreateGroups(Mesh,BnkGroups,BnkMaterial)
    Local GroupCount,Group,I,Material,Brush,Surface

    GroupCount = PeekShort(BnkGroups,0)
    Group      = CreateBank(GroupCount * 4)

    For I = 1 To GroupCount
        Material = PeekByte(BnkGroups,I + 1)
        If Material <> 255 Then
            Brush = PeekInt(BnkMaterial,Material * 4)
            Surface = CreateSurface(Mesh,Brush)
        Else
            Surface = CreateSurface(Mesh)
        EndIf

        PokeInt Group,(I - 1) * 4,Surface
    Next

    Return Group
End Function 
; -------------------------------------------------------------------------------------------

; -------------------------------------------------------------------------------------------
Function ms3dCreateVertices(BnkVertices,BnkTriangles,BnkGroup)
    Local VertexCount,Group,TriangleCount,I,GI,V0,V1,V2,X#,Y#,Z#
    Local Surface,NX0#,NY0#,NZ0#,NX1#,NY1#,NZ1#,NX2#,NY2#,NZ2#
    Local TU0#,TV0#,TU1#,TV1#,TU2#,TV2#

    VertexCount   = PeekShort(BnkVertices,0)
    Group        = CreateBank(VertexCount)
    TriangleCount = PeekShort(BnkTriangles,0)

    For I = 1 To TriangleCount
        GI = PeekByte(BnkTriangles,(I - 1) * 67 + 68)
        V0 = PeekShort(BnkTriangles,(I - 1) * 67 + 02)
        V1 = PeekShort(BnkTriangles,(I - 1) * 67 + 04)
        V2 = PeekShort(BnkTriangles,(I - 1) * 67 + 06)
        PokeByte Group,V0,GI
        PokeByte Group,V1,GI
        PokeByte Group,V2,GI
    Next

    For I = 1 To VertexCount
        X# = PeekFloat#(BnkVertices,(I - 1) * 13 + 02)
        Y# = PeekFloat#(BnkVertices,(I - 1) * 13 + 06)
        Z# = PeekFloat#(BnkVertices,(I - 1) * 13 + 10)
        GI = PeekByte(Group,I - 1)
        Surface = PeekInt(BnkGroup,GI * 4)
        AddVertex(Surface,X#,Y#,Z#)
    Next
    FreeBank Group

    For I = 1 To TriangleCount
        GI = PeekByte(BnkTriangles,(I - 1) * 67 + 68)
        V0 = PeekShort(BnkTriangles,(I - 1) * 67 + 02)
        V1 = PeekShort(BnkTriangles,(I - 1) * 67 + 04)
        V2 = PeekShort(BnkTriangles,(I - 1) * 67 + 06)
        Surface = PeekInt(BnkGroup,GI * 4)

        NX0# = PeekFloat#(BnkTriangles,(I - 1) * 67 + 08)
        NY0# = PeekFloat#(BnkTriangles,(I - 1) * 67 + 12)
        NZ0# = PeekFloat#(BnkTriangles,(I - 1) * 67 + 16)
        NX1# = PeekFloat#(BnkTriangles,(I - 1) * 67 + 20)
        NY1# = PeekFloat#(BnkTriangles,(I - 1) * 67 + 24)
        NZ1# = PeekFloat#(BnkTriangles,(I - 1) * 67 + 28)
        NX2# = PeekFloat#(BnkTriangles,(I - 1) * 67 + 32)
        NY2# = PeekFloat#(BnkTriangles,(I - 1) * 67 + 36)
        NZ2# = PeekFloat#(BnkTriangles,(I - 1) * 67 + 40)
        TU0# = PeekFloat#(BnkTriangles,(I - 1) * 67 + 44)
        TU1# = PeekFloat#(BnkTriangles,(I - 1) * 67 + 48)
        TU2# = PeekFloat#(BnkTriangles,(I - 1) * 67 + 52)
        TV0# = PeekFloat#(BnkTriangles,(I - 1) * 67 + 56)
        TV1# = PeekFloat#(BnkTriangles,(I - 1) * 67 + 60)
        TV2# = PeekFloat#(BnkTriangles,(I - 1) * 67 + 64)
        
        VertexNormal Surface,V0,NX0#,NY0#,NZ0#
        VertexNormal Surface,V1,NX1#,NY1#,NZ1#
        VertexNormal Surface,V2,NX2#,NY2#,NZ2#
        VertexTexCoords Surface,V0,TU0#,TV0#
        VertexTexCoords Surface,V1,TU1#,TV1#
        VertexTexCoords Surface,V2,TU2#,TV2#
    Next
End Function 
; -------------------------------------------------------------------------------------------

; -------------------------------------------------------------------------------------------
Function ms3dCreateTriangles(BnkTriangles,BnkGroup)
    Local TriangleCount,I,GI,V0,V1,V2,Surface

    TriangleCount = PeekShort(BnkTriangles,0)
    For I = 1 To TriangleCount
        GI = PeekByte(BnkTriangles,(I - 1) * 67 + 68)
        V0 = PeekShort(BnkTriangles,(I - 1) * 67 + 02)
        V1 = PeekShort(BnkTriangles,(I - 1) * 67 + 04)
        V2 = PeekShort(BnkTriangles,(I - 1) * 67 + 06)
        Surface = PeekInt(BnkGroup,GI * 4)
        AddTriangle Surface,V0,V1,V2
    Next
End Function 
; -------------------------------------------------------------------------------------------

; -------------------------------------------------------------------------------------------
Function ms3dGetPath$(File1$,File2$)
    Local I,LastBackshlash

    If Left$(File2$,1) = "." Or Left$(File2$,1) = "\" Then
        For I = 1 To Len(File1$)
            If Mid$(File1$,I,1) = "\" Then LastBackshlash = I
        Next
        Return Left$(File1$,LastBackshlash) + File2$
    Else
        Return File2$
    EndIf
End Function
; -------------------------------------------------------------------------------------------

; -------------------------------------------------------------------------------------------
Function ms3dSetAlphamap(Texture,Alphamap)
    Local OldBuffer,TexBuffer,AlphaBuffer
    Local TexWidth,TexHeight,AlphaWidth
    Local AlphaHeight,M1#,M2#,X,Y,RGB1,R,G,B
    Local RGB2,A,ARGB

    OldBuffer   = GraphicsBuffer()
    TexBuffer   = TextureBuffer(Texture)
    AlphaBuffer = TextureBuffer(Alphamap)
    TexWidth    = TextureWidth(Texture)
    TexHeight   = TextureHeight(Texture)
    AlphaWidth  = TextureWidth(Alphamap)
    AlphaHeight = TextureHeight(Alphamap)
    M1#         = Float#(TexWidth) / Float#(AlphaWidth)
    M2#         = Float#(TexHeight) / Float#(AlphaHeight)

    SetBuffer TexBuffer : LockBuffer
    LockBuffer AlphaBuffer
    For X = 0 To TexWidth - 1
        For Y = 0 To TexHeight - 1
            RGB1 = ReadPixelFast(X,Y,TexBuffer)
            R    = (RGB1 And $FF0000) / $10000
            G    = (RGB1 And $FF00) / $100
            B    = (RGB1 And $FF)

            RGB2 = ReadPixelFast(M1# * X,M2# * Y,AlphaBuffer)
            A    = (RGB2 And $FF0000) / $10000
            ARGB = A * $1000000 + R * $10000 + G * $100 + B
            WritePixelFast(X,Y,ARGB,TexBuffer)
        Next
    Next
    UnlockBuffer AlphaBuffer
    UnlockBuffer : SetBuffer OldBuffer
End Function
; -------------------------------------------------------------------------------------------
