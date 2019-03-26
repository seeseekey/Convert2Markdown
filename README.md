# WordPress2Markdown

WordPress2Markdown is a tool that convert WordPress eXtended RSS (WXR) into markdown.

## Usage

WordPress2Markdown is a command line tool.

> java -jar WordPress2Markdown.jar -i wordpress-export.xml -s DATETIME -o /home/seeseekey/MarkdownExport

### Parameter

Input Path
-input -i (required)

Output Path
-output -o (optional)

Scheme of filenames
-scheme -s (optional)
Parameters: POST_ID, DATETIME

Help
--help -h

## Conversion

WordPress2Markdown converted the following html and other tags:

* <em>
* <b>
* <blockquote>
* <pre>
* <img>
* <a>
* Lists
* WordPress caption blocks ([caption])

All other tags are striped.

## Developing

Project can be compiled with:

> mvn clean compile

Package can be created with:

> mvn clean package

## Authors

* seeseekey - https://seeseekey.net

## License

WordPress2Markdown is licensed under GPL3.