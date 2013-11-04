# pimy

pim

## Prerequisites

You will need [Leiningen][1] 1.7.0 or above installed.

[1]: https://github.com/technomancy/leiningen

## Running

To start a web server for the application, run:

    lein serve

## TODO
+ floating menu component
= hide scrolls to save space
+ check scroll on right panel
+ does not reload record on click in records list
+ remove codemirror
x integrate ace editor
+ use requirejs to load angular modules

- improve scrollables event bubbling

- write custom editor using contenteditable attribute

- edit record
- go to element in record list
- implement proper record save
- in records list retrieve records without body to save memory
- implement records cache
- possibility to hide left menu
- clojure: generate version in config file with leiningen

- add possibility for logger to serialize cyclic dependencies http://stackoverflow.com/questions/9382167/serializing-object-that-contains-cyclic-object-value

- add links to other projects: EpicEditor, angularjs-requirejs-seed

## License

Copyright © 2013 mbme
