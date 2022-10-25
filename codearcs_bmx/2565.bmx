; ID: 2565
; Author: TaskMaster
; Date: 2009-08-22 22:40:15
; Title: FastQuickSort
; Description: A very fast sorting algorithm.

SuperStrict

Function FastQuickSortString(array:String[])
	QuickSort(array, 0, array.Length - 1)
	InsertionSort(array, 0, array.Length - 1)

	Function QuickSort(a:String[], l:Int, r:Int)
		If (r - l) > 4
			Local tmp:String
			Local i:Int, j:Int, v:String
			i = (r + l) / 2
			If (a[l] > a[i]) 'swap(a, l, i)
				tmp = a[l]
				a[l] = a[i]
				a[i] = tmp
			End If
			If (a[l] > a[r]) 'swap(a, l, r)
				tmp = a[l]
				a[l] = a[r]
				a[r] = tmp
			End If
			If (a[i] > a[r]) 'swap(a, i, r)
				tmp = a[i]
				a[i] = a[r]
				a[r] = tmp
			End If
			j = r - 1
			'swap(a, i, j)
			tmp = a[i]
			a[i] = a[j]
			a[j] = tmp
			i = l
			v = a[j]
			Repeat
				i:+1
				While a[i] < v ; i:+1; Wend
				j:-1
				While a[j] > v ; j:-1;Wend
				If (j < i) Exit
				'swap (a, i, j)
				tmp = a[i]
				a[i] = a[j]
				a[j] = tmp
			Forever
			'swap(a, i, r - 1)
			tmp = a[i]
			a[i] = a[r - 1]
			a[r - 1] = tmp
			QuickSort(a, l, j)
			QuickSort(a, i + 1, r)
		End If
	End Function

	Function InsertionSort(a:String[], lo0:Int, hi0:Int)
		Local i:Int, j:Int, v:String
		For i = lo0 + 1 To hi0
			v = a[i]
			j = i
			While (j > lo0) And (a[j - 1] > v)
				a[j] = a[j - 1]
				j:-1
			Wend
			a[j] = v
		Next
	End Function

End Function
