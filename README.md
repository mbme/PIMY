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
+ fix pagination bugs
+ fix pagination ui/position
+ fix fixed for edit record save button
+ fix bug with unclickable last record while pagination is visible
+ sort records by last edited date
+ add test cases for string params offset and limit
+ ensure that we're unbinding all jquery events on $destroy
+ ensure that we're unbinding all $on functions
+ update scrollables once a second
+ edit record
+ implement proper record save
+ use bourbon/neat instead of bootstrap
+ implement notifications/popup service
+ implement records delete
+ check if we need to wrap angular element with jquery ot it's already jquery object
+ auto setup test data
+ clojure: generate version in config file with leiningen


- implement new pagination
- font colors

- use codemirror :)

- use angular events instead of jquery (ngMousemove etc.)

- selected text color

- go to element in record list

- in records list retrieve records without body to save memory

- implement records cache
- possibility to hide left menu
- add drawing board to create sketches http://leimi.github.io/drawingboard.js/
- client-side encryption
- IMPLEMENT CORRECT DATABASE INITIALIZATION depending of config settings
- 400 bad request instead of 500 for IllegalArgumentException
- setup text data (maybe database level) compression
- create annotation @Transactional

?- write custom editor using contenteditable attribute

- add links to other projects: EpicEditor, angularjs-requirejs-seed

## License

Copyright © 2013 mbme
