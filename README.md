# hakuna-sprite

Generate Sprite files from SVG files.
This tool generates both an index json file and the sprite as a PNG image.
Output can be used for example by mapbox-gl, see: https://www.mapbox.com/mapbox-gl-js/style-spec/#sprite

# Usage

Example:
`java -jar hakuna-sprite.jar /mydir/my.file`

where my.file looks like:
```
swamp 64 64
cross 128 128
plane 256 256
```

the program would then look for the following SVG files:
* /mydir/swamp.svg
* /mydir/cross.svg
* /mydir/plane.svg

and would generate the following files:
* /mydir/my.png
* /mydir/my.json
* /mydir/my@2x.png
* /mydir/my@2x.json

# Syntax for the input file

* 1st column is the name of the style and the name of the SVG file (without the ".svg" suffix)
* 2nd column is the target width of the icon in the sprite (optional, SVG width is used as default)
* 3nd column is the target height of the icon in the sprite (optional, SVG height is used as default)
