import re

filepath = "/home/prince/ProjectsMain/AKR-Music/Core/Akr-final app/app/src/main/java/com/akr/finalapp/presentation/screens/LibrarySongsTab.kt"

with open(filepath, "rb") as f:
    content = f.read()

# Let's inspect the line endings
print("Line endings:", b"\r\n" in content)

# Normalize line endings to \n
content_normalized = content.replace(b"\r\n", b"\n")

# Let's do the replacement in string format
content_str = content_normalized.decode("utf-8")

old_pattern = """                                     LibraryPlaybackAwareSongItem(
                                         song = song,
                                         playerViewModel = playerViewModel,
                                         isSelected = isSelected,
                                         //albumArtSize = 46.dp,
                                         isSelectionMode = isSelectionMode,
                                         selectionIndex = if (isSelectionMode) getSelectionIndex(song.id) else null,
                                         onLongPress = rememberedOnLongPress,
                                         onMoreOptionsClick = rememberedOnMoreOptionsClick,
                                         onClick = rememberedOnClick
                                     )"""

new_pattern = """                                    val isCurrent = song.id == currentSongId
                                    val isPlayingThisSong = isCurrent && isPlaying

                                    LibraryPlaybackAwareSongItem(
                                        song = song,
                                        playerViewModel = playerViewModel,
                                        isSelected = isSelected,
                                        //albumArtSize = 46.dp,
                                        isSelectionMode = isSelectionMode,
                                        selectionIndex = if (isSelectionMode) getSelectionIndex(song.id) else null,
                                        onLongPress = rememberedOnLongPress,
                                        onMoreOptionsClick = rememberedOnMoreOptionsClick,
                                        onClick = rememberedOnClick,
                                        isCurrentSong = isCurrent,
                                        isPlaying = isPlayingThisSong
                                    )"""

if old_pattern in content_str:
    print("Found old pattern!")
    content_str = content_str.replace(old_pattern, new_pattern)
    with open(filepath, "w", encoding="utf-8", newline="\n") as f:
        f.write(content_str)
    print("Replacement successful!")
else:
    # Let's try searching using a regex to ignore exact whitespace/newlines
    print("Old pattern not found exactly. Trying regex search...")
    # Normalize whitespaces
    norm_old = re.sub(r'\s+', ' ', old_pattern).strip()
    norm_content = re.sub(r'\s+', ' ', content_str)
    if norm_old in norm_content:
        print("Pattern exists but with different whitespace/indentation!")
        # Let's print the actual lines in the file around LibraryPlaybackAwareSongItem
        idx = content_str.find("LibraryPlaybackAwareSongItem")
        if idx != -1:
            print("Actual context:")
            print(repr(content_str[idx - 100:idx + 500]))
    else:
        print("Pattern not found even with normalized whitespace.")
