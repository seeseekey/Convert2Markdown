# WordPress2Markdown

WordPress2Markdown is a tool that convert WordPress eXtended RSS (WXR) into markdown. Export the WordPress site via
backend and use the WordPress eXtended RSS (WXR) with this tool.

## Usage

WordPress2Markdown is a command line tool.

> java -jar WordPress2Markdown.jar -i wordpress-export.xml -s DATETIME -o /home/seeseekey/MarkdownExport

### Parameter

The options available are:

	[--author -f value] : Filter export by author
	[--authors -a] : Export authors
	[--help -h] : Show help
	[--input -i value] : Input path
	[--output -o value] : Output path
	[--scheme -s /POST_ID|DATETIME/] : Scheme of filenames

## Conversion

WordPress2Markdown converted the following html and other tags:

* \<em\>
* \<b\>
* \<blockquote\>
* \<pre\>
* \<img\>
* \<a\>
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