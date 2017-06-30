# Simple Servlet

A set of (sound) implementations to use when analyzing Servlet based applications,
and a driver generator for Servlet and Apache Struts based applications.

# Building

Simply run `gradle assemble` at the root of the repository. This will produce
`build/lib/simple-servlet.jar`, which contains the compiled stub implementations of the
Servlet and JSP APIs. This process will also compile the jars needed during the model
generation process, and must be run before using the model generation scripts.

To use the model generation scripts you should also have the following python packages:

* jinja2
* PyYaml

# Overview of Model Generation

Simple servlet generates drivers for web applications built on the Servlet Framework.
Precise models for applications that also use the Apache Struts framework are also supported.
The generated model (along with the stub implementations of the Servlet/JSP API)
meant to be used with a static analysis.

The model generation process takes as input a servlet definition
file. This file describes where configuration files and classes can be
found. Based on this information the generation process produces 2 artifacts.

* The **model jar**, which contains instrumented struts/servlet classes, and a driver
class that includes a main method that simulates the lifecycle of the application.
* An **include/exclude file**, which describes packages and classes that should be included or excluded during an analysis.

In addition the process generates a **routing** file, which is used internally to resolve
indirect invocations via the `RequestDispatcher` API.

These artifacts are described in further detail below.

# Servlet Definitions

The definition file is a YAML file which specifies the layout of the web application
and location of key configuration files.

The specification is a dictionary with the following structure. In the following table
the path `foo.bar` represents field `bar` field of a dictionary stored in the top-level
field `foo`.

| Field | Type | Required? | Description |
| --- | --- | --- | --- |
| `web_xml` | Path | yes | The path to the web application's web.xml file |
| `output_pacakge` | String | yes | The java package in which the driver should be generated, e.g., org.example.driver |
| `app_classes` | Path | yes | Path to the folder/jar containing the application's classes |
| `jsp_dir` | Path | no | Path to the folder containing the compiled JSP pages |
| `jsp_mapping` | Path | no | Path to the xml jsp mapping file produced by the Jasper JSP compiler |
| `ignore_serv` | String array | no | List of servlet **names** to omit from the generated driver |
| `error_pages` | String array | no | List of error page URLs, e.g., `/error.jsp` |
| `struts.struts_config` | Path | no | Path to the application's `struts-config.xml` |
| `struts.struts_jar` | Path | yes (if struts support is used) | Path to the struts.jar library used by the application |
| `struts.generated_dir` | Path | yes (if struts support is used) | The directory where instrumented struts classes are placed |
| `struts.ignored_actions` | String array | no | List of action names that should be ignored |
| `struts.ignored_routes` | String array | no | List of routes that should be ignored |
| `struts.tile_config` | Path | no | Path to `tiles-def.xml` |
| `extra_libs.jars` | Path array | no | List of jars that should be included when compiling the generated driver |
| `extra_libs.exclude` | String array | no | List of package patterns (of the form `foo.bar.*`) that define packages to exclude from an analysis |
| `extra_libs.include` | String array | no | List of class names that should be included by an analysis |

# Generating the Servlet Model

Run `python scripts/compile_servlet_model.py path/to/definition/my_servlet_def.yml`. This will
produce the following files:

* `path/to/definition/generated/model.jar` - A Jar containing the generated driver class, and instrumented versions of application and servlet classes to resolve indirect flow
* `path/to/definition/generated/include.list` - A file defining classes and packages to include/exclude during analysis

# Using the Servlet Model

To use the generated servlet model in a static analysis, make both the
generated `model.jar` and `build/libs/simple-servlet.jar` available to
the analysis during class resolution. When resolving classes, analyses
should search `model.jar` before `build/libs/simple-servlet.jar` and
any application classes/jars. For example, if you are using Soot, your
soot classpath should be:

```
path/to/definition/generated/model.jar:path/to/simple-servlet/build/libs/simple-servlet.jar:path/to/application/classes
```

# The Include File

The include file is a line based format. Each line begins with a `-`
or `+` character. The remainder of the line defines a package pattern
(e.g., `org.example.*`) or a classname. The method bodies in these
classes/packages specified should be explicitly included or excluded
from an analysis, depending on the preceding symbol.


# Stub Implementations

The `stubs/` folder contains stub implementations for (most) of the
JSP Servlet API (version 2.5) and implementations of the JSP API
(version 2.0). As described above these classes may be included along
with any application built on top of the Servlet framework.

## Usage notes

* Both `SessionAttributes` and `RequestAttributes` contain a special
static field: `$INSTANCE`. This field can be used to represent
attributes that are global for the lifetime of the application and request respectively.

* Portions of the specification that are not properly modeled throw a new instance
of `UnsupportedModelException`. Analyses can detect the presence of this constructor to
signal an error or issue a warning (or silently continue).

# Specification Variables

Optionally, the servlet definition may contain a _second_ YAML
document that defines a **substition map** for values in the
specification itself (i.e., the first YAML document).  This second
document is a YAML dictionary: the keys are variable names and
corresponding values the values of those variables in the substitution
map.  Any string (including paths or those that appear arrays) in the
servlet specification (i.e., first document) may contain any number of
variable references of the following form: `${`_var\_name_`}`.  When
the servlet defintion is loaded, all references to a variable _v_ are
replaced with the value of the _v_ in the substitution map, i.e., the
value of the _v_ key in the second YAML document.

For example, in the following configuration:

```
web_xml: "${source_dir}/web-app/web.xml"
---
source_dir: /path/to/project_source
```

the `web_xml` field will end up with the value `/path/to/project_source/web-app/web.xml`.

Variable substitution is not recursive, so the following configuration will not work:

```
web_xml: "${source_dir}/web-app/web.xml"
---
source_dir: "${projects_dir}/my_app"
projects_dir: "/path/to/all_projects"
```

However a similar effect can be accomplished using global substitution variables.

## Global Substitution Variables

Simple servlet also defines a set of global variables that are automatically
defined for every servlet specification. By default, this set of variables is a singleton:

* `this_dir`: The folder containing the servlet definition file.

The mapping may be extended in three ways:

* Including a file called `servlet_globals.yml` in the same folder as `servlet_def.py` in this project's `scripts/` directory
* Programmatically, by calling the `servlet_def.declare_globals` function in the `servlet_def` module
* Creating a specialization of simple servlet (see below)

In addition, values in the substitution map undergo their own substitution process: all
references to global variables in the substitution map are replaced by their corresponding
values before substitution in the servlet specification occurs.

Consider the following concrete example. Suppose we have the following `servlet_globals.yml` file:

```
projects_dir: "/path/to/all_projects/"
```

and the following servlet definition file:

```
web_xml: "${source_dir}/web-app/web.xml"
---
source_dir: "${projects_dir}/my_app"
```

Then the `web_xml` field of the servlet specification will ultimately have the value: `/path/to/all_projects/my_app/web-app/web.xml`

## Specializing the Framework

To avoid cluttering a checkout of simple servlet with a configuration file, or creating
wrappers around all scripts to define globals, simple servlet contains a script
that specializes simple servlet to automatically include a set of global variables.

To do so, create the `servlet_globals.yml` file described above, and run the following script

`python scripts/instantiate.py /path/to/servlet_globals.yml output_dir`

This will create a version of `servlet_def.py` in `output_dir` that
includes the global variables in `servlet_globals.yml`, as well as
a symlink to `compile_servlet_model.py`. Thus, to use the instantiation,
simply use `output_dir/compile_servlet_model.py` in place of the
default `compile_servlet_model.py` script.
