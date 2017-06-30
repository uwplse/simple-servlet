import os, os.path, sys, yaml

script_dir = os.path.realpath(os.path.dirname(sys.argv[0]))

with open(os.path.join(script_dir, "servlet_def.py"), "r") as f:
    blob = f.read()

with open(sys.argv[1], 'r') as f:
    global_mapping = yaml.load(f)

blob += "\n"
to_append = ["declare_globals(**{"]
for (k,v) in global_mapping.iteritems():
    to_append.append("    " + repr(k) + ": " + repr(v) + ",")
to_append.append("})")

blob += "\n".join([ "    " + s for s in to_append ])

target_dir = os.path.realpath(sys.argv[2])

if not os.path.exists(target_dir):
    os.makedirs(target_dir)

with open(os.path.join(target_dir, "servlet_def.py"), "w") as f:
    print >> f, blob

rel_path = os.path.relpath(script_dir, target_dir)

os.symlink(os.path.join(rel_path, "compile_servlet_model.py"), os.path.join(target_dir, "compile_servlet_model.py"))
