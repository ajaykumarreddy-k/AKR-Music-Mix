import os
import glob
import shutil

base_dir = "/home/prince/ProjectsMain/AKR-Music/Core/Akr-final app"
old_pkg = "com.theveloper.pixelplay"
new_pkg = "com.akr.finalapp"

old_dir_path = "com/theveloper/pixelplay"
new_dir_path = "com/akr/finalapp"

modules_to_process = ["shared", "wear", "baselineprofile", "innertube"]

for module in modules_to_process:
    module_dir = os.path.join(base_dir, module)
    if not os.path.exists(module_dir):
        continue

    # 1. Replace text in all files
    for root, dirs, files in os.walk(module_dir):
        for file in files:
            if file.endswith((".kt", ".java", ".xml", ".kts", ".pro", ".properties", ".toml")):
                file_path = os.path.join(root, file)
                try:
                    with open(file_path, "r", encoding="utf-8") as f:
                        content = f.read()
                    
                    if old_pkg in content:
                        new_content = content.replace(old_pkg, new_pkg)
                        with open(file_path, "w", encoding="utf-8") as f:
                            f.write(new_content)
                        print(f"Updated content in {file_path}")
                except Exception as e:
                    print(f"Error processing file {file_path}: {e}")

    # 2. Move directories
    src_main_java = os.path.join(module_dir, "src/main/java")
    if os.path.exists(src_main_java):
        old_full_path = os.path.join(src_main_java, old_dir_path)
        new_full_path = os.path.join(src_main_java, new_dir_path)
        
        if os.path.exists(old_full_path):
            os.makedirs(os.path.dirname(new_full_path), exist_ok=True)
            print(f"Moving {old_full_path} to {new_full_path}")
            # If the new directory already exists (e.g., partial move), move contents
            if os.path.exists(new_full_path):
                for item in os.listdir(old_full_path):
                    shutil.move(os.path.join(old_full_path, item), new_full_path)
                shutil.rmtree(old_full_path)
            else:
                shutil.move(old_full_path, new_full_path)

            # Cleanup empty old directories
            com_theveloper = os.path.join(src_main_java, "com/theveloper")
            if os.path.exists(com_theveloper) and not os.listdir(com_theveloper):
                os.rmdir(com_theveloper)
