import difflib

file1 = "/home/prince/ProjectsMain/AKR-Music/Core/PixelPlayer-master/app/src/main/java/com/theveloper/pixelplay/presentation/components/UnifiedPlayerSheetV2.kt"
file2 = "/home/prince/ProjectsMain/AKR-Music/Core/Akr-final app/app/src/main/java/com/akr/finalapp/presentation/components/UnifiedPlayerSheetV2.kt"

with open(file1, "r", encoding="utf-8") as f:
    lines1 = f.readlines()

with open(file2, "r", encoding="utf-8") as f:
    lines2 = f.readlines()

# Filter imports and package lines to compare actual logic
def sanitize_line(line):
    # replace package references
    line = line.replace("com.theveloper.pixelplay", "com.akr.finalapp")
    return line

lines1_sanitized = [sanitize_line(l) for l in lines1]
lines2_sanitized = [sanitize_line(l) for l in lines2]

diff = list(difflib.unified_diff(
    lines1_sanitized,
    lines2_sanitized,
    fromfile="PixelPlayer",
    tofile="Akr-final",
    n=3
))

print(f"Total diff lines: {len(diff)}")
for line in diff[:100]:  # print first 100 lines of diff
    print(line, end="")

if len(diff) > 100:
    print("\n... and more ...")
