# /// script
# requires-python = ">=3.8"
# dependencies = []
# ///

import os

old_pkg = "com.theveloper.pixelplay"
new_pkg = "com.akr.finalapp"

# Rename directory
src_dir = "app/src/main/java/com/theveloper/pixelplay"
dst_dir = "app/src/main/java/com/akr/finalapp"
os.makedirs("app/src/main/java/com/akr", exist_ok=True)
if os.path.exists(src_dir):
    os.rename(src_dir, dst_dir)

# Update file contents
for root, _, files in os.walk("app/src"):
    for file in files:
        if file.endswith(".kt") or file.endswith(".xml"):
            path = os.path.join(root, file)
            with open(path, "r") as f:
                content = f.read()
            new_content = content.replace(old_pkg, new_pkg)
            if content != new_content:
                with open(path, "w") as f:
                    f.write(new_content)
print("Rename complete")
