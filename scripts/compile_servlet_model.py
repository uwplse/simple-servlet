import sys, yaml, subprocess, tempfile, os
import servlet_def as sd_load
import tempfile, shutil, atexit

def construct_classpath(root_dir, servlet_def):
    servlet_class_path = ":".join([
        os.path.join(root_dir, "build/build-deps/javax.servlet-api-3.0.1.jar"),
        os.path.join(root_dir, "build/build-deps/jsp-api-2.0.jar"),
        os.path.join(root_dir, "build/libs/simple-servlet.jar"),
        os.path.join(root_dir, "build/build-deps/tomcat-jasper-7.0.26.jar"),
    ])

    app_class_path = []

    # This has to be here because we may instrument the application classes
    # (to make unspecified public)
    if "struts" in servlet_def:
        struts_sec = servlet_def["struts"]
        app_class_path.append(struts_sec["generated_dir"] + ":" + struts_sec["struts_jar"])

    app_class_path.append(servlet_def["app_classes"])

    if "jsp_dir" in servlet_def:
        app_class_path.append(servlet_def["jsp_dir"])

    if "extra_libs" in servlet_def:
        el_section = servlet_def["extra_libs"]
        app_class_path += el_section["jars"]

    app_class_path = ":".join(app_class_path)
    return (servlet_class_path, app_class_path)

def compile_and_generate_model(root_project_dir, servlet_def, def_path, class_path):
    base_dir = os.path.dirname(def_path)
    output_dir = os.path.join(base_dir, "generated")
    cache_file = os.path.join(output_dir, "_digest")

    if not os.path.exists(output_dir):
        os.makedirs(output_dir)

    pkg_dir = os.path.join(output_dir, servlet_def["output_package"].replace(".", "/"))
    if not os.path.exists(pkg_dir):
        os.makedirs(pkg_dir)

    generated = os.path.join(pkg_dir, "PseudoMain.java")
    generated_routing = os.path.join(output_dir, "routing.yml")

    if (not os.path.exists(generated) or not os.path.exists(generated_routing)) \
       and os.path.exists(cache_file):
        os.remove(cache_file)

    if root_project_dir == "":
        util_dir = "./scripts"
    else:
        util_dir = os.path.join(root_project_dir, "scripts")

    generate_python = os.path.join(util_dir, "generate_servlet_model.py")

    if "custom_compile" in servlet_def:
        subst = {
            "class_path": class_path,
            "this_dir": base_dir,
            "root_dir": root_project_dir,
            "util_dir": util_dir,
            "servlet_file": sys.argv[1]
        }
        for (k,v) in servlet_def.iteritems():
            if type(v) == str and k not in subst:
                subst[k.replace("-","_")] = v
        with open("/dev/null", "w") as out:
            for cmd_tmpl in servlet_def["custom_compile"]:
                import string
                tmpl = string.Template(cmd_tmpl)
                cmd = tmpl.substitute(subst)
                subprocess.check_call(cmd, shell=True, stdout=out, stderr=subprocess.STDOUT)
    else:
        gen_proc = subprocess.Popen(["python", generate_python, def_path, cache_file],
                         stdout = subprocess.PIPE,
                         stderr = subprocess.PIPE)
        (f,r) = gen_proc.communicate()
        if gen_proc.returncode != 0:
            raise subprocess.CalledProcessError(gen_proc.returncode, 
                                                ["python", generate_python, def_path, cache_file])
        if f.strip() != "cached":
            with open(generated, "w") as f_disk:
                f_disk.write(f)
            with open(generated_routing, "w") as r_disk:
                r_disk.write(r)

            subprocess.check_call(["javac", "-cp", class_path, "-d", output_dir, generated])
    return (output_dir, generated_routing)

def check_instrument_digest_file(d_file):
    with open("/dev/null", "w") as out:
        ret = subprocess.call(["md5sum", "-c", d_file, "--status"], stdout = out, stderr = out)
    return ret == 0

def compute_digest_file(d_file, target_files):
    cmd = ["md5sum"] + [ os.path.realpath(f) for f in target_files ]
    with open(d_file, "w") as f:
        subprocess.check_call(cmd, stdout = f)

def instrument_servlet(root_dir, servlet_class_path, app_class_path,
                       generated_dir, routing, servlet_def):
    instrument_dir = os.path.join(generated_dir, "../instrumented")
    if not os.path.exists(instrument_dir):
        os.makedirs(instrument_dir)

    digest_file = os.path.join(instrument_dir, "_digest")
    if os.path.exists(digest_file):
        if check_instrument_digest_file(digest_file):
            return instrument_dir

    instrument_cp = ":".join([
        os.path.join(root_dir, "build/libs/servlet-model.jar"),
        os.path.join(root_dir, "build/build-deps/*")
    ])

    app_class_path += ":" + generated_dir

    instrument_command = [ "java", "-classpath", instrument_cp,
                           "edu.washington.cse.instrumentation.analysis.InstrumentServlet" ]

    if "instrumentation" in servlet_def and servlet_def["instrumentation"].get("aggressive_inline", False):
        instrument_command.append("--aggressive-inline")

    instrument_command += [ servlet_class_path, app_class_path, routing, instrument_dir ]
    with tempfile.NamedTemporaryFile(mode = "r") as f:
        instrument_command.append(f.name)
        subprocess.check_call(instrument_command)
        target_files = [s.strip() for s in f.readlines()]

    target_files.append(os.path.join(generated_dir, "_digest"))
    compute_digest_file(digest_file, target_files)

    return instrument_dir


def merge_class_tree(temp_dir, class_tree_root):
    for (d_name, _, files) in os.walk(class_tree_root):
        for f_name in files:
            if not f_name.endswith(".class"):
                continue
            full_path = os.path.join(d_name, f_name)
            rel_path = os.path.relpath(full_path, class_tree_root)
            package_dir = os.path.dirname(rel_path)
            target_dir = os.path.join(temp_dir, package_dir)
            target_path = os.path.join(temp_dir, rel_path)
            if os.path.exists(target_path):
                continue
            if not os.path.exists(target_dir):
                os.makedirs(target_dir)
            shutil.copy2(full_path, target_path)


def package_model(generated_dir, class_trees):
    temp_dir = tempfile.mkdtemp()
    atexit.register(lambda: shutil.rmtree(temp_dir, True))
    for ct in class_trees:
        merge_class_tree(temp_dir, ct)
    jar_build_cmd = [
        "jar", "cf", os.path.join(generated_dir, "model.jar")
    ]
    for d in os.listdir(temp_dir):
        jar_build_cmd += ["-C", temp_dir, d]
    subprocess.check_call(jar_build_cmd)

def generate_include_file(generated_dir, servlet_def):   
    include = [
        "javax.servlet.*", "org.apache.jasper.runtime.HttpJspBase"
    ]
    exclude = [
        "org.apache.jasper.runtime.*"
    ]

    if "struts" in servlet_def:
        include += [
            "org.apache.struts.action.ActionServlet","org.apache.struts.action.Action", "org.apache.struts.actions.DispatchAction" 
        ]
        exclude = [
            "org.apache.struts.*"
        ]

    if "extra_libs" in servlet_def:
        extra_lib_section = servlet_def["extra_libs"]
        include += extra_lib_section.get("include", [])
        exclude += extra_lib_section.get("exclude", [])

    line_spec = map(lambda il: "+" + il, include) + map(lambda el: "-" + el, exclude)
    generated_include_exclude = os.path.join(generated_dir, "include.list")
    with open(generated_include_exclude, "w") as f:
        for l in line_spec:
            print >> f, l

def main(argv):   
    this_dir = os.path.dirname(os.path.realpath(sys.argv[0]))
    root_project_dir = os.path.join(this_dir, "..")

    servlet_def = sd_load.load(argv[1])
    (servlet_class_path, app_class_path) = construct_classpath(root_project_dir, servlet_def)

    class_path = servlet_class_path + ":" + app_class_path
    (generated_dir, generated_routing) = compile_and_generate_model(root_project_dir, servlet_def, sys.argv[1], class_path)

    instrumented_dir = instrument_servlet(root_project_dir,
                                          servlet_class_path, app_class_path, generated_dir,
                                          generated_routing,
                                          servlet_def)

    class_trees = [
        instrumented_dir,
        generated_dir,
    ]
    if "struts" in servlet_def:
        class_trees.append(servlet_def["struts"]["generated_dir"])

    package_model(generated_dir, class_trees)
    generate_include_file(generated_dir, servlet_def)

if __name__ == "__main__":
    main(sys.argv)
