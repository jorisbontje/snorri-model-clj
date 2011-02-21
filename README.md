# Snorri-model

Tracks stocks, performs analysis and gives trading advise using the
Snorri-model by Henrik Oude Nijhuis.

Uses Google Appengine via the helpful [appengine-magic][1] library.

[1]: https://github.com/gcv/appengine-magic/

## Run interactively
$ lein repl

## Run devserver
$ lein appengine-prepare
$ dev_appserver.sh war/

## Push to production
$ lein appengine-prepare
$ appcfg.sh update war/

## License

Copyright (C) 2011 Joris Bontje

Distributed under the Eclipse Public License, the same as Clojure.
