
import os

def replace_in_file(filepath):
    try:
        with open(filepath, 'r', encoding='utf-8') as f:
            content = f.read()
        
        original_content = content
        
        # 1. Update Package Declarations (only in the moved files)
        if 'src\\main\\java\\az\\hemsoft\\terminaljx\\business\\model' in filepath.replace('/', '\\'):
            content = content.replace('package az.hemsoft.terminaljx.model;', 'package az.hemsoft.terminaljx.business.restaurant.model;')
        elif 'src\\main\\java\\az\\hemsoft\\terminaljx\\business\\service' in filepath.replace('/', '\\'):
            content = content.replace('package az.hemsoft.terminaljx.service;', 'package az.hemsoft.terminaljx.business.restaurant.service;')
        elif 'src\\main\\java\\az\\hemsoft\\terminaljx\\business\\api' in filepath.replace('/', '\\'):
            content = content.replace('package az.hemsoft.terminaljx.api;', 'package az.hemsoft.terminaljx.business.restaurant.api;')

        # 2. Update Import Statements (Global)
        content = content.replace('import az.hemsoft.terminaljx.model.', 'import az.hemsoft.terminaljx.business.restaurant.model.')
        content = content.replace('import az.hemsoft.terminaljx.service.', 'import az.hemsoft.terminaljx.business.restaurant.service.')
        content = content.replace('import az.hemsoft.terminaljx.api.', 'import az.hemsoft.terminaljx.business.restaurant.api.')

        # 3. Update FXML references (if any exist as strings)
        content = content.replace('az.hemsoft.terminaljx.model.', 'az.hemsoft.terminaljx.business.restaurant.model.')
        content = content.replace('az.hemsoft.terminaljx.service.', 'az.hemsoft.terminaljx.business.restaurant.service.')
        
        if content != original_content:
            with open(filepath, 'w', encoding='utf-8') as f:
                f.write(content)
            print(f"Updated: {filepath}")
            
    except Exception as e:
        print(f"Error reading {filepath}: {e}")

# Walk through the src directory
root_dir = 'src/main/java'
for subdir, dirs, files in os.walk(root_dir):
    for file in files:
        if file.endswith('.java') or file.endswith('.fxml'):
            replace_in_file(os.path.join(subdir, file))

# Also check resources for FXMLs
res_dir = 'src/main/resources'
for subdir, dirs, files in os.walk(res_dir):
    for file in files:
        if file.endswith('.fxml'):
            replace_in_file(os.path.join(subdir, file))
