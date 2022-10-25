; ID: 2918
; Author: ZaPx64
; Date: 2012-02-13 06:24:04
; Title: miniB3D - .x mesh loader
; Description: Load .x meshes with minib3d.

Rem

	Direct3D .x Mesh Loader
	
	* Version 2012-FEB-13 12:05:00
	* License: Free for commercial and non-commercial use (do anything you want with it)
	* Contributing authors:
		* ZaP [ contact (at) starfare (dot) eu ]
	* Changelog:
		2012-FEB-06:	* Release
		2012-FEB-13:	* Frame transformation matrices read & applied correctly
					* Fixed texture loading, textures are now loaded from the directory the meshfile is loaded from
					* Fixed multiple creation of the same sub-mesh

End Rem

Private

Type XLoader_TreeNode
	Field children:TList
	Field content:String
	Field template:String
	Field name:String
	
	Method New()
		Self.children = New TList
	End Method
	
	Method Add(element:XLoader_TreeNode)
		Self.children.AddLast(element)
	End Method
End Type

Public

Type TXLoader

	Function LoadXMesh:TMesh(path:String, parent:TEntity = Null)

		Local s:TStream = ReadFile(path)
		
		If Not s Then Return CreateCube() ;
		
		Local header:String = s.ReadString(4)
		Local version:String = s.ReadString(4)
		Local format:String = s.ReadString(4)
		Local floatsize:Int = Int(s.ReadString(4))
		
		If header = "xof "
			If version = "0302"
				If format = "txt "
					
					Local submesh:TMesh = CreateMesh()
								
					While Not s.Eof()
						' check first byte
						Local checkbyte:Int = s.ReadByte()
						s.Seek(s.Pos() - 1)
	
						If Not (XLoader_isWhitespace(Chr(checkbyte)) Or checkbyte = Asc("/") Or checkbyte = Asc("#"))
							Local read:String = s.ReadString(s.Size() - s.Pos())
							read = XLoader_RemoveUnprintables(read)
							read = read.Replace(" {", "{")
							read = read.Replace("{ ", "{")
							read = read.Replace(" }", "}")
							read = read.Replace("} ", "}")
							
							Local tree:XLoader_TreeNode = XLoader_MakeTree(read)
							
							' fetch material data
							Local matlist:TList = XLoader_FindTreeElements(tree, "material")
							Local material:XLoader_TreeNode
							Local brushes:TBrush[matlist.Count()]
							Local brushnames:String[matlist.Count()]
							Local count:Int = 0
							
							For material = EachIn matlist
								
								brushnames[count] = material.name
							
								Local brushrgba:String[] = material.content[..material.content.Find(";;")].Split(";")
								brushes[count] = CreateBrush(Float(brushrgba[0]) * 255.0, Float(brushrgba[1]) * 255.0, Float(brushrgba[2]) * 255.0)
								brushes[count].BrushAlpha(Float(brushrgba[3]))
								
								Local texturefilestart:Int = material.content.Find("~q")
								
								If texturefilestart <> - 1
									Local texturefilename:String = material.content[texturefilestart + 1..material.content.Find("~q", texturefilestart + 1)]
									Local tex:TTexture = LoadTexture(XLoader_ExtractFilePath(path) + "/" + texturefilename)
									If tex <> Null Then brushes[count].BrushTexture(tex)
								End If
								
								count:+1
							Next

							Local framelist:TList = XLoader_FindTreeElements(tree, "frame")
							Local frametree:XLoader_TreeNode

							For frametree = EachIn framelist
							
								If frametree.name.ToLower() <> "world"
							
									' read transformation matrix
									Local tmatlist:TList = XLoader_FindTreeElements(frametree, "FrameTransformMatrix")
																		
									' assemble mesh
									Local meshnodes:TList = XLoader_FindTreeElements(frametree, "mesh")
									Local meshnode:XLoader_TreeNode
									Local currentmeshnode:Int = 0									
									Local tformmat:TMatrix
									
									For meshnode = EachIn meshnodes
									
										' create corresponding tformmatrix
										Local meshlistelm:Object = tmatlist.ValueAtIndex(currentmeshnode)
										
										If meshlistelm <> Null
											Local tmat:String[] = XLoader_TreeNode(meshlistelm).content.Split(",")
											currentmeshnode:+1
																			
											tformmat:TMatrix = New TMatrix
											tformmat.LoadIdentity()
											Local maty:Int = -1
											Local matx:Int = 0
											
											For matx = 0 To 15
												If matx Mod 4 = 0 Then maty:+1
												tformmat.grid[matx - maty * 4, maty] = Float(tmat[matx])
											Next
										EndIf
									
										' create surface
										Local surf:TSurface = CreateSurface(submesh)
										Local meshdata:String = meshnode.content.Trim()
										meshdata.Replace(" ", "")
										
										' read vertex count
										Local offset:Int = meshdata.Find(";")
										Local vertexcount:Int = Int(meshdata[..offset])
										
										meshdata = meshdata[offset + 1..]
										offset = meshdata.Find(";;")
										
										Local vertexdata:String[] = meshdata[..offset].Replace(",", "").Split(";")
										
										' add vertices
										Local vcount:Int = 0
										
										For vcount = 0 To (vertexcount - 1) * 3 Step 3
											Local vertx:Float = Float(vertexdata[vcount])
											Local verty:Float = Float(vertexdata[vcount + 1])
											Local vertz:Float = Float(vertexdata[vcount + 2])
											
											Local x:Float = vertx * tformmat.grid[0, 0] + verty * tformmat.grid[0, 1] + vertz * tformmat.grid[0, 2] + tformmat.grid[0, 3]
											Local y:Float = vertx * tformmat.grid[1, 0] + verty * tformmat.grid[1, 1] + vertz * tformmat.grid[1, 2] + tformmat.grid[1, 3]
											Local z:Float = vertx * tformmat.grid[2, 0] + verty * tformmat.grid[2, 1] + vertz * tformmat.grid[2, 2] + tformmat.grid[2, 3]
											Local w:Float = tformmat.grid[3, 0] + tformmat.grid[3, 1] + tformmat.grid[3, 2] + tformmat.grid[3, 3]
											
											AddVertex(surf, x, y, z)
										Next
										
										' read face count
										meshdata = meshdata[offset + 2..]
										offset = meshdata.Find(";")
										Local facecount:Int = Int(meshdata[..offset])
										
										meshdata = meshdata[offset + 1..]
										offset = meshdata.Find(";;")
										
										Local facedata:String[] = meshdata[..offset].Replace(";,", ";").Split(";")
										
										' draw faces
										Local fcount:Int = 0
			
										For fcount = 1 To facecount * 2 Step 2
											Local faces:String[] = facedata[fcount].Split(",")
																			
											If faces.Length = 3
												surf.AddTriangle(Int(faces[0]), Int(faces[1]), Int(faces[2]))
											ElseIf faces.Length = 4
												surf.AddTriangle(Int(faces[0]), Int(faces[1]), Int(faces[2]))
												surf.AddTriangle(Int(faces[2]), Int(faces[3]), Int(faces[0]))
											EndIf
										Next
										
										' lookup mesh normals
										Local normals:TList = XLoader_FindTreeElements(meshnode, "meshnormals")
										
										If normals.IsEmpty()
											UpdateNormals(submesh)
										Else
											Local normaldata:String = XLoader_TreeNode(normals.ValueAtIndex(0)).content
											Local normalcount:Int = Int(normaldata[..normaldata.Find(";")])
											normaldata = normaldata[normaldata.Find(";") + 1..normaldata.Find(";;")].Replace(",", "")
											Local normalbits:String[] = normaldata.Split(";")
											Local i:Int = 0
											
											For i = 0 To normalcount - 1
												surf.VertexNormal(i, Float(normalbits[i * 3]), Float(normalbits[i * 3 + 1]), Float(normalbits[i * 3 + 2]))
											Next
										EndIf
										
										' texture coordinates
										Local textcoords:TList = XLoader_FindTreeElements(meshnode, "MeshTextureCoords")
										
										If Not textcoords.IsEmpty()
											Local texturecoordsdata:String = XLoader_TreeNode(textcoords.ValueAtIndex(0)).content
											Local texturecoordscount:Int = Int(texturecoordsdata[..texturecoordsdata.Find(";")])
											texturecoordsdata = texturecoordsdata[texturecoordsdata.Find(";") + 1..texturecoordsdata.Find(";;")]
											Local tcoords:String[] = texturecoordsdata.Replace(",", "").Split(";")
											Local i:Int = 0
											
											For i = 0 To texturecoordscount - 1
												surf.VertexTexCoords(i, Float(tcoords[i * 2]), Float(tcoords[i * 2 + 1]))
											Next
										EndIf
										
										' apply materials
										Local texlist:TList = XLoader_FindTreeElements(meshnode, "MeshMaterialList")
										
										If Not texlist.IsEmpty()
											Local texdata:String = XLoader_TreeNode(texlist.ValueAtIndex(0)).content
											texdata = texdata[texdata.Find(";;") + 3..]
											Local texname:String = texdata[..texdata.Find("}")]
											Local i:Int = 0
											
											For i = 0 To brushnames.Length - 1
												If brushnames[i] = texname Then surf.PaintSurface(brushes[i])
											Next
										End If
									Next
								EndIf
							Next
						Else
							If XLoader_isWhitespace(checkbyte)
								s.Seek(s.Pos() + 1)
							Else
								s.ReadLine()
							End If
						EndIf
					Wend
					
					s.Close()
					
					Return submesh
					
				Else
					DebugLog "X Mesh Loader: Unsupported format '" + format + "'!"
				EndIf
			Else
				DebugLog "X Mesh Loader: Unsupported version '" + version + "'!"
			EndIf
		Else
			DebugLog "X Mesh Loader: Invalid x-mesh!"
		EndIf
	
		Return CreateCube()
	
	End Function

	
	Function XLoader_ExtractFilePath:String(path:String)
		
		Local i : Int = 0
		
		For i = Len(path)-1 To 0 Step -1
			If Chr(path[i]) = "/" Or Chr(path[i]) = "\" Then Return path[..i]
		Next
		
		Return path
		
	End Function


	Function XLoader_FindTreeElements:TList(tree:XLoader_TreeNode, template:String)
		
		Local l:TList = New TList
		
		If Lower(tree.template) = template
			l.AddLast(tree)
		EndIf
		
		Local element:XLoader_TreeNode

		For element = EachIn tree.children
			If element.template = template Then l.AddLast(element)
			If Not element.children.IsEmpty() Then l = XLoader_JoinLists(l, XLoader_FindTreeElements(XLoader_TreeNode(element), template))
		Next
		
		Return l
	
	End Function
	
	
	Function XLoader_JoinLists:TList(l1:TList, l2:TList)
		
		Local o:Object
	
		For o = EachIn l2
			l1.AddLast(o)
		Next
	
		Return l1
	
	End Function
	
	
	Function XLoader_MakeTree:XLoader_TreeNode(s:String, parent:XLoader_TreeNode = Null)
		
		Local root:XLoader_TreeNode = New XLoader_TreeNode
		Local pointer:Int = 0
		Local find:Int = 0
		
		s = s.Trim()
		
		While s.Length > 0
			find = s.find("{", pointer)
			
			If find = -1 Then Exit
			
			Local header:String = s[pointer..find]
			Local bits:String[] = header.Split(" ")
	
			pointer = find + 1
			
			root.template = bits[0]
			If bits.Length > 1 Then root.name = bits[1]
			
			Local frameend:Int = XLoader_FindBracketMatch(s[pointer..])
			Local framecontent:String = s[pointer..frameend + pointer]
			
			root.content = framecontent
			
			s = s[pointer + 1 + frameend..]
			
			Select root.template.ToLower()
				Case "frame"
					Local newNode:XLoader_TreeNode = XLoader_MakeTree(framecontent, root)
					If newNode Then root.Add(newNode)
									
				Case "mesh"
					Local contentend:Int = XLoader_FindAlphaChar(framecontent)
					root.content = framecontent[..contentend]
					Local newNode:XLoader_TreeNode = XLoader_MakeTree(framecontent[contentend..], root)
					If newNode Then root.Add(newNode)
					
				Default
					Local newNode:XLoader_TreeNode = XLoader_MakeTree(s, root)
					If newNode Then root.Add(newNode)
					Exit

			End Select
			
			pointer = 0
		Wend
		
		Return root
		
	End Function
	
	
	Function XLoader_FindAlphaChar:Int(s:String)
		
		Local i:Int = 0
		
		For i = 0 To s.Length - 1
			If s[i] >= 65 And s[i] <= 90 Then Return i ' A-Z
			If s[i] >= 97 And s[i] <= 122 Then Return i ' a-z
		Next
		
		Return - 1
	
	End Function
	
	
	Function XLoader_FindBracketMatch:Int(s:String)
		
		Local index:Int = 1
		Local i:Int = 0
		Local ascbracket:Byte[2]
		
		ascbracket[0] = Asc("{")
		ascbracket[1] = Asc("}")
		
		For i = 0 To s.Length - 1
			Select s[i]
				Case ascbracket[0]
					index:+1
				Case ascbracket[1]
					index:-1
			 End Select
			 
			 If index = 0 Then Return i
		Next
		
		Return - 1
		
	End Function
	
	
	Function XLoader_CharCount:Int(s:String, find:String)
		
		Local count:Int = 0
		Local i:Int = 0
		Local ascfind:Byte = Asc(find)
		
		For i = 0 To s.Length - 1
			If s[i] = ascfind Then count:+1
		Next
		
		Return count
		
	End Function
	
	
	Function XLoader_RemoveUnprintables:String(s:String)
		
		Local i:Int = 0
		
		For i = 0 To 31
			s = s.Replace(Chr(i), "")
		Next
		
		Return s
	
	End Function
	
	
	Function XLoader_isWhitespace:Byte(char:String)
		
		Return Asc(char) <= 32
	
	End Function
	
End Type
