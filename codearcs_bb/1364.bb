; ID: 1364
; Author: Picklesworth
; Date: 2005-05-02 22:20:06
; Title: Mesh Position Getting Commands
; Description: Used to get the position of the center of a mesh, and other such important local coordinates

Function MeshX#(mesh)
    Local LeftX#,RightX#
    
    For s = 1 To CountSurfaces(mesh)
        su = GetSurface(mesh,s)
        For v = 0  To CountVertices(su)-1
            If VertexX(su,v)<LeftX Then LeftX=VertexX(su,v)
            If VertexX(su,v)>RightX Then RightX=VertexX(su,v)
        Next
    Next
    
    x# = LeftX + (RightX - LeftX) / 2
    Return x#
End Function
Function MeshY#(mesh)
    Local BottomY#,TopY#

    For s = 1 To CountSurfaces(mesh)
        su = GetSurface(mesh,s)
        For v = 0  To CountVertices(su)-1
            If VertexY(su,v)<BottomY Then BottomY=VertexY(su,v)
            If VertexY(su,v)>TopY Then TopY=VertexY(su,v)
        Next
    Next

    y# = BottomY + (TopY - BottomY) / 2
    Return y#
End Function
Function MeshZ#(mesh)
    Local BackZ#,FrontZ#

    For s = 1 To CountSurfaces(mesh)
        su = GetSurface(mesh,s)
        For v = 0  To CountVertices(su)-1
            If VertexZ(su,v)<BackZ Then BackZ=VertexZ(su,v)
            If VertexZ(su,v)>FrontZ Then FrontZ=VertexZ(su,v)
        Next
    Next

    z# = BackZ + (FrontZ - BackZ) / 2
    Return z#
End Function

Function LeftMost#(mesh)
    Local LeftX#,RightX#    
    For s = 1 To CountSurfaces(mesh)
        su = GetSurface(mesh,s)
        For v = 0  To CountVertices(su)-1
            If VertexX(su,v)<LeftX Then LeftX=VertexX(su,v)
            If VertexX(su,v)>RightX Then RightX=VertexX(su,v)
        Next
    Next
    Return LeftX#
End Function
Function RightMost#(mesh)
    Local LeftX#,RightX#    
    For s = 1 To CountSurfaces(mesh)
        su = GetSurface(mesh,s)
        For v = 0  To CountVertices(su)-1
            If VertexX(su,v)<LeftX Then LeftX=VertexX(su,v)
            If VertexX(su,v)>RightX Then RightX=VertexX(su,v)
        Next
    Next
    Return RightX#
End Function
Function TopMost#(mesh)
    Local BottomY#,TopY#
    For s = 1 To CountSurfaces(mesh)
        su = GetSurface(mesh,s)
        For v = 0  To CountVertices(su)-1
            If VertexY(su,v)<BottomY Then BottomY=VertexY(su,v)
            If VertexY(su,v)>TopY Then TopY=VertexY(su,v)
        Next
    Next
    Return TopY#
End Function
Function BottomMost#(mesh)
    Local BottomY#,TopY#
    For s = 1 To CountSurfaces(mesh)
        su = GetSurface(mesh,s)
        For v = 0  To CountVertices(su)-1
            If VertexY(su,v)<BottomY Then BottomY=VertexY(su,v)
            If VertexY(su,v)>TopY Then TopY=VertexY(su,v)
        Next
    Next
    Return BottomY#
End Function
Function FrontMost#(mesh)
    Local BackZ#,FrontZ#
    For s = 1 To CountSurfaces(mesh)
        su = GetSurface(mesh,s)
        For v = 0  To CountVertices(su)-1
            If VertexZ(su,v)<BackZ Then BackZ=VertexZ(su,v)
            If VertexZ(su,v)>FrontZ Then FrontZ=VertexZ(su,v)
        Next
    Next
    Return FrontZ#
End Function
Function BackMost#(mesh)
    Local BackZ#,FrontZ#
    For s = 1 To CountSurfaces(mesh)
        su = GetSurface(mesh,s)
        For v = 0  To CountVertices(su)-1
            If VertexZ(su,v)<BackZ Then BackZ=VertexZ(su,v)
            If VertexZ(su,v)>FrontZ Then FrontZ=VertexZ(su,v)
        Next
    Next
    Return BackZ#
End Function
