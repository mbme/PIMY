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
x add possibility for logger to serialize cyclic dependencies
+ improve scrollables event bubbling
+ pagination
- fix pagination bugs
- selected text color

- go to element in record list
- implement proper record save
- edit record

- in records list retrieve records without body to save memory
- write custom editor using contenteditable attribute

- implement records cache
- possibility to hide left menu
- clojure: generate version in config file with leiningen
- add test cases for string params offset and limit
- IMPLEMENT CORRECT DATABASE INITIALIZATION depending of config settings
- 400 bad request instead of 500 for IllegalArgumentException
- setup text data (maybe database level) compression
- create annotation @Transactional


- add links to other projects: EpicEditor, angularjs-requirejs-seed

## License

Copyright © 2013 mbme
