# pdf-zoom

## Summary

Command-line tool based on [OpenPDF](https://github.com/LibrePDF/OpenPDF) and [Scopt](https://github.com/scopt/scopt) to make PDF documents more readable for smaller screens of mobile devices.

## Implementation

PDF is a great format for printing on paper, but not so great when it comes to reading from electronic devices. Usually, most PDF documents have margins on each page, which makes sense for printing but is a waste of precious display space on some devices with smaller screens.

Each PDF page has special MediaBox object which basically describes the size of the page. Here is the trick: by changing it's values we can "zoom" the page in ignoring empty margins and increasing content size a little bit.

## Build

To build zip package (could be run anywhere with installed JRE)

```sh
sbt "Universal / packageBin"
```

This will create a file under `target/universal/` directory with name `pdf-zoom-<VERSION>.zip`

To simply run it locally it's enough to run the following command to build and prepare

```sh
sbt stage
cd target/universal/stage
bin/pdf-zoom [options] input output
```

You could also use `sbt run` if you prefer.

## Usage
Help is available on
```sh
pdf-zoom --help
```

Tool requires two arguments: input filename and output filename. First is your existing PDF document, second will be created as a copy.
Optional parameters:
 -  -s, --skip-pages - which pages to skip in format "1-2,3,5", usually useful for title pages
 -  -l, --left - how much to "crop" from left side
 -  -b, --bottom - how much to "crop" from bottom side
 -  -r, --right - how much to "crop" from right side
 -  -t, --top - how much to "crop" from top side

Example

```
pdf-zoom -s 1 -l 40 -b 30 -r 50 -t 10 input.pdf output.pdf
```

Which reads document `input.pdf` crops 40 units from left size, 30 from bottom, 50 from right, 10 from top, skipping page 1 and saves the result in `output.pdf`.

