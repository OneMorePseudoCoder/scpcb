Type Mods
    Field Name$
    Field Description$
    Field Path$
End Type

Function ReloadMods()
    Delete Each Mods
    d% = ReadDir("Mods")
    Repeat
        f$=NextFile(d)
        If f$="" Then Exit
        If f$<>"." And f$<>".." And FileType("Mods\"+f) = 2 Then
            m.Mods = new Mods
            m\Name = file
            m\Path = "Mods\"+f+"\"
        EndIf
    Forever
    CloseDir(d)
End Function

Function DetermineModdedPath$(f$)
    For m.Mods = Each Mods
        modPath$ = m\Path + f
        If FileType(modPath) = 1 Then Return modPath
    Next
    Return f
End Function
